/*
 * Copyright 2017 RTG Portale GmbH (Marcel Trotzek).
 * - Original Copyright 2013 WhiteByte (Nick Russler, Ahmet Yueksektepe).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.rtg.component.commentbox;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.ProjectStage;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.primefaces.component.outputpanel.OutputPanel;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;

import de.rtg.component.reversedtree.ReversedTreeNode;

/**
 * Component class for the <code>Commentbox</code> component.
 *
 * @author Nick Russler / last modified by $Author$
 * @version $Revision$
 * @since 0.0.1
 */
@ResourceDependencies({
		@ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
		@ResourceDependency(library = "primefaces", name = "core.js"),
		@ResourceDependency(library = "primefaces", name = "components.js"),
		@ResourceDependency(library = "rtg", name = "commentbox/commentbox.css"),
		@ResourceDependency(library = "rtg", name = "commentbox/commentbox.js"),
		@ResourceDependency(library = "rtg", name = "commentbox/emoticon.js")
})
@FacesComponent(value = CommentBox.COMPONENT_TYPE)
public class CommentBox extends UINamingContainer {
	public static final String RESOURCE_BUNDLE = "de.rtg.component.commentbox.messages";
	public static final String COMPONENT_TYPE = "de.rtg.component.commentbox";

	enum PropertyKeys {
		contextID, comments, currentPage, commentsPerPage, commentCount, currentUserID, currentUserUsername, currentUserAvatarUrl, canEditAll, canDeleteAll, enableLiveFeatures
	}

	String new_comment_editor_text;
	String edit_comment_editor_text;
	String answer_comment_editor_text;

	private TreeNode reversedTree;
	private OutputPanel innerWrapper;

	// Getter and Setter Start

	@SuppressWarnings("unchecked")
	private <T> T getAttribute(PropertyKeys propertyKey) {

		T result = (T) getStateHelper().eval(propertyKey);

		if (result == null) {
			result = (T) this.getAttributes().get(propertyKey + "");
		}

		return result;
	}

	// private <T> void setAttribute(PropertyKeys propertyKey, T value) {
	// getStateHelper().put(propertyKey, value);
	// }

	private List<Comment> getComments() {

		return getAttribute(PropertyKeys.comments);
	}

	private Long getCommentsPerPage() {

		return getAttribute(PropertyKeys.commentsPerPage);
	}

	private Long getCommentCount() {

		return getAttribute(PropertyKeys.commentCount);
	}

	private String getCurrentUserID() {

		return getAttribute(PropertyKeys.currentUserID);
	}

	private String getCurrentUserUsername() {

		return getAttribute(PropertyKeys.currentUserUsername);
	}

	private String getCurrentUserAvatarUrl() {

		return getAttribute(PropertyKeys.currentUserAvatarUrl);
	}

	private String getContextID() {

		return getAttribute(PropertyKeys.contextID);
	}

	private Boolean isEnableLiveFeatures() {

		return Boolean.parseBoolean(getAttribute(PropertyKeys.enableLiveFeatures) + "");
	}

	// Getter and Setter End

	// Utils Start

	public ResourceBundle getCustomMessageBundle() {

		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		return ResourceBundle.getBundle(RESOURCE_BUNDLE, locale);
	}

	public static void log(String msg) {

		FacesContext ctx = FacesContext.getCurrentInstance();
		if (ctx.isProjectStage(ProjectStage.Development)) {
			Logger.getLogger("CommentBox").info(msg);
		}
	}

	public void push(String msg) {

		if (!isEnableLiveFeatures()) {
			return;
		}

		log("push: " + msg);

		PushContext pushContext = PushContextFactory.getDefault().getPushContext();
		pushContext.push("/commentboxpush-" + this.getClientId() + "-" + this.getContextID(), msg);
	}

	public static String cid(UIComponent component) {

		FacesContext context = FacesContext.getCurrentInstance();
		return component.getClientId(context);
	}

	private Object executeMethodExpression(MethodExpression me, Object[] params) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		ELContext elContext = facesContext.getELContext();

		return me.invoke(elContext, params);
	}

	private static String mapToJSON(ResourceBundle bundle) {

		String json = "{";
		String iterator = "";

		for (String k : bundle.keySet()) {
			json += iterator + "\"" + k + "\"" + ":" + "\"" + bundle.getString(k).replace("'", "&#39;") + "\"";
			iterator = ",";
		}

		json += "}";

		return json;
	}

	// Utils End

	private void _getTreeAsList(List<TreeNode> children, List<TreeNode> nodesAsList) {

		for (TreeNode node : children) {
			nodesAsList.add(node);
			_getTreeAsList(node.getChildren(), nodesAsList);
		}
	}

	private List<TreeNode> getTreeAsList() {

		List<TreeNode> result = new ArrayList<TreeNode>();
		_getTreeAsList(reversedTree.getChildren(), result);
		return result;
	}

	@SuppressWarnings("unchecked")
	private TreeNode findByComment(Comment comment) {

		List<TreeNode> treeAsList = getTreeAsList();

		for (TreeNode treeNode : treeAsList) {
			Pair<Boolean, Comment> wrapper = (Pair<Boolean, Comment>) treeNode.getData();

			if (wrapper.getRight().equals(comment)) {
				return treeNode;
			}
		}

		return null;
	}

	private void _getCommentsAsList(List<Comment> comments, List<Comment> commentsAsList) {

		for (Comment comment : comments) {
			commentsAsList.add(comment);
			_getCommentsAsList(comment.getAnswers(), commentsAsList);
		}
	}

	private List<Comment> getCommentsAsList() {

		List<Comment> result = new ArrayList<Comment>();
		_getCommentsAsList(getComments(), result);
		return result;
	}

	private Comment findCommentByID(String id) {

		List<Comment> commentsAsList = getCommentsAsList();

		for (Comment comment : commentsAsList) {
			if (comment.getId().equals(id)) {
				return comment;
			}
		}

		return null;
	}

	public void fetchNewComments(MethodExpression onFetchNewComments) {

		log("fetchNewComments");

		if (onFetchNewComments != null) {
			executeMethodExpression(onFetchNewComments, new Object[] {});
		}

		this.reversedTree = null;
		initTree();
	}

	public void fetchNewAnswers(MethodExpression onFetchNewAnswers) {

		log("fetchNewAnswers");

		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String answerID = params.get("currentAnswerComment");

		if (onFetchNewAnswers != null) {
			executeMethodExpression(onFetchNewAnswers, new Object[] {
					this.findCommentByID(answerID)
			});
		}

		this.reversedTree = null;
		initTree();
	}

	private Comment _createComment(String text) {

		Comment comment = new Comment();
		comment.setComment_text(text);
		comment.setUser_id(getCurrentUserID());
		comment.setModification_time(new Date());
		comment.setUser_username(getCurrentUserUsername());
		comment.setUser_profile_avatar_url(getCurrentUserAvatarUrl());

		return comment;
	}

	public void createComment(MethodExpression onCreateComment) {

		log("createComment");

		boolean insertComment = false;

		List<Comment> comments = getComments();
		Comment comment = _createComment(new_comment_editor_text);

		if (onCreateComment != null) {
			insertComment = (Boolean) executeMethodExpression(onCreateComment, new Object[] {
					comment
			});
		}

		if (insertComment) {
			comments.add(comment);
			addNode(comment, this.reversedTree, 0);
			new_comment_editor_text = null;

			push("{\"a\": \"NC\", \"c\":\"" + comment.getId() + "\"}");
		}
	}

	public void addNode(Comment comment, TreeNode parent, int index) {

		TreeNode node0 = new ReversedTreeNode(new Pair<Boolean, Comment>(false, comment), parent, index);
		node0.setExpanded(true);

		// Add dummy child
		new DefaultTreeNode(new Pair<Boolean, Comment>(true, comment), node0);
	}

	public void deleteComment(MethodExpression onCommentDelete, Comment comment) {

		log("deleteComment: " + comment);

		comment.setComment_text(
				"<i>" + this.getCustomMessageBundle().getString("commentbox.comments.comment.deletedmsg") + "</i>");
		comment.setDeleted(true);

		// Alternative from old modified version (remove setComment_text() line above):
		// TreeNode node = findByComment(comment);
		// if (node != null) {
		// node.getParent().getChildren().remove(node);
		// }

		if (onCommentDelete != null) {
			executeMethodExpression(onCommentDelete, new Object[] {
					comment
			});
		}
	}

	public void editComment(MethodExpression onEditComment) {

		log("editComment");

		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String editID = params.get("currentEditComment");

		Comment comment = findCommentByID(editID);

		if (comment == null) {
			return;
		}

		comment.setComment_text(this.edit_comment_editor_text);

		if (onEditComment != null) {
			executeMethodExpression(onEditComment, new Object[] {
					comment
			});
		}
	}

	public void answerComment(MethodExpression onCreateAnswer) {

		log("answerComment");

		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String answerID = params.get("currentAnswerComment");

		boolean insertComment = false;

		Comment parent = findCommentByID(answerID);

		Comment comment = _createComment(answer_comment_editor_text);
		comment.setParent(parent);

		if (onCreateAnswer != null) {
			insertComment = (Boolean) executeMethodExpression(onCreateAnswer, new Object[] {
					comment
			});
		}

		if (insertComment) {
			comment.getParent().getAnswers().add(comment);
			addNode(comment, findByComment(comment.getParent()), 1);

			push("{\"a\": \"AC\", \"c\":\"" + comment.getId() + "\", \"p\":\"" + comment.getParent().getId()
					+ "\", \"u\":\"" + getCurrentUserID() + "\"}");
		}
	}

	public void likeComment(MethodExpression onLikeComment, Comment comment) {

		log("likeComment");

		comment.setLikecount(comment.getLikecount() + 1);

		if (onLikeComment != null) {
			executeMethodExpression(onLikeComment, new Object[] {
					comment
			});
		}
	}

	public void spamComment(MethodExpression onSpamComment, Comment comment) {

		log("spamComment");

		comment.setSpamcount(comment.getSpamcount() + 1);

		if (onSpamComment != null) {
			executeMethodExpression(onSpamComment, new Object[] {
					comment
			});
		}
	}

	public void pageChange(MethodExpression onPageChange, Long page) {

		FacesContext ctx = FacesContext.getCurrentInstance();

		ValueExpression valueExpression = this.getValueExpression(PropertyKeys.currentPage.toString());

		if (valueExpression != null) {
			valueExpression.setValue(ctx.getELContext(), page);
		}

		if (onPageChange != null) {
			executeMethodExpression(onPageChange, new Object[] {
					page
			});
		}

		this.reversedTree = null;
		initTree();
	}

	public void onUserType() {

		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String answerID = params.get("currentAnswerComment");

		push("{\"a\": \"T\", \"c\":\"" + answerID + "\", \"u\":\"" + this.getCurrentUserID() + "\"}");
	}

	private void _generateTree(TreeNode tn, Comment comment) {

		TreeNode node0 = new DefaultTreeNode(new Pair<Boolean, Comment>(false, comment), tn);
		node0.setExpanded(true);

		// Add dummy child
		new DefaultTreeNode(new Pair<Boolean, Comment>(true, comment), node0);

		List<Comment> comments = comment.getAnswers();

		for (int i = comments.size() - 1; i >= 0; i--) {
			_generateTree(node0, comments.get(i));
		}
	}

	private void initTree() {

		if (reversedTree == null) {
			reversedTree = new DefaultTreeNode("Root", null);

			List<Comment> comments = getComments();

			for (int i = comments.size() - 1; i >= 0; i--) {
				_generateTree(reversedTree, comments.get(i));
			}
		}
	}

	public String getNew_comment_editor_text() {

		// initialize here because getResourceBundleMap return null on construction time
		if (this.new_comment_editor_text == null) {
			this.new_comment_editor_text = "<span style=\"font-family: Arial, Verdana; font-size: 13px;\"><font color=\"#666666\">"
					+ this.getCustomMessageBundle().getString("commentbox.editor.notclicked") + "</font></span>";
		}

		return new_comment_editor_text;
	}

	public void setNew_comment_editor_text(String new_comment_editor_text) {

		this.new_comment_editor_text = new_comment_editor_text;
	}

	public String getEdit_comment_editor_text() {

		return edit_comment_editor_text;
	}

	public void setEdit_comment_editor_text(String edit_comment_editor_text) {

		this.edit_comment_editor_text = edit_comment_editor_text;
	}

	public TreeNode getTree() {

		initTree();

		return this.reversedTree;
	}

	public void setTree(TreeNode tree) {

		this.reversedTree = tree;
	}

	public String getAnswer_comment_editor_text() {

		return answer_comment_editor_text;
	}

	public void setAnswer_comment_editor_text(String answer_comment_editor_text) {

		this.answer_comment_editor_text = answer_comment_editor_text;
	}

	public OutputPanel getInnerWrapper() {

		return innerWrapper;
	}

	public void setInnerWrapper(OutputPanel innerWrapper) {

		this.innerWrapper = innerWrapper;
	}

	public Long getLastPage() {

		return (long) Math.ceil((double) this.getCommentCount() / (double) this.getCommentsPerPage());
	}

	public String getMessageBundleAsJSON() {

		return mapToJSON(this.getCustomMessageBundle());
	}
}
