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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Comment implements Serializable{
	private static final long serialVersionUID = -7901024765095281401L;
	
	private String id;
	private String comment_text;
	
	private String user_profile_url;
	private String user_profile_avatar_url;
	private String user_username;
	private String user_id;
	
	private Date modification_time;
	private int likecount;
	private int spamcount;
	
	private boolean deleted;
	
	private Comment parent;
	
	private List<Comment> answers = new ArrayList<Comment>();
	
	private Object tag;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getComment_text() {
		return comment_text;
	}

	public void setComment_text(String comment_text) {
		this.comment_text = comment_text;
	}

	public String getUser_profile_url() {
		return user_profile_url;
	}

	public void setUser_profile_url(String user_profile_url) {
		this.user_profile_url = user_profile_url;
	}

	public String getUser_profile_avatar_url() {
		return user_profile_avatar_url;
	}

	public void setUser_profile_avatar_url(String user_profile_avatar_url) {
		this.user_profile_avatar_url = user_profile_avatar_url;
	}

	public String getUser_username() {
		return user_username;
	}

	public void setUser_username(String user_username) {
		this.user_username = user_username;
	}

	public Date getModification_time() {
		return modification_time;
	}

	public void setModification_time(Date modification_time) {
		this.modification_time = modification_time;
	}

	public int getLikecount() {
		return likecount;
	}

	public void setLikecount(int likecount) {
		this.likecount = likecount;
	}

	public int getSpamcount() {
		return spamcount;
	}

	public void setSpamcount(int spamcount) {
		this.spamcount = spamcount;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public Object getTag() {
		return tag;
	}

	public void setTag(Object tag) {
		this.tag = tag;
	}

	public List<Comment> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Comment> answers) {
		this.answers = answers;
	}

	public Comment getParent() {
		return parent;
	}

	public void setParent(Comment parent) {
		this.parent = parent;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Comment) {
			Comment other = (Comment)obj;
		
			return this.getId().equals(other.getId());
		}
		
		return false;
	}
}
