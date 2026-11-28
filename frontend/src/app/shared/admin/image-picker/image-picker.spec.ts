import { TestBed } from '@angular/core/testing';
import { ImagePicker } from './image-picker';

describe('ImagePicker', () => {
  async function render(imageUrl: string | null = null) {
    TestBed.configureTestingModule({ imports: [ImagePicker] });
    const fixture = TestBed.createComponent(ImagePicker);
    fixture.componentRef.setInput('label', 'Escudo');
    fixture.componentRef.setInput('imageUrl', imageUrl);
    fixture.componentRef.setInput('alt', 'Escudo de Los Chicharros V.C.');
    const picked: File[] = [];
    fixture.componentInstance.picked.subscribe((file) => picked.push(file));
    await fixture.whenStable();
    return { fixture, page: fixture.nativeElement as HTMLElement, picked };
  }

  function choose(page: HTMLElement, file: File) {
    const input = page.querySelector('input[type=file]') as HTMLInputElement;
    Object.defineProperty(input, 'files', { value: [file], configurable: true });
    input.dispatchEvent(new Event('change'));
  }

  it('muestra la imagen actual y avisa con el fichero elegido', async () => {
    const { page, picked } = await render('/api/images/teams/escudo.png');

    expect(page.querySelector('img')?.getAttribute('src')).toBe('/api/images/teams/escudo.png');
    expect(page.querySelector('img')?.getAttribute('alt')).toBe('Escudo de Los Chicharros V.C.');
    expect(page.textContent).toContain('Cambiar una imagen');

    const file = new File(['png'], 'escudo.png', { type: 'image/png' });
    choose(page, file);
    expect(picked).toEqual([file]);
  });

  it('no avisa con ficheros que no son imágenes o pesan más de 5 MB', async () => {
    const { fixture, page, picked } = await render();
    expect(page.querySelector('img')).toBeNull();

    choose(page, new File(['gif'], 'foto.gif', { type: 'image/gif' }));
    await fixture.whenStable();
    expect(page.querySelector('.field__error')?.textContent).toContain('JPG, PNG o WebP');

    choose(
      page,
      new File([new Uint8Array(5 * 1024 * 1024 + 1)], 'grande.jpg', { type: 'image/jpeg' }),
    );
    await fixture.whenStable();
    expect(page.querySelector('.field__error')?.textContent).toContain('más de 5 MB');
    expect(picked).toHaveLength(0);
  });

  it('acepta una imagen arrastrada y muestra el error de la API', async () => {
    const { fixture, page, picked } = await render();
    const file = new File(['jpg'], 'foto.jpg', { type: 'image/jpeg' });
    const drop = new Event('drop') as DragEvent;
    Object.defineProperty(drop, 'dataTransfer', { value: { files: [file] } });
    page.querySelector('label')?.dispatchEvent(drop);
    expect(picked).toEqual([file]);

    fixture.componentRef.setInput('error', 'Tiene que ser una imagen JPG, PNG o WebP');
    await fixture.whenStable();
    expect(page.querySelector('.field__error')?.textContent).toContain('JPG, PNG o WebP');
  });
});
