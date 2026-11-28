package com.loschicharros.api.news;

import java.util.List;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;

/**
 * Cuerpo de una noticia de Markdown a HTML saneado (reglas técnicas, regla 15 de Java). El HTML que
 * se escriba dentro del Markdown pasa por la misma lista blanca: un {@code <script>} o un {@code
 * onclick} desaparecen. Los títulos {@code #} bajan a {@code h2}, porque el {@code h1} de la página
 * es el título de la noticia, y los enlaces llevan {@code rel="nofollow noopener noreferrer"}.
 */
@Component
class MarkdownRenderer {

  private static final PolicyFactory POLICY =
      new HtmlPolicyBuilder()
          .allowElements(
              "p",
              "br",
              "strong",
              "em",
              "b",
              "i",
              "ul",
              "ol",
              "li",
              "blockquote",
              "h2",
              "h3",
              "h4",
              "pre",
              "code",
              "hr",
              "table",
              "thead",
              "tbody",
              "tr",
              "th",
              "td")
          .allowAttributes("align")
          .matching(false, "left", "center", "right")
          .onElements("th", "td")
          .allowElements((name, attributes) -> "h2", "h1")
          .allowElements((name, attributes) -> "h4", "h5", "h6")
          .allowElements("a")
          .allowAttributes("href")
          .onElements("a")
          .allowStandardUrlProtocols()
          .requireRelsOnLinks("nofollow", "noopener", "noreferrer")
          .toFactory();

  /** Además del Markdown básico, las tablas de GitHub (los horarios, por ejemplo). */
  private static final List<Extension> EXTENSIONS = List.of(TablesExtension.create());

  private final Parser parser = Parser.builder().extensions(EXTENSIONS).build();
  private final HtmlRenderer renderer = HtmlRenderer.builder().extensions(EXTENSIONS).build();

  String toSafeHtml(String markdown) {
    return POLICY.sanitize(renderer.render(parser.parse(markdown)));
  }
}
