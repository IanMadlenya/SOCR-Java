/****************************************************
Statistics Online Computational Resource (SOCR)
http://www.StatisticsResource.org
 
All SOCR programs, materials, tools and resources are developed by and freely disseminated to the entire community.
Users may revise, extend, redistribute, modify under the terms of the Lesser GNU General Public License
as published by the Open Source Initiative http://opensource.org/licenses/. All efforts should be made to develop and distribute
factually correct, useful, portable and extensible resource all available in all digital formats for free over the Internet.
 
SOCR resources are distributed in the hope that they will be useful, but without
any warranty; without any explicit, implicit or implied warranty for merchantability or
fitness for a particular purpose. See the GNU Lesser General Public License for
more details see http://opensource.org/licenses/lgpl-license.php.
 
http://www.SOCR.ucla.edu
http://wiki.stat.ucla.edu/socr
 It s Online, Therefore, It Exists! 
****************************************************/

package edu.ucla.stat.SOCR.modeler;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.ucla.stat.SOCR.modeler.gui.ModelerConstant;
import edu.ucla.stat.SOCR.util.ObservableWrapper;

public class UQuadraticFit_Modeler extends Modeler implements ItemListener{
	
	public edu.ucla.stat.SOCR.distributions.UQuadraticDistribution UQuadraticDistr;	

	private double[] modelX;
	private double[] modelY;	
	private int modelCt = 1;
	private int modelType = CONTINUOUS_DISTRIBUTION_TYPE;	
	private static boolean isContinuous = true;
	public static final int SLICE_SIZE = 4001;
	private int numberOfQuantiles=100;
	
	public JCheckBox estimateParams = new JCheckBox("Estimate Parameters");
	public JCheckBox userParams = new JCheckBox("User Parameters",true);

	public JLabel lowerParamLabel = new JLabel("lower Parameter");
	public JTextField lowerParamField = new JTextField("0.0",3);
	public JLabel upperParamLabel = new JLabel("upper Parameter");
	public JTextField upperParamField = new JTextField("1.0",3);
	
	private ButtonGroup buttonGroup = new ButtonGroup();
	

	//default constructor
	public UQuadraticFit_Modeler(){
	}
	
	//constructor
	public UQuadraticFit_Modeler(JPanel controlpanel) {
		controlpanel.add(estimateParams);
		controlpanel.add(userParams);
		buttonGroup.add(estimateParams);
		buttonGroup.add(userParams);
		estimateParams.addItemListener(this);
		userParams.addItemListener(this);
		addParams(controlpanel);
		controlpanel.repaint();	
	}
	
	
	public void addParams(JPanel controlpanel){
		controlpanel.add(lowerParamLabel);
		controlpanel.add(lowerParamField);
		controlpanel.add(upperParamLabel);
		controlpanel.add(upperParamField);
	}

	public void fitCurve(float[] rawDat, double minx, double maxx,JTextArea resultPanelTextArea, boolean rescaleClicked, boolean scaleUp, boolean initReset) {
		fitCurve(rawDat, (float)minx, (float)maxx, resultPanelTextArea, rescaleClicked, scaleUp, initReset);
	}
	
	
	public void fitCurve(float[] rawDat, float minx, float maxx,
			JTextArea resultPanelTextArea, boolean rescaleClicked, boolean scaleUp, boolean initReset) {

		try{
			if(estimateParams.isSelected())
				UQuadraticDistr = new edu.ucla.stat.SOCR.distributions.UQuadraticDistribution(rawDat);
			else
				UQuadraticDistr = new edu.ucla.stat.SOCR.distributions.UQuadraticDistribution(Double.parseDouble(lowerParamField.getText()),Double.parseDouble(upperParamField.getText()));

			resultPanelTextArea.setText("Lower Parameter = "+ Double.toString(UQuadraticDistr.getAlpha())+"\nUpper Parameter = " + Double.toString(UQuadraticDistr.getBeta())+"\n\n");

			float ind = 1;
			int number = SLICE_SIZE;
			ind = (maxx-minx)/ (number - 2);
			
			modelX = new double[number];
			modelY = new double[number];
			for(int i = 0; i<number ;i++) {
				modelX[i] = minx+ind*i;
				modelY[i] = UQuadraticDistr.getDensity(modelX[i]);
			}
			
			double[] temp = new double [rawDat.length];
			double[] x = new double[numberOfQuantiles];
			double[] y = new double[numberOfQuantiles];
				//	 Model Quantiles
			for (int i = 0; i < numberOfQuantiles; i++)  y[i] = UQuadraticDistr.getQuantile((i+0.5)/numberOfQuantiles);
				//	 Data Quantiles
			for (int i= 0; i<rawDat.length; i++) temp[i] = (double)rawDat[i];
			x = edu.ucla.stat.SOCR.util.AnalysisUtility.getQuantileArray(temp);
			resultPanelTextArea.append(getKSModelTestString(UQuadraticDistr.getName(), numberOfQuantiles, x, y));
			
		} catch(Exception e) {
			resultPanelTextArea.setText(e.getMessage());
		}
		
	}
	
	
	public double[] generateSamples(int sampleCount) {
		UQuadraticDistr = new edu.ucla.stat.SOCR.distributions.UQuadraticDistribution(Double.parseDouble(lowerParamField.getText()) , Double.parseDouble(upperParamField.getText()));
		double[] dat = new double[sampleCount];
		
		for(int i = 0; i< sampleCount;i++)
			dat[i] = UQuadraticDistr.simulate();
		return dat;
	}
	
	public String getDescription() {
		String desc = new String();
		desc = "URL: http://socr.ucla.edu/htmls/dist/UQuadratic_Distribution.html";
		return desc;
	}
	
	public double getGraphLowerLimit() {
		return ModelerConstant.GRAPH_LOWER_LIMIT;
	}
	
	public double getGraphUpperLimit() {
		return ModelerConstant.GRAPH_UPPER_LIMIT;
	}
	
	public String getInstructions() {
		String instructions = new String();
		instructions = "\tInstructions: http://wiki.stat.ucla.edu/socr/index.php/UQuadraticDistribuionAbout";
		return instructions;
	}
	
	
    public double getLowerLimit() {
    	return Double.NEGATIVE_INFINITY;
    }
	
	public int getModelCount() {
		return modelCt;
	}
	
	public int getModelType() {
		return modelType;
	}
	
	
	public String getResearch() {
		String research = new String();
		research = "\tresearch field returned by the modeler.";
		return research;
	}
	
	
	public double getUpperLimit() {
		return Double.POSITIVE_INFINITY;
	}
	
	public boolean isContinuous() {
		return UQuadraticFit_Modeler.isContinuous;
	}
	
	public void itemStateChanged(ItemEvent event){
		if(event.getSource() == userParams || event.getSource() == estimateParams) {
			if(estimateParams.isSelected())
				toggleParams(false);
			else
				toggleParams(true);
		}
	}
	
    public void registerObservers(ObservableWrapper o) {
    	o.addJCheckBox(estimateParams);
		o.addJCheckBox(userParams);
		o.addJTextField(lowerParamField);
		o.addJTextField(upperParamField);
    }
	
	public double[] returnModelX() {
		return modelX;
	}

	public double[] returnModelY() {
		return modelY;
	}	
	
    public void toggleParams(boolean istrue) {
        if(istrue) {
            lowerParamLabel.setVisible(true);
            lowerParamField.setVisible(true);
            upperParamLabel.setVisible(true);
            upperParamField.setVisible(true);

        }else {
        	lowerParamLabel.setVisible(false);
        	lowerParamField.setVisible(false);
            upperParamLabel.setVisible(false);
            upperParamField.setVisible(false);
        }
    }
	
	
	public boolean useInitButton() {
		return false;
	}
	
}