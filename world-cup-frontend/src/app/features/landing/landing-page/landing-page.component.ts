import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  HostListener,
  OnDestroy,
  Renderer2,
  ViewChild
} from '@angular/core';

interface Countdown {
  days: string;
  hours: string;
  minutes: string;
  seconds: string;
}

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.scss'],
  standalone: false
})
export class LandingPageComponent implements AfterViewInit, OnDestroy {
  @ViewChild('heroVideo') heroVideo?: ElementRef<HTMLVideoElement>;
  @ViewChild('experienceVideo') experienceVideo?: ElementRef<HTMLVideoElement>;

  loading = true;
  loaderProgress = 0;
  menuOpen = false;
  scrollProgress = 0;
  heroOffset = 0;
  videoPlaying = true;
  countdown: Countdown = { days: '000', hours: '00', minutes: '00', seconds: '00' };

  private readonly tournamentDate = new Date('2026-06-11T00:00:00-05:00').getTime();
  private countdownTimer?: ReturnType<typeof setInterval>;
  private loaderTimer?: ReturnType<typeof setInterval>;
  private revealObserver?: IntersectionObserver;

  constructor(
    private readonly elementRef: ElementRef<HTMLElement>,
    private readonly renderer: Renderer2,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {}

  ngAfterViewInit(): void {
    this.startLoader();
    this.startHeroVideo();
    this.startExperienceVideo();
    this.updateCountdown();
    this.countdownTimer = setInterval(() => this.updateCountdown(), 1000);

    if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
      this.elementRef.nativeElement
        .querySelectorAll<HTMLElement>('[data-reveal]')
        .forEach((element) => element.classList.add('is-visible'));
      return;
    }

    this.revealObserver = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('is-visible');
            this.revealObserver?.unobserve(entry.target);
          }
        });
      },
      { threshold: 0.16, rootMargin: '0px 0px -40px' }
    );

    this.elementRef.nativeElement
      .querySelectorAll('[data-reveal]')
      .forEach((element) => this.revealObserver?.observe(element));
  }

  ngOnDestroy(): void {
    clearInterval(this.countdownTimer);
    clearInterval(this.loaderTimer);
    this.revealObserver?.disconnect();
    this.renderer.removeClass(document.body, 'landing-menu-open');
  }

  @HostListener('window:scroll')
  onScroll(): void {
    const maxScroll = document.documentElement.scrollHeight - window.innerHeight;
    this.scrollProgress = maxScroll > 0 ? (window.scrollY / maxScroll) * 100 : 0;
    this.heroOffset = Math.min(window.scrollY * 0.18, 120);
  }

  toggleMenu(force?: boolean): void {
    this.menuOpen = force ?? !this.menuOpen;
    if (this.menuOpen) {
      this.renderer.addClass(document.body, 'landing-menu-open');
    } else {
      this.renderer.removeClass(document.body, 'landing-menu-open');
    }
  }

  scrollTo(sectionId: string): void {
    this.toggleMenu(false);
    document.getElementById(sectionId)?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  toggleVideo(): void {
    const video = this.heroVideo?.nativeElement;
    if (!video) {
      return;
    }

    if (video.paused) {
      void video.play();
    } else {
      video.pause();
    }
  }

  private startLoader(): void {
    const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (reducedMotion) {
      this.loaderProgress = 100;
      this.loading = false;
      this.changeDetectorRef.markForCheck();
      return;
    }

    this.loaderTimer = setInterval(() => {
      const remaining = 100 - this.loaderProgress;
      this.loaderProgress += Math.max(1, Math.ceil(remaining * 0.14));

      if (this.loaderProgress >= 100) {
        this.loaderProgress = 100;
        clearInterval(this.loaderTimer);
        setTimeout(() => {
          this.loading = false;
          this.changeDetectorRef.markForCheck();
        }, 280);
      }

      this.changeDetectorRef.markForCheck();
    }, 42);
  }

  private startHeroVideo(): void {
    const video = this.heroVideo?.nativeElement;
    if (!video) {
      return;
    }

    video.muted = true;
    void video.play().catch(() => {
      this.videoPlaying = false;
      this.changeDetectorRef.markForCheck();
    });
  }

  private startExperienceVideo(): void {
    const video = this.experienceVideo?.nativeElement;
    if (!video) {
      return;
    }

    video.muted = true;
    void video.play().catch(() => undefined);
  }

  private updateCountdown(): void {
    const distance = Math.max(this.tournamentDate - Date.now(), 0);
    const days = Math.floor(distance / 86_400_000);
    const hours = Math.floor((distance / 3_600_000) % 24);
    const minutes = Math.floor((distance / 60_000) % 60);
    const seconds = Math.floor((distance / 1000) % 60);

    this.countdown = {
      days: String(days).padStart(3, '0'),
      hours: String(hours).padStart(2, '0'),
      minutes: String(minutes).padStart(2, '0'),
      seconds: String(seconds).padStart(2, '0')
    };
    this.changeDetectorRef.markForCheck();
  }
}
