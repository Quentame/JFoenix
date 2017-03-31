/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.jfoenix.controls;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.javafx.css.converters.SizeConverter;

import javafx.beans.binding.Bindings;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.text.Text;

/**
 *  JFXProgressIndicator is the material design implementation of a progress indicator.
 *  
 * @author	Quentin POLLET
 * @version 1.0
 * @since   2016-05-10
 */
public class JFXProgressIndicator extends StackPane {

	private static final String DEFAULT_STYLE_CLASS = "jfx-progress-indicator";
	private Color greenColor, redColor, yellowColor, blueColor, initialColor;
	private Arc arc;
	private boolean initialized;
	private StyleableDoubleProperty radius = new SimpleStyleableDoubleProperty(StyleableProperties.RADIUS, JFXProgressIndicator.this, "radius", 45.0);
	private Text text;
	private byte progress = 0;


	public JFXProgressIndicator() {
		this((byte) 40);
	}
	
	public JFXProgressIndicator(int progress) {
		this((byte) progress);
	}

	public JFXProgressIndicator(byte progress) {
		super();
		if (progress < 100) {
			this.progress = progress;
		} else {
			this.progress = 100;
		}
		initialize();
	}

	private void initialize() {
		getStyleClass().add(DEFAULT_STYLE_CLASS);
		setMinSize(50.0, 50.0);

		blueColor = Color.valueOf("#4285f4");
		redColor = Color.valueOf("#db4437");
		yellowColor = Color.valueOf("#f4b400");
		greenColor = Color.valueOf("#0F9D58");

		arc = new Arc(0, 0, 12, 12, 180, 380);
		arc.setStroke(Color.grayRgb(0, 0.3));
		arc.setStrokeWidth(3);
		arc.setFill(Color.TRANSPARENT);
		arc.getStyleClass().addAll("jfx-progress-indicator-empty");
		arc.radiusXProperty().bindBidirectional(radius);
		arc.radiusYProperty().bindBidirectional(radius);
		getChildren().add(arc);

		arc = new Arc(0, 0, 12, 12, 180, 380);
		arc.setStrokeWidth(3);
		arc.setFill(Color.TRANSPARENT);
		arc.getStyleClass().addAll("jfx-progress-indicator-fill");
		arc.radiusXProperty().bindBidirectional(radius);
		arc.radiusYProperty().bindBidirectional(radius);
		getChildren().add(arc);

		text = new Text();
		text.setScaleX(2.0);
		text.setScaleY(2.0);
		text.getStyleClass().addAll("jfx-progress-indicator-text");
		getChildren().add(text);

		minWidthProperty().bind(Bindings.createDoubleBinding(() ->
		getRadius()*2 + arc.getStrokeWidth() + 5
		, radius,arc.strokeWidthProperty()));

//		maxWidthProperty().bind(Bindings.createDoubleBinding(() ->
//		getRadius()*2 + arc.getStrokeWidth() + 5
//		, radius,arc.strokeWidthProperty()));

		minHeightProperty().bind(Bindings.createDoubleBinding(() ->
		getRadius()*2 + arc.getStrokeWidth() + 5
		, radius,arc.strokeWidthProperty()));

//		maxHeightProperty().bind(Bindings.createDoubleBinding(() ->
//		getRadius()*2 + arc.getStrokeWidth() + 5
//		, radius,arc.strokeWidthProperty()));

//		prefHeightProperty().bind(((Pane) getParent()).heightProperty());
//		prefWidthProperty().bind(((Pane) getParent()).widthProperty());
	}

	public void setProgress(byte progress) {
		if (progress < 100) {
			this.progress = progress;
		} else {
			this.progress = 100;
		}
		updateProgress();
	}

	public void setProgress(int progress) {
		setProgress((byte) progress);
	}

	private void updateProgress() {
		text.setText(progress + "%");
		Pos align = getAlignment() == null ? Pos.CENTER : getAlignment();
		HPos alignHpos = align.getHpos();
		VPos alignVpos = align.getVpos();
		double width = getWidth();
		double height = getHeight();
		double top = getInsets().getTop();
		double right = getInsets().getRight();
		double left = getInsets().getLeft();
		double bottom = getInsets().getBottom();
		double baselineOffset = alignVpos == VPos.BASELINE ? getMaxBaselineOffset(getManagedChildren())
				: height/2;
		double contentWidth = width - left - right;
		double contentHeight = height - top - bottom;
		Pos childAlignment = StackPane.getAlignment(text);
		layoutInArea(text, left, top,
				contentWidth, contentHeight,
				baselineOffset, getMargin(text),
				childAlignment != null? childAlignment.getHpos() : alignHpos,
						childAlignment != null? childAlignment.getVpos() : alignVpos);

		arc.setLength(-(360 * progress)/100);
		requestLayout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void layoutChildren() {
		if (!initialized) {
			super.layoutChildren();
			initialColor = (Color) arc.getStroke();
			if (initialColor == null) {
				arc.setStroke(blueColor);
			}

			setProgress(progress);

			initialized = true;
		}
	}

	//From javafx.scene.layout.Region
	private static double getMaxBaselineOffset(List<Node> content) {
		double max = 0;
		for (int i = 0, maxPos = content.size(); i < maxPos; i++) {
			final Node node = content.get(i);
			final double baselineOffset = node.getBaselineOffset();
			max = max >= baselineOffset ? max : baselineOffset; // Math.max
		}
		return max;
	}

	private static class StyleableProperties {
		private static final CssMetaData<JFXProgressIndicator, Number> RADIUS =
				new CssMetaData<JFXProgressIndicator, Number>("-fx-radius",
						SizeConverter.getInstance(), 12) {
			@Override
			public boolean isSettable(JFXProgressIndicator control) {
				return control.radius == null || !control.radius.isBound();
			}
			@Override
			public StyleableDoubleProperty getStyleableProperty(JFXProgressIndicator control) {
				return control.radius;
			}
		};

		private static final CssMetaData<JFXProgressIndicator, Number> STARTING_ANGLE =
				new CssMetaData<JFXProgressIndicator, Number>("-fx-starting-angle",
						SizeConverter.getInstance(), 360 - Math.random()*720) {
			@Override
			public boolean isSettable(JFXProgressIndicator control) {
				return control.startingAngle == null || !control.startingAngle.isBound();
			}
			@Override
			public StyleableDoubleProperty getStyleableProperty(JFXProgressIndicator control) {
				return control.startingAngle;
			}
		};


		private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;
		static {
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Parent.getClassCssMetaData());
			Collections.addAll(styleables,
					RADIUS,
					STARTING_ANGLE
					);
			CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
		}
	}

	// inherit the styleable properties from parent
	private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

	@Override
	public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
		if(STYLEABLES == null){
			final List<CssMetaData<? extends Styleable, ?>> styleables =
					new ArrayList<CssMetaData<? extends Styleable, ?>>(Parent.getClassCssMetaData());
			styleables.addAll(getClassCssMetaData());
			styleables.addAll(super.getClassCssMetaData());
			STYLEABLES = Collections.unmodifiableList(styleables);
		}
		return STYLEABLES;
	}
	public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
		return StyleableProperties.CHILD_STYLEABLES;
	}

	public Arc getArc() {
		return arc;
	}

	public final StyleableDoubleProperty radiusProperty() {
		return this.radius;
	}

	public final double getRadius() {
		return this.radiusProperty().get();
	}

	public final void setRadius(final double radius) {
		this.radiusProperty().set(radius);
	}

	private StyleableDoubleProperty startingAngle = new SimpleStyleableDoubleProperty(StyleableProperties.STARTING_ANGLE, JFXProgressIndicator.this, "starting_angle", 360.0);

	public final StyleableDoubleProperty startingAngleProperty() {
		return this.startingAngle;
	}

	public final double getStartingAngle() {
		return this.startingAngleProperty().get();
	}

	public final void setStartingAngle(final double startingAngle) {
		this.startingAngleProperty().set(startingAngle);
	}
}
