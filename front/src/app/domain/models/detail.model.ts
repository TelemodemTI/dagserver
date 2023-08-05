class Node {
    index!: number
	operations!: String[]
}
export class Detail {
    dagname!: String
	cronExpr!: String
	group!: String
	onStart!: String
	onEnd!: String
	node!: Node[]
}