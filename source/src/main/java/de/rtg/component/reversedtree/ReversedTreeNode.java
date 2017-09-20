package de.rtg.component.reversedtree;

import java.util.ArrayList;

import org.primefaces.model.TreeNode;

/**
 * Klasse hinzugefügt, damit neue Kommentare mit korrektem Index abgelegt werden können
 *
 * @author Marcel Trotzek
 */
public class ReversedTreeNode extends org.primefaces.model.DefaultTreeNode {

	public ReversedTreeNode(Object data, TreeNode parent, int index) {
		this.setType(DEFAULT_TYPE);
		this.setData(data);
		this.setChildren(new ArrayList<TreeNode>());
		this.setParent(parent);

		if (parent != null) {
			this.getParent().getChildren().remove(this);
			this.getParent().getChildren().add(index, this);
		}
	}
}
