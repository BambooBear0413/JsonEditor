package io.bamboobear.json_editor.component;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GridBagConstraintsBuilder implements Cloneable{
	private int gridx;
	private int gridy;
	private int gridwidth;
	private int gridheight;
	private double weightx;
	private double weighty;
	private int anchor;
	private int fill;
	private Insets insets;
	private int ipadx;
	private int ipady;
	
	public static final int RELATIVE = GridBagConstraints.RELATIVE;
	public static final int REMAINDER = GridBagConstraints.REMAINDER;
	
	public GridBagConstraintsBuilder() {
		gridx = RELATIVE;
        gridy = RELATIVE;
        gridwidth = 1;
        gridheight = 1;

        weightx = 0;
        weighty = 0;
        anchor = Anchor.CENTER.getValue();
        fill = Fill.NONE.getValue();

        insets = new Insets(0, 0, 0, 0);
        ipadx = 0;
        ipady = 0;
	}
	
	public GridBagConstraintsBuilder(int gridx, int gridy, int gridwidth, int gridheight,
            double weightx, double weighty, int anchor, int fill, Insets insets, int ipadx, int ipady) {
		this.gridx = gridx;
        this.gridy = gridy;
        this.gridwidth = gridwidth;
        this.gridheight = gridheight;
        this.weightx = weightx;
        this.weighty = weighty;
        this.anchor  = anchor;
        this.fill = fill;
        this.insets = insets;
        this.ipadx = ipadx;
        this.ipady = ipady;
        
	}
	
	public GridBagConstraintsBuilder(GridBagConstraints gbc) {
		this(gbc.gridx, gbc.gridy, gbc.gridwidth, gbc.gridheight, gbc.weightx, gbc.weighty, gbc.anchor, gbc.fill, gbc.insets, gbc.ipadx, gbc.ipady);
	}
	
	public GridBagConstraintsBuilder setGridx(int gridx) {
		this.gridx = gridx;
		return this;
	}
	
	public GridBagConstraintsBuilder setGridy(int gridy) {
		this.gridy = gridy;
		return this;
	}
	
	public GridBagConstraintsBuilder setGridLocation(int gridx, int gridy) {
		return setGridx(gridx).setGridy(gridy);
	}
	
	public GridBagConstraintsBuilder setGridwidth(int gridwidth) {
		this.gridwidth = gridwidth;
		return this;
	}
	
	public GridBagConstraintsBuilder setGridheight(int gridheight) {
		this.gridheight = gridheight;
		return this;
	}
	
	public GridBagConstraintsBuilder setGridSize(int gridwidth, int gridheight) {
		return setGridwidth(gridwidth).setGridheight(gridheight);
	}
	
	public GridBagConstraintsBuilder setWeightx(double weightx) {
		this.weightx = weightx;
		return this;
	}
	
	public GridBagConstraintsBuilder setWeighty(double weighty) {
		this.weighty = weighty;
		return this;
	}
	
	public GridBagConstraintsBuilder setWeight(double weightx, double weighty) {
		return setWeightx(weightx).setWeighty(weighty);
	}
	
	public GridBagConstraintsBuilder setAnchor(int anchor) {
		this.anchor = anchor;
		return this;
	}
	
	public GridBagConstraintsBuilder setAnchor(Anchor anchor) {
		if(anchor == null) {
			anchor = Anchor.CENTER;
		}
		
		this.anchor = anchor.getValue();
		return this;
	}
	
	public GridBagConstraintsBuilder setFill(int fill) {
		this.fill = fill;
		return this;
	}
	
	public GridBagConstraintsBuilder setFill(Fill fill) {
		if(fill == null) {
			fill = Fill.NONE;
		}
		
		this.fill = fill.getValue();
		return this;
	}
	
	public GridBagConstraintsBuilder setInsets(Insets insets) {
		this.insets = insets;
		return this;
	}
	
	public GridBagConstraintsBuilder setInsets(int top, int left, int bottom, int right) {
		return this.setInsets(new Insets(top, left, bottom, right));
	}
	
	public GridBagConstraintsBuilder setIpadx(int ipadx) {
		this.ipadx = ipadx;
		return this;
	}
	
	public GridBagConstraintsBuilder setIpady(int ipady) {
		this.ipady = ipady;
		return this;
	}
	
	public GridBagConstraintsBuilder setIpad(int ipadx, int ipady) {
		return setIpadx(ipadx).setIpady(ipady);
	}
	
	public int getGridx() {
		return gridx;
	}
	
	public int getGridy() {
		return gridy;
	}
	
	public int getGridwidth() {
		return gridwidth;
	}
	
	public int getGridheight() {
		return gridheight;
	}
	
	public double getWeightx() {
		return weightx;
	}
	
	public double getWeighty() {
		return weighty;
	}
	
	public Anchor getAnchor() {
		return Anchor.getAnchorByValue(anchor);
	}
	
	public Fill getFill() {
		return Fill.getAnchorByValue(fill);
	}
	
	public Insets getInsets() {
		return insets;
	}
	
	public int getIpadx() {
		return ipadx;
	}
	
	public int getIpady() {
		return ipady;
	}
	
	public GridBagConstraints build() {
		return new GridBagConstraints(gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets, ipadx, ipady);
	}
	
	public GridBagConstraintsBuilder clone() {
		return new GridBagConstraintsBuilder(gridx, gridy, gridwidth, gridheight, weightx, weighty, anchor, fill, insets, ipadx, ipady);
	}
	
	public static enum Anchor {
		//Absolute values
		CENTER(GridBagConstraints.CENTER),
		NORTH(GridBagConstraints.NORTH),
		NORTHEAST(GridBagConstraints.NORTHEAST),
		EAST(GridBagConstraints.EAST),
		SOUTHEAST(GridBagConstraints.SOUTHEAST),
		SOUTH(GridBagConstraints.SOUTH),
		SOUTHWEST(GridBagConstraints.SOUTHWEST),
		WEST(GridBagConstraints.WEST),
		NORTHWEST(GridBagConstraints.NORTHWEST),
		
		//Orientation relative values
		PAGE_START(GridBagConstraints.PAGE_START),
		PAGE_END(GridBagConstraints.PAGE_END),
		LINE_START(GridBagConstraints.LINE_START),
		LINE_END(GridBagConstraints.LINE_END),
		FIRST_LINE_START(GridBagConstraints.FIRST_LINE_START),
		FIRST_LINE_END(GridBagConstraints.FIRST_LINE_END),
		LAST_LINE_START(GridBagConstraints.LAST_LINE_START),
		LAST_LINE_END(GridBagConstraints.LAST_LINE_END),
		
		//Baseline relative values
		BASELINE(GridBagConstraints.BASELINE),
		BASELINE_LEADING(GridBagConstraints.BASELINE_LEADING),
		BASELINE_TRAILING(GridBagConstraints.BASELINE_TRAILING),
		ABOVE_BASELINE(GridBagConstraints.ABOVE_BASELINE),
		ABOVE_BASELINE_LEADING(GridBagConstraints.ABOVE_BASELINE_LEADING),
		ABOVE_BASELINE_TRAILING(GridBagConstraints.ABOVE_BASELINE_TRAILING),
		BELOW_BASELINE(GridBagConstraints.BELOW_BASELINE),
		BELOW_BASELINE_LEADING(GridBagConstraints.BELOW_BASELINE_LEADING),
		BELOW_BASELINE_TRAILING(GridBagConstraints.BELOW_BASELINE_TRAILING);
		
		private final int value;
		
		Anchor(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
		public static Anchor getAnchorByValue(int value) {
			var anchors = values();
			for(Anchor anchor : anchors) {
				if(anchor.value == value) {
					return anchor;
				}
			}
			return Anchor.CENTER;
		}
	}
	
	public static enum Fill {
		NONE(GridBagConstraints.NONE),
		HORIZONTAL(GridBagConstraints.HORIZONTAL),
		VERTICAL(GridBagConstraints.VERTICAL),
		BOTH(GridBagConstraints.BOTH);
		
		private final int value;
		
		Fill(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
		
		public static Fill getAnchorByValue(int value) {
			var fills = values();
			for(Fill fill : fills) {
				if(fill.value == value) {
					return fill;
				}
			}
			return Fill.NONE;
		}
	}
}
