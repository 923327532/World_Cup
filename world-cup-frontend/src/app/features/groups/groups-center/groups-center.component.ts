import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { GroupApiService } from '../../../services/api/group-api.service';
import { RoomResponse } from '../../../models/group.model';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-groups-center',
  templateUrl: './groups-center.component.html',
  styleUrls: ['./groups-center.component.scss'],
  standalone: false,
})
export class GroupsCenterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly groupApiService = inject(GroupApiService);
  private readonly notificationService = inject(NotificationService);

  readonly createForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    description: [''],
    maxMembers: [20, [Validators.min(2), Validators.max(200)]],
  });

  readonly joinForm = this.fb.group({
    inviteCode: ['', [Validators.required, Validators.minLength(3)]],
  });

  groups: RoomResponse[] = [];

  constructor() {
    this.reload();
  }

  reload(): void {
    this.groupApiService.getGroups().subscribe((groups) => {
      this.groups = groups;
    });
  }

  createGroup(): void {
    if (this.createForm.invalid) return;
    const payload = this.createForm.getRawValue();
    this.groupApiService
      .createGroup({
        name: payload.name || '',
        description: payload.description || undefined,
        maxMembers: payload.maxMembers || undefined,
      })
      .subscribe(() => {
        this.notificationService.success('Grupo creado');
        this.createForm.reset({ name: '', description: '', maxMembers: 20 });
        this.reload();
      });
  }

  joinGroup(): void {
    if (this.joinForm.invalid) return;
    const code = (this.joinForm.value.inviteCode || '').trim();
    this.groupApiService.joinGroup(code).subscribe(() => {
      this.notificationService.success('Solicitud de union enviada');
      this.joinForm.reset({ inviteCode: '' });
      this.reload();
    });
  }
}
