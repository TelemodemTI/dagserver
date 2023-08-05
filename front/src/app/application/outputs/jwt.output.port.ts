import { DagToken } from "src/app/domain/models/dagtoken.model";

export abstract class JWTOutputPort {
  public abstract getDecodedAccessToken(): DagToken;
  public abstract removeAccessToken(): void;
}