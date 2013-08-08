package org.openlca.core.editors.analyze.sankey;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.SWT;

/**
 * Figure for connection links.
 */
public class ConnectionLinkFigure extends PolylineConnection {

	private int lineWidth;
	public SankeyDiagram diagram;

	public ConnectionLinkFigure(int lineWidth, SankeyDiagram diagram) {
		super();
		setAntialias(SWT.ON);
		this.diagram = diagram;
		this.lineWidth = lineWidth;
	}

	@Override
	public void paint(Graphics graphics) {
		setLineWidth((int) (lineWidth * diagram.getZoom()));
		super.paint(graphics);
	}
}
