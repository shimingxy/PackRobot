package com.packrobot.svn.db;

public class SvnNodes {
	
	long rowId;
	String localRelpath;
	String reposPath;
	long revision;
	String kind;
	String changedAuthor;
	long changedRevision;
	String changedDate;
	String changedType;
	
	public SvnNodes() {
		
	}

	public long getRowId() {
		return rowId;
	}

	public void setRowId(long rowId) {
		this.rowId = rowId;
	}

	public String getLocalRelpath() {
		return localRelpath;
	}

	public void setLocalRelpath(String localRelpath) {
		this.localRelpath = localRelpath;
	}

	public String getReposPath() {
		return reposPath;
	}

	public void setReposPath(String reposPath) {
		this.reposPath = reposPath;
	}

	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getChangedAuthor() {
		return changedAuthor;
	}

	public void setChangedAuthor(String changedAuthor) {
		this.changedAuthor = changedAuthor;
	}

	public long getChangedRevision() {
		return changedRevision;
	}

	public void setChangedRevision(long changedRevision) {
		this.changedRevision = changedRevision;
	}

	public String getChangedDate() {
		return changedDate;
	}

	public void setChangedDate(String changedDate) {
		this.changedDate = changedDate;
	}

	public String getChangedType() {
		return changedType;
	}

	public void setChangedType(String changedType) {
		this.changedType = changedType;
	}

	@Override
	public String toString() {
		return "SvnNodes [rowId=" + rowId + ", localRelpath=" + localRelpath + ", reposPath=" + reposPath
				+ ", revision=" + revision + ", kind=" + kind + ", changedAuthor=" + changedAuthor
				+ ", changedRevision=" + changedRevision + ", changedDate=" + changedDate + ", changedType="
				+ changedType + "]";
	}

	
	
}
