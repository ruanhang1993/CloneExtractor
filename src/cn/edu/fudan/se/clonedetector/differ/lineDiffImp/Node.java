package cn.edu.fudan.se.clonedetector.differ.lineDiffImp;

class Node {
	private Node pleft, pright;
	private int linenum;

	static final int freshNode = 0, oldonce = 1, newonce = 2, bothonce = 3, other = 4;

	private int linestate;
	private String line;

	static Node panchor = null;

	Node(String pline) {
		pleft = pright = null;
		linestate = freshNode;
		line = pline;
	}

	public int getLinenum() {
		return linenum;
	}

	static Node matchsymbol(String pline) {
		int comparison;
		Node pNode = panchor;
		if (panchor == null)
			return panchor = new Node(pline);
		for (;;) {
			comparison = pNode.line.compareTo(pline);
			if (comparison == 0)
				return pNode; /* found */

			if (comparison < 0) {
				if (pNode.pleft == null) {
					pNode.pleft = new Node(pline);
					return pNode.pleft;
				}
				pNode = pNode.pleft;
			}
			if (comparison > 0) {
				if (pNode.pright == null) {
					pNode.pright = new Node(pline);
					return pNode.pright;
				}
				pNode = pNode.pright;
			}
		}
	}

	static Node addSymbol(String pline, boolean inoldfile, int linenum) {
		Node pNode;
		pNode = matchsymbol(pline); /* find the Node in the tree */
		if (pNode.linestate == freshNode) {
			pNode.linestate = inoldfile ? oldonce : newonce;
		} else {
			if ((pNode.linestate == oldonce && !inoldfile) || (pNode.linestate == newonce && inoldfile))
				pNode.linestate = bothonce;
			else
				pNode.linestate = other;
		}
		if (inoldfile)
			pNode.linenum = linenum;
		return pNode;
	}

	boolean symbolIsUnique() {
		return (linestate == bothonce);
	}

	String getSymbol() {
		return line;
	}
}
