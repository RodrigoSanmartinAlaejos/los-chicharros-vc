"""Resume el informe de JaCoCo en el resumen del job de GitHub Actions.

Lee backend/target/site/jacoco/jacoco.csv y escribe una tabla en Markdown con la cobertura
total y la de cada clase *Service, que es donde se mide la meta del 70 % (RNF-33).
Uso: python3 .github/scripts/jacoco-summary.py [ruta/al/jacoco.csv]
"""

import csv
import os
import sys

META = 70.0
csv_path = sys.argv[1] if len(sys.argv) > 1 else "backend/target/site/jacoco/jacoco.csv"


def percent(covered, missed):
    total = covered + missed
    return None if total == 0 else 100.0 * covered / total


def fmt(value):
    return "—" if value is None else f"{value:.1f} %"


def sums(rows):
    return {
        "lines": percent(sum(r["LINE_COVERED"] for r in rows), sum(r["LINE_MISSED"] for r in rows)),
        "branches": percent(
            sum(r["BRANCH_COVERED"] for r in rows), sum(r["BRANCH_MISSED"] for r in rows)
        ),
    }


out = ["## Cobertura del backend (JaCoCo)", ""]

if not os.path.exists(csv_path):
    out.append(f"No se encontró `{csv_path}`: el informe no se generó.")
else:
    with open(csv_path, encoding="utf-8") as f:
        rows = list(csv.DictReader(f))
    for r in rows:
        for key in ("LINE_COVERED", "LINE_MISSED", "BRANCH_COVERED", "BRANCH_MISSED"):
            r[key] = int(r[key])

    # Las clases internas aparecen como Clase.Interna; se cuentan dentro de su servicio
    services = [r for r in rows if r["CLASS"].split(".")[0].endswith("Service")]
    total = sums(rows)

    out += ["| Ámbito | Líneas | Ramas |", "| --- | --- | --- |"]
    out.append(f"| Todo el backend | {fmt(total['lines'])} | {fmt(total['branches'])} |")
    if services:
        svc = sums(services)
        out.append(f"| **Servicios (`*Service`)** | **{fmt(svc['lines'])}** | {fmt(svc['branches'])} |")
        out.append("")
        if svc["lines"] is not None and svc["lines"] < META:
            out.append(f"Los servicios están por debajo de la meta del {META:.0f} % (RNF-33).")
        else:
            out.append(f"Los servicios cumplen la meta del {META:.0f} % (RNF-33).")
        out += ["", "| Servicio | Paquete | Líneas | Ramas |", "| --- | --- | --- | --- |"]
        for r in sorted(services, key=lambda r: (r["PACKAGE"], r["CLASS"])):
            s = sums([r])
            out.append(f"| `{r['CLASS']}` | `{r['PACKAGE']}` | {fmt(s['lines'])} | {fmt(s['branches'])} |")
    else:
        out += ["", f"Todavía no hay clases `*Service`; la meta del {META:.0f} % se medirá cuando las haya."]
    out += ["", "El informe completo está en el artifact `jacoco-report` (abrir `index.html`)."]

text = "\n".join(out) + "\n"
summary = os.environ.get("GITHUB_STEP_SUMMARY")
if summary:
    with open(summary, "a", encoding="utf-8") as f:
        f.write(text)
print(text)
