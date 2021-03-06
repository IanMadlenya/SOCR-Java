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

package edu.ucla.stat.SOCR.experiments;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import edu.ucla.stat.SOCR.core.*;
import edu.ucla.stat.SOCR.distributions.*;
import edu.ucla.stat.SOCR.util.*;

/** Basic coin-tossing experiment that illustrates the binomial distribution */
public class BinomialCoinExperiment extends Experiment {
    private int n = 10, coins = 60, x;
    private double p = 0.5, m;

    private JComboBox rvJComboBox = new JComboBox();
    private CoinBox coinBox = new CoinBox(coins);
    private BinomialDistribution sumDist = new BinomialDistribution(n, p);
    private LocationScaleDistribution averageDist = new LocationScaleDistribution(
            sumDist, 0, 1.0 / n);
    private RandomVariable sumRV = new RandomVariable(sumDist, "X");
    private RandomVariable averageRV = new RandomVariable(averageDist, "M");
    private RandomVariableGraph rvGraph = new RandomVariableGraph(sumRV);
    private RandomVariableTable rvTable = new RandomVariableTable(sumRV);

    //Constructor
    public BinomialCoinExperiment() {
        setName("Binomial Coin Experiment");
    }
    
    public void initialize() {
        createValueSetter("n", Distribution.DISCRETE, 1, coins, n);
        createValueSetter("p", Distribution.CONTINUOUS, 0, 1);
        rvJComboBox.addItemListener(this);
        //JComboBox
        rvJComboBox.addItem("X: Number of Heads");
        rvJComboBox.addItem("M: Proportion of Heads");
        SOCRToolBar toolbar3 = new SOCRToolBar();
        toolbar3.add(rvJComboBox);
        addToolbar(toolbar3);
        //Graphs
        addGraph(coinBox);
        addGraph(rvGraph);
        //Tables
        addTable(rvTable);

        reset();
    }

    //Experiment
    public void doExperiment() {
        super.doExperiment();
        coinBox.toss(n);
        x = coinBox.getHeadCount(n);
        m = (double) x / n;
        sumRV.setValue(x);
        averageRV.setValue(m);
    }

    //Reset
    public void reset() {
        super.reset();
        getRecordTable().append("\tX\tM");
        coinBox.showCoins(0);
        sumRV.reset();
        averageRV.reset();
        rvGraph.reset();
        rvTable.reset();
    }

    //Update
    public void update() {
        super.update();
        coinBox.showCoins(n);
        rvGraph.repaint();
        rvTable.update();
        getRecordTable().append("\t" + x + "\t" + format(m));
    }

    public void update(Observable o, Object arg) {
        n = getValueSetter(0).getValueAsInt();
        p = getValueSetter(1).getValue();
        setDistribution();
        reset();

    }

    //JComboBox events
    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() == rvJComboBox) {
            if (rvJComboBox.getSelectedIndex() == 0) {
                rvGraph.setRandomVariable(sumRV);
                rvTable.setRandomVariable(sumRV);
            } else {
                rvGraph.setRandomVariable(averageRV);
                rvTable.setRandomVariable(averageRV);
            }
            rvGraph.repaint();
            rvTable.update();
        } else super.itemStateChanged(event);
    }

    public void setDistribution() {
        coinBox.setProbability(p);
        sumDist.setParameters(n, p);
        averageDist.setParameters(sumDist, 0, 1.0 / n);
        reset();
    }
}

