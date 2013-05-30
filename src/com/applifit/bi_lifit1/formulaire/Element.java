package com.applifit.bi_lifit1.formulaire;

/**
 * la classe Element
 * @author Faissal
 *
 */
public class Element {
	
	int id;
	String type;
	int position_x;
	int position_y;
	int Idform;
	
	
	public Element() {
		super();
	}


	public Element(int id, String type, int position_x, int position_y, int IdForm) {
		super();
		this.id = id;
		this.type = type;
		this.position_x = position_x;
		this.position_y = position_y;
		this.Idform = IdForm;
	}


	public Element(String type, int position_x, int position_y, int IdForm) {
		super();
		this.type = type;
		this.position_x = position_x;
		this.position_y = position_y;
		this.Idform = IdForm;
	}

	public int getIdform() {
		return Idform;
	}


	public void setIdform(int idform) {
		Idform = idform;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public int getPosition_x() {
		return position_x;
	}


	public void setPosition_x(int position_x) {
		this.position_x = position_x;
	}


	public int getPosition_y() {
		return position_y;
	}


	public void setPosition_y(int position_y) {
		this.position_y = position_y;
	}


	public int getId() {
		return id;
	}


}
