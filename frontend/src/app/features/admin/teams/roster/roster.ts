import { HttpErrorResponse } from '@angular/common/http';
import {
  Component,
  ElementRef,
  Injector,
  afterNextRender,
  effect,
  inject,
  input,
  output,
  signal,
  untracked,
  viewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  FormControl,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { catchError, debounceTime, distinctUntilChanged, map, of, switchMap } from 'rxjs';
import { applyProblem, errorMessage } from '../../../../shared/admin/problem';
import { PeopleApi } from '../../people/people.api';
import { Person } from '../../people/people.models';
import { TeamsApi } from '../teams.api';
import { Membership, MembershipType, POSITIONS, Team } from '../teams.models';

/**
 * Plantilla de un equipo (H17): jugadores con dorsal y posición, y entrenadores. Se añade gente
 * buscándola entre las personas dadas de alta; el dorsal no se puede repetir en el equipo.
 */
@Component({
  selector: 'cv-roster',
  imports: [ReactiveFormsModule],
  templateUrl: './roster.html',
  styleUrls: ['../../../../shared/admin/crud-panel.scss', './roster.scss'],
})
export class Roster {
  private readonly api = inject(TeamsApi);
  private readonly people = inject(PeopleApi);
  private readonly injector = inject(Injector);

  readonly teamId = input.required<number>();
  /** Nombre del equipo con su categoría y temporada, para el título. */
  readonly title = input.required<string>();
  readonly closed = output<void>();
  private readonly heading = viewChild.required<ElementRef<HTMLElement>>('heading');
  /** Al abrir otra plantilla, el foco va a su título (en móvil queda bajo una lista larga). */
  private focusPending = true;

  protected readonly positions = POSITIONS;
  protected readonly status = signal<'loading' | 'ready' | 'error'>('loading');
  protected readonly team = signal<Team | null>(null);
  protected readonly listError = signal<string | null>(null);
  protected readonly notice = signal('');

  // Alta: buscar a la persona y elegir su papel
  protected readonly addOpen = signal(false);
  protected readonly searchControl = new FormControl('', { nonNullable: true });
  protected readonly candidates = signal<Person[]>([]);
  protected readonly chosen = signal<Person | null>(null);
  protected readonly adding = signal(false);
  protected readonly addError = signal<string | null>(null);
  protected readonly addForm = inject(NonNullableFormBuilder).group({
    type: ['PLAYER' as MembershipType],
    shirtNumber: new FormControl<number | null>(null, [Validators.min(0), Validators.max(99)]),
    position: [''],
  });

  // Edición de dorsal y posición, y baja
  protected readonly editingId = signal<number | null>(null);
  protected readonly confirmingId = signal<number | null>(null);
  protected readonly editError = signal<string | null>(null);
  protected readonly editForm = inject(NonNullableFormBuilder).group({
    shirtNumber: new FormControl<number | null>(null, [Validators.min(0), Validators.max(99)]),
    position: [''],
  });

  constructor() {
    effect(() => {
      const id = this.teamId();
      untracked(() => {
        this.closeAdd();
        this.editingId.set(null);
        this.confirmingId.set(null);
        this.status.set('loading');
        this.focusPending = true;
        this.load(id);
      });
    });
    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        map((text) => text.trim()),
        distinctUntilChanged(),
        switchMap((text) =>
          text.length < 2
            ? of([])
            : this.people.search(text, 0).pipe(
                map((page) => page.content),
                catchError(() => of([])),
              ),
        ),
        takeUntilDestroyed(),
      )
      .subscribe((people) => this.candidates.set(people));
    // Los entrenadores no llevan dorsal ni posición
    this.addForm.controls.type.valueChanges.pipe(takeUntilDestroyed()).subscribe((type) => {
      if (type === 'COACH') {
        this.addForm.patchValue({ shirtNumber: null, position: '' });
      }
    });
  }

  protected load(id = this.teamId()): void {
    this.api.get(id).subscribe({
      next: (team) => {
        this.team.set(team);
        this.status.set('ready');
        if (this.focusPending) {
          this.focusPending = false;
          // Cuando ya está pintada la plantilla, para que el título pueda quedar arriba del todo
          afterNextRender(
            () => {
              const heading = this.heading().nativeElement;
              heading.focus({ preventScroll: true });
              heading.scrollIntoView?.({ block: 'start' });
            },
            { injector: this.injector },
          );
        }
      },
      error: () => this.status.set('error'),
    });
  }

  protected openAdd(): void {
    this.addForm.reset({ type: 'PLAYER', shirtNumber: null, position: '' });
    this.searchControl.setValue('');
    this.candidates.set([]);
    this.chosen.set(null);
    this.addError.set(null);
    this.addOpen.set(true);
  }

  protected closeAdd(): void {
    this.addOpen.set(false);
    this.chosen.set(null);
  }

  protected choose(person: Person): void {
    this.chosen.set(person);
    this.candidates.set([]);
  }

  protected add(): void {
    const person = this.chosen();
    if (person === null) {
      this.addError.set('Elige primero a una persona.');
      return;
    }
    if (this.addForm.invalid) {
      this.addForm.markAllAsTouched();
      return;
    }
    const { type, shirtNumber, position } = this.addForm.getRawValue();
    this.adding.set(true);
    this.addError.set(null);
    this.api
      .addMember(this.teamId(), {
        personId: person.id,
        type,
        shirtNumber: type === 'PLAYER' ? shirtNumber : null,
        position: type === 'PLAYER' && position ? position : null,
      })
      .subscribe({
        next: (member) => {
          this.adding.set(false);
          this.closeAdd();
          this.notice.set(`Se ha añadido a ${member.person.displayName} a la plantilla.`);
          this.load();
        },
        error: (error: unknown) => {
          this.adding.set(false);
          this.addError.set(applyProblem(this.addForm, error));
        },
      });
  }

  protected startEdit(member: Membership): void {
    this.confirmingId.set(null);
    this.editError.set(null);
    this.editForm.reset({ shirtNumber: member.shirtNumber, position: member.position ?? '' });
    this.editingId.set(member.id);
  }

  protected saveEdit(member: Membership): void {
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }
    const { shirtNumber, position } = this.editForm.getRawValue();
    this.api
      .changeMember(this.teamId(), member.id, { shirtNumber, position: position || null })
      .subscribe({
        next: () => {
          this.editingId.set(null);
          this.notice.set(`Se ha guardado a ${member.person.displayName}.`);
          this.load();
        },
        error: (error: unknown) => this.editError.set(applyProblem(this.editForm, error)),
      });
  }

  protected askRemove(member: Membership): void {
    this.editingId.set(null);
    this.listError.set(null);
    this.confirmingId.set(member.id);
  }

  protected confirmRemove(member: Membership): void {
    this.api.removeMember(this.teamId(), member.id).subscribe({
      next: () => {
        this.confirmingId.set(null);
        this.notice.set(`Se ha quitado a ${member.person.displayName} de la plantilla.`);
        this.load();
      },
      error: (error: unknown) => {
        this.confirmingId.set(null);
        this.listError.set(
          error instanceof HttpErrorResponse && error.error?.detail
            ? error.error.detail
            : `No se ha podido quitar a ${member.person.displayName}.`,
        );
      },
    });
  }

  protected addErrorOf(name: string): string | null {
    return errorMessage(this.addForm.get(name));
  }

  protected editErrorOf(name: string): string | null {
    return errorMessage(this.editForm.get(name));
  }

  protected positionLabel(position: string | null): string {
    return position ? position.charAt(0).toUpperCase() + position.slice(1) : '—';
  }

  protected birthYear(person: Person): string {
    return person.birthDate ? ` (${person.birthDate.slice(0, 4)})` : '';
  }
}
