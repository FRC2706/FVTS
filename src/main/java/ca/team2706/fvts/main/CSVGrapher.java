package ca.team2706.fvts.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.nio.file.Files;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ca.team2706.fvts.core.Constants;

public class CSVGrapher extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) throws Exception{
		new CSVGrapher();
	}
	public CSVGrapher() throws Exception{
		System.out.println("FVTS CSV Grapher "+Constants.VERSION_STRING+" developed by "+Constants.AUTHOR);
	
		initUI();
	}
	private void initUI() throws Exception{

        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        add(chartPanel);

        pack();
        setTitle("FVTS CSV");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
	private XYDataset createDataset() throws Exception{
		System.out.print("File path: ");
		Scanner in = new Scanner(System.in);
		File file = new File(in.nextLine());
		boolean showFPS = false;
		boolean showTargets = false;
		boolean showX = false;
		boolean showY = false;
		boolean showArea = false;
		boolean showDist = false;
		boolean continueing = true;
		while(continueing) {
			System.out.print("Enter data type to include (1=fps 2=targets 3=xAngle 4=yAngle 5=area 6=dist 0=done): ");
			int input = Integer.valueOf(in.nextLine());
			switch(input) {
			case 0:
				continueing = false;
				break;
			case 1:
				showFPS = true;
				break;
			case 2:
				showTargets = true;
				break;
			case 3:
				showX = true;
				break;
			case 4:
				showY = true;
				break;
			case 5:
				showArea = true;
				break;
			case 6:
				showDist = true;
				break;
			}
		}
		in.close();
		CSVParser parser = new CSVParser(Files.newBufferedReader(file.toPath()), CSVFormat.DEFAULT);
		
		XYSeries fps = new XYSeries("FPS");
		XYSeries targets = new XYSeries("Targets");
		XYSeries xCtr = new XYSeries("Angle X");
		XYSeries yCtr = new XYSeries("Angle Y");
		XYSeries area = new XYSeries("Area Percentage");
		XYSeries distance = new XYSeries("Distance");
        long currTime = 0;
		boolean first = true;
        for(CSVRecord entry : parser) {
			if(first) {
				first = false;
				continue;
			}
			long deltaTime = Long.valueOf(entry.get(0));
			
			currTime += deltaTime;
			
			double currTimeS = currTime/1000d;
			
			double fpsD = Double.valueOf(entry.get(1));
			int targetsD = Integer.valueOf(entry.get(2));
			
			fps.add(currTimeS, fpsD);
			targets.add(currTimeS,targetsD);
			
			if(entry.size() > 3) {
				double xCtrD = Double.valueOf(entry.get(3));
				double yCtrD = Double.valueOf(entry.get(4));
				double areaD = Double.valueOf(entry.get(5));
				double distanceD = Double.valueOf(entry.get(6));
				
				double xAngle = xCtrD * 45;
				double yAngle = yCtrD * 45;
				double areaPercent = areaD * 100;
				
				xCtr.add(currTimeS, xAngle);
				yCtr.add(currTimeS, yAngle);
				area.add(currTimeS, areaPercent);
				distance.add(currTimeS,distanceD);
			}
		}
		parser.close();

       	XYSeriesCollection dataset = new XYSeriesCollection();
       	if(showFPS)
       		dataset.addSeries(fps);
        if(showTargets)
        	dataset.addSeries(targets);
        if(showX)
        	dataset.addSeries(xCtr);
        if(showY)
        	dataset.addSeries(yCtr);
        if(showArea)
        	dataset.addSeries(area);
        if(showDist)
        	dataset.addSeries(distance);
        

        return dataset;
	}
	private JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "FVTS CSV Output",
                "Time",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("FVTS CSV Output",
                        new Font("Serif", java.awt.Font.BOLD, 18)
                )
        );

        return chart;
    }
}
