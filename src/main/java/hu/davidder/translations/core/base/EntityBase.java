package hu.davidder.translations.core.base;

import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * The EntityBase class serves as a base class for entities with common attributes
 * such as id, deleted status, and various timestamps.
 */
@MappedSuperclass
public class EntityBase {

    @Id
    @GeneratedValue
    @JsonIgnore
    protected Long id;

    @JsonIgnore
    protected boolean deleted = false;

    /**
     * Enabled/Disabled status of the entity.
     */
    @JsonIgnore
    protected boolean status = true;

    @Column(name = "CREATE_DATE", nullable = false)
    @JsonIgnore
    protected ZonedDateTime createDate = ZonedDateTime.now();

    @Column(name = "MODIFY_DATE", nullable = true)
    @JsonIgnore
    protected ZonedDateTime modifyDate;

    @Column(name = "DELETE_DATE", nullable = true)
    @JsonIgnore
    protected ZonedDateTime deleteDate;

    @Column(name = "STATUS_MODIFY_DATE", nullable = true)
    @JsonIgnore
    protected ZonedDateTime statusModifyDate;

    /**
     * Gets the id of the entity.
     * 
     * @return The id of the entity.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id of the entity.
     * 
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Checks if the entity is deleted.
     * 
     * @return true if the entity is deleted, false otherwise.
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Sets the deleted status of the entity.
     * 
     * @param deleted The deleted status to set.
     */
    public void setDeleted(boolean deleted) {
        this.deleteDate = deleted ? ZonedDateTime.now() : null;
        this.modifyDate = ZonedDateTime.now();
        this.deleted = deleted;
    }

    /**
     * Checks if the entity is enabled or disabled.
     * 
     * @return true if the entity is enabled, false otherwise.
     */
    public boolean isStatus() {
        return status;
    }

    /**
     * Sets the enabled/disabled status of the entity.
     * 
     * @param status The status to set.
     */
    public void setStatus(boolean status) {
        this.statusModifyDate = status ? ZonedDateTime.now() : null;
        this.modifyDate = ZonedDateTime.now();
        this.status = status;
    }

    /**
     * Gets the creation date of the entity.
     * 
     * @return The creation date of the entity.
     */
    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    /**
     * Sets the creation date of the entity.
     * 
     * @param createDate The creation date to set.
     */
    public void setCreateDate(ZonedDateTime createDate) {
        this.createDate = createDate;
    }

    /**
     * Gets the modification date of the entity.
     * 
     * @return The modification date of the entity.
     */
    public ZonedDateTime getModifyDate() {
        return modifyDate;
    }

    /**
     * Sets the modification date of the entity.
     * 
     * @param modifyDate The modification date to set.
     */
    public void setModifyDate(ZonedDateTime modifyDate) {
        this.modifyDate = modifyDate;
    }

    /**
     * Gets the deletion date of the entity.
     * 
     * @return The deletion date of the entity.
     */
    public ZonedDateTime getDeleteDate() {
        return deleteDate;
    }

    /**
     * Sets the deletion date of the entity.
     * 
     * @param deleteDate The deletion date to set.
     */
    public void setDeleteDate(ZonedDateTime deleteDate) {
        this.deleteDate = deleteDate;
    }
}
