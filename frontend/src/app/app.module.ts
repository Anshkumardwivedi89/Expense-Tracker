import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { LoginComponent } from './components/auth/login.component';
import { RegisterComponent } from './components/auth/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { NavbarComponent } from './components/shared/navbar.component';
import { CategoryListComponent } from './components/categories/category-list.component';
import { ProfileComponent } from './components/profile/profile.component';
import { NotificationsComponent } from './components/notifications/notifications.component';
import { WeeklyReportComponent } from './components/reports/weekly-report.component';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { AuthGuard } from './guards/auth.guard';

const routes: Routes = [
  { path: '', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'categories', component: CategoryListComponent, canActivate: [AuthGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  { path: 'notifications', component: NotificationsComponent, canActivate: [AuthGuard] },
  { path: 'reports', component: WeeklyReportComponent, canActivate: [AuthGuard] },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: '**', redirectTo: '' }
];

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    CategoryListComponent,
    ProfileComponent,
    NotificationsComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(routes),
    DashboardComponent,
    WeeklyReportComponent,
    LoginComponent,
    RegisterComponent
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    AuthGuard
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
