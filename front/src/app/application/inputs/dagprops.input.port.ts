import { AvailableJobs } from "src/app/domain/models/availableJobs.model";

export abstract class DagPropsInputPort {
    public abstract getAvailableJobs():Promise<AvailableJobs[]>;
}  