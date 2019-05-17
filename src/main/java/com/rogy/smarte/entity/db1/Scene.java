package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the scene database table.
 *
 */
@Entity
//@NamedQueries({
//	@NamedQuery(name="Scene.findAll", query="SELECT s FROM Scene s"),
//	@NamedQuery(name="Scene.findByUserIDAndName", query="SELECT s FROM Scene s WHERE s.user.id = :userID AND s.name = :name"),
//	@NamedQuery(name="Scene.findByUserID", query="SELECT s FROM Scene s WHERE s.user.id = :userID"),
//	@NamedQuery(name="Scene.findBySceneID", query="SELECT s FROM Scene s WHERE s.sceneID = :sceneID")
//})
public class Scene implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer sceneID;

	private int iconType;

	//bi-directional many-to-one association to Subregion
	@ManyToOne
	@JoinColumn(name="userID")
	private User user;

	private String name;

	public Scene() {
	}

	public Integer getSceneID() {
		return sceneID;
	}

	public void setSceneID(Integer sceneID) {
		this.sceneID = sceneID;
	}


	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIconType() {
		return iconType;
	}

	public void setIconType(int iconType) {
		this.iconType = iconType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
