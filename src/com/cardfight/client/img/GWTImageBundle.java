package com.cardfight.client.img;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import java.util.HashMap;

public class GWTImageBundle {
	
	private static HashMap<String, AbstractImagePrototype> images = new HashMap<String, AbstractImagePrototype>();
	private static GWTImages myImages = GWT.create(GWTImages.class);
	
	static {
		images.put("2c50", myImages.RS2c50());
		images.put("2d50", myImages.RS2d50());
		images.put("2h50", myImages.RS2h50());
		images.put("2s50", myImages.RS2s50());
		images.put("3c50", myImages.RS3c50());
		images.put("3d50", myImages.RS3d50());
		images.put("3h50", myImages.RS3h50());
		images.put("3s50", myImages.RS3s50());
		images.put("4c50", myImages.RS4c50());
		images.put("4d50", myImages.RS4d50());
		images.put("4h50", myImages.RS4h50());
		images.put("4s50", myImages.RS4s50());
		images.put("5c50", myImages.RS5c50());
		images.put("5d50", myImages.RS5d50());
		images.put("5h50", myImages.RS5h50());
		images.put("5s50", myImages.RS5s50());
		images.put("6c50", myImages.RS6c50());
		images.put("6d50", myImages.RS6d50());
		images.put("6h50", myImages.RS6h50());
		images.put("6s50", myImages.RS6s50());
		images.put("7c50", myImages.RS7c50());
		images.put("7d50", myImages.RS7d50());
		images.put("7h50", myImages.RS7h50());
		images.put("7s50", myImages.RS7s50());
		images.put("8c50", myImages.RS8c50());
		images.put("8d50", myImages.RS8d50());
		images.put("8h50", myImages.RS8h50());
		images.put("8s50", myImages.RS8s50());
		images.put("9c50", myImages.RS9c50());
		images.put("9d50", myImages.RS9d50());
		images.put("9h50", myImages.RS9h50());
		images.put("9s50", myImages.RS9s50());
		images.put("Ac50", myImages.RSAc50());
		images.put("Ad50", myImages.RSAd50());
		images.put("Ah50", myImages.RSAh50());
		images.put("As50", myImages.RSAs50());
		images.put("Jc50", myImages.RSJc50());
		images.put("Jd50", myImages.RSJd50());
		images.put("Jh50", myImages.RSJh50());
		images.put("Js50", myImages.RSJs50());
		images.put("Kc50", myImages.RSKc50());
		images.put("Kd50", myImages.RSKd50());
		images.put("Kh50", myImages.RSKh50());
		images.put("Ks50", myImages.RSKs50());
		images.put("Qc50", myImages.RSQc50());
		images.put("Qd50", myImages.RSQd50());
		images.put("Qh50", myImages.RSQh50());
		images.put("Qs50", myImages.RSQs50());
		images.put("Tc50", myImages.RSTc50());
		images.put("Td50", myImages.RSTd50());
		images.put("Th50", myImages.RSTh50());
		images.put("Ts50", myImages.RSTs50());
		images.put("blank", myImages.blank());

	}
	
	public static Image getImage(String name) {	    
	    // Retrieve the image prototypes from myImageBundle.
	    AbstractImagePrototype something = images.get(name);
	    return(something.createImage());
	}
	
	public static AbstractImagePrototype getProto(String name) {	    
	    // Retrieve the image prototypes from myImageBundle.
	    AbstractImagePrototype something = images.get(name);
	    return(something);
	}
	

}
