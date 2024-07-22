package hu.davidder.translations.core.base;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class EntityBase {

	@Id
	@GeneratedValue
	@JsonIgnore
	protected Long id;

	@JsonIgnore
	protected boolean deleted = false;

	/**
	 * Enabled/Disabled
	 */
	@JsonIgnore
	protected boolean status = false;

	@Column(name="CREATE_DATE", nullable = false)
	@JsonIgnore
	protected ZonedDateTime createDate = ZonedDateTime.now();
	@Column(name="MODIFY_DATE", nullable = true)
	@JsonIgnore
	protected ZonedDateTime modifyDate;
	@Column(name="DELETE_DATE", nullable = true)
	@JsonIgnore
	protected ZonedDateTime deleteDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public ZonedDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(ZonedDateTime createDate) {
		this.createDate = createDate;
	}

	public ZonedDateTime getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(ZonedDateTime modifyDate) {
		this.modifyDate = modifyDate;
	}

	public ZonedDateTime getDeleteDate() {
		return deleteDate;
	}

	public void setDeleteDate(ZonedDateTime deleteDate) {
		this.deleteDate = deleteDate;
	}

}
