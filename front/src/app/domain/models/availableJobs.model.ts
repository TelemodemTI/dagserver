export class AvailableJobs {
    jarname!: String
	classname!: String
	groupname!: String
	dagname!: String
	cronExpr!: String
	triggerEvent!: String
	targetDagname!: String
	nextFireAt?: number;
	hasScheduled?: boolean;
}