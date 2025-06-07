// src/app/models/register-request.model.ts

export interface RegisterRequest {
  forename: string;
  surname: string;
  email: string;
  password: string;
  confirmPassword: string;
}
