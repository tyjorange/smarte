package com.rogy.smarte.entity.db1;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the scene_switch database table.
 *
 */
@Entity
@Table(name="scene_switch")
//@NamedQueries({
//	@NamedQuery(name="SceneSwitch.findAll", query="SELECT s FROM SceneSwitch s"),
//	@NamedQuery(name="SceneSwitch.findBySceneIDAndSwitchID", query="SELECT s FROM SceneSwitch s WHERE s.scene.sceneID = :sceneID AND s.switchs.switchID = :switchID"),
//	@NamedQuery(name="SceneSwitch.findBySceneID", query="SELECT s FROM SceneSwitch s WHERE s.scene.sceneID = :sceneID ORDER BY s.switchs.collector.collectorID"),
//	@NamedQuery(name="SceneSwitch.findBySceneSwitchID", query="SELECT s FROM SceneSwitch s WHERE s.id = :sceneSwitchID")
//})
public class SceneSwitch implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private Byte cmdData;

	//bi-directional many-to-one association to Scene
	@ManyToOne
	@JoinColumn(name="switchID")
	private Switch switchs;

	//bi-directional many-to-one association to Scene
	@ManyToOne
	@JoinColumn(name="sceneID")
	private Scene scene;

	public SceneSwitch() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Byte getCmdData() {
		return cmdData;
	}

	public void setCmdData(Byte cmdData) {
		this.cmdData = cmdData;
	}

	public Scene getScene() {
		return this.scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

	public Switch getSwitchs() {
		return switchs;
	}

	public void setSwitchs(Switch switchs) {
		this.switchs = switchs;
	}

}
