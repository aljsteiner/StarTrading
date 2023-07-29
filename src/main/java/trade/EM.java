/*
 Copyright (C) 2012 Albert Steiner
 Copyright (C) 2022 Albert Steiner

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package trade;

/**
 * This is in many ways an extension of StarTrader the main program which also
 * contains the user interface and methods to see results and change priorities
 * EM contains the variable which can be changed, it also contains related
 * constants.
 *
 * @author albert Steiner
 */
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;
import java.util.TreeMap;
import javax.swing.JTable;

/**
 * this is an output interface, it specifies where to put values using values
 * should be in EM param name.accept(data)
 *
 * @author albert
 */
interface Consumer1 {

  public double apply(E d, double v);
}

class EM {

  /**
   * eE and eM are set when a different eM is chosen, it is the only value
   * needed. so they can be static across all instances of eM
   */
  static E eE;   // EM.eE
  static EM eM;  // EM.eM
  EM myEM;  // This is the this of an instance
  static StarTrader st;
  public static boolean myTestDone = false;

  // The following is a start of the capability to save and instance of
  // a game at the current level with new referents for arrays etc.
  // the cureent game could continue or another EM instance could be made active
  // could be reloded onto the active instance of 
  EM copyTo; // during a copy, doVal, doRes etc must copy
  boolean doingCopy = false;

 static final public String statsButton0Tip = "0: Cum Game Worths,";
  static final public String statsButton1Tip = "1: cum Favors and trade effects";
  static final public String statsButton2Tip = "2: catastrophes, deaths, randoms, forwardfund";
  static final public String statsButton3Tip = "3: deaths. trades acc";
  static final public String statsButton4Tip = "4: deaths, Rej misd Trades";
  static final public String statsButton5Tip = "5: trades accepted, rejected, missed ";
  static final public String statsButton6Tip = "6: forwardFunds, deaths";
  static final public String statsButton7Tip = "7: Resource, staff, knowledge values";
  static final public String statsButton8Tip = "8: creates. growth, forwardFunds and costs details";
  static final public String statsButton9Tip = "9: Catastrophes, Fertility, health and effects";
  static final public String statsButton10Tip = "10: list by ages deaths with trades missed, rejected, lost";
  static final public String statsButton11Tip = "11: list by ages deaths with trades accepted ";
  static final public String statsButton12Tip = "12: list by ages trades missed, rejected, lost";
  static final public String statsButton13Tip = "13: list by ages affects with growths depreciation";
  static final public String statsButton14Tip = "14: list by ages affects with catastrophies, forwardFunds ";
  static final public String statsButton15Tip = "15: list by ages live trades";
  static final public String statsButton16Tip = "16: list by ages worths, work,faculty,research interns";
  static final public String statsButton17Tip = "17: list by ages helps, creations ";
  static final public String statsButton18Tip = "18: Swaps years xfer skips, redos and dos";
  static final public String statsButton19Tip = "19: Swaps years Forward Fund imbalance or save";
  static final public String statsButton20Tip = "20: rcsg";
    static final public String statsButton21Tip = "21: TB assigned";
  static final public String statsButton22Tip = "22: TB assigned";
  static final public String statsButton23Tip = "23: display table";

  volatile long[][][][] resI;
  volatile double[][][][] resV;
  volatile String[][] resS;
  final Integer syncRes=25; // sync res setting and surround during yearEnds
  final Integer syncE = 30; // possible mult doYear associateds econ counts
  final Integer syncY = 35; // mult doYear locks
  static final int lStatsWaitList = 10;
  static String[] statsWaitList = new String[lStatsWaitList];
  static volatile int ixStatsWaitList = 0;
  static String description = "";
//  Econ ec;
  static volatile ArrayList<Econ> ships = new ArrayList<Econ>();
  static volatile ArrayList<Econ> deadPlanets = new ArrayList<Econ>();
  static volatile ArrayList<Econ> deadShips = new ArrayList<Econ>();
  static volatile ArrayList<Econ> planets = new ArrayList<Econ>();
  static volatile ArrayList<Econ> econs = new ArrayList<Econ>();
  // static volatile ArrayList<EM> ems = new ArrayList<EM>();
  static volatile TreeMap<String, Econ> names2ec = new TreeMap<String, Econ>();
  static volatile ArrayList<String> emNames = new ArrayList<String>();
  static int[] envsPerYear = {40, 40, 50, 60, 20}; //see minEcons
  static int lEnvsPerYear = envsPerYear.length;
  // int porsClanCntd[][] = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}}; // defaults
  static volatile int porsClanCnt[][] = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
  //int clanCntd[] = {0, 0, 0, 0, 0};
  static volatile int clanCnt[] = {0, 0, 0, 0, 0};
  static volatile Integer econCnt = 0;
  static volatile int deadCnt = 0;
  static final int econLock[] = {0};
  // int porsCntd[] = {0, 0};
  static volatile int porsCnt[] = {0, 0};

  static volatile int porsClanTraded[][] = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
  static volatile int clanTraded[] = {0, 0, 0, 0, 0};
  static volatile int porsTraded[] = {0, 0};
  static volatile int tradedCnt = 0;
  static volatile int porsClanVisited[][] = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
  static volatile int clanVisited[] = {0, 0, 0, 0, 0};
  static volatile int porsVisited[] = {0, 0};
  static volatile int visitedCnt = 0;
  static volatile int porsClanDead[][] = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
  static volatile int clanDead[] = {0, 0, 0, 0, 0};
  static volatile int porsDead[] = {0, 0};
  static volatile int cntDead = 0;

  public final static int LSECS = E.LSECS;
  public final static int L2SECS = E.L2SECS;
  public final static double INVLSECS = E.INVLSECS;
  public final static double INVL2SECS = E.INVL2SECS;
  public final static int[] ASECS = E.ASECS;
  public final static int[] A2SECS = E.A2SECS;

  static final Charset CHARSET = Charset.forName("US-ASCII");
  //                                                                        TUE 04/02/2021 14:03:02:233 cdt
  static final public SimpleDateFormat MYDATEFORMAT = new SimpleDateFormat("EEE MM/dd/YYYY HH:mm:ss:SSSz");
  static final Path REMEMBER = Paths.get("remember");
  static final Path KEEP = Paths.get("keep");
  static BufferedWriter bRemember = null, bKeep = null;
  static BufferedReader bKeepr = null;

  /* each keep goes false after end of page process new page or settings ended */
  static boolean keepFromPage = false;  // keep clicked titles on this page
  static boolean keepHeaderPrinted = false; // header already printed
  static boolean keepBuffered = false; // true if flush needed
  static final String keepInstruct = "keep any changes made in this page, describe why you kept this changes";
  static final String initialKeepCmt = "put HERE why you changed the settings on this page";
  static String prevKeepCmt = initialKeepCmt + "";
  static String nextLineOfOutput = ""; //both keep and remember

  /* each remember goes false at next page request or end stats */
  static boolean rememberFromPage = false; // remember clicked lines on this page
  static boolean rememberHeader = false; //header for this page printed
  static boolean rememberBuffered = false;// flush needed
  static final String rememberInstruct = "click the name of a line in this table to remember it, also indicate why.";
  static final String initialRememberCmt = "put the why you chose to remember ths lines in this page.";
  static String prevRememberCmt = initialRememberCmt + "";

  static volatile double[] maxLY = {15.};// ship max light years for search
  static final double[][] mMaxLY = {{.5, 25.}};//planet or ship max light years
  static volatile double[] addLY = {.9}; // add to ly in planet search per round of search
  static final double[][] mAddLY = {{.3, 5.}};
  static volatile double[] multLYM = {1.1, 1.2, 1.4, 1.6, 1.9, 2.2, 2.7, 3.5}; //8
  // multiplier of the ship goals.
  static volatile double[] addGoal = {0.0, 0.015, .025, .045, .06, .08, 2.0, 2.3};//9
  // .25 = 1ship/3 planets
  static volatile double[] gameShipFrac = {.70};  // 2.3 ships / econs .75 means 3ships/1 planet, .8 = 4ships/1planet
  static final double[][] mGameShipFrac = {{.25, 1.20}, {.25, 1.20}};
// double[][] clanShipFrac = {{.70, .70, .70, .501, .6}, {.70, .70, .70, .501, .6}}; // .3->5. clan choice of clan ships / clan econs
  static volatile double[][] clanShipFrac = {{.501, .501, .501, .501, .6}};
  static final double[][] mClanShipFrac = {{.25, .81}, {.20, 1.20}};
  static volatile double[][] clanAllShipFrac = {{.501, .501, .501, .501, .501}}; // clan (ships/econs)
  static final double[][] mClanAllShipFrac = {{.25, 1.20}, {.2, 1.20}};
  static volatile double econLimits1[] = {150.}; // start limiting econs
  static final double mEconLimits1[][] = {{100., 500.}, {200., 500.}};
  static volatile double econLimits2[] = {150.}; // more limiting of econs
  static final double mEconLimits2[][] = {{35., 550.}};
  static volatile double econLimits3[] = {110.}; // max of econs
  static volatile double mEconLimits3[][] = {{35., 600.}};
  //double[][] LimitEcons = {{140.}};
  static final double[][] mLimitEcons = {{100., 300.}, {100., 300.}};
  static String tError = " no tError";
  static volatile Econ curEcon;  //eM only changes at the end a a year run, EM.curEcon
  static volatile String curEconName = "no Econ name";
  static volatile String curEconClan = "A";
  static volatile long curEconTime = 0; // time of this econ
  static volatile int curEconAge=0;
  static volatile Econ otherEcon;
  static volatile String otherEconName = "no other name";
  static volatile String otherEconClan = "A";

  /**
   * set curEcon, curEconName curEconClan;curEconTime,curEconAge
   * get a null error the offered Econ is null
   *
   * @param x econ to be set in curEcon
   * @return curEconName
   */
  static String setCurEcon(Econ x) {
    if (x != curEcon) {
      curEconTime = (new Date()).getTime();
    }
    curEcon = x;
    curEconClan = x.getColor();
    curEconAge = x.getAge();
    return curEconName = x.getName();
  }

  /**
   * set values for other Econ, otherEconClan otherEconName
   *
   * @param x Econ to be set
   * @return name of the Econ to be set
   */
  static String setOtherEcon(Econ x) {
    otherEcon = x;
    otherEconClan = x.getColor();
    return otherEconName = x.getName();
  }
  static double[][] wildCursCnt = {{7.}};
  static double[][] mWildCursCnt = {{3., 20.}};
  static double[] difficultyPercent = {30.};
  static final double[][] mDifficultyPercent = {{0., 99.}, {0., 99.}};
  static double[][] hiLoMult = {{1.3, 1.3, 1.3, .3, .3}, {1.3, 1.3, 1.3, .3, .3}};
  static final double[][] mHiLoMult = {{.2, 2.}, {.2, 2.}};
  static double[][] minEconsMult = {{1.1}}; // nultiplies values in envsPerYear
  static final double[][] mMinEconsMult = {{.5, 10.0}};
  static volatile double[][] minEcons = {{30.}}; // goes into last envsPerYear;
  static final double[][] mMinEcons = {{10., 100.}};
  static volatile double[][] maxThreads = {{10.0}};
  static final double[][] mmaxThreads = {{1.0, 12.0}};
  static volatile int[] iMaxThreads = {1};
  static volatile double[][] haveColors = {{.3}};
  static final double[][] mHaveColors = {{0.2, 2.2}};
  static volatile double[][] haveCash = {{2.0}};
  static final double[][] mHaveCash = {{0.2, 2.2}};

  static double[] sendHistLim = {20};
  static final double[][] mSendHistLim = {{5., 50}, {-1, -1}};
  static volatile double[] nominalWealthPerCommonKnowledge = {.2}; // was .9
  static final double[][] mNominalWealthPerCommonKnowledge = {{.15, .5}};
  // worth = Wealth/ frac ??
  static volatile double[] fracCommonKnowledge = {1.5};//value CommonKnowledge/Worth
  static final double[][] mFracCommonKnowledge = {{.5, 3.5}, {-1, -1}};
  static volatile double[] fracNewKnowledge = {1.5};//value newKnowledge/nominalWealthPerCommonKnowledge
  static final double[][] mFracNewKnowledge = {{.5, 3.5}, {-1, -1}};
  static volatile double[] nominalWealthPerTrade = {5., 5.};  // guests and cargo
  // double[][] pNominalWealthPerTrade = {{nominalWealthPerTrade[0]},{nominalWealthPerTrade[1]}};
  static final double[][] mNominalWealthPerTrade = {{2., 9.}, {2., 9.}};
  static volatile double[] tradeWealthPerStaff = {2.6, 3.5};
  static final double[][] mTradeWealthPerStaff = {{1.3, 6.}, {1.3, 6.}};
  static volatile double[] tradeWealthPerResource = {2., 3.5};  // and cargo
  static final double[][] mTradeWealthPerResource = {{1., 6.}, {-1., -1.}};
  static volatile double[] tradeReservFrac = {.15, .15};
  static final double[][] mTradeReservFrac = {{.05, .5}, {.05, .5}};
//  static protected double nominalWealthPerTradeShipGuests = 5.2;
  static volatile double[] nominalWealthPerResource = {3., 4.};  // and cargo
  static final double[][] mNominalWealthPerResource = {{1., 6.}, {1.5, 8.}};
  //  nominalCGWealth[pors]
  static volatile int nameCnt = 1;
  static volatile double[] nominalWealthPerStaff = {3., 4.5};  // and guests
  static final double[][] mNominalWealthPerStaff = {{1.7, 6.}, {1.7, 8.}};

  // double[][] tradeRSWealth = {tradeWealthPerResource, tradeWealthPerStaff};
  static volatile double[][] tradeRSWealth = {nominalWealthPerResource, nominalWealthPerStaff};
  static volatile double[][] rawHealthsSOS0 = {{.15}};  // rawHealths , rawProspects2 max rawProspects2.min() cnt
  static volatile double[][] rawHealthsSOS1 = {{.05}};  // rawHealths , rawProspects2 below this cause sos
  static volatile double[][] rawHealthsSOS2 = {{.02}};
  static volatile double[][] rawHealthsSOS3 = {{-.02}};

  // [rors][pors]
  static volatile double[] maxFertility = {2.0, 2.0};
  static volatile double[][] mMaxFertility = {{1., 2.5}, {1., 3.5}};
  static volatile double[] minFertility = {0., 0.};
  static volatile double[][] mMinFertility = {{0., .05}, {0., .05}};
  static volatile double[][] nominalRSWealth = {nominalWealthPerResource, nominalWealthPerStaff};

  static volatile double[][] nominalWealthPerNewKnowledge = {{1.4}, {2.}};
  static volatile double[][] mNominalWealthPerNewKnowledge = {{.6, 4.}, {.7, 6.}};
  static volatile double[] manualFracKnowledge = {.7, .8};
  static final double[][] mManualFracKnowledge = {{.3, 1.5}, {.3, 2.}};
  static volatile double[] nominalWealthPerTradeManual = {manualFracKnowledge[0] * nominalWealthPerCommonKnowledge[0], manualFracKnowledge[1] * nominalWealthPerCommonKnowledge[0]};
  static volatile double[] initialWorth = {10000., 12000.};
  static final double[][] mInitialWorth = {{5000., 20000.}, {2000., 30000.}};
  static volatile double[] initialKnowledge = {1000., 1000.}; //900
  static final double[][] mInitialKnowledge = {{500., 4000.}, {500., 6000}};
  static volatile double[] initialCommonKnowledgeFrac = {0.106383, .106383};
  static final double[][] mInitialCommonKnowledgeFrac = {{.1, .3}, {.08, .5}};
  static volatile double[] initialColonists = {1300., 1300.}; //3900
  static final double[][] mInitialColonists = {{500., 3000.}, {500., 3000.}};
  static volatile double[] initialColonistFrac = {.188298, .27};
  static final double[][] mInitialColonistFrac = {{.1, .3}, {.08, .5}};
  static volatile double[] initialResources = {1300., 1300.};
  static final double[][] mInitialResources = {{500., 3000.}, {500., 3000.}};
  static volatile double[] initialResourceFrac = {.166596, .22}; // 1566
  static final double[][] mInitialResourceFrac = {{.1, .3}, {.1, .5}};
  static volatile double[] initialReserve = {.1, .5};
  static final double[][] mInitialReserve = {{.05, 1.5}, {.05, 1.5}};
  static volatile double[] initialWealth = {500., 500.};
  static final double[][] mInitialWealth = {{100., 2000.}, {100., 2000.}};
  static volatile double[] initialWealthFrac = {0.2127659574, 0.2127659574};
  static final double[][] mInitialWealthFrac = {{.1, .5}, {.1, .9}};

  static double getInitialEconWorth(int pors, int clan) {
    // 12 = 1.25 (1. + 2*.125), 14 = 1.5 (1. + 4 * .125)
    double ret = initialWorth[pors] * (porsClanCnt[pors][clan] <= 10 ? 1.
            : 1. + (porsClanCnt[pors][clan] - 10.) * .125);
    return ret;
  }

  /**
   * get the choice of whether the next econ should be P planet or S ship also
   * may output a line to System.out
   *
   * @param clan for which clan
   * @return pors for the given clan
   */
  static int getNewPorS(int clan) {
    double sFrac1 = 99.;
    double sFrac2 = 99.;
    double sFrac3 = 99.;
    int S = E.S;
    int P = E.P;
    // use .0001 to prevent divide by zero
    double sFrac1a = (sFrac1 = (.0001 + eM.porsCnt[S]) / (.0001 + eM.econCnt));
    double sFrac2a = (sFrac2 = (.0001 + eM.porsCnt[S]) / (.0001 + eM.econCnt));
    double sFrac3a = (sFrac3 = (.0001 + eM.porsClanCnt[S][clan]) / (.0001 + eM.clanCnt[clan]));
    int pors = (eM.clanCnt[clan] > 0
            && eM.porsClanCnt[P][clan] > 0
            && sFrac1 < eM.clanAllShipFrac[P][clan]
            && sFrac2 < eM.gameShipFrac[P]
            && sFrac3 < eM.clanShipFrac[P][clan]) ? E.S : E.P;
    if (E.debugDoYearOut) {
      System.out.println("%%%%newEcon lPlanets=" + eM.porsCnt[P]
              + " lShips=" + eM.porsCnt[S]
              + " lClanPlanets=" + eM.porsClanCnt[P][clan]
              + " lEconCnt=" + eM.econCnt
              + " lClanCnt[" + clan + "]=" + eM.clanCnt[clan]
              + " lClanShips=" + eM.porsClanCnt[S][clan]
              + " lCEcons=" + eM.clanCnt[clan]
              + " clanShipsFrac=" + eM.df(sFrac3)
              + " clanShipFrac[P][clan] =" + eM.clanShipFrac[P][clan]
              + " pors=" + pors
              + "+++"
              + (eM.clanCnt[clan] == 0 ? "P0000" : "S"
                      + (eM.porsClanCnt[P][clan] == 0 ? "P000" : "S"
                              + (sFrac1 < eM.clanAllShipFrac[P][clan] ? "S" : "P")
                              + (sFrac2 < eM.gameShipFrac[P] ? "S" : "P")
                              + (sFrac3 < eM.clanShipFrac[P][clan] ? "S" : "P"))));
    }
    return pors;
  }

  static volatile double[] upWorth = {1.3, 1.3};
  static final double[][] mUpWorth = {{1.05, 1.7}, {1.05, 1.7}};
  //double nominalColonistsInWorkerDeaths = .8;
  static volatile double[][] initStaffGrossAdjustmentPerEnvirn = {{2.}, {2.}};
  static volatile double[][] initGuestGrossAdjustmentPerEnvirn = {{0.2}, {0.7}};
  static volatile double[] effBias = {.5, .5}; // 170309 .25->.5
  static final double[][] mEffBias = {{.25, .75}, {.25, .75}}; // 170309 .25->.5
  // this value is in units or staff and resource not cash;
  static volatile double[] clanFutureFunds = {0., 0., 0., 0., 0.};
  // dues start the first year if this is less than initial worth
  //static double[][] clanStartFutureFundDues = {{8000., 8000., 8000., 8000, 8000.}, {8000, 8000., 8000., 8000., 8000.}};  //place to start future fund dues
  // static double[][] clanStartFutureFundDues = {{4000., 4000., 4000., 4000, 4000.}, {8000, 8000., 8000., 8000., 8000.}};  //place to start future fund dues
  static double[][] clanStartFutureFundDues = {{700., 700., 700., 700, 700.}, {1000, 1000., 1000., 1000., 1000.}};  //place to start future fund dues
  static final double[][] mClanStartFutureFundDues = {{500., 5000.}, {500., 5000.}};
  static volatile double[][] clanStartFutureFundFrac = {{.7, .7, .7, .7, .7}, {.02, .02, .02, .02, .02}};  //frac of bals.curSum()
  static final double[][] mClanStartFutureFundFrac = {{.03, 1.6}, {.01, 1.6}};

  static double[][] emergFundFrac = {{5.0, 5.0, 5.0, 5.0, 5.0}, {5.0, 5.0, 5.0, 5.0, 5.0}}; //frac of bals.curSum();
  static final double[][] mEmergFundFrac = {{10., .01}, {10., .01}};
  static volatile double[][] futureFundFrac = {{.9, .9, .9, .9, .9}, {.8, .8, .8, .8, .8}}; //frac of bals.curSum();
  static final double[][] mFutureFundFrac = {{.001, 1.9}, {.001, 1.9}};
  static volatile double[][] futureFundTransferFrac = {{1.9, 1.9, 1.9, 1.9, 1.9}, {1.9, 1.9, 1.9, 1.9, 1.9}};
  static final double[][] mFutureFundTransferFrac = {{.3, 5.4}, {.3, 5.4}};
  static double[] gameStartSizeRestrictions = {800., 600.};
  static final double[][] mGameStartSizeRestrictions = {{300., 5000.}, {300., 5000.}};
  // double[] effMax = {2., 2.}; // 170309
  // double[][] mEffMax = {{.5, 3.}, {.5, 3.}}; // 170309
  static volatile double[][] rsefficiencyMMin = {{.15}, {.15}};  // .3 => .15
  static final double[][] mRsefficiencyMMin = {{.1, .8}, {.1, .8}};
  static volatile double[][] rsefficiencyGMin = {{.15}, {.15}};
  static final double[][] mRsefficiencyGMin = {{.1, .8}, {.1, .8}};
  static volatile double[][] rsefficiencyMMax = {{2.}, {2.}};
  static final double[][] mRsefficiencyMMax = {{1., 5.}, {1., 5.}};
  static volatile double[][] rsefficiencyGMax = {{2.}, {2.}};
  static final double[][] mRsefficiencyGMax = {{1., 5.}, {1., 5.}}; // 170309
  // [pors] of difficulty control
  // static double[] difficultyByPriorityMin = {.4, .4}; //increase increases costs
  static volatile double[] difficultyByPriorityMin = {.6, .6}; //increase increases costs
  static final double[][] mDifficultyByPriorityMin = {{.2, .6}, {.2, .8}}; //
  static double[] difficultyByPriorityMult = {2.4, 2.4}; // increase increases costs of low priority
  static final double[][] mDifficultyByPriorityMult = {{1., 6.}, {1., 6.}}; //
  static volatile double[][] randFrac = {{0.5}, {0.4}};  // game risk
  static final double[][] mRandFrac = {{.0, .9}, {0., .9}};  // range 0. - .9,.7
  static volatile double[][] clanRisk = {{.5, .4, .6, .3, .5}, {.5, .4, .6, .3, .5}};  //risk taken with assets
  static final double[][] mClanRisk = {{.0, .7}, {.0, .7}};
  static volatile double[][] gameClanRiskMult = {{1.}, {1.}};  // range .0 - .6
  static final double[][] mGameClanRiskMult = {{.0, 1.6}, {.0, 1.6}};  // range .0 - .6
  static volatile double[][] catastrophyUnitReduction = {{5.}, {5.}}; // frac of balance reduction
  static final double[][] mCatastrophyUnitReduction = {{.0, 1.0}, {.0, 1.0}};
  static volatile double[][] catastrophyBonusYears = {{8.}, {12.}};  // 2 - 25
  static final double[][] mCatastrophyBonusYears = {{02., 25.0}, {1., 25.}};  // 
  static volatile double[][] catastrophyBonusYearsBias = {{1.6}, {1.9}}; // adds to the divisor year into bonus units
  static final double[][] mCatastrophyBonusYearsBias = {{.5, 15.}, {.5, 15.}};
  static volatile double[][] catastrophyBonusGrowthValue = {{1.3}, {1.3}};  // frac balances
  static final double[][] mCatastrophyBonusGrowthValue = {{.2, 2.}, {.2, .7}};
  static volatile double[][] catastrophyBonusDecayMultSumSectors = {{.00005}, {.00005}};
  static final double[][] mCatastrophyBonusDecayMultSumSectors = {{.00002, .0002}, {.00005, .0002}};
  static volatile double[][] catastrophyManualsMultSumKnowledge = {{0.}, {25.}};//  .5 -10.
  static final double[][] mCatastrophyManualsMultSumKnowledge = {{0., .0}, {.5, 50.}};//  .5 -10

  Dimension screenSize;
  static volatile int screenHeight = -2, screenWidth = -2, myHeight = -2, myWidth = -2, myH2 = -2, myW2 = -2;
  static volatile int table2W = -2, table2H = -2;
  static volatile int myW3 = -2;
  //16,9:1920,1080  1600,900  1280,720  800,450  .5625
  //16,12: 1680,1050  1024,768  800,600 .75
  //            1280,1024  .8
  static volatile double[][] screenW = {{1200.}};
  static final double[][] mScreenW = {{800.}, {1920.}};
  static volatile double[][] panelW = {{1920.}};
  static final double[][] mPanelW = {{800.}, {1920.}};
  static volatile double[][] panelH = {{1080.}};
  static final double[][] mPanelH = {{600.}, {1080,}};
  static volatile double[][] tableW = {{1280.}};
  static final double[][] mTableW = {{800.}, {1920.}};
  static volatile double[][] tableH = {{1080.}};

  static volatile int allGameErrMax = 40;  //EM.allGameErrMax
  static volatile int allGameErrCnt = 0;
  static volatile int gameErrMax = 30;
  static volatile int gameErrCnt = 0;
  static volatile int yearErrMax = 0; // was 20
  static volatile int yearErrCnt = 0;
  static volatile boolean didMore = false;
  static volatile String errLine = "";
  static volatile String prevLine = "";
  static volatile String addlErr = "";
  static volatile String wasHere = "";
  static volatile String wasHere2 = "";
  static volatile String wasHere3 = "";
  static volatile String wasHere4 = "";
  static volatile String wasHere5 = "";
  static volatile String wasHere6 = "";
  static volatile String wasHere7 = "";
  static volatile String wasHere8 = "";
  static volatile long twh = 0, twh1 = 0, twh2 = 0, twh3 = 0, twh4 = 0, twh5 = 0, twh6 = 0, twh7 = 0, twh8 = 0;
  static volatile int year = -1;  // year of StarTrader, updated in StarTrader runYear;
  static volatile int keepHistsByYear[] = {99, 4, 2};

  /**
   * clear all of the wasHere and other extra saved lines
   *
   */
  static void clearWH() {
    errLine = "";
    prevLine = "";
    addlErr = "";
    wasHere = "";
    wasHere2 = "";
    wasHere3 = "";
    wasHere4 = "";
    wasHere5 = "";
    wasHere6 = "";
    wasHere7 = "";
    wasHere8 = "";
  }

  /**
   * instantiate another EM, set static eE and static eM = new EM
   *
   * @param d pointer to E
   * @param f pointer to StarTrader;
   */
  EM(E aE, StarTrader aST) {
    eE = aE;
    st = aST;
    eM = this;
    myEM = this;
  }

  static StringWriter sw;
  static PrintWriter pw;
  static String firstStack = "", secondStack = "", thirdStack = "", fourthStack = "";
  /**
   * initialize eM read the existing keeps, close that and open as
   *
   */
  int rende3 = 700;

  void aInit() {
    try {
      String dateString = MYDATEFORMAT.format(new Date());
      String rOut = "New Game V" + StarTrader.versionText + " " + dateString + "\r\n";
      sw = new StringWriter();
      pw = new PrintWriter(sw);
      Econ.nowThread = Thread.currentThread().getName(); // goes into Static Econ
      // define each of the first dimension of res or stats values
      resS = new String[rende3][]; //space for desc, comment
      resV = new double[rende3][][][];
      resI = new long[rende3][][][];
      defRes();
      runVals();
      System.out.println("++++counts at init EM doVal vvend=" + vvend + ", doRes rend4=" + rende4 + ", assiged doRes arrays rende3=" + rende3 + " +++++++");
      doReadKeepVals();
      if (bKeepr != null) {
        bKeepr.close(); //close reading keep
      }
      // bRemember = Files.newBufferedWriter(REMEMBER, CHARSET);     
      //  bRemember.write(rOut,0,rOut.length());
      bKeep = Files.newBufferedWriter(KEEP, CHARSET, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
      bKeep.write(rOut, 0, rOut.length());
      keepBuffered = true;

    } catch (Exception | Error ex) {
      firstStack = secondStack + "";
      ex.printStackTrace(pw);
      secondStack = sw.toString();
      ex.printStackTrace(System.err);
      flushes();
      System.err.println("aInit Error " + new Date().toString() + " " + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName() + ", addlErr=" + eM.addlErr + andMore());
      System.exit(-17);
      fatalError = true;

      // ex.printStackTrace(System.err);
      st.setFatalError();
    }
  }

  /**
   * determine of a string does not exist or is empty
   *
   * @param a string to be tested
   * @return true if string does not exist or is of length 0
   */
  static Boolean isEmpty(String a) {
    return a == null || a.isEmpty();
  }
  ;
  
   /**
   * determine of a string does not exist or is empty
   *
   * @param a string to be tested
   * @return true if string exists or is gt length 0
   */
  static Boolean notEmpty(String a) {
    return a != null &&  !a.isEmpty();
  }
  ;
  protected static String curThread = "";

  /**
   * list the stack of each active thread
   *
   * @return return string
   */
  protected static String threadsStacks() {
    String ret = "\n----THs---- Start ThreadStacks by " + Thread.currentThread().getName() + since() + sinceDoYear() +"\n" + mem() + lfe() ;
    Thread[] tarray = new Thread[10];
    Thread.enumerate(tarray);
    for (Thread th : tarray) {
      if (th != null) {
        //   th.dumpStack();
        StackTraceElement[] stk = th.getStackTrace();
        // Map<Thread,StackTraceElement[]> tMap = Thread.getAllStackTraces();
        // for(StackTraceElement[] val:tMap.values()){
        ret += ("\nthreadStacks thread=" + th.getName() + " P" + th.getPriority() + " L" + stk.length + "\n");
        for (StackTraceElement elem : stk) {
          ret += ("at " + elem.getClassName() + "." + elem.getMethodName() + "(" + elem.getFileName() + ":" + elem.getLineNumber() + ")" + "\n");
          //ret += elem.toString();
        }
         ret += "\n****end ThreadsStacks****\n";
        // System.err.println(ret);
      }
    }
    return ret;
  }

  /**
   * printAddMore() then set didMore=false;
   *
   */
  static void printAddMore() {
    System.out.println("----PAM---" + andMore());
    didMore = false;
  }

  /**
   * list possible string of memory strings
   *
   * @return the string
   */
  public static String listMore() {
    String ret
            = (isEmpty(prevLine) ? "" : " PL::" + prevLine + "\n")
            + (isEmpty(errLine) ? "" : " EL::" + errLine + "\n")
            + (isEmpty(addlErr) ? "" : " AE::" + addlErr + "\n")
            + (isEmpty(wasHere) ? "" : " WH1::" + (twh1 > 0 ? " T" + (StarTrader.startYear - twh1) + ":" : "") + wasHere + "\n")
            + (isEmpty(wasHere2) ? "" : " WH2::" + (twh2 > 0 ? " T" + (StarTrader.startYear - twh2) + ":" : "") + wasHere2 + "\n")
            + (isEmpty(wasHere3) ? "" : " WH3::" + (twh3 > 0 ? " T" + (StarTrader.startYear - twh3) + ":" : "") + wasHere3 + "\n")
            + (isEmpty(wasHere4) ? "" : " WH4::" + (twh4 > 0 ? " T" + (StarTrader.startYear - twh4) + ":" : "") + wasHere4 + "\n")
            + (isEmpty(wasHere5) ? "" : " WH5::" + (twh5 > 0 ? " T" + (StarTrader.startYear - twh5) + ":" : "") + wasHere5 + "\n")
            + (isEmpty(wasHere6) ? "" : " WH6::" + (twh6 > 0 ? " T" + (StarTrader.startYear - twh6) + ":" : "") + wasHere6 + "\n")
            + (isEmpty(wasHere7) ? "" : " WH7::" + (twh7 > 0 ? " T" + (StarTrader.startYear - twh7) + ":" : "") + wasHere3 + "\n")
            + (isEmpty(wasHere8) ? "" : " WH8::" + (twh8 > 0 ? " T" + (StarTrader.startYear - twh8) + ":" : "") + wasHere8 + "\n");
    return ret;
  }

  /**
   * possibly add some extra information lines to an error report
   *
   * @return the possible lines
   */
  protected static String andMore() {
    //System.err.println();
    //threadsStacks();
    String rrr = "";
    String rtn = (isEmpty(addlErr) && isEmpty(wasHere) && isEmpty(wasHere2) && isEmpty(wasHere3) && isEmpty(wasHere4) && isEmpty(wasHere5) && isEmpty(wasHere6) && isEmpty(wasHere7) && isEmpty(wasHere8) && isEmpty(prevLine) && isEmpty(firstStack) && isEmpty(secondStack) && isEmpty(thirdStack) && isEmpty(fourthStack) ? "" : "\n");
    rtn += "\n" + lfe();
    if (didMore) {
      return rtn += "\n======didMore===== stop already did andMore";
    } else {
      didMore = true;
      rtn += ", thread=" + (curThread = Thread.currentThread().getName()) + ", Ty=" + ((new Date()).getTime() - StarTrader.startYear)
              + (isEmpty(firstStack) ? "" : " STK1::" + firstStack + "\n")
              + (isEmpty(secondStack) ? "" : " STK2:: " + secondStack + "\n")
              + (isEmpty(thirdStack) ? "" : " STK3::" + thirdStack + "\n")
              + (isEmpty(fourthStack) ? "" : " STK4::" + fourthStack + "\n")
              + listMore();
      if (false) {
        rrr = (isEmpty(prevLine) ? "" : " PL::" + prevLine + "\n")
                + (isEmpty(errLine) ? "" : " EL::" + errLine + "\n")
                + (isEmpty(addlErr) ? "" : " AE::" + addlErr + "\n")
                + (isEmpty(wasHere) ? "" : " WH1::" + (twh1 > 0 ? " T" + (StarTrader.startYear - twh1) + ":" : "") + wasHere + "\n")
                + (isEmpty(wasHere2) ? "" : " WH2::" + (twh2 > 0 ? " T" + (StarTrader.startYear - twh2) + ":" : "") + wasHere2 + "\n")
                + (isEmpty(wasHere3) ? "" : " WH3::" + (twh3 > 0 ? " T" + (StarTrader.startYear - twh3) + ":" : "") + wasHere3 + "\n")
                + (isEmpty(wasHere4) ? "" : " WH4::" + (twh4 > 0 ? " T" + (StarTrader.startYear - twh4) + ":" : "") + wasHere4 + "\n")
                + (isEmpty(wasHere5) ? "" : " WH5::" + (twh5 > 0 ? " T" + (StarTrader.startYear - twh5) + ":" : "") + wasHere5 + "\n")
                + (isEmpty(wasHere6) ? "" : " WH6::" + (twh6 > 0 ? " T" + (StarTrader.startYear - twh6) + ":" : "") + wasHere6 + "\n")
                + (isEmpty(wasHere7) ? "" : " WH7::" + (twh7 > 0 ? " T" + (StarTrader.startYear - twh7) + ":" : "") + wasHere3 + "\n")
                + (isEmpty(wasHere8) ? "" : " WH8::" + (twh8 > 0 ? " T" + (StarTrader.startYear - twh8) + ":" : "") + wasHere8 + "\n");
      }
      rtn += threadsStacks();
      rtn += andStats();
      rtn += andWaiting();
      rtn += andET();
    }
    return rtn;
  }

  /**
   * possibly add some extra information lines to an error report
   *
   * @return the possible lines
   */
  protected static String addMore() {
    return andMore();
  }

  /**
   * possibly add some stats information lines to an error report
   *
   * @return the possible lines
   */
  static String andStats() {
    String rtn = "";
    for (int ii = 0; ii < lStatsWaitList; ii++) {
      rtn += (statsWaitList[ii] == null || statsWaitList[ii].isEmpty() ? "+" : statsWaitList[ii].length() < 5 ? ">>" + statsWaitList[ii] + "<< " : " :>>" + statsWaitList[ii] + "<<<\n");
    }
    return rtn;
  } // andStats

  /**
   * possibly add some waiting information lines to an error report
   *
   * @return the possible lines
   */
  static String andWaiting() {
    String rtn = "";
    for (int ii = 0; ii < Econ.lImWaitingList; ii++) {
      rtn += Econ.imWaitingList[ii] == null || Econ.imWaitingList[ii].isEmpty() ? "" : " :" + Econ.imWaitingList[ii] + "\n";
    }
    return rtn;
  }

  /**
   * possibly add some waiting information lines to an error report
   *
   * @return the possible lines
   */
  static String andET() {
    String rtn = "";
    for (int ii = 0; ii < Econ.lETList; ii++) {
      rtn += Econ.sETList[ii] == null || Econ.sETList[ii].isEmpty() ? "" : " :" + Econ.sETList[ii] + "\n";
    }
    return rtn;
  }

  static String eo = "none";
  /** pause for a long time
   * 
   */
  protected static void pauses(){
    try {
    for(int rep=0; rep < 2000;rep++){
      Thread.sleep(1000);
    }
    } catch (Exception | Error ex) { // print and ignore this error
      firstStack = secondStack + "";
      ex.printStackTrace(pw);
      secondStack = sw.toString();
      System.err.println("Ignore " + eo + " pauses() error " + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName() + ", addlErr=" + eM.addlErr + andMore());
    }
  }

  /**
   * flush System.out and System.err ignoring any errors
   *
   */
  protected static void flushes() {
    try {
      eo = "out";
      System.out.flush();
      System.out.flush();
      System.out.flush();
      System.out.flush();
      eo = "err";
      System.err.flush();
      System.err.flush();
      eo = "keep";
      if (keepBuffered) {
        bKeep.flush();
        keepBuffered = false;
      }
      // Thread.sleep(5000); //wait 5 seconds for printouts
      //   if(rememberBuffered) {bRemember.flush(); rememberBuffered = false; }
    } catch (Exception | Error ex) { // print and ignore this error
      firstStack = secondStack + "";
      ex.printStackTrace(pw);
      secondStack = sw.toString();
      System.err.println("Ignore " + eo + " flush() error " + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName() + ", addlErr=" + eM.addlErr + andMore());
    }
  }

  /**
   * flush System.out and System.err ignoring any errors
   *
   */
  protected static void waitFlushes() {
    try {
      eo = "out";
      System.out.flush();
      System.out.flush();
      System.out.flush();
      System.out.flush();
      eo = "err";
      System.err.flush();
      System.err.flush();
      eo = "keep";
      if (keepBuffered) {
        bKeep.flush();
        keepBuffered = false;
      }
      Thread.sleep(5000); //wait 5 seconds for printouts
    } catch (Exception | Error ex) { // print and ignore this error
      firstStack = secondStack + "";
      ex.printStackTrace(pw);
      secondStack = sw.toString();
      System.err.println("waitFlushes Ignore " + eo + " flush() error " + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName() + ", addlErr=" + eM.addlErr + andMore());
    }
  }

  /**
   * issue the MyErr line, stack and then throw
   *
   * @param aLine the line of output
   */
  protected static void doMyErr(String aLine) {
    //  System.err.println(aLine + andMore()); //later
    new Throwable().printStackTrace(pw); // later
    secondStack = sw.toString();
    if(!dfe())System.err.println("-----NWER-----doMyErr setting newError");
    newError = true;
    System.err.println(tError =("\n------DMER----doMyError thread=" + Thread.currentThread().getName() + " " + aLine + andMore()));
    st.setFatalError(st.redish); // should do exit
    flushes();
    pauses();
  //  System.exit(-11);
    //throw new MyErr(Econ.nowName + " " + Econ.nowThread + " " + aLine);
  }

  static long startTime;

  /**
   * get seconds since starting job
   *
   * @return seconds format ssss.mmm
   */
  protected static String sinceStartTime() {
    long now = (new Date()).getTime();
    double nu = now - startTime;
    return " " + mf(nu * .001);
  }

  static long runYearsTime;

  /**
   * get seconds since oldTime
   *
   * @param oldTime original time milli seconds
   * @return seconds ssss.mmm
   */
  protected static String since(long oldTime) {
    long now = (new Date()).getTime();
    double nu = now - oldTime;
    return " " + mf(nu * .001);
  }

  /**
   * get seconds since oldTime
   *
   * @param desc description of the time
   * @param oldTime original time milli seconds
   * @return seconds ssss.mmm
   */
  protected static String since(String desc, long oldTime) {
    long now = (new Date()).getTime();
    double nu = now - oldTime;
    return " " + desc + "=" + mf(nu * .001);
  }

  /**
   * return the seconds since start of StarTrader
   *
   * @return seconds nnn.mmm
   */
  public static String since() {
    return " since game start" + since(StarTrader.startTime);
  }

  /**
   * get seconds since runYears
   *
   * @return seconds ssss.mmm
   */
  protected static String sinceRunYear() {
    return since(runYearsTime);
  }

  static long doYearTime;

  /**
   * get seconds since the last doYear
   *
   * @return seconds ssss.mmm
   */
  protected static String sinceDoYear() {
    return since(doYearTime);
  }

  static long gigMem = 1000000000L, totMem = 0, freeMem = 0, usedMem = 0, maxMem = 0;
  static double gmem = .000000001;
  static String prGigMem = "";
  static Runtime runtime = Runtime.getRuntime();

  /**
   * find out approximately how much more memory is available for this job
   *
   * @return bytes available
   */
  public static long getAvailMemory() {
    long ret = 0;
    long maxMem = runtime.maxMemory();
    long allocated = maxMem - runtime.freeMemory();
    return ret = maxMem - allocated;
  }

  /**
   * get the percent of used/max memory
   *
   * @return percent used memory
   */
  public static double getPercentUsedMemory() {
    long ret = 0;
    double used = (runtime.maxMemory() - runtime.freeMemory()) / runtime.maxMemory();
    return used * 100.;
  }

  /**
   * get string of tot, used, free, max memory
   *
   * @return avail megabytes + percent used
   */
  public static String mem() {
    totMem = runtime.totalMemory();
    freeMem = runtime.freeMemory();
    maxMem = runtime.maxMemory();
    usedMem = totMem - freeMem;
   double tmem = (double) totMem / gigMem, fmem = (double) freeMem / gigMem, umem = (double) usedMem / gigMem;
    double mmem = (double) maxMem / gigMem;
    double gmax = (double) maxMem * gmem;
    String ret = " Gmem "  + " max" + mf(mmem) + " gmax" + mf(gmax) + " tot" + mf(tmem) + " used" + mf(umem) + " free" + mf(fmem) ;
    return ret;
  }

  /**
   * print memory facts for this program
   *
   */
  public static void printMem3() {
    // runtime.gc(); // garbage collect
    totMem = runtime.totalMemory();
    freeMem = runtime.freeMemory();
    maxMem = runtime.maxMemory();
    usedMem = totMem - freeMem;
    double tmem = (double) totMem * gmem, fmem = (double) freeMem * gmem, umem = (double) usedMem * gmem;
    double mmem = (double) maxMem * gmem;
    //System.out.println("");
    prGigMem = " Game Memory " + StarTrader.stateStringNames[StarTrader.stateConst] + " year=" + eM.year + " Gigs total=" + EM.mf(tmem) + " max" + mf(mmem) + " used=" + EM.mf(umem) + " free=" + EM.mf(fmem) + " used%max" + getPercentUsedMemory();
    System.out.println("----PM----" + since() + prGigMem + "<<<<<<\n");
  }

  /**
   * make a new copy of EM mot finished or used
   *
   * @param name
   * @param title
   * @param oldEM
   * @param aE
   * @param ast
   * @return
   */
  EM newCopy(String name, String title, EM oldEM, E aE, StarTrader ast) {
    EM rtn = new EM(eE, st);
    Econ tmpEcon;
    for (int iEcons = 0; iEcons < oldEM.econs.size(); iEcons++) {
      rtn.econs.add(tmpEcon = oldEM.econs.get(iEcons).newCopy(oldEM, rtn, aE, ast));
    }
    // much more
    return rtn;
  }

  //for Assets.AssetsYr.Trades
  // [pors][clan] multiply strategic sums
  //static NumberFormat dFrac = NumberFormat.getNumberInstance();
  //static NumberFormat whole = NumberFormat.getNumberInstance();
  static private NumberFormat dFrac = NumberFormat.getNumberInstance();
  static private NumberFormat whole = NumberFormat.getNumberInstance();
  static private NumberFormat exp = new DecimalFormat("0.####E0");

  static public int dfN = 2;

  /**
   * format the value of an int
   *
   * @param v the int value to convert to a string
   * @return string of the value
   */
  static public String mf(int v) {
    return v + ""; // force v into a string
  }

  /**
   * format the Double value to a 9 char String
   *
   * @param desc description of format
   * @param v value to format
   * @return desc + mf(v)
   */
  static public String mf(String desc, Double v) {
    return " " + desc + mf(v);
  }

  /**
   * format the Double value to a 7 char String
   *
   *
   * @param desc description of format
   * @param v value to format
   * @return desc + mf(v)
   */
  static public String mf2(String desc, Double v) {
    return " " + desc + mf2(v);
  }

  /**
   * format the Double value to a 9 char string
   *
   * @param v the input value
   * @return value as a string
   */
  static public String mf(Double v) {
    if (v.isNaN()) {
      return "# " + v;
    }
    // infinite returns inf sign
    NumberFormat dFrac = NumberFormat.getNumberInstance();
    NumberFormat whole = NumberFormat.getNumberInstance();
    NumberFormat exp = new DecimalFormat("0.###E0");
    if (v > -999999. && (v % 1 > E.NZERO) && v < 999999. && (v % 1 < E.PZERO)) {  //very close to zero remainder
      return whole.format(v);
    }
    if (v == .0 || v == -0) {  // actual zero
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(1);
      return dFrac.format(v);
    } else if ((v > -999999. && v < -.001) || (v > .001 && v < 999999.)) {
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(3);
      return dFrac.format(v);
    } else if ((v > -9999999. && v < -.001) || (v > .001 && v < 9999999.)) {
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(2);
      return dFrac.format(v);
    } else if ((v > -99999999. && v < -.001) || (v > .001 && v < 99999999.)) {
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(1);
      return "w" + dFrac.format(v);
    } else if ((v > -999999999. && v < -.001) || (v > .001 && v < 999999999.)) {
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(0);
      return dFrac.format(v);
    } else if ((v > -.001 && v < -.0000001) || (v > .0000001 && v < .001)) {
      dFrac.setMinimumFractionDigits(0);
      dFrac.setMaximumFractionDigits(7);
      return dFrac.format(v);
    } else {
      return exp.format(v);
    } 
  }

  /**
   * format the value to a 7 character string
   *
   * @param v input value
   * @return value as a string
   */
  static public String mf2(Double v) {
    if (v.isNaN()) {
      return "# " + v;
    }
    // infinite returns inf sign
    if (!E.outputLess) {  // ignore the need for less than 9 char out
      return mf(v);
    } else {
      NumberFormat dFrac = NumberFormat.getNumberInstance();
      NumberFormat exp = new DecimalFormat("0.###E0");
      if (v == .0 || v == -0) {
        dFrac.setMinimumFractionDigits(0);
        dFrac.setMaximumFractionDigits(1);
        return dFrac.format(v);
      } else if ((v > -.001 && v < -.000001) || (v > .0000001 && v < .001)) {
        dFrac.setMinimumFractionDigits(0);
        dFrac.setMaximumFractionDigits(6);
        return dFrac.format(v);
      } else if ((v > -99999. && v < -.001) || (v > .001 && v < 99999.)) {
        dFrac.setMinimumFractionDigits(0);
        dFrac.setMaximumFractionDigits(2);
        return dFrac.format(v);
      } else if ((v > -999999. && v < -.001) || (v > .001 && v < 999999.)) {
        dFrac.setMinimumFractionDigits(0);
        dFrac.setMaximumFractionDigits(2);
        return dFrac.format(v);
      } else if ((v > -9999999. && v < -.001) || (v > .001 && v < 9999999.)) {
        dFrac.setMinimumFractionDigits(0);
        dFrac.setMaximumFractionDigits(0);
        return dFrac.format(v);
      } else {
        return exp.format(v);
      }
    }
  }
  
  /**
   * format the value to a 5 character string and a sign 
   *
   * @param v input value
   * @return value as a string
   */
  static public String mf3(Double v) {
    if (v.isNaN()) {
      return "# " + v;
    }
    // infinite returns inf sign
    if (!E.outputLess) {  // ignore the need for less than 9 char out
      return mf(v);
    } else {
      NumberFormat dFrac = NumberFormat.getNumberInstance();
      NumberFormat exp = new DecimalFormat("0.###E0");
      if (v == .0 || v == -0) {
        dFrac.setMinimumFractionDigits(0);
        dFrac.setMaximumFractionDigits(1);
        return dFrac.format(v);
      } else if ((v > -.001 && v < -.000001) || (v > .0000001 && v < .001)) {
        dFrac.setMinimumFractionDigits(0);
        dFrac.setMaximumFractionDigits(3);
        return dFrac.format(v);
      } else if ((v > -99. && v < -.001) || (v > .001 && v < 99.)) {
        dFrac.setMinimumFractionDigits(0);
        dFrac.setMaximumFractionDigits(2);
        return dFrac.format(v);
      } else if ((v > -999. && v < -.001) || (v > .001 && v < 999.)) {
        dFrac.setMinimumFractionDigits(0);
        dFrac.setMaximumFractionDigits(1);
        return dFrac.format(v);
      } else if ((v > -99999. && v < -.001) || (v > .001 && v < 99999.)) {
        // I hope no .
        dFrac.setMinimumFractionDigits(0);
        dFrac.setMaximumFractionDigits(0);
        return dFrac.format(v);
      } else {
        return exp.format(v);
      }
    }
  }

  /**
   * format the value
   *
   * @param v input value
   * @return value as a string
   */
  protected String df(Double v) {
    return mf(v);
  }

  /**
   * format the value
   *
   * @param v input value
   * @param n ignored
   * @return value as a string
   */
  protected String df(Double v, int n) {
    return mf(v);
  }

  /**
   * return just a whole number, rounded up
   *
   * @param n number to text
   * @return n rounded up
   */
  static public String wh(Double n) {
    whole.setMaximumFractionDigits(0);

    return whole.format(n + .4999999);
  }

  /**
   * return the max of 2 arguments
   *
   * @param a first arg
   * @param b second arg
   * @return max of a or b
   */
  static public double myMax(double a, double b) {
    return a > b ? a : b;
  }

  /**
   * return the max of 3 arguments
   *
   * @param a arg 1
   * @param b arg 2
   * @param c arg 3
   * @return max of arg 1, arg 2 or arg 3
   */
  static public double myMax(double a, double b, double c) {
    return myMax(a, myMax(b, c));
  }

  /**
   * return the max of 4 arguments
   *
   * @param a arg 1
   * @param b arg 2
   * @param c arg 3
   * @param d arg 4
   * @return max of arg 1, arg 2, arg 3, arg 4
   */
  static public double myMax(double a, double b, double c, double d) {
    return myMax(a, myMax(b, myMax(c, d)));
  }

  /**
   * return the least of 2 arguments
   *
   * @param a arg 1
   * @param b arg 2
   * @return the least of the arguments
   */
  static public double myMin(double a, double b) {
    return a < b ? a : b;
  }

  /**
   * return the most of 2 arguments
   *
   * @param a arg 1
   * @param b arg 2
   * @return the greatest argument
   */
  static public int myMax(int a, int b) {
    return a > b ? a : b;
  }

  /**
   * return the most of 3 arguments
   *
   * @param a arg 1
   * @param b arg 2
   * @param c arg 3
   * @return max of arg 1, arg 2 or arg 3
   */
  static public int myMax(int a, int b, int c) {
    return myMax(a, myMax(b, c));
  }

  /**
   * return the least of 2 arguments
   *
   * @param a arg 1
   * @param b arg 2
   * @return the least of the arguments
   */
  static public int myMin(int a, int b) {
    return a < b ? a : b;
  }

  // static int clans = 5; E.lclans
  static int lRow = E.lclans * 2;

  static int lDisp = 10;  // 0-9 = last of display for game or clan
  int gCntr = -1; //counts number of game doVals
  int cCntr = -1; //counts number of clan doVal s
  // gStart,cStart are arrays of tab game page start points in dovals valD, valI valS
  int gStart[] = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
  int cStart[] = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1}; // start c displays
  int gPntr = 0;  //vv value of current start of game gamemaster display of gStart
  int cPntr = 0;  //vv value of current start of game clanmaster display of cStart
  // int vgc = 0; // game cpde game(vone,vtwo) or clan (vten)
  static int vone = 1;  // only one value difficulty -game {n}
  static int v1 = vone;
  static int vtwo = 2; // p and s values  -- game {n,n}
  static int v2 = vtwo;
  static int vthree = 3; // [][] reference with one value {{n}}
  static int v11 = vthree;
  static int vfour = 4; // [][] reference with 2 values [pors] {{m},{n}}
  static int v21 = vfour;
  static int vfive = 5; // [] with 5 values
  static int vseven = 72; // [7] [lsecs][pors]
  static int v72 = vseven; // unused
  static int vten = 10; // p and s clan values -- clan values
  static int v25 = vten; // [5 clans][2 p|s]
  static int v725 = 725; // unused [7 lsecs][2 p|s][5 clans];
  static int v162 = 162; // unused[grades][pors] staff/guests grades
  static int v4 = 0;  // counts doVal entries
  /**
   * valI [vv][modeC][sliderC][prevSliderC][prev2SliderC][prev3SliderC] above
   * all SliderC columns are [pors][clan] valI[23][0][2][1] master = valI[23] =
   * {{{25},{33}}} valI[24][0][2][5] clans =
   * valI[24]{{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}},{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}}};
   * //valI[24][0][4][] ;* valI[25][7][2][5] sectors= valI[25]
   * {{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}},{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}},{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}},{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}},{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}},{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}},{{1.,2.,3.,4.,5.},{1.2,2.2,3.2,4.2,5.6}}}};
   */
  static int lvals = 200;
  static volatile int valI[][][][] = new int[lvals][][][];
  static int modeC = 0; // gc in valI
  static int sevenC = 1;  //unused index into a 7 sector array
  // static int divByC = 2;  // unused number of entry7 to divide by
  static int sliderC = modeC + 2; //2 [][] slider values of valD[vv][gameAddrC];
  //static int sliderC = modeC;// [][] active real values of valD[vv][modeC][p|s}[clan]
  // valI[vv][sliderC][p!s][1|2] slider values for V1,2,3,4
  // vali[vv][sliderC][p|s][0-d] slider values for v10
  static int prevSliderC = sliderC + 1; //3 [][] previous slider values
  static int prev2SliderC = prevSliderC + 1; //4 prevprev slider val
  static int prev3SliderC = prev2SliderC + 1; //5 prevprevprev slider values
  static int prevOriginalC = prev3SliderC; //5
  static final int sliderLow = 0;  // lowest value of slider
  static final double sliderExtent = 100.;// 0 - 99
  static final double invSliderExtent = 1. / sliderExtent;//.01
  static int gameAddrC = 0;  // valD[rn][0 gameAddrC]
  static int gameLim = 1; // valD[rn][1 gameLim]  lims[pors]{{vLowLim},{vHighLim}}
  static int vLowLim = 0;
  static int vHighLim = 1;
  static int dPrevRealC = 2; // original value of vaddr
  static int vDesc = 0;  // part of valS
  static int vDetail = 1; // valS
  static int vMore = 2; // valS
  static volatile String valS[][] = new String[lvals][]; // second column [desc,detail]
  /**
   * [vv][column][pors][clan]
   */
  static volatile double valD[][][][] = new double[lvals][][][];
  // eventually column = modeC,p

  /**
   * references of Environments being logged
   */
  static volatile Econ[] logEnvirn = new Econ[2];
  /**
   * references the history ArrayList in each Econ
   */
  static volatile ArrayList<History>[] hists = new ArrayList[2];
  static volatile boolean fatalError = false;
  static volatile boolean newError = false;
  static volatile boolean stopExe = false;

  /**
   * determine if a fatalError exists
   *
   * @return true if EM.fatalError, EM.newError or StarTrader.fatalError
   */
  static boolean dfe() {
    return newError || fatalError || stopExe|| StarTrader.fatalError || StarTrader.doStop;
  }

  /**
   * list fatal Errors if they are found
   *
   * @return found EM.newError or found EM.fatalError or found
   * StarTrader.fatalError
   */
  static String efe() {
    return (newError ? " found EM.newError " : " no newE ") + (fatalError ? " found EM.fatalError" : " no EM.fatalE") + (stopExe ? " found EM.stopExe" : " no EM.stopExe") + (StarTrader.fatalError ? " found StarTrader.fatalError" : " no StarTrader.fatalE");
  }
  /**
   * list fatal Errors if they are found
   *
   * @return any found error
   */
  static String lfe() {
    return "---ListERRs---" + (newError ? " new" : "") + (fatalError ? " fatal" : "") + (stopExe ? " stop" : "") + (StarTrader.fatalError ? " STfatal" : "") + (StarTrader.doStop? " doStop":"") + " ---endErrs---\n";
  }

  /**
   * short listing of fatal errors
   *
   * @return found EM.newError nE or found EM.fatalError efEW or found
   * StarTrader.fatalError sfE or no errors !nE !efE !sfE
   */
  static String sfe() {
    return (newError ? " nE " : " !nE ") + (fatalError ? " efE" : " !efE") + (StarTrader.fatalError ? " sfE" : " !sfE");
  }

  // priority settings of sectors for new planets and ships
  static double[][] uLifePriAdj = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  static double[][] uStrucPriAdj = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  static double[][] uEnergyPriAdj = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  static double[][] uPropelPriAdj = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  static double[][] uDefensePriAdj = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  static double[][] uGovPriAdj = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  static double[][] uColonistsPriAdj = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  static double[][][] userPriorityAdjustment = {uLifePriAdj, uStrucPriAdj, uEnergyPriAdj, uPropelPriAdj, uDefensePriAdj, uGovPriAdj, uColonistsPriAdj};
  static double[][] mUserPriorityAdjustment = {{.01, .15}, {.01, .15}};
  static final double[] oldNominalPriorities = {23, 21, 2, 3, 5, 6, 7};
  // slider 0-24(<2.0):random, 25-49(<3.0): current goods of searched: 
// slider 50-100 (3.->5.)select by trade history
  static double[][] tradeEconSearchType = {{2.1, 2.1, 2.1, 2.1, 2.1}, {1.6, 2.6, 2.6, 3.6, 3.6}};
  static final double[][] mTradeEconSearchType = {{1., 5.}, {1., 5.}};
  static double[][] priorityLims = {{1.5, 21.}, {2.5, 21}};//min 1.5*.5=.75, max 21*1.5=31.5
  static double[][] prioritySetMult = {{2.5}, {2.5}}; //picked by gameMaster
  static final double[][] mPrioritySetMult = {{1., 3.}, {1., 3.}};
  // midPrioritySetMult = 2.5
  // pors values
  static final double[] midPrioritySetMult = {(mPrioritySetMult[0][0] + 3. * mPrioritySetMult[0][1]) / 4., (mPrioritySetMult[1][0] + 3. * mPrioritySetMult[1][1]) / 4.}; //1+3*3 /4 = 2.5 ratio = .75
  static double[] uLifeNomPri = {23, 23};
  static double[] uStrucNomPri = {21, 21};
  static double[] uEnergyNomPri = {2, 2};
  static double[] uPropelNomPri = {3, 3};
  static double[] uDefenseNomPri = {5, 5};
  static double[] uGovNomPri = {6, 6};
  static double[] uColonistsNomPri = {7, 7};
  static double[][] nomPriAdjustment = {uLifeNomPri, uStrucNomPri, uEnergyNomPri, uPropelNomPri, uDefenseNomPri, uGovNomPri, uColonistsNomPri};
  static double[][] mNomPriAdjustment = {{1., 25.}, {1., 25.}};
  //double[] prioritiesRandomMult = {7., 6., 2., 2., 2., 3., 3.5};
  static double[][] uLifePriRanMult = {{7., 7., 7., 7., 7.}, {7., 7., 7., 7., 7.}};
  static double[][] uStrucPriRanMult = {{6., 6., 6., 6., 6.}, {6., 6., 6., 6., 6.}};
  static double[][] uEnergyPriRanMult = {{2., 2., 2., 2., 2.}, {2., 2., 2., 2., 2.}};
  static double[][] uPropelPriRanMult = {{2., 2., 2., 2., 2.}, {2., 2., 2., 2., 2.}};
  static double[][] uDefensePriRanMult = {{2., 2., 2., 2., 2.}, {2., 2., 2., 2., 2.}};
  static double[][] uGovPriRanMult = {{3., 3., 3., 3., 3.}, {3., 3., 3., 3., 3.}};
  static double[][] uColonistsPriRanMult = {{3.5, 3.5, 3.5, 3.5, 3.5}, {3.5, 3.5, 3.5, 3.5, 3.5}};
  static double[][][] userPriorityRanMult = {uLifePriRanMult, uStrucPriRanMult, uEnergyPriRanMult, uPropelPriRanMult, uDefensePriRanMult, uGovPriRanMult, uColonistsPriRanMult}; // 7,2,5
  static double[][] mUserPriorityRanMult = {{.01, .15}, {.01, .15}};
  static double[] uPrioritiesRandomMult = {7., 6., 2., 2., 2., 3., 3.5};
  // users adjust priority random additions
  static double[][] priorityRandAdditions = {{1., 1., 1., 1., 1.}, {1., 1., 1., 1., 1.}};
  static double[][] mPriorityRandAdditions = {{.3, 2.}, {.3, 2.}};
  static double[][] manualEfficiencyMult = {{.02}, {.02}}; // .01 - .08
  static double[][] mManualEfficiencyMult = {{.01, .09}, {.01, 2.}}; // .01 - .08
  static double[][] gRGrowthMult1 = {{.55}, {.55}}; // higher growth .03 - .1
  static double[][] mGRGrowthMult1 = {{.03, 1.4}, {.01, 1.4}}; // higher growth .03 - .1
  static double[][] gRGrowthMult2 = {{.8}, {.8}}; // lower growth .01 - .06;
  static double[][] mGRGrowthMult2 = {{.01, 1.6}, {.01, 1.6}};
  // freq .2 means chance for today is .2 or 1 in 5years, .333 = 1 in 3 years
  // goal freq from 1 in 2yrs to 1 in 10 yrs  per econ
  static double[][] userCatastrophyFreq = {{0.2, .3, .2, .55, .25}, {0.2, .3, .2, .55, .25}};
  static double[][] mUserCatastrophyFreq = {{.0, .65}, {.0, .65}};
  // value 1.5 means  mult user value by 1.5 so .2 * 1.5= .3 about 1 in 3 yrs
  // remember there are also random multipliers
  static double[][] gameUserCatastrophyMult = {{.3}, {.25}};
  static double[][] mGameUserCatastrophyMult = {{.0,.65}, {.0,.65}};

  static final double[][] mGuestWorthBias = {{.2, 1.5}, {.2, 1.5}};
  static final double[][] mScoreMult = {{0., 1.}, {0., 1.}};
  static final double[][] mNegScoreMult = {{0., -1.}, {0., -1.}};
  static final double[][] mNegPluScoreMult = {{-1., 1.0}, {-1.0, 1.0}};
  static final double[][] mPlusNegScoreMult = {{1.0, -1.0}, {1.0, -1.0}};
  static double[][] wLiveWorthScore = {{.01}};
  static double[][] iLiveWorthScore = {{.01}};
  static double[][] iBothCreateScore = {{.01}};
  static double[][] wYearTradeV = {{.01}};
  static double[][] wYearTradeI = {{.01}};
  static double[][] iNumberDied = {{.01}};
  static double[][] wGiven = {{.8}};
  static double[][] wGiven2 = {{.8}};
  static double[][] wGenerous = {{-.8}};
  static double[][] iGiven = {{.7}};
  static double[][] iDeadLost = {{.01}};
  static double[][] wDeadLost = {{.01}};

  // multiply table cargo costs by cargoBias when calculating Maint Travel Growth Req cargo costs
  static double[] guestWorthBias = {1.};
  static double[] cargoWorthBias = {1.}; // reservs have the same value as working
  static double[] resourceWorth = {1.};
  static double[] staffWorth = {1.};
  static double[][] worthBias = {resourceWorth, cargoWorthBias, staffWorth, guestWorthBias};
  static final double[][] mCargoWorthBias = {{.2, 1.5}, {.2, 1.5}}; //no changeable
  /**
   * multipliers for annual costs 3/10/27 Required assets should be 2 to 3 times
   * the annual cost Resource maintenance includes maintenance for each sector
   * and should be around .2 to .3 of the total resources (including staff of
   * resources)(life of 4 to 8 years). Staff Annual costs should probably be
   * about 10% of staff (8-15years) religion is not a separate sector, but it is
   * part of defense (against disease, disaster etc.) government (influences how
   * well people work together, col[onists] because it supports colonist
   * collective life. guests cost about 30% of staff, they use no working
   * resources. they cost as much to transport cargo costs about .1 because it
   * uses sheltered space, transport cost the same as resource planet guests are
   * unemployed workers, not youth, babies or seniors all of whom in some way
   * are working transmuting or repurposing resource (magic or politics) is very
   * costly in resources and staff, but may be the only solution to allow
   * growth, or fix health problems
   *
   *
   * staff costs should about match resource per unit costs raw growth should
   * average 2.5 of all balance with penalty at 50 percent difficulty
   */
  static int res = 0;
  static int stf = 1;
  static double[] pa = {5.};  // planet mult 3/10/17 3. -> 5.,181008->11 181213 5.
  static double[] sa = {.25}; //ship mult 3/10/17 .4 -> .25
  static double[] rb = {1.};
  static double[] sb = {1.};
  static final double[][] mcb = {{.1, .95}, {.1, .95}};
  static double[] cb = {.3, .3};  // cargo cost this fraction of resource costs p,s
  static double[] gb = cb;  // guest cost this fraction of staff costs
  static double[] rcsgMult = {rb[0], cb[0], sb[0], gb[0]};
  //[p|s][r|c|s|g]
  double econMult[][] = {{.5, .15, .5, .15}, {.25, .075, .25, .075}};

  static double[][] multEconsUnused(double pa, double sa, double cb, double gb) {
    int m = 0, n = 0;
    double dd[][] = new double[2][];
    for (m = 0; m < 2; m++) {
      dd[m] = new double[4];
      for (n = 0; n < 4; n++) {
        dd[m][n] = 0.;

      }
    }
    return dd;
  }
  double aa[] = {pa[0], sa[0]};

  // the following is {{planet r,c,s,g},{ship r,c,s,g} using the above numbers
  //double ps[][] = {{pa[0], pa[0] * cb[0], pa[0], pa[0] * gb[0]}, {sa[0], sa[0] * cb[0], sa[0], sa[0] * gb[0]}};
  double ps1[] = {1., 1., 1., 1.};
  double ps[][] = {ps1, ps1};

  /**
   * sysBias, bias to cost, work by sIx
   */
  public double[] sysBias = {1., cb[0], 1., gb[0]};
  //public double[] xwBias = {1., 1., 1., 1.}; // not really used

//  static double growthFactor = .5;  // reduces growth after meeting req
//  static double healthFactor = .45; // reduces health after meeting req
  static double mReqAdjPRes1[] = {1.5, .7};
  static double gReqAdjPRes1[] = {1.5, .7};
  static double mCstAdjPRes1[] = {.8, .2};
  static double gCstAdjPRes1[] = {.2, .1};
  static double tCstAdjPRes1[] = {.13, .1,};

  double mReqAdjPStf1[] = {1.5, .7};
  double gReqAdjPStf1[] = {1.5, .7};
  double mCstAdjPStf1[] = {.8, .2};
  double gCstAdjPStf1[] = {.2, .1};
  double tCstAdjPStf1[] = {.13, .1,};

  double mReqAdjSRes1[] = {1.5, .7};
  double gReqAdjSRes1[] = {1.5, .7};
  double mCstAdjSRes1[] = {.8, .2};
  double gCstAdjSRes1[] = {.2, .1};
  ;
  double tCstAdjSRes1[] = {.13, .1,};

  double mReqAdjSStf1[] = {1.5, .7};
  double gReqAdjSStf1[] = {1.5, .7};
  double mCstAdjSStf1[] = {.8, .2};
  double gCstAdjSStf1[] = {.2, .1};
  double tCstAdjSStf1[] = {.13, .1,};

  // this assumes [r or staff cost][p or ship][ rc(r) or sg(staff) ]
  double maintReqAdj1[][][] = {{mReqAdjPRes1, mReqAdjSRes1}, {mReqAdjPStf1, mReqAdjSStf1}};
  double growthReqAdj1[][][] = {{gReqAdjPRes1, gReqAdjSRes1}, {gReqAdjPStf1, gReqAdjSStf1}};
  double maintCostAdj1[][][] = {{mCstAdjPRes1, mCstAdjSRes1}, {mCstAdjPStf1, mCstAdjSStf1}};
  double growthCostAdj1[][][] = {{gCstAdjPRes1, gCstAdjSRes1}, {gCstAdjPStf1, gCstAdjSStf1}};
  double travelCostAdj1[][][] = {{tCstAdjPRes1, tCstAdjSRes1}, {tCstAdjPStf1, tCstAdjSStf1}};
  //[reqM reqG m,t,g][r or s][p or s][rc sg]
  double rs4[][][][] = {maintReqAdj1, growthReqAdj1, maintCostAdj1, travelCostAdj1, growthCostAdj1};
  // a second set of options
  double rs4a[][][][]; //[5 reqM reqG m t g][2 r s][2 p s][4 rcsg]
  // double rs[][][]; //[5 reqM reqG m t g][2 r s][4 rcsg]
  int maintReqTabRow[] = {0, 9, 7, 8};
  int growthReqTabRow[] = {0, 9, 7, 8};
  int maintCostTabRow[] = {0, 0, 7, 7};
  int growthCostTabRow[] = {0, 0, 7, 7};
  int travelCostTabRow[] = {0, 0, 7, 7};

  static double[] multReqMaintC = {1.5, .5}; // mult ReqM costs p,s
  static final double[][] mmult5Ctbl = {{.2, 2.2}, {.2, 2.2}}; // limits all 5
  static double[] multReqGrowthC = multReqMaintC;
  static double[] multMaintC = multReqMaintC;
  static double[] multTravC = multReqMaintC;
  static double[] multGrowthC = multReqMaintC;

  /**
   * get the fraction of cum thisYearRCSG/initialRCSG*2
   *
   * @param clan The clan for which this is evaluated
   * @return an array planet,star fractions
   */
  double[] getRCSGGrowFrac(int clan) {
    double rcsgSI = getCurCumPorsClanAve(INITRCSG, ICUM, 1, E.S, E.S + 1, clan, clan + 1);
    double rcsgSL = getCurCumPorsClanAve(LIVERCSG, ICUM, 1, E.S, E.S + 1, clan, clan + 1);
    double rcsgPI = getCurCumPorsClanAve(INITRCSG, ICUM, 1, E.P, E.P + 1, clan, clan + 1);
    double rcsgPL = getCurCumPorsClanAve(LIVERCSG, ICUM, 1, E.P, E.P + 1, clan, clan + 1);
    double[] rcsgGrowFracRtn = {rcsgSL / rcsgSI * 2., rcsgPL / rcsgPI * 2.};
    return rcsgGrowFracRtn;
  }
  double[] rcsgGrowFrac = {1., 1.};
  double mult5Ctbl[][] = {multReqMaintC, multReqGrowthC, multMaintC, multTravC, multGrowthC};
  //double mab1[] = {1.3, .23}; // resource costs planet,ship
  //double mab1[] = {.6,.6}; // resource costs planet,ship
  double mab1[] = {.6, .13}; // resource c planet ship
  // double mac1[] = {.6,.6}; // staff costs planet ship 
  //double mac1[] = {1.3, .23}; // staff costs planet ship 
  double mac1[] = {.6, .13}; // staff costs, planet, ship
  double mabc[][] = {mab1, mac1}; //r or s, p or s
  static double mmab1[][] = {{.01, 2.}, {.01, 2.1}}; // resource costs planet,ship
  static double[][] mmab2 = {{.05, 1.9}, {.05, 1.9}};
  static double mmac1[][] = {{.01, 3.}, {.01, 3.}};
  double mab2[] = {.9, .9}; // resource, staff cost
  static double[][] mmac2 = {{.1, 2.6}, {.1, 2.6}};
  double mac2[] = {.5, 1.8}; //planet or ship costs
  double mad[] = {1., 1.}; //rc costs, sg costs
  // multiplier of difPercent in makClanRS
  static double vdifMult = 0.035; // was 0.025,0.05,0.075
  // multiply the rs4 above by the above maa to mad

  /**
   * OBSOLETE make a multiplyer for CashFlow.calcRawCosts, creates a table rs
   * that modifies each cost uses EM variables: difficultyPercent rcsgGrow
   * mult5Ctbl
   *
   *
   * @param rs4 a sub tabel of cost multipliers, unused
   * @param mult5Ctbl a table used for costs
   * @param ps unused
   * @return the rs[][][][][] table
   */
  double[][][][] badmakeRS(double[][][][] rs4, double[][] mult5Ctbl, double[][] ps) {
    rs4a = new double[5][][][];//[5 reqM reqG m t g][2 r s][2 p s][4 rcsg][5 clan]
    //make the table
    for (int aa = 0; aa < 5; aa++) { //type
      rs4a[aa] = new double[2][][];
      for (int ab = 0; ab < 2; ab++) { //
        rs4a[aa][ab] = new double[2][];
        for (int ac = 0; ac < 2; ac++) {
          rs4a[aa][ab][ac] = new double[4];
        }
      }
    }
    String stss = ">>>>>>>>>>>>>>>>>>>>>>>>>>>\n";
    String strs = "", strs4 = "", stdif = "", stm5t = "", stmabc = "";
    String staa[] = {"reqMaint ", "reqGro ", "maint, ", "travel, ", "growth, "};
    String stabrs4[] = {"rs4_r ", "rs4_s "};
    String stabm5t[] = {"m5t_r ", "m5t_s "};
    String stabdif[] = {"dif_r ", "difr_s "};
    String stabmabc[] = {"mabc_r ", "mabc_s "};
    String stabrs[] = {"rs_r  ", "rs_s "};
    String stac[] = {"plnt ", "ship "};
    String stad[] = {"r=", "c=", "s=", "q= "};
    double vrs = 0., vrs4 = 0., vdif = 0., vm5t = 0., vmabc = 0., vrcsg = 0.;
    //now set the table elements
    for (int aa = 0; aa < 5; aa++) { // typegrReq
      for (int ab = 0; ab < 2; ab++) { // res or staff
        strs += staa[aa] + stabrs[ab];
        strs4 += stabrs4[ab];
        stdif += stabdif[ab];
        stm5t += stabm5t[ab];
        stmabc += stabmabc[ab];
        for (int ac = 0; ac < 2; ac++) { // planet or ship
          strs += stac[ac];
          strs4 += stac[ac];
          stdif += stac[ac];
          stm5t += stac[ac];
          stmabc += stac[ac];
          for (int ad = 0; ad < 4; ad++) {  // rcsg
            /*
            rs[aa][ab][ac][ad] = 1.;  // finding out of range
            double xx = rs4[aa][ab][ac][(int)ad/2];
            double xy = ps[ac][ad];
            double x1 = mult5Ctbl[aa][ac];
            double x2 = mabc[ab][ac];
             */
            vrs = rs4a[aa][ab][ac][ad]
                    = // add difficultyPercent as a cost factor 50% = 1. mult
                    //(vdif = difficultyPercent[ac]) * .1  *
                    (vdif = difficultyPercent[0] * 0.0125)
                    // (vdif = difficultyPercent[0] * 0.0990)
                    // reduce costs so that final rcsg = 2*init rcsg
                    * (vrcsg = rcsgGrowFrac[ac])
                    * (vrs4 = rs4[aa][ab][ac][(int) ad / 2])
                    * (vm5t = mult5Ctbl[aa][ac])
                    * //mabc[ab][ac] * ps[ac][ad];
                    (vmabc = mabc[ab][ac]);
            if (E.debugLogsOut) {
              strs += mf(vrs) + " ";
              strs4 += mf(vrs4) + " ";
              stdif += mf(vdif) + " ";
              stm5t += mf(vm5t) + " ";
              stmabc += mf(vmabc) + " ";

            }
          } //ad
          // stss += "\n"; // start another line
        }//ac
      }//ab
      if (E.debugLogsOut) {
        stss += strs + "\n";
        stss += strs4 + "\n";
        stss += stdif + "\n";
        stss += stm5t + "\n";
        stss += stmabc + "\n";

        strs = " ";
        strs4 = " ";
        stdif = " ";
        stm5t = " ";
        stmabc = " ";
      }
    } // aa 
    if (E.debugLogsOut) {
      System.out.println(stss + "<<<<<<<<<<<<<<<<<"); // 12 lines
    }
    return rs4a;
  }

  double vFracSum = 0.;
  static boolean AlwaysMakeRS = true;

  /**
   * obsolete make a multiplyer for CashFlow.calcRawCosts, creates a table rs
   * that modifies each cost uses EM variables: difficultyPercent rcsgGrow
   * mult5Ctbl and uses getCurCumPorsClanAve(INITRCSG, ICUM, 1, pors, pors + 1,
   * clan,clan+1);
   *
   * @param rs4 a sub tabel of cost multipliers, unused
   * @param mult5Ctbl a table used for costs
   * @param ps unused
   * @return the rs[][][][][] table
   */
  double[][][][][] notmakeClanRS(double[][][][] rs4, double[][] mult5Ctbl, double[][] ps) {
    double[][][][][] rs = new double[5][][][][];;// make [5 reqM reqG m t g][2 r s][2 p s][4 rcsg][5 clan]
    try {
      double vinit = 0., vlive = 0., vfrac = 0.;
      int aa, ab, ac, ad, ae;

      double clanSum[][] = new double[2][]; // { pors,clan}
      for (aa = 0; aa < 5; aa++) { //type reqM reqG m t g
        rs[aa] = new double[2][][][];
        for (ab = 0; ab < 2; ab++) { // r s 
          rs[aa][ab] = new double[2][][];
          for (ac = 0; ac < 2; ac++) { // p s
            rs[aa][ab][ac] = new double[4][];//rcsg
            clanSum[ac] = new double[5];
            for (ad = 0; ad < 4; ad++) {  //rcsg
              rs[aa][ab][ac][ad] = new double[5];
              for (ae = 0; ae < E.LCLANS; ae++) {
                rs[aa][ab][ac][ad][ae] = 0.;
              }
            }
          }
        }
      }
      String stss = ">>>>>>>>>>>>>>>>>>>>>>>>>>>\n";
      String strs = "", strs4 = "", stdif = "", stm5t = "", stmabc = "";
      String staa[] = {"reqMaint ", "reqGro ", "maint, ", "travel, ", "growth, "};
      String stabrs4[] = {"rs4_r ", "rs4_s "};
      String stabm5t[] = {"m5t_r ", "m5t_s "};
      String stabdif[] = {"dif_r ", "difr_s "};
      String stabmabc[] = {"mabc_r ", "mabc_s "};
      String stabrs[] = {"rs_r  ", "rs_s "};
      String stac[] = {"plnt ", "ship "};
      String stad[] = {"r=", "c=", "s=", "q= "};
      double vrs = 0., vrs4 = 0., vdif = 0., vm5t = 0., vmabc = 0., vrcsg = 0.;
      //now set the table elements
      for (aa = 0; aa < 5; aa++) { // typegrReq
        for (ab = 0; ab < 2; ab++) { // res or staff
          strs += staa[aa] + stabrs[ab];
          strs4 += stabrs4[ab];
          stdif += stabdif[ab];
          stm5t += stabm5t[ab];
          stmabc += stabmabc[ab];
          for (ac = 0; ac < 2; ac++) { // planet or ship
            strs += stac[ac];
            strs4 += stac[ac];
            stdif += stac[ac];
            stm5t += stac[ac];
            stmabc += stac[ac];
            for (ad = 0; ad < 4; ad++) {  // rcsg
              for (ae = 0; ae < E.LCLANS; ae++) { //clans
                /*
            rs[aa][ab][ac][ad] = 1.;  // finding out of range
            double xx = rs4[aa][ab][ac][(int)ad/2];
            double xy = ps[ac][ad];
            double x1 = mult5Ctbl[aa][ac];
            double x2 = mabc[ab][ac];
                 */
                vinit = getCurCumPorsClanAve(INITRCSG, ICUM, 1, ac, ac + 1, ae, ae + 1);
                vlive = getCurCumPorsClanAve(LIVERCSG, ICUM, 1, ac, ac + 1, ae, ae + 1);
                vfrac = vinit > 0. ? vlive / (vinit * 3.0) : 1.0;
                vFracSum += vfrac;
                prevLine = " before rsae aa=" + aa + " ab=" + ab + " ac=" + ac + " ad=" + ad + " ae=" + ae;

                double rsaa[][][][] = rs[aa]; // test for null pointer??
                double rsab[][][] = rsaa[ab];
                // int irsab = rsab[ac].length;
                prevLine += " rsab[ac].length=" + rsab[ac].length;
                double rsac[][] = rsab[ac];
                double rsad[] = rsac[ad];
                double rsae = rsad[ae];
                prevLine += "rsad[ae]=" + mf(rsad[ae]);
                vrs = rs[aa][ab][ac][ad][ae]
                        = // add difficultyPercent as a cost factor 50% = 1. mult
                        //(vdif = difficultyPercent[ac]) * .1  *
                        (vdif = difficultyPercent[0] * 0.0125)
                        // (vdif = difficultyPercent[0] * 0.0990)
                        // reduce costs so that final rcsg = 2*init rcsg
                        * (vrcsg = rcsgGrowFrac[ac])
                        * (vrs4 = rs4[aa][ab][ac][(int) ad / 2])
                        * (vm5t = mult5Ctbl[aa][ac])
                        * //mabc[ab][ac] * ps[ac][ad];
                        (vmabc = mabc[ab][ac])
                        * //[pors][clan]
                        (vfrac);
                if (E.debugLogsOut) {
                  strs += mf(vrs) + " ";
                  strs4 += mf(vrs4) + " ";
                  stdif += mf(vdif) + " ";
                  stm5t += mf(vm5t) + " ";
                  stmabc += mf(vmabc) + " ";

                } //logs out
              } //ae
              // stss += "\n"; // start another line
            }//ad
          }//ac
        }// ab
        if (E.debugRsOut) {
          stss += strs + "\n";
          stss += strs4 + "\n";
          stss += stdif + "\n";
          stss += stm5t + "\n";
          stss += stmabc + "\n";

          strs = " ";
          strs4 = " ";
          stdif = " ";
          stm5t = " ";
          stmabc = " ";
        }
      } // aa 
      if (E.debugRsOut) {
        System.out.println(stss + "<<<<<<<<<<<<<<<<<"); // 12 lines
      }
      return rs;

    } catch (trade.WasFatalError ex) {
      ex.printStackTrace(pw);
      thirdStack = sw.toString();
      flushes();
      System.err.println(curEconName + " " + Econ.nowThread + " Caught Exception " + ex.toString() + "  cause=" + ex.getCause() + " message=" + ex.getMessage() + andMore());
//      ex.printStackTrace(System.err);
      System.exit(-5);
    } catch (Exception | Error ex) {
      firstStack = secondStack + "";
      ex.printStackTrace(pw);
      secondStack = sw.toString();
      newError = true;
      System.err.println("makeClanRs" + curEconName + " " + Thread.currentThread().getName() + " Caught Exception " + ex.toString() + "  cause=" + ex.getCause() + " message=" + ex.getMessage() + Thread.currentThread().getName() + andMore());
//      ex.printStackTrace(System.err);
      st.setFatalError(); // throws WasFatalError
      System.exit(-14);
      throw new WasFatalError(curEconName + " " + Econ.nowThread + " Caught Exception " + ex.toString() + "  cause=" + ex.getCause() + " message=" + ex.getMessage() + andMore());

    }
    return rs;
  }

  /**
   * make a multiplyer for CashFlow.calcRawCosts, creates a table rs that
   * modifies each cost by resource or staff, planet or ship, which SubAsset
   * [rcsg] uses EM variables: difficultyPercent rcsgGrow ways cost are set by
   * game master mult5Ctbl and uses getCurCumPorsClanAve(INITRCSG, ICUM, 1,
   * pors, pors + 1, clan,clan+1) to increase costs as sum of clan rcsg
   * increases;
   *
   * @param rs4 a sub tabel of cost multipliers, unused
   * @param mult5Ctbl a table used for costs
   * @param ec the Econ for this call
   * @return the rs[][][] table [5cost types].[Resource|staff],[SubAssets rcsg]
   */
  double[][][] makeClanRS(double[][][][] rs4, double[][] mult5Ctbl, Econ ec) {
    double[][][] rs = new double[5][][];;// make [5 reqM reqG m t g][2 r s][4 rcsg]
    clearWH();
    int pors = ec.getPors();
    int clan = ec.getClan();
    double hiLoMult = ec.getHiLoMult();
    try {
      double vinit = 0., vlive = 0., vfrac = 0.;
      int aa, ab, ac, ac2;

      double clanSum[][] = new double[2][]; // { pors,clan}
      for (aa = 0; aa < 5; aa++) { //type reqM reqG m t g
        rs[aa] = new double[2][];
        for (ab = 0; ab < 2; ab++) { // r s 
          rs[aa][ab] = new double[4];
          for (ac = 0; ac < 4; ac++) { //rcsg
            rs[aa][ab][ac] = 0.;
          }//ac
        } // ab
      } // aa
      //  vinit = getCurCumPorsClanAve(INITRCSG, ICUM, 1, pors,pors + 1, clan,clan+1);
      //         vlive = getCurCumPorsClanAve(LIVERCSG, ICUM, 1, pors,pors + 1, clan,clan+1);
      // vfrac =  vinit > 0.?vlive/(vinit*3.0) : 1.0/3.;
      vfrac = 1.0; // lets ignore this
      vFracSum += vfrac;
      String stss = ">>>>>>>>>>>>>>>>>>>>>>>>>>>\n";
      String strs = "", strs4 = "", stdif = "", stm5t = "", stmabc = "";
      String staa[] = {"M ", "G ", "m, ", "t, ", "gr, "};
      String stabrs4[] = {"rs4_r ", "rs4_s "};
      String stabm5t[] = {"m5t_r ", "m5t_s "};
      String stabdif[] = {"dif_r ", "dif_s "};
      String stabmabc[] = {"mabc_r ", "mabc_s "};
      String stabrs[] = {"rs4_r  ", "rs4_s "};
      String spors[] = {"planet ", "ship "};
      String srcsg[] = {"r=", "c=", "s=", "q= "};
      double vrs = 0., vrs4 = 0., vdif = 0., vm5t = 0., vmabc = 0., vrcsg = 0.;
      //now set the table elements
      for (aa = 0; aa < 5; aa++) { // typegrReq
        for (ab = 0; ab < 2; ab++) { // res or staff
          strs += spors[pors] + staa[aa] + stabrs[ab];
          strs4 += stabrs4[ab];
          stdif += stabdif[ab];
          stm5t += stabm5t[ab];
          stmabc += stabmabc[ab];
          for (ac = 0; ac < 4; ac++) { // rcsg
            ac2 = (int) (ac / 2);
            //    strs += spors[pors];
            strs4 += srcsg[ac];
            //stdif += spors[pors];
            //stm5t += spors[pors];
            //stmabc += spors[pors];

            /*
            rs[aa][ab][ac] = 1.;  // finding out of range
            double xx = rs4[aa][ab][(int)ad/2];
            double xy = ps[pors][ad];
            double x1 = mult5Ctbl[aa][pors];
            double x2 = mabc[ab][pors];
             */
            prevLine = " before rsac year=" + EM.year + ",  aa=" + aa + " ab=" + ab + " ac=" + ac + " Ty" + (new Date().getTime() - st.startYear) + " th=" + Thread.currentThread().getName();
            if (eM.curEcon != null) {
              wasHere += ", name=" + eM.curEcon.getName() + ", age=" + eM.curEcon.getAge();
            }
            double rsaa[][] = rs[aa]; // test for null pointer??
            double rsab[] = rsaa[ab];
            // int irsab = rsab[ac].length;
            // wasHere += " rsab[ac].length=" + rsab[ac].length;
            double rsac = rsab[ac];
            // vrs = rs[aa][ab][ac];
            prevLine = " rsac=" + mf(rsac) + ",  aa=" + aa + " ab=" + ab + " ac=" + ac + " Ty" + (new Date().getTime() - st.startYear) + " th=" + Thread.currentThread().getName();
            //vrs4a = rs4[aa][ab][pors][ac2];
            vrs = 0;
            vrs
                    = rs[aa][ab][ac]
                    = // add difficultyPercent as a cost factor 50% = 1. mult
                    //(vdif = difficultyPercent[0]) * .1 * hiLoMult
                    //(vdif = difficultyPercent[0]) * .4 * hiLoMult
                    //(vdif = difficultyPercent[0]) * .2 * hiLoMult
                    //(vdif = difficultyPercent[0] * 0.0125)
                    // (vdif = difficultyPercent[0] * 0.0990)
                    // (vdif = difficultyPercent[0] * 0.025)
                    //  (vdif = difficultyPercent[0] * 0.05)
                    (vdif = difficultyPercent[0] * vdifMult)
                    // reduce costs so that final rcsg = 2*init rcsg
                    * (vrcsg = rcsgGrowFrac[pors])
                    * (vrs4 = rs4[aa][ab][pors][ac2])
                    * (vm5t = mult5Ctbl[aa][pors])
                    * //mabc[ab][ac] * ps[ac][ad];
                    (vmabc = mabc[ab][pors])
                    * //[pors][clan]
                    (vfrac);
            prevLine = " vrs=" + mf(vrs) + ",  aa=" + aa + " ab=" + ab + " ac=" + ac + " Ty" + (new Date().getTime() - st.startYear) + " th=" + Thread.currentThread().getName() + ", vdif=" + mf(vdif) + ", vrcsg=" + mf(vrcsg) + ", vrs4=" + mf(vrs4) + ", vm5t=" + mf(vm5t) + ", vmabc=" + mf(vmabc) + ", vfrac=" + mf(vfrac);
            assert vrs > E.PZERO : "vrs zero=" + mf(vrs) + "\n" + wasHere3;
            rs[aa][ab][ac] = vrs;
            if (E.debugLogsOut) {
              strs += mf(vrs) + " ";
              strs4 += mf(vrs4) + " ";
              stdif += mf(vdif) + " ";
              stm5t += mf(vm5t) + " ";
              stmabc += mf(vmabc) + " ";
            }//if
          }//ac
        }// ab
        if (E.debugRsOut) {  //az = 5 lines
          stss += strs + "\n";
          stss += strs4 + "\n";
          stss += stdif + "\n";
          stss += stm5t + "\n";
          stss += stmabc + "\n";

          strs = " ";
          strs4 = " ";
          stdif = " ";
          stm5t = " ";
          stmabc = " ";
        }// if
      } // aa 5
      if (E.debugRsOut) {
        System.out.println(stss + "<<<<<<<<<<<<<<<<<"); // 12 lines
      }
      return rs;

    } catch (trade.WasFatalError ex) {
      EM.newError = true;
      ex.printStackTrace(pw);
      thirdStack = sw.toString();
      flushes();
      System.err.println(curEconName + " " + Thread.currentThread().getName() + " Caught Exception " + ex.toString() + "  cause=" + ex.getCause() + " message=" + ex.getMessage() + andMore());
//      ex.printStackTrace(System.err);
      System.exit(-5);
    } catch (Exception | Error ex) {
      firstStack = secondStack + "";
      ex.printStackTrace(pw);
      secondStack = sw.toString();
      newError = true;
      System.err.println("makeClanRs " + curEconName + " " + Thread.currentThread().getName() + " Caught Exception " + ex.toString() + "  cause=" + ex.getCause() + " message=" + ex.getMessage() + Thread.currentThread().getName() + andMore());
//      ex.printStackTrace(System.err);
      st.setFatalError();
      System.exit(-55);
      throw new WasFatalError(curEconName + " " + Econ.nowThread + " Caught Exception " + ex.toString() + "  cause=" + ex.getCause() + " message=" + ex.getMessage() + andMore());

    }
    return rs;
  }

  /**
   * Calculate cost of cargo and quests for planets and ships
   *
   * @param cb currently .3 of resource for cargo costs
   * @param gb currently .3 of staff for quest costs
   * @return
   */
  static double[][] makePS(double[] cb, double[] gb) {
    double ps0[] = {1., cb[0], 1., gb[0]};
    double ps1[] = {1., cb[1], 1., gb[1]};
    double[][] ps3 = {ps0, ps1};
    return ps3; // p,s  r,c,s,g
  }

  // find the smallest ship frac for this clan
  /**
   * calculate the number of ships per a planet for this clan. Use each of the
   * ship limits to find ships per econ convert that ships per Econ to ships per
   * planet and return it
   *
   * @param kln The clan for this calculation
   * @return The numbers of ships per planet for this clan
   */
  double shipsPerPlanet(int kln) {
    double smallestShipFrac = clanAllShipFrac[E.P][kln] < gameShipFrac[E.P]
            ? clanAllShipFrac[E.P][kln] < clanShipFrac[E.P][kln]
                    ? clanAllShipFrac[E.P][kln] : clanShipFrac[E.P][kln]
            : gameShipFrac[E.P] < clanShipFrac[E.P][kln]
                    ? gameShipFrac[E.P] : clanShipFrac[E.P][kln];
    double shipsPerP = smallestShipFrac / (1. - smallestShipFrac);
    return shipsPerP;
  }

  /**
   * set some values dependent on some values set by StarTrader.getGameValues
   * called in StarTrader after input of gameValues
   */
  void setMoreValues() {
    double[][] ps3 = makePS(cb, gb);
    //makeClanRS(rs4, mult5Ctbl, ps3);
  }
  // the following variables are used to calculate the knowledgeBias which is
  // used to calculate the SEfficiency and REfficiency in CalcReq see CalcReq
  //calcEfficiency
  static double[] knowledgeForPriority = {.40}; //init assign commonknowledge
  static double[] knowledgeByDefault = {.60};  //init assign commonknowledge
  static double[] commonKnowledgeTradeManualFrac = {.10};
  static double[] newKnowledgeTradeManualFrac = {.8};
  static double[] manualTradeManualFrac = {.05};
  static double[] commonKnowledgeDifTradeManualFrac = {.5};
  static double[] manualsMin = {25,};
  static final double[][] mknowledgeForPriority = {{.2, .50}}; //init assign commonknowledge
  static final double[][] mknowledgeByDefault = {{.3, .9}};  //init assign commonknowledge
  static final double[][] mCommonKnowledgeTradeManualFrac = {{.05, .25}};
  static final double[][] mnewKnowledgeTradeManualFrac = {{.5, 1.}};
  static final double[][] mmanualTradeManualFrac = {{.03, .1}};
  static final double[][] mcommonKnowledgeDifTradeManualFrac = {{.3, .8}};
  static double[][] kLearnManuals = {{1.}, {1.}};  // manuals convertable because of created knowledge
  static final double[][] mKLearnManuals = {{.5, 1.5}, {.5, 1.5}};
  static double[][] manualsMaxPercent = {{.4, .4, .4, .4, .4}, {.1, .1, .1, .1, .1}};
  static final double[][] mManualsMaxPercent = {{.1, 1.}, {.01, .3}};

  static final double[][] mNominalKnowledgeForBonus = {{60000., 1900000.}, {60000., 1900000}};
  static double[] nominalKnowledgeForBonus = {900000.};
  static double[] additionalKnowledgeGrowthForBonus = {.05}; // .2=>.05 reduce 200624
  static double[] additionToKnowledgeBiasSqrt = {.6};
  static double[] nominalDistance = {7.};
  static double[] nominalStrategicDif = {3.};
  static double[] knowledgeGrowthPerResearcher = {25.};

  /**
   * ..Growth is for planet [resource,cargo]growth *swork, ship growth
   * [resource,cargo]growth * yearsTravel*resource.balance // public // percent
   * /** adjust second use of priority in growth, fraction of priority to use
   * growth is limited by fertility, availableResource, availableStaff after
   * removal of maintenance and travel costs, multiplied by any poor health
   * costs
   */
  //3/15/15 more staff  public static double[] fracPriorityInGrowth = {.5, .5};  //mult priority in growth calc
  static Double minRand = .1;
  static double[] fracBiasInGrowth = {.22, .22};
  static double[][] mFracBiasInGrowth = {{.02, .9}, {.02, .9}};
  static double[] fracPriorityInGrowth = {.6, .5};  //mult priority in growth calc and percent to frac
  static final double[][] mFracPriorityInGrowth = {{.1, .9}, {.1, .9}};
  static double[] resourceGrowth = {3.7, .002}; // growth per work
  static final double[][] mResourceGrowth = {{.01, 6.}, {0.002, .9}};
  // decay mining cumulative related to each years growth
  static double[] resourceGrowthDecay = {.0006, .0006}; //per unit
  // decay mining cumulative related to each years growth
  static final double[][] mResourceGrowthDecay = {{.00003, .009}, {.00003, .009}};
  static double[] cargoGrowth = {0.000001, .00000001};
  static final double[][] mCargoGrowth = {{0.0000001, 0.00009}, {0.000000001, 0.000009}};
  // cargo decay use resourceGrowthDecay
  static double[] staffGrowth = {3.7, .002}; // growth per work
  static final double[][] mStaffGrowth = {{.01, 6.}, {0.002, .9}};
  static double[] staffGrowthDecay = {.0006, .0006}; //per unit
  static final double[][] mStaffGrowthDecay = {{.00003, .009}, {.00003, .009}};
  static double[] travelGrowth = {.0015, .0025}; // this multiplies against work
  static final double[][] mTravelGrowth = {{.0001, .001}, {.0001, .01}}; //
  static double[] guestsGrowth = {0.000001, .00000001};
  static final double[][] mGuestsGrowth = {{0.0000001, 0.00009}, {0.000000001, 0.0000009}};
  static final double[][][] mRCSGGrowth = {mResourceGrowth, mCargoGrowth, mStaffGrowth, mGuestsGrowth};
  // growth is in terms of staff units, decay is in term of previous yr growth
  static double[][] assetsUnitGrowth = {resourceGrowth, cargoGrowth, staffGrowth, guestsGrowth};
  static double[][] growthDecay = {resourceGrowthDecay, resourceGrowthDecay, staffGrowthDecay, staffGrowthDecay};
  static final double[][] mfracBiasInGrowth = {{.1, .3}, {.1, .3}};
  static final double[][] mfracPriorityInGrowth = {{.3, .7}, {.3, .7}};  //mult priority in growth calc and percent to frac
  static double[][] clanFutureFundEmerg2 = {{.15, .15, .15, .15, .15}, {.15, .15, .15, .15, .15}};
  static final double[][] mClanFutureFundEmerg = {{.01, .3}, {.01, .3}};
  static double[][] clanFutureFundEmerg1 = {{.25, .25, .25, .25, .25}, {.25, .25, .25, .25, .25}};
  static double[][] swapDif = {{3., 3., 3., 3., 3.}, {3., 3., 3., 3., 3.}};
  static final double[][] mSwapDif = {{2., 5.}, {2., 5.}};

  /**
   * (1-health)*penalty -> (1-.5)*.4 => .2, travel cost=>travelCost*(1+.2) if
   * health = 1.5 (1-1.5)*.4 => -.2 travelCost=>travelCost*(1 -.2) reduces cost
   * raw poor health effect = (1. - health) * poorHealthPenalty poorHealthEffect
   * = poorHealthEffectLimit[0] < raw poor health effect <
   * poorHealthEffectLimit[1]
   */
  // [pors]
  double[] poorHealthPenalty = {1., 1.}; // (1.-rqGCfracMin)*poorHealthPenalty
  static final double[][] mPoorHealthPenalty = {{.7, 1.3}, {.7, 1.3}};

  //poorHealthEffectLimits[min,max]
  double[] poorHealthEffectLimitsL = {.5};  // use -poorHealthEffectLimitsL[0]
  static final double[][] mPoorHealthEffectLimitsL = {{.0, .7}};
  double[] poorHealthEffectLimitsH = {.5};  // use poorHealthEffectLimitsH[0]
  static final double[][] mPoorHealthEffectLimitsH = {{.3, .8}};
  double[] moreOfferBias = {.2, .3};
  static final double[][] mMoreOfferBias = {{.05, .4}, {.07, .5}};

  // [low,high][0]
  double[][] poorHealthEffectLimits = {poorHealthEffectLimitsL, poorHealthEffectLimitsH};
  /**
   * Fractions used to evaluate the sum value trades. Critical members get
   * additional criticalFrac added to their value High Critical numbers really
   * must be requested, never offered Offers above the low critical
   *
   */
  static double[][] searchStrategicFrac = {{.5, .5, .5, .5, .5}, {.5, .5, .5, .5, .5}};
  static final double[][] mTradeSCNFracs = {{.1, 1.}, {.1, 1.}};
  static double[][] tradeStrategicFrac = {{.5, .5, .5, .5, .5}, {.5, .5, .5, .5, .5}};
  static double[][] searchCriticalFrac = {{.5, .5, .5, .5, .5}, {.5, .5, .5, .5, .5}};
  // Increase the trade value if the good[n] is critical
  static double[][] tradeCriticalFrac = {{.05, .05, .05, .05, .05}, {.05, .05, .05, .05, .05}};
  static final double[][] mTradeCriticalFrac = {{.01, .5}, {.01, .5}};
  static double[][] tradeManualsFrac = {{.01, .01, .01, .01, .01}, {.01, .01, .01, .01, .01}};
  static final double[][] mTradeMFracs = {{.0, 1.}, {.0, 1.}};
  static double[][] searchCriticalNumber = {{3, 3, 3, 3, 3}, {3, 3, 3, 3, 3}};
  static double[][] tradeCriticalNumber = {{3, 3, 3, 3, 3}, {3, 3, 3, 3, 3}};
  static double[][] searchNominalFrac = {{.1, .1, .1, .1, .1}, {.1, .1, .1, .1, .1}};
  static double[][] tradeNominalFrac = {{.1, .1, .1, .1, .1}, {.1, .1, .1, .1, .1}};
  // at the start of search or trade,use trade as the frac of rc,sg for reserve
  static double[][] searchStartTradeCFrac = {{.7, .7, .7, .7, .7}, {3, .3, .3, .3, .3}};
  static double[][] searchYearBias = {{.05, .05, .05, .05, .05}, {.05, .05, .05, .05, .05}};
  static final double[][] mSearchYearBias = {{.005, .5}, {.005, .5}};
  static double[][] tradeStartTradeCFrac = {{.7, .7, .7, .7, .7}, {.3, .3, .3, .3, .3}};
  static double[][] searchStartTradeGFrac = {{.7, .7, .7, .7, .7}, {.3, .3, .3, .3, .3}};
  static double[][] tradeStartTradeGFrac = {{.7, .7, .7, .7, .7}, {.3, .3, .3, .3, .3}};
  static final double[][] mTradeSearchStart = {{.2, .8}, {.2, .8}};
  static double[][] startSwapsCFrac = {{.3, .3, .3, .3, .3}, {.4, .4, .4, .4, .4}};
  static double[][] startSwapsGFrac = {{.3, .3, .3, .3, .3}, {.4, .4, .4, .4, .4}};
  static final double[][] mStartSwaps = {{.2, .7}, {.15, .8}};
  static double[][] strategicOfferFrac = {{.3, .3, .3, .3, .3}, {.3, .3, .3, .3, .3}};
  // clan value amount of change to c or g for trade reserving c & g
  static double[][] tradeReserveIncFrac = {{.4, .5, .4, .3, .4}, {.5, .4, .5, .4, .5}};
  static final double[][] mTradeReserveIncFrac = {{.02, .6}, {.01, .5}};
  static double[][] mtgWEmergency = {{.1, .1, .1, .1, .1}, {.1, .1, .1, .1, .1}};
  static final double[][] mMtgWEmergency = {{.03, .5}, {.03, .5}};
  static double[][] searchStrategicFrac2 = {{.5, .5, .5, .5, .5}, {.5, .5, .5, .5, .5}};
  static final double[][] mTradeSCNFracs2 = {{.1, 1.}, {.1, 1.}};
  static double[][] tradeStrategicFrac2 = {{1., 1.0, 1.0, 1.0, 1.0}, {1.0, 1.0, 1.0, 1.0, 1.0}};
  static double[][] searchCriticalFrac2 = {{1.5, 1.5, 1.5, 1.5, 1.5}, {1.5, 1.5, 1.5, 1.5, 1.5}};
  static double[][] tradeCriticalFrac2 = {{1.5, 1.5, 1.5, 1.5, 1.5}, {1.5, 1.5, 1.5, 1.5, 1.5}};
  static double[][] tradeManualsFrac2 = {{.1, .1, .1, .1, .1}, {.1, .1, .1, .1, .1}};
  static double[][] mTradeMFracs2 = {{.0, 1.5}, {.0, 1.5}};
  static double[][] searchCriticalNumber2 = {{3, 3, 3, 3, 3}, {3, 3, 3, 3, 3}};
  static double[][] tradeCriticalNumber2 = {{3, 3, 3, 3, 3}, {3, 3, 3, 3, 3}};
  static double[][] searchNominalFrac2 = {{1.0, 1.0, 1.0, 1.0, 1.0}, {1.0, 1.0, 1.0, 1.0, 1.0}};
  static double[][] tradeNominalFrac2 = {{1.0, 1.0, 1.0, 1.0, 1.0}, {1.0, 1.0, 1.0, 1.0, 1.0}};
  // at the start of search or trade,use trade as the frac of rc,sg for reserve
  static double[][] searchStartTradeCFrac2 = {{.7, .7, .7, .7, .7}, {3, .3, .3, .3, .3}};
  static double[][] searchYearBias2 = {{.05, .05, .05, .05, .05}, {.05, .05, .05, .05, .05}};
  static double[][] mSearchYearBias2 = {{.005, .5}, {.005, .5}};
  static double[][] tradeStartTradeCFrac2 = {{.7, .7, .7, .7, .7}, {.3, .3, .3, .3, .3}};
  static double[][] searchStartTradeGFrac2 = {{.7, .7, .7, .7, .7}, {.3, .3, .3, .3, .3}};
  static double[][] tradeStartTradeGFrac2 = {{.7, .7, .7, .7, .7}, {.3, .3, .3, .3, .3}};
  static double[][] startSwapsCFrac2 = {{.3, .3, .3, .3, .3}, {.4, .4, .4, .4, .4}};
  static double[][] startSwapsGFrac2 = {{.3, .3, .3, .3, .3}, {.4, .4, .4, .4, .4}};
  static double[][] strategicOfferFrac2 = {{.3, .3, .3, .3, .3}, {.3, .3, .3, .3, .3}};
  // clan value amount of change to c or g for trade reserving c & g
  static double[][] tradeReserveIncFrac2 = {{.4, .5, .4, .3, .4}, {.5, .4, .5, .4, .5}};
  static final double[][] mTradeReserveIncFrac2 = {{.02, .6}, {.01, .5}};
  static double[][] mtgWEmergency2 = {{1.0, 1.0, 1.0, 1.0, 1.0}, {1.0, 1.0, 1.0, 1.0, 1.0}};
  static final double[][] mMtgWEmergency2 = {{.03, .5}, {.03, .5}};
  static double[][] criticalStrategicRequestsRejectFrac = {{.1, .1, .1, .1, .1}, {.1, .1, .1, .1, .1}};
  static final double[][] mCriticalStrategicRequestsRejectFrac = {{.01, .9}, {.01, .9}};
  static double[][] strategicCurrentNeedsMult = {{-2.15, -2.15, -2.15, -2.15, -2.15}, {-2.15, -2.15, -2.15, -2.15, -2.15}};
  static double[][] strategicFutureNeedsMult = {{-1.05, -1.05, -1.05, -1.05, -1.05}, {-1.05, -1.05, -1.05, -1.05, -1.05}};
  static double[][] strategicEmergencyNeedsMult = {{-2.45, -2.45, -2.45, -2.45, -2.45}, {-2.45, -2.45, -2.45, -2.45, -2.45}};
  static final double[][] mStrategicNeedsMult = {{-1.01, -4, 1}, {-1.01, -4, 1}};
  static double[][] strategicMultMin = {{.05, .05, .05, .05, .05}, {.05, .05, .05, .05, .05}};
  static final double[][] mStrategicMultMin = {{.01, 1.}, {.01, 1.}};

  //[search | trade] [P | S] [clan]
  static final int DOINGSEARCH = 0, DOINGTRADE = 1;
  static double[][][] strategicFracs = {tradeStrategicFrac, tradeStrategicFrac};
  //double[][][] strategicFracs = {searchStrategicFrac, tradeStrategicFrac};
  static double[][][] criticalFracs = {tradeCriticalFrac, tradeCriticalFrac};
  static double[][][] criticalNumbers = {tradeCriticalNumber, tradeCriticalNumber};
  static double[][][] nominalFracs = {tradeNominalFrac, tradeNominalFrac};
  static double[][][] startTradeCFrac = {tradeStartTradeCFrac, tradeStartTradeCFrac};
  static double[][][] startTradeGFrac = {tradeStartTradeGFrac, tradeStartTradeGFrac};

  /**
   * values used in Assets.AssetsYr.Trades values that are not subject to change
   */
  // static double maxStrategicFrac = 10., minStrategicFrac = .03;
  /**
   * years to keep TradeRecords
   */
  static double[][] yearsToKeepTradeRecord = {{12.}, {12.}};
  static final double[][] mYearsToKeepTradeRecord = {{6., 20.}, {6., 20.}};

  //double fava[][] = {{3., 3., 3., 3., 3.}, {2., 2., 2., 2., 2.}, {1., 1., 1., 1., 1.}, {4., 4., 4., 4., 4.}, {5., 5., 5., 5., 5.}};
  static double fav0[] = {3., 3., 3., 3., 3.};
  static double fav1[] = {2., 2., 2., 2., 2.};
  static double fav2[] = {1., 1., 1., 1., 1.};
  static double fav3[] = {4., 4., 4., 4., 4.};
  static double fav4[] = {5., 5., 5., 5., 5.};
  static double fav[][] = {fav0, fav1, fav2, fav3, fav4};
  static final double mfavs[][] = {{0.5, 5.5}};
  // decrease required strategicFrac for fav > 3, increase if < 3
  // ,28 - FavMult*5 == -.2
  // .28 - FavMult*1 == .2
  // clanBias -FavMult*5
  static double clanBias = .28;
  static double favMult = .08;
  static double oClanMult = .5;
  static double randMax = 1.95;
  static double randMin = .1;
  static double randMult = .3;
  // double[][] randFrac = {{0.0}, {0.0}};  // range 0. - .7
  // ssFrac is a special frac for ship to ship trade
  static double[][] ssFrac = {{1.2, 1., .9, 1.1, 1.2}};
  static double mSsFrac[][] = {{.7, 1.4}};
  // [pors][clan]
  // tradeFrac is initial goal of requests/offers
  // ships get much more to survive and grow with planets
  // the fracs get reduced as the trades continue
  static final double mTradeFrac[][] = {{.001, .4}, {.1, 1.4}};
  static double[][] tradeFrac = {{.03, .03, .03, .03, .03}, {.5, .6, .5, .6, .6}, ssFrac[0]};
  // termFrac = (goalTermBias )/(goalTermBias + barterStart - term)
  //    gtb=18 t=18  18/18 = 1;  t=9  18/(18 + 18-9=27) = .6666; t=`0 18/36 = .5
  // related to decrement per term
  static double goalMaxBias[][] = {{1.1}, {1.1}};
  static double goalTermBias[] = {18., 18.};
  static double[] sosTrigger = {1.1, -.1}; // sos = rawFertilities2.min() < sosTrigger
  static double sosfrac[] = {.3, .35};
  static final double msosfrac[][] = {{.2, .4}, {.25, .45}};
  static int barterStart = 18; // initial term for bartering
  static final double[][] mTradeEmergFrac = {{.03, .4}, {.03, .4}};
  // emergency if min rawProspects2 lt tradeEmergFrac
  // [pors][clan]
  static double[][] tradeEmergFrac = {{.2, .2, .2, .1, .3}, {.2, .1, .2, .3, .2}};
  /*
     thFrac = 1.0 - (thReject*EM.rejectBias[pors][clan] + thLost*EM.lostBias[pors][clan] +thAccept)*EM.historyBias[pors][clan];
   */
  static double[][] historyBias = {{.002, .002, .002, .0015, .0015}, {.003, .002, .002, .003, .0035}};
  static final double[][] mHistoryBias = {{.0001, .01}, {.0001, .01}};
  static double[][] rejectBias = {{5., 5., 5., 4., 4.}, {5., 5., 5., 5., 5.}};
  static final double[][] mRejectBias = {{0., 20.}, {0., 20.}};
  static double[][] lostBias = {{5., 5., 5., 4., 4.}, {5., 5., 5., 5., 5.}};
  static double[][] mLostBias = {{0., 20.}, {0., 20.}};
  static boolean trade2HistOutputs = true;
  static int trade1PlanetOverrideShipGoods = 6;
  static int tradePlanetAcceptHigherOffer = 7;
  double[] cntMult = {2.0, 2.5};
  static final double[][] mCntMult = {{1., 5.}, {2., 7.}};
  static double[] maxTries = {2., 3.};
  static final double[][] mIncTriesPTerm = {{.1, 1.1}, {.1, 1.1}};
  static double incTriesPTerm[] = {.4, .5};
  static double[][] mMaxTries = {{1., 5.}, {1., 5.}};
  static double[][] startAvail = {{8., 8., 8., 8., 8.}, {8., 8., 8., 8., 8.}};
  static final double[][] mStartAvail = {{4., 10.}, {4., 10.}};
  static double[][] availFrac = {{.6, .6, .6, .6, .6}, {.6, .6, .6, .6, .6}};
  static final double[][] mAvailFrac = {{.3, .9}, {.3, .9}};
  static double[][] emergFrac = {{.9, .9, .9, .9, .9}, {.9, .9, .9, .9, .9}};
  static final double[][] mEmergFrac = {{.5, .99}, {.5, .99}};
  static double[] availMin = {.33, .33};
  static final double[][] mAvailMin = {{.1, .9}, {.1, .9}};

  /* swaps taken from E, a gradual move to these */
  // penalty in move to earlier position if swapped across resources
  public static int[] sXSwapPenalty = {2, 2};
  // swap to same Resource
  public static int[] sSwapPenalty = {0, 0};
  // trade to the same resource in a different env use sSwapPenalty
  public static int[] sTSwapPenalty = {0, 0};
  // swapPenalty[TR,TX,TT][P, S];
  public static int[][] iSwapPenalty = {sSwapPenalty, sXSwapPenalty, sTSwapPenalty};

  final static public double[] swapTRtoRRcost = {.5, .5};
  final static public double[] swapTRtoCRcost = {.5, .5};
  final static public double[] swapTRtoRScost = {.02, .02};
  final static public double[] swapTRtoCScost = {.02, .02};
  final static public double[] swapTCtoRCcost = {.1, .1};
  final static public double[] swapTCtoCCcost = {.01, .01};
  final static public double[] swapTCtoRGcost = {.02, .02};
  final static public double[] swapTCtoCGcost = {.005, .005};
  final static public double[] swapTStoSScost = {.1, .1};
  final static public double[] swapTStoSRcost = {.5, .5};
  final static public double[] swapTStoGRcost = {.5, .5};
  final static public double[] swapTStoGScost = {.1, .1};
  final static public double[] swapTGtoSCcost = {.5, .5};
  final static public double[] swapTGtoSGcost = {.1, .1};
  final static public double[] swapTGtoGCcost = {.005, .005};
  final static public double[] swapTGtoGGcost = {.1, .1};
  //static double [][] mXferCosts = {{10.,60.},{10,60}};
  public double xferrC = 45.;
  // final static public double[] swapXRtoRRcost = {17., 17.};
  // final static public double[] swapXRtoCRcost = {18., 18.};
  // swapXRtoRRcost, swapXRtoCRcost, swapXStoSRcost, swapXStoGRcost
  // swapXRtoRScost, swapXRtoCScost, swapXStoSScost, swapXStoGScost;
  static double[][] mSwapXRtoRRcost = {{5.0, 35.0}, {5.0, 35.0}};
  double[] swapXRtoRRcost = {18., 18.};
  double[] swapXCtoCCcost = swapXRtoRRcost;
  static double[][] mSwapXRtoCRcost = {{.3, 5.}, {.3, 5.}};
  double[] swapXRtoCRcost = {.5, .5};
  double[] swapXCtoRCcost = swapXRtoCRcost;
  static double[][] mSwapXStoSRcost = {{.03, 5.}, {.03, 5.}};
  public double[] swapXStoSRcost = {.005, .005};
  public double[] swapXStoGRcost = swapXStoSRcost;

  static double[][] mSwapXRtoRScost = {{.3, 15.}, {.3, 15.}};
  double[] swapXRtoRScost = {.5, .5};
  double[] swapXCtoCScost = swapXRtoRScost;
  double[] swapXCtoCGcost = swapXRtoRScost;
  static double[][] mSwapXRtoCScost = {{.03, 5.}, {.03, 5.}};
  double[] swapXRtoCScost = {.05, .05};
  double[] swapXCtoRScost = swapXRtoCScost;
  double[] swapXCtoRGcost = swapXRtoCScost;
  static double[][] mSwapXStoSScost = {{.03, 5.}, {.03, 5.}};
  double[] swapXStoSScost = {.1, .1};
  static double[][] mSwapXSgoGScost = {{.003, .5}, {.003, .5}};
  public double[] swapXStoGScost = {.01, .01};
  public double[] swapXGtoSCcost = swapXStoGScost;
  public double[] swapXGtoSGcost = swapXStoGScost;
  public double[] swapXGtoGCcost = swapXStoGScost;
  public double[] swapXGtoGGcost = swapXStoGScost;
  public double[] swapRtoRRcost = swapXRtoRRcost;
  final static public double[] swapRtoCRcost = {.005, .005};
  public double[] swapRtoRScost = swapXRtoRScost;
  final static public double[] swapRtoCScost = {.002, .002};
  final static public double[] swapCtoRRcost = {.001, .001};
  final static public double[] swapCtoRCcost = swapCtoRRcost;
  public double[] swapCtoCCcost = swapXRtoRRcost;
  final static public double[] swapCtoRScost = {.002, .002};
  final static public double[] swapCtoRGcost = swapCtoRScost;
  public double[] swapCtoCGcost = swapXRtoRScost;
  public double[] swapStoSScost = swapXStoSScost;
  public double[] swapStoSRcost = swapXStoSRcost;
  public double[] swapStoGRcost = swapXStoSRcost;
  final static public double[] swapStoGScost = {.001, .001};
  final static public double[] swapGtoSCcost = {.005, .005};
  final static public double[] swapGtoSScost = {.005, .005};
  final static public double[] swapGtoSGcost = {.001, .001};
  final static public double[] swapGtoGCcost = {.0005, .0005};
  final static public double[] swapGtoSRcost = {.0005, .0005};
  final static public double[] swapGtoGGcost = {.0001, .0001};

  // index [iEl][oEl][pors] costs of trade Rswap
  final static public double[][][] swapRtradeRcost = {{swapTRtoRRcost, swapTRtoCRcost}, {swapTCtoRCcost, swapTCtoCCcost}};

  // index [iEl][oEl][pors] costs of trade Sswap
  final static public double[][][] swapStradeScost = {{swapTStoSScost, swapTStoGScost}, {swapTGtoSGcost, swapTGtoGGcost}};

  // in growth phase, multiply the min positive by the following numbers this
  // should cause some resources to violate a growthReq, and cause either .INCR
  // P=0  planet
  // S=1; ship
  final static public int W = 0; // to working
  final static public String[] Els = {"W", "R"};
  final static public int R = 1; // to reserve
  //  final static public int[] oswpr = {swpMaxAvailTo,swpCargoTo};
  //  final static public int[] oswps = {swpMaxStaffTo,swpGuestsTo};
  //index [iEl][oEl][pors] costs of regular Rswap
  public double[][][] swapRregRcost = {{swapRtoRRcost, swapRtoCRcost}, {swapCtoRCcost, swapCtoCCcost}};
  //index [iEl][oEl][pors] costs of xmute (transmute) Rswap
  // final static public double[][][] swapRtransRcost = {{swapXRtoRRcost, swapXRtoCRcost}, {swapXCtoRCcost, swapXCtoCCcost}};
  // index [iEl][oEl][pors] costs of regular Sswap
  public double[][][] swapSregScost = {{swapStoSScost, swapStoGScost}, {swapGtoSGcost, swapGtoGGcost}};
  // index [iEl][oEl][pors] costs of Xmute Sswap
  // final static public double[][][] swapStransScost = {{swapXStoSScost, swapXStoGScost}, {swapXGtoSGcost, swapXGtoGGcost}};
  int maxEconHist = Econ.keepHist; // Econs later than 5 null hist to save heap space
  int maxClrHist = 200000; // don't clear hist until it reach this number
  static final public int TR = 0;  // regular W R of same resource
  static final public int TX = 1;  // Transmute W R of different resources
  static final public int TT = 2;  // Trade  W R of different environment
  // [TR,TX,TT][iW,iR][oW,oR][P,S]
  // static final public double[][][][] swapRrxtcost = {swapRregRcost, swapRtransRcost, swapRtradeRcost};  
  // res cargo cost
  //  final static public double[][][][] swapSrxtcost = {swapSregScost, swapStransScost, swapStradeScost};  
  // staff guests cost
  static final public int CR = 0;  // class resource
  static final public int CS = 1;  // class staff
  /**
   * [CRes,CStaf][TR,TX,TT][iW,iR][oW,oR][Plan,Ship]
   */
  // static final public double[][][][][] swapcosts = {swapRrxtcost, swapSrxtcost};

  double swapResourcesAveMinMult = .3;  //use AssetsYr inline values
  double swapSubAssetMinSwap = .01;
  double[] minSwapIncrAveMult = {.03, .001};
  double[] minSwapDecrAveMult = {.5, .3};
  double[] minXferAveMult = {.03, .001};

  /**
   * the below fractions are the amount of swaps devoted to the g = growth or
   * fertility with the fertility goal h = health or wellness with the health
   * goal f = future maximizing the future needs gf = grow first ignore any
   * health or future need
   */
  double maxHealth = 1.5;
  double minHealth = 0.0;
  double initHealth = 0.4;
  double initFertility = 0.6;
  // travel on planets are trains, planes, rockets,cars, trucks,boats,bikes ...
  double[][] initTravelYears = {{0.65}, {7.0}}; // default travel if none other
  static double[][] mInitTravelYears = {{0.3, 2.0}, {3., 20.}};
  double maintMinPriority = 1.;
  double growthMinPriority = .5;  // only each limited fertility
  double maxStaffGrowth[] = {900000, 900000};
  double max7Growth[] = {7 * maxStaffGrowth[0], 7 * maxStaffGrowth[1]};
  double max14Growth[] = {14 * maxStaffGrowth[0], 14 * maxStaffGrowth[1]};
  static double mMaxStaffGrowth[][] = {{100000, 99990000}, {100000, 99990000}};
  // [pors][clan]
  double goalResvFrac[][] = {{.1, .1, .1, .1, .1}, {.5, .5, .5, .5, .5}};
  double goalGrowth[][] = {{.6, .6, .6, .6, .6}, {.5, .5, .5, .5, .5}};
  double goalHealth[][] = {{.5, .5, .5, .5, .5}, {.5, .5, .5, .5, .5}};
  static double[][] mRegGoals = {{.05, .95}, {.05, .95}};
  static double[][] mAllGoals = {{.05, .90}, {.01, .9}};
  double emergGrowth[][] = {{.3, .3, .3, .3, .3}, {.3, .3, .3, .3, .3}};
  double emergHealth[][] = {{.3, .3, .3, .3, .3}, {.3, .3, .3, .3, .3}};
  double tradeGrowth[][] = {{.7, .7, .7, .7, .7}, {.8, .9, .8, .9, .9}};
  double tradeHealth[][] = {{.4, .4, .4, .4, .4}, {.7, .8, .6, .8, .8}};
  double offerAddlFrac[] = {.001, .2};
  static double[][] mOfferAddlFrac = {{.0001, .1}, {.001, .4}};
//  double tradeDistance[] = {0.65, 7.};
  double reqGrowthFertilityMinMult[][] = {{.5, .5, .5, .5, .5}, {.5, .5, .5, .5, .5}};
  double emergTradeReserve[][] = {{.9, .9, .9, .9, .9}, {.9, .9, .9, .9, .9}};
  double availTradeReserve[][] = {{.7, .7, .7, .7, .7}, {.7, .7, .7, .7, .7}};
  double futGrowthFrac[][] = {{.7, .8, .7, .8, .7}, {.8, .8, .7, .4, .3}};
  static double mFutGrowthFrac[][] = {{.2, 2.2}, {.2, 2.2}};
  double futGrowthYrMult[][] = {{5., 5., 5., 4., 5.}, {8., 5., 3., 5., 5.}};
  static double[][] mFutGrowthYrMult = {{1.5, 11.5}, {1.5, 11.5}};
  double futtMTGCostsMult[][] = {{2., 2., 4., 4., 3.}, {6., 4., 5., 3., 4.}};
  double[][] mFuttMTGCostsMult = {{.7, 7.}, {.7, 7.}};
  // double growthGoals[][][] = {emergGrowth, goalGrowth, tradeGrowth};
  //double maintGoals[][][] = {emergHealth, goalHealth, tradeHealth};
  // A2Row and A6Row sum the min cnt values
  int minSumCnt = 7;  // for large sum of min's
  int minSum2Cnt = 3;  // for smaller sum of min's
//                 [pors]
  public double[] minProspects = {.1, .1};
  public double[][] mMinProspects = {{.03, .5}, {.03, .5}};
  public double[] maxn = {50., 50.}; // max swaps
  public double[][] mMaxn = {{15., 70.}, {15., 70.}};
  double[] fFrac = {.5, .6};   //future frac
  double[] gFrac = {.5, .5};    // growth frac
  double[] gmFrac = {.85, .85};  //g more from 0
  double[] gfFrac = {.25, .20};   //grow first before health
  double[] geFrac = {.75, 1.1};  // g emerg above this
  double[] nheFrac = {.35, .35}; // not yet health emergency
  double[] hFrac = {.50, .50};   //  health frac
  double[] hmFrac = {.7, .7};  // h more from 0
  double[] heFrac = {.85, .85};// health emergency above this value

  // static double gameMaxRandomSum = .7;
  /**
   * status values input for the game tab
   */
  static int gameClanStatus = 5; // 0-4 regular clans, 5 = gameMaster\
  int gameDisplayNumber[] = {-1, -1, -1, -1, -1, -1};//-1=not set, 0-nn start of current disp in the array of game or clan enums
  int clanisplayNumber[] = {-1, -1, -1, -1, -1, -1};//-1=not set, 0-nn start of current disp in the array of game or clan enums
  static int gamePorS = 0;  // 0=p,1=ship, used in getIval and setIval
  int vv = -1, gc = -2, vFill = 0, lowC = 0, highC = 1, vvend = -1;

  /**
   * values of game where the next display will start
   */
  int prevGameClanStatus = -1;  // not yet set
  int prevGameDisplayNumber[] = {-1, -1, -1, -1, -1, -1};
  int prevClanDisplayNumber[] = {-1, -1, -1, -1, -1, -1};
  String vDetailPrefix = "1.23 2.5=>2.7 4.56";

  /**
   * get a game value that may be either clan or game value, check the length of
   * the arrays to decide whether to us PorS and clan. Use PorS if A.length == 2
   * If A.length == 2 use PorS if A[PorS].length == 5 than use A[PorS][clan]
   *
   * @param A reference to an array holding game or clan values
   * @param PorS specify 0:planet or 1:ship
   * @param clan clan used if A[PorS].length == 5
   * @return
   */
  double getVal(double[][] A, int PorS, int clan) {
    if (A.length == 1) {
      return A[0].length == 5 ? A[0][clan] : A[0][0];
    } else if (A.length == 2) {
      return A[PorS].length == 5 ? A[PorS][clan] : A[PorS][0];
    }
    return 5 / 0.;  // I think infinite not NaN
  }

  /**
   * get a value, either game master or clan value if A.length == 1 this is a
   * master single value if A.length == 2 this is a master PorS value if
   * A.length == 5 this is a clan value for both P and S
   *
   * @param A
   * @param PorS
   * @param clan
   * @return a value
   */
  double getval(double[] A, int PorS, int clan) {
    if (A.length == 1) {
      return A[0];
    } else if (A.length == 2) {
      return A[PorS];
    } else if (A.length == 5) {
      return A[clan];
    } else {
      return 5 / 0.; // infinite
    }
  }

  /**
   * get the multiple of 2 values A B
   *
   * @param A root of first value
   * @param B root of the second value
   * @param PorS planet or ship
   * @param clan clan value of caller
   * @return values for A times B
   */
  double getVal(double[][] A, double[][] B, int PorS, int clan) {
    return getVal(A, PorS, clan) * getVal(B, PorS, clan);
  }

  /**
   * get the multiple of 3 value A B C
   *
   * @param A root of the first value
   * @param B root of the second value
   * @param C root of the third value
   * @param PorS caller planet or ship value
   * @param clan caller clan
   * @return multiple of A * B * C
   */
  double getVal(double[][] A, double[][] B, double[][] C, int PorS, int clan) {
    return getVal(A, PorS, clan) * getVal(B, PorS, clan) * getVal(C, PorS, clan);
  }

  /**
   * doVal with vaddr only a double diff[] = {.5} or {.5,.5} Find gc=vone one
   * value {val}, gc=vtwo {pVal,sVal}
   *
   * @param vdesc title of the input
   * @param vaddr address of the input
   * @param lims limits of the input
   * @param vdetail details about the input
   * @return vv the number of the input in valI,valD,valS
   */
  int doVal(String vdesc, double[] vaddr, double[][] lims, String vdetail) throws IOException {
    int vl = -1;
    if (E.debugSettingsTab) {
      if (vaddr.length > 2 && vaddr.length != 5) {
        doMyErr("doVal {1} illegal length vdesc=" + vdesc + ", vaddr.length=" + vaddr.length);
      }
    }
    gc = (vl = vaddr.length) == 2 ? vtwo : vl == 1 ? vone : vl == 5 ? vfive : 11;
    vv = doVal1(gc, vdesc, lims, vdetail);
    double[][] vacc = {vaddr};
    valD[vv][gameAddrC] = vacc; //valD[vv][0][vaddr] //valD[vv][0][0]{addr0,addr1}
    doVal3(vv);
    return vv;
  }

  /**
   * doVal with vaddr a 7 sector array {0.3} => vone {0.1,0.2} => vtwo {
   * 0.,1.,2.,3.,4.,5.,6.} = vseven not used I think
   *
   * @param vdesc title of the input
   * @param vaddr address of the input
   * @param vindex index into the 7 sector addr
   * @param lims limits of the input
   * @param vdetail details about the input
   * @return vv the number of the input in valI,valD,valS
   */
  int doVal(String vdesc, double[] vaddr, int vindex, double[][] lims, String vdetail) throws IOException {
    if (E.debugSettingsTab) {
      if (vaddr.length > 2 && vaddr.length != 7) {
        doMyErr("doVal {1.1} vdesc=" + vdesc + ", vaddr.length=" + vaddr.length);
      }
    }
    gc = vaddr.length == 1 ? vone : vaddr.length == 2 ? vtwo : vaddr.length == 7 ? vseven : 11;
    vv = doVal1(gc, vdesc, lims, vdetail);
    double[][] vacc = {vaddr}; // {{val0}}  or {{val0,val1}} 
    valD[vv][gameAddrC] = vacc; //valD[vv][vFill] {vaddr} //valD[vv][vFill][] {{addr0,addr1}}
    valI[vv][vFill][vFill][sevenC] = vindex; //valI[vv][0][0][vindex] (0-6)
    doVal3(vv);
    return vv;
  }

  /**
   * doVal with vaddr full double val[][p,s] = {{.5}} or {{.5},{.5}} gc vone
   * val[vv][0]{val}, valD {{val}} gc vtwo val[vv][0][pors] {pVal,sVal}, valD
   * {{pVal,sVal}} gc vthree val[vv][0][val1] {{val}}, valD {{val}} same as vone
   * gc vfour val[vv][pors][val1] {{pVal},{sVal}}. valD {{pVal},{sVal}} gc vfive
   * val[vv][0][val5] {{1,2,3,4,5}}, valD{{1,2,3,4,5}}; gc vten
   * valD[vv][0][pors][val5] {{1,2,3,4,5},{6,7,8,9,10}}
   *
   * @param vdesc title of the input
   * @param vaddr address of the input
   * @param lims limits of the input
   * @param vdetail details about the input
   * @return vv the number of the input in valI,valD,valS
   */
  int doVal(String vdesc, double[][] vaddr, double[][] lims, String vdetail) throws IOException {
    // int v1 = -1, v2 = -1, v3 = -1;
    int v1 = vaddr.length;
    int v2 = vaddr[0].length;
    int v3 = v1 == 2 ? vaddr[1].length : v1 == 3 ? vaddr[1].length : -1;
    if (E.debugSettingsTab) {

      if ((v1 <= 2 && v2 != 1 && v2 != 2 && v2 != 5) || (v1 == 2 && v3 != 1 && v3 != 2 && v3 != 5) || (v1 == 3 && v2 != 5 && v3 != 5)) {
        doMyErr("doVal{2} illegal length vdesc=" + vdesc + ", vaddr.length=" + vaddr.length + ", vaddr[0].length=" + v2 + (v1 == 2 ? "vaddr[1].length =" + v3 : ""));
      }
    }
    // 11 should create an error some where
    gc = v1 == 1 ? v2 == 1 ? vone
            : v2 == 2 ? vtwo
                    : v2 == 5 ? vfive : 11
            : v1 == 2 && v2 == 1 && v3 == 1 ? vfour
                    : v1 == 2 && v2 == 5 && v3 == 5 ? vten
                            : v1 == 3 && v2 == 5 && v3 == 5 ? vten : 11; // specail case
    if (E.debugSettingsTab) {
      if (gc == 11) {
        doMyErr("doval{3} illegal length vdesc=" + vdesc + ", vaddr.length=" + vaddr.length + ", vaddr[0].length=" + v2 + (v1 == 2 ? "vaddr[1].length =" + v3 : "v1!=2"));
      }
    }
    vv = doVal1(gc, vdesc, lims, vdetail);
    valD[vv][gameAddrC] = vaddr; //valD[vv][0][pors][valu]
    doVal3(vv);
    return vv;
  }

  /**
   * sub doVal1 assign the next vv and the initial storage that will be filled
   * in doVal1
   *
   * @param gc the storage type code
   * @param vdesc the title of the storage
   * @param lims the limits of the values
   * @param vdetail the extended details of this input
   * @return vv the current input number
   */
  int doVal1(int gc, String vdesc, double[][] lims, String vdetail) {
    vv = v4++;
    //set up valMap for reading file keep
    if (valMap.containsKey(vdesc)) {
      doMyErr(">>>>>>vdesc=" + vdesc + " already exists, vv=" + vv);
    }
    valMap.put(vdesc, vv);
    valI[vv] = new int[prev3SliderC + 1][][];
    valD[vv] = new double[dPrevRealC + 1][][];
    valD[vv][gameLim] = lims; //valD[vv][1]...
    int[][] val7 = {{-1}}; // unused
    valI[vv][sevenC] = val7; //unused
    if (E.debugSettingsTabOut && E.debugDoRes) {
      System.out.printf("in doVal1 vv=%2d, gc=%1d, desc=%7s, detail=%7s, %n", vv, gc, vdesc, vdetail);
    }

    String[] valSn = {vdesc, vdetail, "change detail" + vv};
    valS[vv] = valSn;
    int[][] mode = {{gc}};
    valI[vv][modeC] = mode;  //valI[vv][0][0]{gc}
    return vv;
  }

  /**
   * now fill out valI with the values set into valD for vv, and the gc in valI
   * now fill out the rest of the valD, valI, valS field Also check for range
   * errors, and method errors set arrays cstart and gstart the starts of the
   * display panels
   *
   * @param vv
   * @return vv
   */
  int doVal3(int vv) throws IOException {
    int[][] slider, prevSlider, prev2Slider, prev3Slider;
    int svalp = -1, ib = -1;
    int klan = 0, clan = 0;
    int pors = E.P;
    double vR, lL, lH;
    int gc = valI[vv][modeC][0][0];

    if (gc >= vone && gc <= vfour) { // count display starts
      gCntr++;
      clan = 5;
      if (E.debugSettingsTabOut) {
        System.out.format("doval3 vone tst1 +  vv=%3d,name=%5s,gCntr=%2d,cCntr=%2d%n", vv, valS[vv][0], gCntr, cCntr);
      }
      if ((gCntr % lDisp) == 0) {
        gStart[(int) (gCntr / lDisp)] = vv;
        if (E.debugSettingsTabOut) {
          System.out.format("doval3 vone tst1 +  vv=%3d,name=%5s,gCntr=%2d,cCntr=%2d%n", vv, valS[vv][0], gCntr, cCntr);
        }
      }
    } else if (gc == vten || gc == vfive) {
      clan = 0;
      cCntr++;
      // System.out.format("doval3 vten tst1 +  vv=%3d,name=%5s,gCntr=%2d,cCntr==%2d%n",vv,valS[vv][0],gCntr,cCntr);
      if ((cCntr % lDisp) == 0) {
        cStart[(int) (cCntr / lDisp)] = vv;
        System.out.format("doval3 vten tst1 +  vv=%3d,name=%5s,gCntr=%2d,cCntr==%2d%n", vv, valS[vv][0], gCntr, cCntr);
      }
    } else if (E.debugSettingsTab) {
      doMyErr("doVal3 err, vDesc=" + valS[vv][vDesc] + ", vv=" + vv + ", invalid gc=" + gc + ", vaddr[1].length =" + valD[vv][gameAddrC][1].length);
    }
    // Save 4 different versions of the values to go into the slider
    if (vone == gc || gc == vtwo) { // gameMaster
      int[][] slidern = {{-1, -1}};
      int[][] slidern2 = {{-1, -1}};
      int[][] slidern3 = {{-1, -1}};
      int[][] slidern4 = {{-1, -1}};
      valI[vv][sliderC] = slidern;  //doVal3
      valI[vv][prevSliderC] = slidern2;
      valI[vv][prev2SliderC] = slidern3;
      valI[vv][prev3SliderC] = slidern4;
      pors = E.P;
      clan = 5;
      klan = clan % 5;
      svalp = valToSlider(vR = valD[vv][gameAddrC][clan % 5][pors], lL = valD[vv][gameLim][pors][vLowLim], lH = valD[vv][gameLim][pors][vHighLim]);
      valI[vv][sliderC][0][pors] = svalp;
      valI[vv][prevSliderC][0][pors] = svalp;
      valI[vv][prev2SliderC][0][pors] = svalp;
      valI[vv][prev3SliderC][0][pors] = svalp;
      double[][] dPrevRealn = {{valD[vv][gameAddrC][0][pors]}, {-1}};
      valD[vv][dPrevRealC] = dPrevRealn;
      doVal5(vv, gCntr, gStart, gc, svalp, pors, clan, vR, lL, lH);
      if (gc == vtwo) {  // double [] version of addrs'
        pors = E.S;
        svalp = valToSlider(vR = valD[vv][gameAddrC][0][pors], lL = valD[vv][gameLim][pors][vLowLim], lH = valD[vv][gameLim][pors][vHighLim]);
        valI[vv][sliderC][0][pors] = svalp;
        valI[vv][prevSliderC][0][pors] = svalp;
        valI[vv][prev2SliderC][0][pors] = svalp;
        valI[vv][prev3SliderC][0][pors] = svalp;
        doVal5(vv, gCntr, gStart, gc, svalp, pors, clan, vR, lL, lH);
      }
    } else if (gc == vthree || gc == vfour) { // more gameMaster
      int[][] slidern = {{-1}, {-1}};
      int[][] slidern2 = {{-1}, {-1}};
      int[][] slidern3 = {{-1}, {-1}};
      int[][] slidern4 = {{-1}, {-1}};
      valI[vv][sliderC] = slidern;
      valI[vv][prevSliderC] = slidern2;
      valI[vv][prev2SliderC] = slidern3;
      valI[vv][prev3SliderC] = slidern4;
      pors = E.P;
      clan = 5;
      svalp = valToSlider(vR = valD[vv][gameAddrC][pors][0], lL = valD[vv][gameLim][pors][vLowLim], lH = valD[vv][gameLim][pors][vHighLim]);
      valI[vv][sliderC][pors][0] = svalp;
      valI[vv][prevSliderC][pors][0] = svalp;
      valI[vv][prev2SliderC][pors][0] = svalp;
      valI[vv][prev3SliderC][pors][0] = svalp;
      double[][] dPrevRealn = {{vR}, {-1}};
      valD[vv][dPrevRealC] = dPrevRealn;
      doVal5(vv, gCntr, gStart, gc, svalp, pors, clan, vR, lL, lH);
      if (gc == vfour) {  // double [pors][0] version of address
        pors = E.S;
        klan = 0;
        svalp = valToSlider(vR = valD[vv][gameAddrC][pors][0], lL = valD[vv][gameLim][pors][vLowLim], lH = valD[vv][gameLim][pors][vHighLim]);
        valI[vv][sliderC][pors][0] = svalp;
        valI[vv][prevSliderC][pors][0] = svalp;
        valI[vv][prev2SliderC][pors][0] = svalp;
        valI[vv][prev3SliderC][pors][0] = svalp;
        doVal5(vv, gCntr, gStart, gc, svalp, pors, clan, vR, lL, lH);
      }
      // for vone vthree the valI[vv][0-4][E.S]{-1} not {svalp}
      // for vtwo vfour the valI[vv][0-4][E.P]{svalp} display value
    } else if (gc == vten || gc == vfive) {
      int[][] slidern = {{-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}};
      int[][] prev2Slidern = {{-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}};
      int[][] prevSlidern = {{-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}};
      int[][] prev3Slidern = {{-1, -1, -1, -1, -1}, {-1, -1, -1, -1, -1}};
      valI[vv][sliderC] = slidern;
      valI[vv][prevSliderC] = prevSlidern;
      valI[vv][prev2SliderC] = prev2Slidern;
      valI[vv][prev3SliderC] = prev3Slidern;
      if (E.debugSettingsTabOut) {
        System.out.format("doval3 vten tst2 +  vv=%3d,name=%5s,gCntr=%2d,cCntr==%2d%n", vv, valS[vv][0], gCntr, cCntr);
      }
      int porslim = gc == vfive ? 1 : 2;
      for (pors = 0; pors < porslim; pors++) {
        for (clan = 0; clan < 5; clan++) {
          svalp = valToSlider(vR = valD[vv][gameAddrC][pors][clan], lL = valD[vv][gameLim][pors][vLowLim], lH = valD[vv][gameLim][pors][vHighLim]);
          valI[vv][sliderC][pors][clan] = svalp;
          valI[vv][prevSliderC][pors][clan] = svalp;
          valI[vv][prev2SliderC][pors][clan] = svalp;
          valI[vv][prev3SliderC][pors][clan] = svalp;
          doVal5(vv, cCntr, cStart, gc, svalp, pors, clan, vR, lL, lH);
        }
      }// for vten all of the valI[vv][0-4][0-1]{svalp[0-4]} set &gt; 0
    } else { // case 11 etc fatal error
      int[][] slidern = {{-1, -1}};
      int[][] prev2Slidern = {{-1, -1}};
      int[][] prevSlidern = {{-1, -1}};
      int[][] prev3Slidern = {{-1, -1}};
      System.out.flush();
      System.err.flush();
      String verr = "doVal3 illegal gc =" + gc + ", name=" + valS[vv][vDesc] + ", vv=" + vv + ", pors=" + pors + ", clan=" + clan;
      myTestDone = true;
      doMyErr(verr);
    }
    return vv;
  }

  /**
   * check for errors by the previous doVal methods
   *
   * @param vv counter into which val
   * @param xCntr counter gCntr or cCntr
   * @param xStart start gStart or cStart
   * @param gc type of val
   * @param iinput the slider value for this entry
   * @param pors planet or ship
   * @param clan clan being tested
   * @param val the val that was set
   * @param low
   * @param high
   */
  void doVal5(int vv, int xCntr, int[] xStart, int gc, int iinput, int pors, int clan, double val, double low, double high) throws IOException {
    double t1 = 0., t2 = 0., t3 = 0.;
    int j1 = -3, j2 = -4, j3 = -5, j4 = -6, j5 = -7;
    int klan = clan < 5 ? clan : clan == 5 ? 0 : clan % 5;
    if (E.debugSettingsTabOut && E.debugDoRes) {
      System.out.format("in doval5 gc=%1d, lmode=%1d, mode=%1d, vv=%3d =\"%5s\",xCnt=%1d, xStrt[xCnt]=%2d,  iinput=%3d, pors=%1d,klan=%1d,val=%7.2f, low=%7.2f,high=%7.2f 00slider 0123=" + valI[vv][sliderC][0][0] + " " + valI[vv][prevSliderC][0][0] + " " + valI[vv][prev2SliderC][0][0] + " " + valI[vv][prev3SliderC][0][0] + " " + "%n", gc, valI[vv][modeC].length, valI[vv][modeC][0][0], vv, valS[vv][vDesc], xCntr, xCntr < 0 ? 9999 : xStart[(int) (xCntr / lDisp)], iinput, pors, clan, val, low, high);
    }
    // test for legal gc
    if (E.debugSettingsTab) {
      if ((gc != vone && gc != vtwo && gc != vthree && gc != vfour && gc != vfive && gc != vten)) {
        doMyErr("doVal5 Illegal gc=" + gc + ", vv=" + vv + ", desc=" + valS[vv][vDesc]);
      }
    }
    // test value between low and high. note high may be &lt; low
    if (E.debugSettingsTab) {
      if (!((val >= low && val <= high) || (val >= high && val <= low))) {
        doMyErr("doval5 " + valS[vv][vDesc] + " value=" + mf(val) + " out of limits high=" + mf(high) + " low=" + mf(low));
      }
    }
    // test gc == saved gc
    if (E.debugSettingsTab) {
      if (gc != valI[vv][modeC][vFill][0]) {
        doMyErr("doval5 " + valS[vv][vDesc] + "gc =" + gc + "not equal stored gc=" + valI[vv][modeC][vFill][0]);
      }
    }
    // test getVal matches iinput(the converted game slider Value
    if (E.debugSettingsTab) {
      if (iinput != (j1 = getVal(vv, pors, clan))) {
        doMyErr("doval5 vv=" + vv + ", desc=" + valS[vv][vDesc] + " iinput=" + iinput + " not equal to saved slider  value =" + j1);
      }
    }
    // test that input matches the value derived from the saved slider value
    if (gc == vone || gc == vtwo) {
      j2 = valI[vv][sliderC][vFill][pors];
    } else if (gc == vthree || gc == vfour) {
      j2 = valI[vv][sliderC][pors][vFill];
    } else if (gc == vten || gc == vfive) {
      j2 = valI[vv][sliderC][pors][klan];
    }
    if (E.debugSettingsTab) {
      if (iinput != j2) {
        doMyErr("doval5 vv=" + vv + ", desc=" + valS[vv][vDesc] + " iinput=" + iinput + " not equal to saved slider  value =" + j2);
      }
    }
    // now test that the value save in valI results in a real number about 5% of original value
    double dif0 = Math.abs(high - low);
    double dif1 = dif0 * .5;
    // overwrite the real number with the putVal value
    // leave the original value without change
    j3 = putVal(iinput, vv, pors, clan);
    if (gc == vone || gc == vtwo) {
      t1 = valD[vv][gameAddrC][vFill][pors];
      //   valD[vv][gameAddrC][vFill][pors] = val;
    } else if (gc == vthree || gc == vfour) {
      t1 = valD[vv][gameAddrC][pors][vFill];
      // valD[vv][gameAddrC][pors][vFill] = val;
    } else if (gc == vseven) {
      t1 = valD[vv][gameAddrC][vFill][valI[vv][sevenC][vFill][vFill]];
    } else if (gc == vten || gc == vfive) {
      t1 = valD[vv][gameAddrC][pors][klan];
      //   valD[vv][gameAddrC][pors][klan] = val;
    }
    if (E.debugSettingsTab) {
      if (Math.abs(val - t1) > dif1) {
        doMyErr("doVal5.6 regenerated value too different=" + mf(val - t1) + ", allowed=" + mf(dif1) + ", val=" + mf(val) + ", reval=" + mf(t1) + ", frac dif=" + mf((val - t1) / dif0) + ", vv=" + vv + " name=" + valS[vv][vDesc] + ", gc=" + gc + ", pors=" + pors + ", clan=" + clan % 5);
      }
    }

    if (gc == vone || gc == vtwo) {
      j1 = valI[vv][sliderC][vFill][pors];  // j1 should be val
      valI[vv][sliderC][vFill][pors] = -3;// force a different old gc value
      j4 = putVal(iinput, vv, pors, klan); // change the gamefare to regen val
      t1 = valD[vv][gameAddrC][vFill][pors];
      valD[vv][gameAddrC][vFill][pors] = val; // restore val
      valI[vv][sliderC][vFill][pors] = j1; // restore sliderC val
      valI[vv][prevSliderC][vFill][pors] = j1; // restore prevSliderC val
    } else if (gc == vthree || gc == vfour) {
      j1 = valI[vv][sliderC][pors][vFill];
      valI[vv][sliderC][pors][vFill] = -3;// force a different old value
      j4 = putVal(iinput, vv, pors, klan); // change the gamefare to regen val
      t1 = valD[vv][gameAddrC][pors][vFill];
      valD[vv][gameAddrC][pors][vFill] = val;
      valI[vv][sliderC][pors][vFill] = j1;
      valI[vv][prevSliderC][pors][vFill] = j1;
    } else if (gc == vfive || gc == vten) {
      j1 = valI[vv][sliderC][pors][klan]; // save iinput
      valI[vv][sliderC][pors][klan] = -3;// force a different old value
      j4 = putVal(iinput, vv, pors, klan); // change the gamefare to regen val
      t1 = valD[vv][gameAddrC][pors][klan];
      valD[vv][gameAddrC][pors][klan] = val; // restore original
      valI[vv][sliderC][pors][klan] = j1;
      valI[vv][prevSliderC][pors][klan] = j1;
    }
    if (E.debugSettingsTab) {
      if (Math.abs(val - t1) > dif1) {
        doMyErr("doVal5.7 regenerated value too different=" + mf(val - t1) + ", allowed=" + mf(dif1) + ", val=" + mf(val) + ", reval=" + mf(t1) + ", frac dif=" + mf((val - t1) / dif0) + ", vv=" + vv + " name=" + valS[vv][vDesc] + ", gc=" + gc + ", pors=" + pors + ", clan=" + clan);
      }
    }
  }//doVal5

  String ret = "234f";

  String doReadKeepVals() {
    try {
      // String dateString = MYDATEFORMAT.format(new Date());
      // String rOut = "New Game " + dateString + "\n";
      bKeepr = Files.newBufferedReader(KEEP, CHARSET);
      if (false) {
        String sd = "Hello World! 3 + 3.0  -5.0 = 6 true";
        Scanner sds = new Scanner(sd);
        sds.useLocale(Locale.US);
        while (sds.hasNext()) {
          if (sds.hasNextDouble()) {
            System.out.println("Found :" + sds.nextDouble());
          } else {
            System.out.println("Not Found :" + sds.next());
          }
        }
        sds.close();
      }
      Scanner s = new Scanner(bKeepr);
      System.err.println("locale " + s.locale());
      Locale locale = Locale.US;
      s.useLocale(Locale.US);
onceAgain:
      s.useDelimiter("\\s");
      while (s.hasNext()) {
        String cname = "notNot";
        String sname = "notNot";
        String fname = "notnot";
        String lname = "notNot";
        String pound = "notNot";
        int vv = -999;
        int ps = -999;
        int klan = -999;
        int clan = -999;
        int slider = -999;
        Boolean isNeg = false;
        double val = -999.;
        try {
          cname = s.useDelimiter("\\s").next();
          sname = "space";
          fname = "notnot";
          lname = "line";
          switch (cname) {
            case "new":
            case "New":
            case "year":
            case "title":
            case "more":
            case "version":
              lname = s.nextLine();
              System.out.println("a line=" + cname + " :: " + lname);
              break;
            case "keep":
              // sname = s.next("\\s");
              sname = s.useDelimiter("\\S*").next(); //stop at non space
              fname = s.useDelimiter("#\\s*").next();
              pound = s.useDelimiter("\\s*").next(); // to a space
              Object vo = valMap.get(fname);
              double prevVal = -100.;
              if (vo != null) {
                vv = (int) vo;
                s.useDelimiter("\\s");
                ps = s.nextInt();
                klan = s.nextInt();
                clan = klan = klan % 5;
                //if(s.hasNext("\\s*-")) {s.useDelimiter("\\s*-").next(); isNeg=true;}
                if (s.hasNextDouble()) {
                  val = s.nextDouble();
                  System.out.println("found double= " + mf(val));
                } else if (s.hasNextFloat()) {
                  val = s.nextFloat();
                  System.out.println("found Float= " + mf(val));
                } else if (s.hasNextInt()) {
                  System.out.println("oops hasNextInt= " + s.nextInt());
                } else {
                  System.out.println("Oops just something not number = " + s.next());
                }
                //     val = isNeg?-val:val;
                //  svalp = valToSlider(vR = valD[vv][gameAddrC][pors][0], lL = valD[vv][gameLim][pors][vLowLim], lH = valD[vv][gameLim][pors][vHighLim]);
                if (val > valD[vv][gameLim][ps][vHighLim] || val < valD[vv][gameLim][ps][vLowLim]) {
                  double val0 = val;

                  val = val > valD[vv][gameLim][ps][vHighLim] ? valD[vv][gameLim][ps][vHighLim] : val < valD[vv][gameLim][ps][vLowLim] ? valD[vv][gameLim][ps][vLowLim] : val;
                  if (E.debugScannerOut) {
                    System.out.println("keep  val restored to range " + fname + " " + pound + " " + vv + "  " + ps + " " + mf(val0) + " => " + mf(val));
                  }// debug
                } // restored
                prevVal = valD[vv][gameAddrC][ps][klan];
                valD[vv][gameAddrC][ps][klan] = val; // set to kept value
                slider = valToSlider(valD[vv][gameAddrC][clan][ps], valD[vv][gameLim][ps][vLowLim], valD[vv][gameLim][ps][vHighLim]);
                int prev3a = valI[vv][prev3SliderC][ps][clan];
                int prev2a = valI[vv][prev2SliderC][ps][clan];
                int prev1a = valI[vv][prevSliderC][ps][clan];
                int prev0a = valI[vv][sliderC][ps][clan];
                int prev3 = valI[vv][prev3SliderC][ps][clan] = valI[vv][prev2SliderC][ps][clan];
                int prev2 = valI[vv][prev2SliderC][ps][clan] = valI[vv][prevSliderC][ps][clan];
                int prev1 = valI[vv][prevSliderC][ps][clan] = valI[vv][sliderC][ps][clan];
                valI[vv][sliderC][ps][clan] = slider; // a new value for slider

                lname = s.nextLine();
                if (E.debugScannerOut) {
                  System.out.println("keep changed  \"" + fname + "\" vv=" + vv + " pound=" + pound + " ps=" + ps + " klan=" + klan + " prevVal=" + mf(prevVal) + " =>val=" + mf(val) + "slider a0123,0123=" + prev0a + " " + prev1a + " " + prev2a + " " + prev3a + " , " + slider + " " + prev1 + " " + prev2 + " " + prev3 + " " + " \n  :: moreLine=" + lname);
                }
              } else {

                lname = s.nextLine();
                if (E.debugScannerOut) {
                  System.out.println("keep unknown = \"blank=" + sname + "\" name=\"" + fname + "\"  pound=" + pound + " :: " + lname);
                }
              }
              break;
            default:
              lname = s.nextLine();
              System.out.println("Unknow line cmd=" + cname + " :: line=" + lname);
          } // switch
        } catch (Exception | Error ex) {
          firstStack = secondStack + "";
          ex.printStackTrace(pw);
          secondStack = sw.toString();
          // newError = true;
          System.out.println("Igmore doReadKeepVals Input error " + " " + " Caught Exception cause=" + ex.getCause() + " message=" + ex.getMessage() + " err string=" + ex.toString() + Thread.currentThread().getName() + "\n  keep found \"" + fname + "\" vv=" + vv + " pound=" + pound + " ps=" + ps + " klan=" + klan + (isNeg ? " isNeg " : " notNeg ") + "val=" + mf(val) + " :: moreLine=" + s.nextLine() + andMore());
        }
      } // while
      if (bKeepr != null) {
        bKeepr.close();
      }

    } catch (Exception | Error ex) {
      firstStack = secondStack + "";
      ex.printStackTrace(pw);
      secondStack = sw.toString();
      System.err.println("doReadKeepVals error  Caught Exception cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + " " + Thread.currentThread().getName() + andMore());
      System.err.flush();
      // ex.printStackTrace(System.err);
      System.err.println("doReadKeepVals Ignore this error " + new Date().toString() + " " + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + ", addlErr=" + eM.addlErr + andMore());
    } finally {
      return ret;
    }
  }
  /*
   static boolean keepFromPage = false;  // keep clicked titles on this page
  static boolean keepHeaderPrinted = false; // header already printed
  static boolean keepBuffered = false; // true if flush needed
  static final String keepInstruct = "keep any ettings changes made in this page, describe why you kept this changes at KeepCmt";
  static final String initialKeepCmt = "KeepCmt put the why you changed the settings on this page";
  static String prevKeepCmt = initialKeepCmt + "";
  static String nextLineOfOutput = ""; //both keep and remember
   */
  int keepYear = -1; // initial unused year

  /**
   * write the keep file if the keepFromPage flag set, a new page clears the
   * flag
   *
   * @param vv the index of the val used to get the title
   * @param ps the first index often the pors value
   * @param klan the second index often the clan
   * @param val the value to be saved
   * @param val the previous value before the change
   * @param slider the new slider value
   * @param prevSlider the previous slider value
   * @param prev2Slider the previous previous slider value
   * @throws IOException
   */
  public void doWriteKeepVals(int vv, int ps, int klan, double val, double prevVal, int slider, int prevslider, int prev2slider) throws IOException {

    String ll = " ";
    if (keepFromPage) {
// something happens to opens, so it is ok to do it again I think.   
      bKeep = Files.newBufferedWriter(KEEP, CHARSET, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
      System.err.println("did reopen of keep");
      if (year != keepYear || !st.settingsComment.getText().matches(prevKeepCmt)) { // need another year comment page
        keepYear = year;
        prevKeepCmt = st.settingsComment.getText() + ""; // force a copy
        String dateString = MYDATEFORMAT.format(new Date());
        //    String rOut = "New Game " + dateString + "\r\n";
        ll = "year" + year + " version " + st.versionText + " " + dateString + " " + st.settingsComment.getText() + "\r\n";
        bKeep.write(ll, 0, ll.length());
        System.err.println("wrote=" + ll);
      }
      ll = "title " + valS[vv][1] + "\r\n"; // the detail description of the keep
      bKeep.write(ll, 0, ll.length());
      System.err.println("wrote=" + ll);
      ll = "keep " + valS[vv][0] + "# " + ps + " " + klan + " " + mf(val) + " <= " + mf(prevVal) + " sliders " + slider + " <= " + prevslider + " <= " + prev2slider + "\r\n";
      bKeep.write(ll, 0, ll.length());
      System.err.println("wrote=" + ll);

      keepBuffered = true;

    }//keep from page
  }//doWriteKeepVals

  /**
   * get value from valD and turn it into a slider int between 0-100 This is
   * used to generate the slider window
   *
   * @param vv The entry being set to a slider to show its value in slider
   * @param pors 0,1 planet or ship being set
   * @param clan 0-4,5 5 means a game value, 0-4 are the 5 clans
   * @return the value to set in the slider
   */
  int getVal(int vv, int pors, int clan) {
    int slider1 = -1;
    int klan = clan % 5;
    int gc = valI[vv][modeC][0][0];
    if (clan == 5 && gc <= vfour) {
      if ((gc == vone && pors == E.P) || gc == vtwo) {
        return valToSlider(valD[vv][gameAddrC][0][pors], valD[vv][gameLim][pors][lowC], valD[vv][gameLim][pors][highC]);
      } else if ((gc == vthree && pors == E.P) || gc == vfour) {
        return valToSlider(valD[vv][gameAddrC][pors][0], valD[vv][gameLim][pors][lowC], valD[vv][gameLim][pors][highC]);

      } else if (gc == vseven) { // ignored
        return valToSlider(valD[vv][gameAddrC][vFill][valI[vv][sevenC][vFill][vFill]], valD[vv][gameLim][vFill][lowC], valD[vv][gameLim][vFill][highC]);
      } else if (E.debugSettingsTab) {  //problem with clan == 5, unknown gc

        String verr = "getVa; illegal clan =" + clan + " with gc=" + gc + ", desc=" + valS[vv][vDesc] + ", vv=" + vv + ",  pors=" + pors;
        doMyErr(verr);
      }
    } // end of gameMaster clan == 5
    // now do clan entries gc == vten
    else if ((gc == vfive || gc == vten) && pors >= 0 && pors <= 1 && klan >= 0 && klan <= 4) {
      return valToSlider(valD[vv][gameAddrC][pors][klan], valD[vv][gameLim][pors][lowC], valD[vv][gameLim][pors][highC]);

    } else if (E.debugSettingsTab) {
      String verr = "getVa; illegal clan =" + clan + " with gc=" + gc + ", desc=" + valS[vv][vDesc] + ", vv=" + vv + ",  pors=" + pors;
      doMyErr(verr);
    }
    return 50;
  }

  /**
   * put value from slider to the gameValue in the EM. .. values for the game if
   * the slider value DID NOT CHANGE DO NOT CHANGE gameValue, do not change the
   * prevs and return 0 that NOTHING CHANGED
   *
   * @param slider new value from the slider, probably didn't change
   * @param vv position in the values arrays
   * @param pors planet or ship
   * @param clan 0-4 a clan-master 5=game-master
   * @return 1 == change gameValue, 0=noChange
   */
  int putVal(int slider, int vv, int pors, int clan) throws IOException {
    int va  = -1;
    int gc = valI[vv][modeC][vFill][0];
    int klan = clan % 5;
    if (E.debugPutValue2 && E.debugDoRes) {
      // System.out.print("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan);
    }

    if (gc <= vfour || gc == vfive) {
      //double sosfrac[] = {.3, .35};
      if ((gc == vone && pors == E.P) || gc == vtwo) {
        if (slider == (va  = valI[vv][sliderC][vFill][pors])) {
          if (E.debugPutValue2 && E.debugDoRes) {
            System.out.println("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", Thread=" + Thread.currentThread().getName() + ", Ty=" + ((new Date().getTime() - st.startYear)) + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan + " no change");
          }
          return 0; // no change
        }
        //value must change for vone and vtwo
        double val0 = valD[vv][gameAddrC][vFill][pors]; //save prev value
        double val1 = valD[vv][gameAddrC][vFill][pors] = sliderToVal(slider, valD[vv][gameLim][pors][vLowLim], valD[vv][gameLim][pors][vHighLim]);

        int prev3Slider = valI[vv][prev3SliderC][vFill][pors] = valI[vv][prev2SliderC][vFill][pors]; //putVal
        int prev2Slider = valI[vv][prev2SliderC][vFill][pors] = valI[vv][prevSliderC][vFill][pors];
        int prevSlider = valI[vv][prevSliderC][vFill][pors] = valI[vv][sliderC][vFill][pors];
        valI[vv][sliderC][vFill][pors] = slider; // a new value for slider
        if (E.debugPutValue && E.debugDoRes) {
          System.out.println("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan + ", was=" + mf(val0) + ", to=" + mf(val1) + " sliders " + prev2Slider + " => " + prevSlider + " => " + slider);
        }
        doWriteKeepVals(vv, vFill, pors, val1, val0, slider, prevSlider, prev2Slider);
        return 1;
      } // note different way gameMaster values stored
      //double[][] rsefficiencyGMax = {{2.}, {2.}}
      else if ((gc == vthree && pors == E.P) || gc == vfour) {
        if (slider == (va  = valI[vv][sliderC][pors][vFill])) {
          if (E.debugPutValue2 && E.debugDoRes) {
            System.out.println("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan + "no change");
          }
          return 0; // no change
        }
        double val0 = valD[vv][gameAddrC][pors][0];
        double val1 = valD[vv][gameAddrC][pors][0] = sliderToVal(slider, valD[vv][gameLim][pors][vLowLim], valD[vv][gameLim][pors][vHighLim]);
        int prev3Slider = valI[vv][prev3SliderC][pors][vFill] = valI[vv][prev2SliderC][pors][vFill];
        int prev2Slider = valI[vv][prev2SliderC][pors][vFill] = valI[vv][prevSliderC][pors][vFill];
        int prevSlider = valI[vv][prevSliderC][pors][vFill] = valI[vv][sliderC][pors][vFill];
        valI[vv][sliderC][pors][vFill] = slider; // a new value for slider
        if (E.debugPutValue && E.debugDoRes) {
          System.out.println("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan + ", was=" + mf(val0) + ", to=" + mf(val1) + " sliders " + prev2Slider + " => " + prevSlider + " => " + slider);
        }
        doWriteKeepVals(vv, pors, vFill, val1, val0, slider, prevSlider, prev2Slider);
        return 1;
      } else if (gc == vseven) {
        if (slider == (va  = valI[vv][sliderC][pors][vFill])) {
          if (E.debugPutValue2 && E.debugDoRes) {
            System.out.println("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan + " no change");
          }
          return 0; // no change
        }
        // I think this address is wrong, but not used

        double val0 = valD[vv][gameAddrC][vFill][valI[vv][sevenC][vFill][vFill]];
        double val1 = valD[vv][gameAddrC][vFill][valI[vv][sevenC][vFill][vFill]] = sliderToVal(slider, valD[vv][gameLim][vFill][vLowLim], valD[vv][gameLim][vFill][vHighLim]);
        int prev3Slider = valI[vv][prev3SliderC][vFill][valI[vv][sevenC][vFill][vFill]] = valI[vv][prev2SliderC][vFill][valI[vv][sevenC][vFill][vFill]];
        int prev2Slider = valI[vv][prev2SliderC][vFill][valI[vv][sevenC][vFill][vFill]] = valI[vv][prevSliderC][vFill][valI[vv][sevenC][vFill][vFill]];
        int prevSlider = valI[vv][prevSliderC][vFill][valI[vv][sevenC][vFill][vFill]] = valI[vv][sliderC][vFill][valI[vv][sevenC][vFill][vFill]];
        valI[vv][sliderC][vFill][valI[vv][sevenC][vFill][vFill]] = slider; // a new value for slider
        if (E.debugPutValue && E.debugDoRes) {
          System.out.println("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan + ", was=" + mf(val0) + ", to=" + mf(val1) + " sliders " + prev2Slider + " => " + prevSlider + " => " + slider);
        }
        doWriteKeepVals(vv, pors, valI[vv][sevenC][vFill][vFill], val1, val0, slider, prevSlider, prev2Slider);
        return 1;
      }
    } //  double[][] clanStartFutureFundDues = {{700., 700., 700., 700., 700.}, {600., 600., 600., 600., 600.}};
    else if ((gc == vten || gc == vfive)) {
      va  = valI[vv][sliderC][pors][klan];
      if (E.debugPutValue2 && E.debugDoRes) {
        System.out.print(" old slider=" + va  + ", new slider=" + slider);
      }
      if (slider == va) {
        if (E.debugPutValue2 && E.debugDoRes) {
          System.out.println("no change");
        }
        return 0; // no change
      }

      double val0 = valD[vv][gameAddrC][pors][klan];
      double val1 = valD[vv][gameAddrC][pors][klan] = sliderToVal(slider, valD[vv][gameLim][pors][vLowLim], valD[vv][gameLim][pors][vHighLim]);
      int prev3Slider = valI[vv][prev3SliderC][pors][clan] = valI[vv][prev2SliderC][pors][clan];
      int prev2Slider = valI[vv][prev2SliderC][pors][clan] = valI[vv][prevSliderC][pors][clan];
      int prevSlider = valI[vv][prevSliderC][pors][clan] = valI[vv][sliderC][pors][clan];
      valI[vv][sliderC][pors][clan] = slider; // a new value for slider
      if (E.debugPutValue && E.debugDoRes) {
        System.out.println("EM putVal gc=" + gc + " " + ", vv=" + vv + " " + valS[vv][0] + ", " + E.cna[pors] + ", clan=" + clan + ":" + klan + ", was=" + mf(val0) + ", to=" + mf(val1) + " sliders " + prev2Slider + " => " + prevSlider + " => " + slider);
      }
      doWriteKeepVals(vv, pors, clan, val1, val0, slider, prevSlider, prev2Slider);
      return 1;
    } else if (E.debugSettingsTab) {  //problem with gc
      if (E.debugPutValue2 && E.debugDoRes) {
        System.out.println("gc oops =" + valS[vv][0] + ", old slider=" + va  + ", new slider=" + slider + ", gc=" + gc + ", pors=" + pors + ", clan=" + clan);
      }
      String verr = "putval illegal gc=" + gc + "  " + valS[vv][vDesc] + ", vv=" + vv + ",  pors=" + pors + ", klan=" + klan;
      doMyErr(verr);
    }
    return 1;
  }

  /**
   * convert val to slider int using the value low and high limits find the
   * value as the faction of the low to high extent then apply to the slider
   * starting at 1 and going to 100
   *
   * @param val real value being converted
   * @param low 1st limit usually lowest from doVal initialization
   * @param high 2nd limit usually highest from doVal initialization
   * @return new slider value
   */
  int valToSlider(double val, double low, double high) {
    // dif1 20 = 29-10+1  dif2 = -20 10 - 29-1
    double gameValueExtent = high - low; // accept both limits
    double gameFrac = (val - low) / gameValueExtent;
    int sliderVal = sliderLow + (int) (sliderExtent * gameFrac);
    // dif = dif < 0? dif -1: dif+1; // include both upper and lower limits
    // dif1 rtn 25 = 15 -10 (5) *(5) 100 / 20
    // dif2 rtn 75  = 14 - 29 (-15) * (-5) 100 / -20  75 = 3/4 from 1st lim
    //int rtnVal = (int) ((val - low) * sliderExtent / dif);
    return sliderVal;
  }

  /**
   * convert a slider value back to a real game value the full formula is
   * sliderExtent defined for slider the gameValExtent = limit2 limit1
   * sliderFrac = (slider -sliderLow)/sliderExtent or * invSliderExtent gameVal1
   * = sliderFrac * gameValueExtent result = val2 = gameVal1 + limit1
   *
   * @param slider value from the slider
   * @param limit1 first game value limit usually low
   * @param limit2 second game value limit usually high not included in extent
   * @return result
   */
  double sliderToVal(int slider, double limit1, double limit2) {
    double gameValueExtent = limit2 - limit1;
    double sliderFrac = (slider - sliderLow) * invSliderExtent;
    double gameVal1 = (sliderFrac * gameValueExtent);
    double val2 = gameVal1 + limit1; // if gameLow > gameHigh, val1 < 0, high + -val
    if (E.debugSettingsTabOut) {
      System.out.println("sliderToVal slider=" + slider + ", game limit1=" + mf(limit1) + ",game limit2=" + mf(limit2) + ", gameValueExtent=" + mf(gameValueExtent) + ",sliderExtent=" + mf(sliderExtent) + ", invSliderExtent=" + mf(invSliderExtent) + ", sliderFrac=" + mf(sliderFrac) + ", gameVal1=" + mf(gameVal1) + ", game val2 result=" + mf(val2));
    }
    //if(E.debugSettingsTab)System.out.format("sliderToVal slider=%3d, gameLow=%7.5f,gameHigh=%7.5f, gamevalueExtent= %7.5f,sliderExtent=%5.2f, invSliderExtent=%5.2f, sliderFrac=%5.2f, val add=%5.2f, val result=%5.2f\n", slider, gameLow, gameHigh, gameValueExtent, sliderExtent, invSliderExtent, sliderFrac, val1, val2);
    return val2;
  }

  /**
   * return true if this doVal matches the gameClanStaus currently being
   * displayed
   *
   * @param vv index of the doVal
   * @return true if match, false otherwise
   */
  boolean matchGameClanStatus(int vv) {
    int gc = valI[vv][modeC][0][0];
    boolean rtn = ((gc >= vone && gc <= vfour) && gameClanStatus == 5)
            || (vten == gc || gc == vfive) && (0 <= gameClanStatus && 4 >= gameClanStatus);
    if (E.debugSettingsTabOut) {
      System.out.println("----MGSc----match game clan vv=" + vv + " valS=" + valS[vv][vDesc] + " match game clan status=" + gameClanStatus + ", gc=" + gc + ", " + (rtn ? "" : "!!") + "rtn");
    }
    return rtn;
  }

  /**
   * return the number of slider lines this doVal will need
   *
   * @param vv index of doVal
   * @return number of slider lines
   */
  int dispLen(int vv) {
    int gc = valI[vv][modeC][vFill][0];
    return (vone <= gc && vfour >= gc) || gc == v25 ? 1 : gc == v72 || gc == v725 ? 7 : gc == v162 ? 16 : 1;
  }

  /**
   * return the detail about this input doVal
   *
   * @param vv index of the input doVal
   * @return
   */
  String getDetail(int vv) {
    return valS[vv][0] + "  " + valS[vv][1];
  }

  void checkLims(double v, double[][] lims, String sDesc, String sDetail) {
    int age = curEcon.age;
    int clan = curEcon.clan;
    int pors = curEcon.pors;
    if (v > lims[pors][1]) {
      bErr(" value=" + df(v) + " exceeds max=" + df(lims[pors][1]) + " for " + sDesc + " with " + sDetail);
    }
    if (v < lims[pors][0]) {
      bErr(" value=" + df(v) + " below min=" + df(lims[pors][0]) + " for " + sDesc + " with " + sDetail);
    }
  }

  /**
   * force limits on int[] result values
   *
   * @param pors indication if this is planet or ship being processed
   * @param res result array
   * @param src source values from doVal
   * @param imask double array of limits
   */
  void doLims(int pors, int[] res, double[] src, int[][] imask) {
    res[pors] = (int) src[pors] < imask[pors][0] ? imask[pors][0] : (int) src[pors] > imask[pors][1] ? imask[pors][1] : (int) src[pors];
  }

  /**
   * force limits on double[] result values
   *
   * @param pors indication if this is planet or ship being processed
   * @param res result array
   * @param src source values from doVal
   * @param imask double array of limits
   */
  void doLims(int pors, double[] res, double[] src, double[][] mask) {
    res[pors] = src[pors] < mask[pors][0] ? mask[pors][0] : src[pors] > mask[pors][1] ? mask[pors][1] : src[pors];
  }

  /**
   * run a series of adjustments from values set by doVal's, to a set of values
   * derived by function of doVal's with some
   */
  void runAdjusts() {
    // process those values for both P and S
    for (int mpors = 0; mpors < 2; mpors++) {
      maxTries[mpors] = maxTries[mpors];
      // doLims(mpors, maxTries, dMaxTries, mMaxTries);

      // within pors process clans
      for (int nclan = 0; nclan < LCLANS; nclan++) {
      }

    } //mpors

  }

  /**
   * run the initialization of the valD, valI, valS arrays that set the sliders
   * also run settings adjustments at the end
   */
  void runVals() throws IOException {
    doVal("difficulty", difficultyPercent, mDifficultyPercent, "Normally, the named change in effect is dependent on a increase in the value of the slider.  Increase the difficulty for ships as well as  Planets ,more difficulty increases costs of  resources and staff each year, increases the probability of ship and planet deaths.  More difficulty probably requires more clan-master expertise.");
   doVal("maxEcons", econLimits3, mEconLimits3, "Increase the max number of econs (planets+ships) in this game, it maxEcons too large the game will blow up out of memory, depending on your available memory");
    doVal("randomActions", randFrac, mRandFrac, "Normally, the named change in effect is dependent on a increase in the value of the slider.  Increase the random effects, increases possibility of gain, and of loss, inccreasing possibility of deaths");
    doVal("clanRiskMult", gameClanRiskMult, mGameClanRiskMult, "increase slider: increase effect of clan risk settings");
    doVal("clanRisks", clanRisk, mClanRisk, "increase slider: ncreases the random multipliers for your clan for many of the prioities set by clan-masters.");
    /*
     winner = scoreVals(TRADELASTGAVE, iGiven, ICUM, isI);
    winner = scoreVals(TRADELASTGAVE, wGiven, ICUM, isV);
    winner = scoreVals(TRADENOMINALGAVE, wGiven2, ICUM, isV);
    winner = scoreVals(TRADESTRATLASTGAVE, wGenerous, ICUM, isV);//%given
    winner = scoreVals(LIVEWORTH, wLiveWorthScore, ICUM, isV);
    winner = scoreVals(LIVEWORTH, iLiveWorthScore, ICUR0, isI);
    winner = scoreVals(WTRADEDINCRMULT, wYearTradeV, ICUR0, isV);
    // winner = scoreVals(WTRADEDINCRMULT, wYearTradeI, ICUR0, isI);
    winner = scoreVals(DIED, iNumberDied, ICUM, isI);
    winner = scoreVals(BOTHCREATE, iBothCreateScore, ICUM, isI);
     */
    doVal("wGiven", wGiven, mNegPluScoreMult, "Normally, the named change in effect is dependent on a increase in the value of the slider.  Increase the sub-score for TRADELASTGAVE the last value given in trade by the clan. The sub-score is higher based on how much the clan value is higher than the smallest clan value.");
    doVal("wGiven2", wGiven2, mNegPluScoreMult, "Normally, the named change in effect is dependent on a increase in the value of the slider.  Increase the score for TRADENOMINALGAVE the nominal value given in trade by the clan. The sub-score is higher based on how much the clan value is higher than the smallest clan value.");
    doVal("wGenerous", wGenerous, mNegPluScoreMult, "Normally, the named change in effect is dependent on a increase in the value of the slider.  Increase the score for TRADESTRATLASTGAVE the strategic value given in trade by the clan. The sub-score is higher based on how much the clan value is higher than the smallest clan value.");
    doVal("iGiven", iGiven, mNegPluScoreMult, "Normally, the named change in effect is dependent on a increase in the value of the slider.  Increase the score for TRADELASTGAVE the number of economies traded. The sub-score is higher based on how much the clan value is higher than the smallest clan value.");
    doVal("wLiveWorthScore", wLiveWorthScore, mNegPluScoreMult, "Normally, the named change in effect is dependent on a increase in the value of the slider.  Increase the winning score for LIVEWORTH the clan final worth.");
    doVal("iLiveWorthScore", iLiveWorthScore, mNegPluScoreMult, "increase slider, increase the winning score for LIVEWORTH the clan final count of planets and ships");
    doVal("iBothCreateScore", iBothCreateScore, mNegPluScoreMult, "Normally, the named change in effect is dependent on a increase in the value of the slider.  Increase the winning score for BOTHCREATE the number of this clan in ever created");
    doVal("wYearTradeV", wYearTradeV, mNegPluScoreMult, "Normally, the named change in effect is dependent on a increase in the value of the slider.  Increase the winning score for the increase in WTRADEDINCRMULT the year trade increase");
    doVal("wYearTradeI", wYearTradeI, mNegPluScoreMult, "Normally, the named change in effect is dependent on a increase in the value of the slider.  Increase the winning score for WTRADEDINCRMULT the increase in the number of economies with at least one trade that year");
    doVal("iNumberDiedI", iNumberDied, mNegPluScoreMult, "Decrease the winning score for DIED the number of dead economies for this clan this year");
    doVal("years To Win", winDif, mwinDif, "Normally, the named change in effect is dependent on a increase in the value of the slider.  Increase the years before a winner is declared");
    doVal("resourceCosts", mab1, mmab1, "raise the cost of resources planet and ship, makes game harder");
    doVal("staffCosts", mac1, mmac1, "raise the costs of staff for planets and ships, makes planets and ships die more often");
    doVal("Threads", maxThreads, mmaxThreads, "Increase the number of possible threads. If your computer supports more than 1 cpu, more threads may decrease the total time per year.");
    doVal("haveColors", haveColors, mHaveColors, "Above slider 50 the tab display will show the color of the current economy. The changing of colors helps understand how fast the game is going.  Some persons have trouble with blinking colors, so setting this slider below 50 will stop the color changes.");
    doVal("haveCash", haveCash, mHaveCash, "Above slider 50 the tab display Economies will start with some cash, cash is just another resource that can be traded, think of it as bars of gold, not as paper money");
    doVal("minEconomies ", minEcons, mMinEcons, "iincrease the minimum number of economies after the startup numbers, the game creates new economies, new economies start to be added at a random clan, rotating to other clans next");
    doVal("Min Econs by Year", minEconsMult, mMinEconsMult, " increase min econs for each year, also affect minEcons");
    doVal("tradeAddlSVFrac", offerAddlFrac, mOfferAddlFrac, "increase the process excessOffers in a barter");
    doVal("tradeFraction", tradeFrac, mTradeFrac, "Increase the desired trade profit (received/given) in a trade, this may reduce the number of successful trades");
    //   doVal("tradeGrowthGoal", tradeGrowth, mAllGoals, "adjust growth goals while trading, increases the level of requests to meet goals");
    // doVal("HiLoFactorDif",);
    doVal("tradeGrowthGoal", tradeGrowth, mAllGoals, "adjust growth goals while trading, increases the level of requests to meet goals");
    doVal("healthGoal", goalHealth, mRegGoals, "set normal, non-emergency health goal, may increase health and reduce costs");
    doVal("emergHealthGoal", emergHealth, mAllGoals, "set emergency health goals for when economies are weak more might help or might may make them worse");
    doVal("tradeHealthGoal", tradeHealth, mAllGoals, "adjust health goals while trading, increases the level of requests to meet goals");
    doVal("tradeStrtAvail", startAvail, mStartAvail, "increase the number of staff&resource sectors available for trade");
    doVal("tradeAvailFrac", availFrac, mAvailFrac, "increase the amount of staff&resource available for trade");
    doVal("tradeAvailMin", availMin, mAvailMin, "increase the minimum value below which a sector is not available for trade");
    doVal("ManualsMax%", manualsMaxPercent, mManualsMaxPercent, "increase the max value of manuals in calculating requests and offers, less forces more goods as part of the trade");
    doVal("ShipsTradeFraction", ssFrac, mSsFrac, "Increase the desired trade profit with other ships (received/given) in a trade, this may reduce the number of successful trades");
    doVal("tradeReservFrac", tradeReservFrac, mTradeReservFrac, "increase the amount of resource or staff to reserve for a trade, higher reduces risk and reduces gain");
    doVal("hiLoMult", hiLoMult, mHiLoMult, "For clan ships as well as  Planets , set the difficulty multiplyer for each Econ of this clan for the game, more hiLoMult increases the variation of difficulty for each Econ of each Clan.  Higher difficulty increases costs of  resources and staff each year, increases the possibility of economy death.  More difficulty requires more clan-master expertise for each clan.");

    doVal("historyFrac", historyBias, mHistoryBias, "increase the amount that trade history of: accepted, rejected, and lost trades, change the amount of profit required in a trade");
    doVal("rejectBias", rejectBias, mRejectBias, "increase the amount that trade history of rejected trades increase the amount of profit required in a trade");
    doVal("lostBias", lostBias, mLostBias, "increase the amount that trade history of lost trades increase the amount of profit required in a trade");
    doVal("Offer Request Bias", moreOfferBias, mMoreOfferBias, "increase the bias for requests in the first round adjust of the bid at each turn of bidding");
    doVal("Max LY  ", maxLY, mMaxLY, "adjust the max Light Years distance of planets for trades");
    doVal("Add LY  ", addLY, mAddLY, "adjust addition per round to the max Light Years distance of planets for traded");
    doVal("SearchPlanetsCnt", wildCursCnt, mWildCursCnt, "adjust the number of planets listed to be judged for the next trade");
    doVal("SearchYearlyBias  ", searchYearBias, mSearchYearBias, "increase,decrease value of prospective trade for earlier years");
    doVal("priorityMult", prioritySetMult, mPrioritySetMult, "increase the differende in value between the high priority financial sectors and the low priority financial sectors. In general this diference is needed to force planets to trade high priority financial sector surplus goods for the low priority financial sectors that need trades to have enough goods to survive the next year. ");
    doVal("commonKTradeF", commonKnowledgeTradeManualFrac, mCommonKnowledgeTradeManualFrac, "increase the manuals in trade for each financial sector. Increasing the manuals in trades, increases the valuw of the trade, and over coming years helps increase the knowledge for individual financial sectors.");
    doVal("increaseLearnM", kLearnManuals, mKLearnManuals, "increase the manuals to knowledge for each financial sector. Increasing the manuals growth, increases the valuw of the trade, and over coming years helps increase the knowledge for individual financial sectors.");

    // doVal("econLimits1  ", econLimits1, mEconLimits1, "Increase the max number of econs (planets+ships) in this game");
    //  doVal("econLimits2  ", econLimits2, mEconLimits2, "Increase the max number of econs (planets+ships) in this game");

    doVal("Clan Ships per planets", clanShipFrac, mClanShipFrac, "increase faction of ships per planets for this clan only, limited by All ships per planets and game ships per planets");
    doVal("All ships per planets", clanAllShipFrac, mClanAllShipFrac, "for this clan increase the fraction of ships per all planets, limited by Clan Ships per planets  and game Ships per planets ");
    doVal("Ships per planets", gameShipFrac, mGameShipFrac, "increase the fraction of ships in the game. bit for each clan, limited by clanShipFrac and clanAllShipFrac");
    doVal("resourceGrowth", resourceGrowth, mResourceGrowth, "increase amount of resource growth per year, dependent on units of staff");
    doVal("cargoGrowth", cargoGrowth, mCargoGrowth, "increase amount of cargo growth per year dependent of units of staff");
    doVal("staffGrowth", staffGrowth, mStaffGrowth, "increase amount of staff growth per year, dependent on units of staff");
    doVal("guestGrowth", guestsGrowth, mGuestsGrowth, "increase amount of guest growth per year, dependent on units of guests");
    doVal("maxStaffGrowth", maxStaffGrowth, mMaxStaffGrowth, "increase the largest possible staffsize, staff growths will slow to prevent reaching this size");
    doVal("CatastrophyFreq", userCatastrophyFreq, mUserCatastrophyFreq, "Increase the frequency of Catastrophies for this Clan. Catastrophies decrement 2 resource financial sectors and 1 staff financial sector.  then catastrophies create benefits by reducing the decays of some planet resource and staff financial sectors, catastrophies also bounus the growth of some financial sectors for a few years  For ships, the catastrophies add a significant amount of manuals, increasing the ship values in trades");
    doVal("Catastrophies", gameUserCatastrophyMult, mGameUserCatastrophyMult, "incr slider: increase the size of catastrophies for all clans.   ");
    doVal("InitYrsTraveled", initTravelYears, mInitTravelYears, "Increase initial travel cost");
    doVal("favr", fav0, mfavs, "increase how much your clan favors clan red by giving a better barter discount, this increases the amount you help clan red ");
    doVal("favo", fav1, mfavs, "increase how much your clan favors clan orange by giving  clan orange a better barter discount");
    doVal("favy", fav2, mfavs, "increase how much your clan favors clan yellow by giving clan yellow a better barter discount");
    doVal("favg", fav3, mfavs, "increase how much your clan favors clan green by giving clan green a better barter discount");
    doVal("favb", fav4, mfavs, "increasehow much your clan favors clan blue by giving clan blue a better barter discount");
    doVal("ClanFutureFundDues", clanStartFutureFundDues, mClanStartFutureFundDues, "increase the economy worth at which staff and resources are converted to cash used to create new planets or ships.  Balances from the biggest financial sectors are reduce and turned into future fund dues");
    doVal("futureFundTransferFrac", futureFundTransferFrac, mFutureFundTransferFrac, "increase the amount transfered to futureFund per year at emergencies and dues. This increases building new economies, decreases growth which may increase deaths.");
    doVal("FutureFundFrac", futureFundFrac, mFutureFundFrac, "also increase the amount transfered to futureFund  at emergencies and dues. increases building new economies, decreases growth may increase deaths, inrease growth, decrease new economies");
    doVal("FutureFEmerg1", clanFutureFundEmerg1, mClanFutureFundEmerg, "adjust first level emergency trigger when staff  or resources are out of bound,divert max staff/resource sectors balances to the futureFund, larger than FutureFEmerg2 to have 2 triggers, decreasing this value increases helps prevent more ship or planet deaths, but also increases unneeded emergency transfers decreasing ship or planet growth");
    doVal("clanFutureFEmerg2", clanFutureFundEmerg2, mClanFutureFundEmerg, "adjust second level trigger when staff and resources are out of bound causing ship or planet death , see FutureEmerge1");
    doVal("TradeCriticalBias", tradeCriticalFrac, mTradeCriticalFrac, "clan increase the trade value of resources or staff critically needed and decrease the trade value of resources or staff least needed");
    doVal("RejectTooSmlTrd", criticalStrategicRequestsRejectFrac, mCriticalStrategicRequestsRejectFrac, "clan increase the required sumCriticalStrategicRequestsFirst, reject trade if sumCriticalStrategicRequests gets too small");
    doVal("CurrNeedsMult", strategicCurrentNeedsMult, mStrategicNeedsMult, "clan increase the power of current needs in making trades");
    doVal("FutureNeedsMult", strategicFutureNeedsMult, mStrategicNeedsMult, "clan increase the power of future needs in making trades");
    doVal("EmergNeedsMult", strategicEmergencyNeedsMult, mStrategicNeedsMult, "clan increase the power of current needs in making trades when there is an emergency level of needs");
    doVal("strategicMultMin", strategicMultMin, mStrategicMultMin, "clan increase min of Strategic values, may reduce trade amount or frequency");
    /*
      static double[][] strategicCurrentNeedsMult= {{.7,.7,.7,.7,.7},{ .7,.7,.7,.7,.7}};
  static double[][] strategicFutureNeedsMult= {{.7,.7,.7,.7,.7},{ .7,.7,.7,.7,.7}};
  static double[][] strategicEmergencyNeedsMult= {{.7,.7,.7,.7,.7},{ .7,.7,.7,.7,.7}};
  static final double[][] mStrategicNeedsMult = {{.1,10.},{.1,10.}};
     */
    doVal("swapDif", swapDif, mSwapDif, "decrease the difference when a resource sum, staff sum difference will permit only swap transmuts/repurposing to keep this ship or planet alive");
    doVal("staffSizeRestrictions", gameStartSizeRestrictions, mGameStartSizeRestrictions, "raise the value at which staff increases are restricted");
    /*
    tradeEconSearchType
     */
    doVal("Trade Search Type", tradeEconSearchType, mTradeEconSearchType, "slider at 15:ship selects a random planet within range with unknown available goods to match ships needs, slider at 40:ship selects a planet with a current available goods best currently matching ships needs, slider at 65: ship eaxmines the planet trade histories to project which planet's available goods probably best match the ship needs");
    doVal("Maint Min Efficiency", effBias, mEffBias, "adjust the size of the efficiency fraction");
    doVal("Trade Reserve Inc Frac", tradeReserveIncFrac, mTradeReserveIncFrac, "adjust the increase of trade offerings");
    doVal("sectorDifficultyFromDifficulty", difficultyByPriorityMin, mDifficultyByPriorityMin, "Adjust the difficulty factor in sector priority");
    doVal("rsefficiencyMMin", rsefficiencyMMin, mRsefficiencyMMin, "increase the smallest maintenance efficiency, decreases the cost of maintence type costs");
    doVal("rsefficiencyMMax", rsefficiencyMMax, mRsefficiencyMMax, "increase the largest maintenance efficiency, decreases the cost of maintence type costs");
    doVal("rsefficiencyGMin", rsefficiencyGMin, mRsefficiencyGMin, "increase the smallest Growth  efficiency, decreases the cost of growth type costs");
    doVal("rsefficiencyGMax", rsefficiencyGMax, mRsefficiencyGMax, "increase the highest growth efficiency by decreasing the growth type costs");
    doVal("minFertility", minFertility, mMinFertility, "adjust the minimum value for fertility, the multiplier of staff for a sector, to calculate the amount of growth");
    doVal("maxFertility", maxFertility, mMaxFertility, "adjust the maximum value for fertility, the multiplier of staff for a sector, to calculate the amount of growth");
    doVal("poorHealthPenalty", poorHealthPenalty, mPoorHealthPenalty, "increase maintenance, travel, growth costs as health = (units - requiredHealthCosts) / required Health Costs");
    doVal("poorHealthEffectLowLim", poorHealthEffectLimitsL, mPoorHealthEffectLimitsL, "increase the lower limit of poorHealthEffect higher is higher costs");
    doVal("poorHealthEffectHighLim", poorHealthEffectLimitsH, mPoorHealthEffectLimitsH, "increase the higher limit of poorHealthEffect higher is higher costs");
    /* double futGrowthFrac[][] = {{.7, .8, .7, .8, .7}, {.8, .8, .7, .4, .3}};
  static double mFutGrowthFrac[][] = {{.2,2.2},{.2,2.2}};
  double futGrowthYrMult[][] = {{5., 5., 5., 4., 5.}, {8., 5., 3., 5., 5.}};
  static double[][] mFutGrowthYrMult = {{1.5,11.5},{1.5,11.5}};*/
    doVal("futureGrowthFrac", futGrowthFrac, mFutGrowthFrac, "increase years of costs lookahead");
    doVal("futureGrowthYears", futGrowthYrMult, mFutGrowthYrMult, "increase the growth multiplier");

    doVal("tradeEmergency", tradeEmergFrac, mTradeEmergFrac, "adjust the level  causing a planet or ship to shift to emergency trading");
    doVal("sectorDifficultyByPriority", difficultyByPriorityMult, mDifficultyByPriorityMult, "Adjust difficulty by the sector priority");

    doVal("initialWorth", initialWorth, mInitialWorth, "adjust the initial worth of a new planet or ship");
    doVal("initialColonists", initialColonists, mInitialColonists, "adjust the minimum  of colonists");
    doVal("initialColonistFrac", initialColonistFrac, mInitialColonistFrac, "increase the initial colonists fraction of initial worth");
    doVal("initialResources", initialResources, mInitialResources, "adjust the minimum h of resources");
    doVal("initialResourceFrac", initialResourceFrac, mInitialResourceFrac, "adjust the initial resources fraction of initial worth");
    doVal("initialReserve", initialReserve, mInitialReserve, "adjust the initial cargo or guests as a fraction of resources or staff");
    doVal("initialCash", initialWealth, mInitialWealth, "adjust the minimum of initial cash");
    doVal("initialWealthFrac", initialWealthFrac, mInitialWealthFrac, "adjust the initial cash fraction of initial worth");
    doVal("Knowledge worth", nominalWealthPerCommonKnowledge, mNominalWealthPerCommonKnowledge, "adjust the worth of knowledge");
    doVal("Frac New Knowledge", fracNewKnowledge, mFracNewKnowledge, "adjust the fraction of new knowledge in relation to knowledge");
    doVal("Frac Common Knowledge", initialCommonKnowledgeFrac, mInitialCommonKnowledgeFrac, "adjust the initial fraction of Initial Common Knowledge in Initial Worth");
    doVal("Manuals Worth", manualFracKnowledge, mManualFracKnowledge, "adjust the worth of manuals in relation to knowledge");

    /* double offerAddlFrac[] = {.001,.2};
  static double[][]mOfferAddlFrac = {{.0001,.1},{.001,.4}}; */
 /* mMtgWEmergency */
    doVal("mtgWEmergency", mtgWEmergency, mMtgWEmergency, "adjust the end of health emergencies based on prospects of life");
    doVal("futtMTGCostsMult", futtMTGCostsMult, mFuttMTGCostsMult, "unused, probably a bad idea. adjust the growth");

    vvend = vv;
    // now settings adjustments

  }

  // values  for doRes opr
  private static long lli = 00001L;
  static final long getP = 00L;
  static final long getS = lli = 01L;
  static final long psmask = getP | getS;
  static final long PSMASK = psmask;
  private static final long sum = lli = lli << 1;//0000002L;  //  sum P + S
  static final long SUM = sum;
  static final long pns = sum;  // p and s are sum
  static final long PNS = sum;
  private static final long both = lli = lli << 1;//0000004L; //  both P & S this round
  static final long BOTH = both;

  private static final long skipUnset = lli = lli << 1;// 0000020L; // skip listing anything if value unset
  static final long SKIPUNSET = skipUnset;
  private static final long CURUNITAVE = lli = lli << 1;// each year ave sum/units
  private static final long curUnitAve = CURUNITAVE;
  static final long CURAVE = CURUNITAVE;
  private static final long cumUnitAve = lli = lli << 1;// cum sum values div by cum units
//  static final long CUMUNITAVE = cumUnitAve;
  static final long CUMAVE = cumUnitAve;
  private static final long thisYearUnitAve = lli = lli << 1;//0000200L;  sum of This Year div by units
  static final long THISYEARAVE = thisYearUnitAve; // sum of This Year div by units
  private static final long thisYr = lli = lli << 1;// 0000010L; // sum of r0 thisYr values
  static final long THISYEAR = thisYr;
  private static final long cur = lli = lli << 1;//0000400L;  // sums of values a listing for each saved year
  static final long CUR = cur;
  private static final long cum = lli = lli << 1;// 0040000L; // cum sum of values both 
  static final long CUM = cum;
  static final long CURUNITS = lli = lli << 1;//0001000L; // total units that divide curUnitAve values
  static final long CUMUNITS = lli = lli << 1;//0002000L; // cum sum of  units
  private static final long zeroUnset = lli = lli << 1;//0004000L; // show unset as zero's
  static final long ZEROUNSET = zeroUnset;
  private static final long thisYearUnits = lli = lli << 1;// 0100000L; // units this year
  static final long THISYEARUNITS = thisYearUnits;

  private static final long mask1 = getP | getS | SUM | BOTH | CURAVE | CUMAVE | THISYEARAVE | CUR | CUM | THISYEAR | CURUNITS | CUMUNITS | THISYEARUNITS | ZEROUNSET | SKIPUNSET;
  static final long CMDSONLYMASK = CURAVE | CUMAVE | THISYEARAVE | CUR | CUM | THISYEAR | CURUNITS | CUMUNITS | THISYEARUNITS | ZEROUNSET | SKIPUNSET; //0777770L; // mask for commands but not both,sum,p
  static final long CMDSMASK = SUM | BOTH | CURAVE | CUMAVE | THISYEARAVE | CUR | CUM | THISYEAR | CURUNITS | CUMUNITS | THISYEARUNITS | ZEROUNSET | SKIPUNSET;// 0777776L; // opr mask commands with both and sum 
  static final long ROWS1 = lli = lli << 1;//   010000000000000000L;
  static final long ROWS2 = lli = lli << 1;//   020000000000000000L;
  static final long ROWS3 = lli = lli << 1;//   040000000000000000L;
  static final long ROWS123 = ROWS1 | ROWS2 | ROWS3;
  static final long ROWSMASK = ROWS123;
  static final long DUP = lli = lli << 1;//      0100000000000000000L;//
  static final long SKIPDUP = lli = lli << 1;//  0200000000000000000L;// 
  // static final long THISYEARUNITAVE = thisYearUnitAve;
//  static final long thisYrUnitAve = thisYearUnitAve;
  // static final long thisYrAve = thisYrUnitAve;
  // static final long THISYRAVE = thisYrUnitAve;
  // static final long TYAVE = thisYrUnitAve;
//  static final long valuesDivByUnits = thisYearUnitAve; // sum this year div by units
  // static final long CURAVEUnits = 0200000; // units for each year
  //static final long tstring = 0010000L; //  unused
//  static final long divByAve = 0020000L; // divide by other val/units unused

  static final long LIST0 = lli = lli << 1;// 00000100000000L; // usually part of 0 table view
  // static final long LIST0 = LIST0;
  static final long LIST1 = lli = lli << 1;// 00000200000000L; // usually part of 1 table view etc
//  static final long LIST1 = LIST1;
  static final long LIST2 = lli = lli << 1;// 00000400000000L;
  // static final long LIST2 = LIST2;
  static final long LIST3 = lli = lli << 1;// 00001000000000L;
  private static final long list4 = lli = lli << 1;// 00002000000000L;
  private static final long list5 = lli = lli << 1;// 00004000000000L;
  private static final long list6 = lli = lli << 1;// 00010000000000L;
  private static final long list7 = lli = lli << 1;// 00020000000000L;
  private static final long list8 = lli = lli << 1;// 00040000000000L;
  //static final long LIST3 = LIST3;
  static final long LIST4 = list4;
  static final long LIST5 = list5;
  // Long LIST3a = LIST3;
  static final long LIST6 = list6;
  static final long LIST7 = list7;
  static final long LIST8 = list8;
  static final long list9 = lli = lli << 1;// 00100000000000L;
  static final long LIST9 = list9;
  private static final long list10 = lli = lli << 1;// 00200000000000L;
  static final long LIST10 = list10;
  private static final long list11 = lli = lli << 1;// 00400000000000L;
  static final long LIST11 = list11;
  private static final long list12 = lli = lli << 1;// 01000000000000L;
  static final long LIST12 = list12;
  private static final long list13 = lli = lli << 1;// 02000000000000L;
  static final long LIST13 = list13;
  static final long LIST14 = lli = lli << 1;// 04000000000000L;
  static final long LIST15 = lli = lli << 1;// 010000000000000L;
  static final long LIST16 = lli = lli << 1;// 020000000000000L;
  static final long LIST17 = lli = lli << 1;// 040000000000000L;
  static final long LIST18 = lli = lli << 1;// 0100000000000000L;
  static final long LIST19 = lli = lli << 1;// 0200000000000000L;
  static final long LIST20 = lli = lli << 1;// 0400000000000000L;
  static final long LIST21 = lli = lli << 1;// 01000000000000000L;
  static final long LIST22 = lli = lli << 1;// 02000000000000000L;
  static final long LIST23 = lli = lli << 1;// 004000000000000000L;
  static final long LIST24 = lli = lli << 1;// 010000000000000000L;
  static final long LIST25 = lli = lli << 1;// 020000000000000000L;
  static final long LIST26 = lli = lli << 1;// 040000000000000000L;
  static final long LIST27 = lli = lli << 1;// 0100000000000000000L;

  static final long lmask = LIST0 | LIST1 | LIST2 | LIST3 | LIST4 | LIST5 | LIST6 | LIST7 | LIST8 | LIST9 | -LIST10 | LIST11 | LIST12 | LIST13 | LIST14 | LIST15 | LIST16 | LIST17 | LIST18 | LIST19 | LIST20 | LIST21 | LIST22 | LIST23 | LIST24 | LIST25 | LIST26 | LIST27; //0177777777700000000L;

  static final long LMASK = lmask;
  static final long AGESMASK = LIST10 | LIST11 | LIST12 | LIST13 | LIST14 | LIST15 | LIST16 | LIST17; // show available age vals
  static final long LISTYRS = 0L;
  static final long LIST1YRS = LIST1 | LISTYRS;
  static final long LIST3YRS = LIST3 | LISTYRS;  // missed deaths
  static final long LIST4YRS = list4 | LISTYRS;  // trades, accepted rej deaths
  static final long LIST5YRS = list5 | LISTYRS;  // other deaths
  static final long LIST41 = list4 | LIST1;
  static final long LIST40 = list4 | LIST0;   // trades accepted, rej deaths
  static final long LIST410 = list4 | LIST1 | LIST0;   // trades accepted, rej deaths
  static final long LIST51 = LIST5 | LIST1;   // other deaths
  static final long LIST52 = LIST5 | LIST2;
  static final long LIST52YRS = LIST52 | LISTYRS;
  static final long LIST41YRS = list4 | LIST1YRS;
  static final long LIST431YRS = LIST3 | LIST41YRS;
  static final long LIST2YRS = LIST2 | LISTYRS;
  static final long LIST29YRS = LIST2 | LIST9 | LISTYRS;
  static final long LIST29 = LIST2 | LIST9;
  static final long LIST42YRS = list4 | LIST2YRS;
  static final long LIST432YRS = LIST3 | LIST42YRS;
  static final long LIST43YRS = LIST3 | LIST4YRS;
  static final long LIST432 = LIST4 | LIST3 | LIST2;
  static final long LIST4321 = LIST4 | LIST3 | LIST2 | LIST1;
  static final long LIST43210 = LIST4 | LIST3 | LIST2 | LIST1 | LIST0;
  static final long LIST94321 = LIST4 | LIST3 | LIST2 | LIST9 | LIST1;
  static final long LIST4321YRS = LIST1 | LIST432YRS;
  static final long LIST4320YRS = LIST0 | LIST432YRS;
  static final long LIST43210YRS = LIST1 | LIST4320YRS;
  static final long LIST32YRS = LIST2 | LIST3YRS;
  static final long LIST320YRS = LIST0 | LIST32YRS;
  static final long LTRADE = LIST1YRS | LIST4;
  static final long LDEATHS = LIST2YRS | LIST3;
  static final long LIST0YRS = LIST0 | LISTYRS;
  static final long LCURWORTH = LIST0YRS;
  static final long LTRADNFAVR = LIST1YRS;
  static final long LCASTFFRAND = LIST2YRS;
  static final long LRESOURSTAF = LIST0YRS | LIST7;
  static final long LGRONCSTS = LIST0YRS | LIST8;
  static final long LXFR = LIST2YRS | LIST14;
  static final long LDECR = LIST2YRS | LIST13;
  static final long LINCR = LIST2YRS | LIST12;
  static final long LFORFUND = LIST2YRS | LIST19;
  static final long LSWAPSA = LISTYRS | LIST17;
  static final long CURSMASK = CUR | CURAVE | CURUNITS | THISYEAR | THISYEARAVE | THISYEARUNITS;
  static final long CUMSMASK = CUM | CUMAVE | CUMUNITS;
  static final long LISTALL = lmask;
  static final long listall = lmask;

  static final public String gameTextFieldText = "This is to be filled with descriptions of the field over which the mouse hovers";

  TreeMap<String, Integer> valMap = new TreeMap<String, Integer>();
  TreeMap<String, Integer> resMap = new TreeMap<String, Integer>();
//                                    -1         0           1            2             3
  static final double[] MAX_PRINT = {0., 10000000., 100000000., 1000000000., 10000000000., 100000000000., 1000000000000., 10000000000000., 100000000000000., 1000000000000000., 10000000000000000., 100000000000000000., 1000000000000000000., 10000000000000000000., 100000000000000000000., 1000000000000000000000., 10000000000000000000000., 100000000000000000000000., 1.E99};
  static final double[] MULT_PRINT = {0., 10, 100, 1000, 10000, 100000, 1000000, 10000000., 100000000., 1000000000., 10000000000., 100000000000., 1000000000000., 10000000000000., 100000000000000., 1000000000000000., 10000000000000000., 100000000000000000., 1000000000000000000., 10000000000000000000., 100000000000000000000., 1000000000000000000000., 10000000000000000000000., 100000000000000000000000., 1.E99};
  static int abc;
  //static String[][] resS;  // [RN][rDesc,rDetail] result string values
  static int rDesc = 0;
  static int rDetail = 1; // detail or tip text
  static final int MAXDEPTH = 7;

  /**
   * resV [resNum][cum,cur0-6,7-13,14-20,21-27,28-34,35-41][[p0-4],[s0-4]]
   *
   */
  static final int DCUM = 0;
  static final int EXTRA1 = 1;
  static final int EXTRA2 = 2;
  // years starting all,<=3,<=7,<=15,<=31,32++
  static final int DCUR0 = 1;  //the curs
  static final int DCUR1 = DCUR0 + MAXDEPTH * 1; //8  0-3
  static final int DCUR2 = DCUR0 + MAXDEPTH * 2; //15 4-7
  static final int DCUR3 = DCUR0 + MAXDEPTH * 3; //22 8-15
  static final int DCUR4 = DCUR0 + MAXDEPTH * 4; //29 16-31
  static final int DCUR5 = DCUR0 + MAXDEPTH * 5; //36 32+
  static final int DCUR6 = DCUR0 + MAXDEPTH * 6; // 43 start unused next age
  static final int[] AGENUM = {0, 4, 8, 16, 32};
  static final int MAXAGES = AGENUM.length; // 5 ages kept
  static final int STATSLONGLEN = DCUR0 + MAXDEPTH * 6 + EXTRA2; //1 + 42+2 =45
  static final int STATSSHORTLEN = DCUR0 + MAXDEPTH * 1 + EXTRA2; // 1 + 7 + 2 = 10
  static final int STATSLONGLIM = DCUR0 + MAXDEPTH * 6 - 1; // 1 + 42 - 1 = 42
  static final int STATSSHORTLIM = DCUR0 + MAXDEPTH * 1 - 1; // 1 + 7 - 1 = 7

  //static final int D3CUR0 = DCUR0 + 3 * MAXDEPTH; // 1+3*7 = 22
  // static final int D7CUR0 = 43; //starts 1, 8 4=15 8=22 16=29,32=36,end=43+2=45,
  // any row vector less than 9 does not have separate age entries
  // static final int STATSLONGLEN = DCUR0 + MAXDEPTH * 1 + EXTRA2; // 10
  //  static final int STATSLONGLEN = STATSSHORTLEN; // 9
  static final int DVECTOR3L = 2;  // P,S
  // extra start next age group cur0-6 entries in vector2
  static final int[] AGESTARTS = {0, 4, 8, 16, 32, 999999, 9999999}; // + over 31+ group
  static final String[] AGESTR = {"", "0-3", "4-7", "8-15", "16-31", "32+"};

  static final long[] AGELISTS = {LIST0 | LIST1 | LIST2, LIST10, LIST11, LIST12, LIST13, LIST14, LIST15, LIST16, LIST17};
  static final long[] SHORTAGELIST = {LIST0 | LIST1 | LIST2};
  static final int shortLength = SHORTAGELIST.length;
  static final int longLength = AGELISTS.length;
  //static final long AGEMASK = LIST10 | LIST11 | LIST12 | LIST13 | LIST14 | LIST15;
  static final int minDepth = 1; // set min number of output for allYears
  static final int maxDepth = MAXDEPTH; // 0 1 2 3 4 5 6 separation betwen age groups
  static final int minYDepth = 1; // min number of output in year groups
  static final int maxYDepth = 2;
  // vector 4 is LCLANS
  //static volatile double[][][][] resV;
  /**
   * resI [resNum][ICUM,ICUR0,...ICUR6(7*6rounds+2] [PCNTS[LCLANS],SCNTS[LCLANS]
   * ,CCONTROLD[ISSET,IVALID,IPOWER, (ICUM only),LOCKS0...3,IFRACS,IDEPTH]]
   * IDEPTH:1-7 max valid number of rows per age
   * IVALID:0-7,0=unset,1=row0..7=row6, highest set row valid entries
   * 0=unset,1=cur0,2=cur1,7=cur6
   */

  static final int ICUM = 0; // continue vector 2
  static final int ICUR0 = DCUR0;
  static final int ICUR1 = DCUR1;
  static final int ICUR2 = DCUR2;
  static final int ICUR3 = DCUR3;
  static final int ICUR4 = DCUR4;
  static final int ICUR5 = DCUR5;
  static final int ICUR6 = DCUR6;
  static final int IVECTOR2L = STATSLONGLEN;//43 ages < 4,8,16,32,31+
  static final int IVECTOR2A = STATSSHORTLEN;
  // end of second vector definition
  // start definitions for ICUM thru ICUR6, total 6 iterations
  static final int PCNTS = 0; // start 3rd vector on cum thru cur6
  static final int SCNTS = 1;
  static final int CCONTROLD = 2; // in level 3
  static final int IVECTOR3L = 3; // PP
  static final int ISSET = 0;
  static final int IVALID = 1;
  static final int IPOWER = 2;
  static final int IVECTOR4A = 3;
  static final int LOCKS0 = 3;
  static final int LOCKS1 = 4;
  static final int LOCKS2 = 5;
  static final int LOCKS3 = 6;
  static final int IFRACS = 7;   // IN FIRST ONE ONLY, FRACTION DIGITS
  static final int IDEPTH = 8; // 1 == only cur0, 3 = cur0,1,2, 7=cur0-6
  static final int IYDEPTH = 9; // depth year groups
  static final int IVECTOR4C = 10;
  // definitions for PCNTS and SCNTS

  // third vector for pCnts and sCnts
  static final int LCLANS = E.lclans;
  // third vector

  int inputClan = 5; // set in game inputs, 5=game
  int inputPorS = 0; // in inputs 0=P


  static int winner = -1;

  // double gameV[][]; //[gameCnt][pval[],sval[],more[sngl,pmin,pmax,smin,smax]]
  // String[][] gameS; //[gameCnt][desc,detail]
  private boolean unset = true;  // value in this rn nerver been set
  private boolean myUnset = false;
  private boolean myCumUnset = false;
  long valid = 0; // number of cur in this rn valid 2 = 0,1 etc.
  private long myAop = 0;
  private int myRn = 0;
  private String myDetail = "";
  private String dds = description + myDetail;
  private String isPercent = "";
  static int rende4;
  static int e4 = -1;
  static final int SCORE = ++e4;
  static final int LIVEWORTH = ++e4;
  static final int TRADELASTGAVE = ++e4;
  static final int TRADENOMINALGAVE = ++e4;
  static final int TRADESTRATLASTGAVE = ++e4;
  static final int WTRADEDINCRMULT = ++e4;
  static final int DIED = ++e4;
  static final int BOTHCREATE = ++e4;
  static final int LIVERCSG = ++e4;
  static final int INCRRCSG = ++e4;
  static final int INITRCSG = ++e4;
  static final int HIGHRCSG = ++e4;
  static final int LOWRCSG = ++e4;
  static final int MAXRCSG = ++e4;
  static final int MINRCSG = ++e4;

  //static final int TRADESTRATEGICGAVE = ++e4;
  static final int STARTWORTH = ++e4;
  // static final int TESTWORTH3 = ++e4;
  static final int WORTHIFRAC = ++e4;
  static final int WORTHINCR = ++e4;
  static final int TRADEWORTH = ++e4;
  static final int HIGHWORTH = ++e4;
  static final int LOWWORTH = ++e4;
  static final int MISCWORTH = ++e4;
  static final int MISCHIGHWORTH = ++e4;
  static final int MISCLOWWORTH = ++e4;
  static final int DWORTH = ++e4;
  static final int DHIGHWORTH = ++e4;
  static final int DLOWWORTH = ++e4;
  static final int DMISCWORTH = ++e4;
  static final int DMISCHIGHWORTH = ++e4;
  static final int DMISCLOWWORTH = ++e4;
  static final int DIEDPERCENT = ++e4;
  static final int HIGHDIEDPERCENT = ++e4;
  static final int LOWDIEDPERCENT = ++e4;
  static final int MISCDIED = ++e4;
  static final int MISCDIEDPERCENT = ++e4;
  static final int MISCHIGHDIEDPERCENT = ++e4;
  static final int MISCLOWDIEDPERCENT = ++e4;
  static final int WTRADEDINCRSOS = ++e4;
  static final int YEARCREATE = ++e4;
  static final int FUTURECREATE = ++e4;
  static final int RGROWTH = ++e4;
  static final int CGROWTH = ++e4;
  static final int SGROWTH = ++e4;
  static final int GGROWTH = ++e4;
  static final int SGMTGC = ++e4;
  static final int RCMTGC = ++e4;
  static final int SGREQMC = ++e4;
  static final int RCREQMC = ++e4;
  static final int SGREQGC = ++e4;
  static final int RCREQGC = ++e4;
  static final int RRAWMC = ++e4;
  static final int CRAWMC = ++e4;
  static final int RCRAWMC = ++e4;
  static final int SRAWMC = ++e4;
  static final int GRAWMC = ++e4;
  static final int SGRAWMC = ++e4;
  static final int KNOWLEDGEB = ++e4;
  static final int KNOWLEDGEFRAC = ++e4;
  static final int POORKNOWLEDGEEFFECT = ++e4;
  static final int POORHEALTHEFFECT = ++e4;
  static final int MANUALSFRAC = ++e4;
  static final int NEWKNOWLEDGEFRAC = ++e4;
  static final int COMMONKNOWLEDGEFRAC = ++e4;
  static final int KNOWLEDGEINCR = ++e4;
  static final int NEWKNOWLEDGEINCR = ++e4;
  static final int MANUALSINCR = ++e4;
  static final int COMMONKNOWLEDGEINCR = ++e4;
  static final int RCfrac = ++e4;
  static final int SGfrac = ++e4;
  static final int MISSINGNAME = ++e4;
  static final int WORTHINCRN0 = ++e4;
  static final int GROWTHSN0 = ++e4;
  static final int FERTILITYSN0 = ++e4;
  static final int RCSGINCRN0 = ++e4;
  static final int WORTHINCRN1 = ++e4;
  static final int GROWTHSN1 = ++e4;
  static final int FERTILITYSN1 = ++e4;
  static final int RCSGINCRN1 = ++e4;
  static final int WORTHINCRN2 = ++e4;
  static final int GROWTHSN2 = ++e4;
  static final int FERTILITYSN2 = ++e4;
  static final int RCSGINCRN2 = ++e4;
  static final int WORTHINCRN3 = ++e4;
  static final int GROWTHSN3 = ++e4;
  static final int FERTILITYSN3 = ++e4;
  static final int RCSGINCRN3 = ++e4;
  static final int DWORTHINCRN0 = ++e4;
  static final int DGROWTHSN0 = ++e4;
  static final int DFERTILITYSN0 = ++e4;
  static final int DRCSGINCRN0 = ++e4;
  static final int DWORTHINCRN1 = ++e4;
  static final int DGROWTHSN1 = ++e4;
  static final int DFERTILITYSN1 = ++e4;
  static final int DRCSGINCRN1 = ++e4;
  static final int DWORTHINCRN2 = ++e4;
  static final int DGROWTHSN2 = ++e4;
  static final int DFERTILITYSN2 = ++e4;
  static final int DRCSGINCRN2 = ++e4;
  static final int DWORTHINCRN3 = ++e4;
  static final int DGROWTHSN3 = ++e4;
  static final int DFERTILITYSN3 = ++e4;
  static final int DRCSGINCRN3 = ++e4;
  /*
        int[] worthIncrA = {EM.WORTHINCRN0,EM.WORTHINCRN1,EM.WORTHINCRN2,EM.WORTHINCRN3};
        int[] growthsA =  {EM.GROWTHSN0,EM.GROWTHSN1,EM.GROWTHSN2,EM.GROWTHSN3};
        int[] fertilitiesA =  {EM.FERTILITYSN0,EM.FERTILITYSN1,EM.FERTILITYSN2,EM.FERTILITYSN3};
        int[] rcsgIncrA = {EM.RCSGINCRN0,EM.RCSGINCRN1,EM.RCSGINCRN2,EM.RCSGINCRN3};
  */
  static final int TRADELASTRECEIVE = ++e4;
  static final int TRADESTRATLASTRECEIVE = ++e4;
  static final int TRADERECEIVELASTPERCENTFIRST = ++e4;
  static final int TradeLastStrategicValue = ++e4;
  static final int AlsoTradeLastStrategicValue = ++e4;
  static final int TradeLastStrategicGoal = ++e4;
  static final int TradeStrategicValueLastPercentFirst = ++e4;
  static final int rejectNegRequests = ++e4;
  static final int rejectNegRequestsTerm = ++e4;
  static final int rejectTinyRequests = ++e4;
  static final int rejectTinyRequestsSv = ++e4;
  static final int rejectTinyRequestsFirst = ++e4;
  static final int rejectTinyRequestsPercentFirst = ++e4;
  static final int rejectTinyRequestsTerm = ++e4;
  static final int rejectGoal0 = ++e4;
  static final int rejectGoal0Term = ++e4;
  static final int rejectAtOne = ++e4;
  static final int rejectAtOneTerm = ++e4;
  //static final int DEADSWAPSNCOUNT = ++e4;
  static final int SWAPRINCRCOST = ++e4;
  static final int SWAPSINCRCOST = ++e4;
  static final int SWAPRDECRCOST = ++e4;
  static final int SWAPSDECRCOST = ++e4;
  static final int SWAPRXFERCOST = ++e4;
  static final int SWAPSXFERCOST = ++e4;
  static final int INCRAVAILFRAC5 = ++e4;
  static final int INCRAVAILFRAC4 = ++e4;
  static final int INCRAVAILFRAC3 = ++e4;
  static final int INCRAVAILFRAC2 = ++e4;
  static final int INCRAVAILFRAC1 = ++e4;
  static final int INCRAVAILFRAC0 = ++e4;
  static final int INCRAVAILFRAC = ++e4;  // sll svvrpyrf
  static final int INCRAVAILFRACa = ++e4;
  static final int INCRAVAILFRACb = ++e4;
  static final int RDEPRECIATIONP = ++e4;
  static final int CDEPRECIATIONP = ++e4;  // sll svvrpyrf
  static final int SDEPRECIATIONP = ++e4;
  static final int GDEPRECIATIONP = ++e4;
  static final int RDADEPRECIATIONP = ++e4;
  static final int CDADEPRECIATIONP = ++e4;  // sll svvrpyrf
  static final int SDADEPRECIATIONP = ++e4;
  static final int GDADEPRECIATIONP = ++e4;
  static final int RDDEPRECIATIONP = ++e4;
  static final int CDDEPRECIATIONP = ++e4;  // sll svvrpyrf
  static final int SDDEPRECIATIONP = ++e4;
  static final int GDDEPRECIATIONP = ++e4;
  static final int DeadNegN = ++e4;
  static final int DeadLt5 = ++e4;
  static final int DeadLt10 = ++e4;
  static final int DeadLt20 = ++e4;
  static final int DeadNegProsp = ++e4;
  static final int DeadRatioS = ++e4;
  static final int DeadRatioR = ++e4;

  static final int DIEDCATASTROPHY = ++e4;
  static final int DEADRATIO = ++e4;
  static final int DEADHEALTH = ++e4;
  static final int DEADFERTILITY = ++e4;
  static final int DEADSWAPSMOVED = ++e4;
  static final int DEADSWAPSCOSTS = ++e4;
  static final int DEADTRADED = ++e4;
  static final int DTRADEACC = ++e4;
  static final int DTRADEOSOSR0 = ++e4;
  static final int DTRADEOSOSR1 = ++e4;
  static final int DTRADEOSOSR2 = ++e4;
  static final int DTRADEOSOSR3 = ++e4;
  static final int DLOSTOSOSR0 = ++e4;
  static final int DLOSTOSOSR1 = ++e4;
  static final int DLOSTOSOSR2 = ++e4;
  static final int DLOSTOSOSR3 = ++e4;
  static final int DLOSTSOSR1 = ++e4;
  //static final int DLOSTR1 = ++e4;
  static final int DTRADESOSR3 = ++e4;
  static final int DTRADESOSR2 = ++e4;
  static final int DTRADESOSR1 = ++e4;
  static final int DTRADESOSR0 = ++e4;
  static final int DIEDSN4 = ++e4;
  static final int DIEDRN4 = ++e4;
  static final int DIEDSN4RM3X5 = ++e4;
  static final int DIEDSN4RM3X4 = ++e4;
  static final int DIEDSM3X5 = ++e4;
  static final int DIEDRM3X4 = ++e4; // END NOT IN IF ELSE CHAIN
  static final int DIEDSN4RN4 = ++e4;
  static final int DIEDSN3RN3 = ++e4;
  static final int DIEDSN3RN2 = ++e4;
  static final int DIEDSN3RM3X4 = ++e4;
  static final int DIEDSN3RM3X3 = ++e4;
  static final int DIEDSN3RN1 = ++e4;
  static final int DIEDSN3RM3X2 = ++e4;
  static final int DIEDSN3RM3X1 = ++e4;
  static final int DIEDSN3RM2X4 = ++e4;
  static final int DIEDSN3RM2X3 = ++e4;
  static final int DIEDSN3RM2X2 = ++e4;
  static final int DIEDSN3RM2X1 = ++e4;
  static final int DIEDSN3RM1X4 = ++e4;
  static final int DIEDSN3RM1X3 = ++e4;
  static final int DIEDSN3RM1X2 = ++e4;
  static final int DIEDSN3RM1X1 = ++e4;
  static final int DIEDSN2RN3 = ++e4;
  static final int DIEDSN2RN2 = ++e4;
  static final int DIEDSN2RM3X4 = ++e4;
  static final int DIEDSN2RM3X3 = ++e4;
  static final int DIEDSN2RN1 = ++e4;
  static final int DIEDSN2RM3X2 = ++e4;
  static final int DIEDSN2RM3X1 = ++e4;
  static final int DIEDSN2RM2X4 = ++e4;
  static final int DIEDSN2RM2X3 = ++e4;
  static final int DIEDSN2RM2X2 = ++e4;
  static final int DIEDSN2RM2X1 = ++e4;
  static final int DIEDSN2RM1X4 = ++e4;
  static final int DIEDSN2RM1X3 = ++e4;
  static final int DIEDSN2RM1X2 = ++e4;
  static final int DIEDSN2RM1X1 = ++e4;
  static final int DIEDSN1RN3 = ++e4;
  static final int DIEDSN1RN2 = ++e4;
  static final int DIEDSN1RM3X4 = ++e4;
  static final int DIEDSN1RM3X3 = ++e4;
  static final int DIEDSN1RN1 = ++e4;
  static final int DIEDSN1RM3X2 = ++e4;
  static final int DIEDSN1RM3X1 = ++e4;
  static final int DIEDSN1RM2X4 = ++e4;
  static final int DIEDSN1RM2X3 = ++e4;
  static final int DIEDSN1RM2X2 = ++e4;
  static final int DIEDSN1RM2X1 = ++e4;
  static final int DIEDSN1RM1X4 = ++e4;
  static final int DIEDSN1RM1X3 = ++e4;
  static final int DIEDSN1RM1X2 = ++e4;
  static final int DIEDSM3X4RN3 = ++e4;
  static final int DIEDSM3X3RN3 = ++e4;
  static final int DIEDSM3X2RN3 = ++e4;
  static final int DIEDSM3X1RN3 = ++e4;
  static final int DIEDSM2X4RN3 = ++e4;
  static final int DIEDSM2X3RN3 = ++e4;
  static final int DIEDSM2X2RN3 = ++e4;
  static final int DIEDSM2X1RN3 = ++e4;
  static final int DIEDSM1X4RN3 = ++e4;
  static final int DIEDSM1X3RN3 = ++e4;
  static final int DIEDSM1X2RN3 = ++e4;
  static final int DIEDSM1X1RN3 = ++e4;
  static final int DIEDSM3X4RN2 = ++e4;
  static final int DIEDSM3X3RN2 = ++e4;
  static final int DIEDSM3X2RN2 = ++e4;
  static final int DIEDSM3X1RN2 = ++e4;
  static final int DIEDSM2X4RN2 = ++e4;
  static final int DIEDSM2X3RN2 = ++e4;
  static final int DIEDSM2X2RN2 = ++e4;
  static final int DIEDSM2X1RN2 = ++e4;
  static final int DIEDSM1X4RN2 = ++e4;
  static final int DIEDSM1X3RN2 = ++e4;
  static final int DIEDSM1X2RN2 = ++e4;
  static final int DIEDSM1X1RN1 = ++e4;
  static final int DIEDSM3X4RN1 = ++e4;
  static final int DIEDSM3X3RN1 = ++e4;
  static final int DIEDSM3X2RN1 = ++e4;
  static final int DIEDSM3X1RN1 = ++e4;
  static final int DIEDSM2X4RN1 = ++e4;
  static final int DIEDSM2X3RN1 = ++e4;
  static final int DIEDSM2X2RN1 = ++e4;
  static final int DIEDSM2X1RN1 = ++e4;
  static final int DIEDSM1X4RN1 = ++e4;
  static final int DIEDSM1X3RN1 = ++e4;
  static final int DIEDSM1X2RN1 = ++e4;
  static final int DIEDSN1RM1X1 = ++e4;
  static final int TradeFirstStrategicValue = ++e4;

  static final int TradeCriticalBidRequestsFirst = ++e4;
  static final int TradeCriticalBidRequests = ++e4;
  static final int TradeBidRequestsFirst = ++e4;
  static final int TradeBidRequests = ++e4;
  static final int TradeAlsoCriticalBidRequestsFirst = ++e4;
  static final int TradeAlsoCriticalBidRequests = ++e4;
  static final int TradeAlsoBidRequestsFirst = ++e4;
  static final int TradeAlsoBidRequests = ++e4;
  static final int TRADEFIRSTRECEIVE = ++e4;
  ;
  static final int TRADEFIRSTGAVE = ++e4;
  static final int TRADESTRATFIRSTRECEIVE = ++e4;

  static final int TRADESTRATFIRSTGAVE = ++e4;
  static final int TradeNominalReceivePercentNominalOffer = ++e4;
  static final int MaxNominalReceivePercentNominalOffer = ++e4;
  static final int MinNominalReceivePercentNominalOffer = ++e4;
  static final int TradeStrategicReceivePercentStrategicOffer = ++e4;
  static final int MaxStrategicReceivePercentStrategicOffer = ++e4;
  static final int MinStrategicReceivePercentStrategicOffer = ++e4;
  static final int YearTradeNominalReceivePercentNominalOffer = ++e4;
  static final int YearMaxNominalReceivePercentNominalOffer = ++e4;
  static final int YearMinNominalReceivePercentNominalOffer = ++e4;
  static final int YearTradeStrategicReceivePercentStrategicOffer = ++e4;
  static final int YearMaxStrategicReceivePercentStrategicOffer = ++e4;
  static final int YearMinStrategicReceivePercentStrategicOffer = ++e4;
  static final int TRADESOS0 = ++e4;
  static final int TRADESOS1 = ++e4;
  static final int TRADESOS2 = ++e4;
  static final int TRADESOS3 = ++e4;
  static final int TRADEOSOS0 = ++e4;
  static final int TRADEOSOS1 = ++e4;
  static final int TRADEOSOS2 = ++e4;
  static final int TRADEOSOS3 = ++e4;
  static final int TRADESOSR1 = ++e4;
  static final int TRADESOSR2 = ++e4;
  static final int TRADESOSR3 = ++e4;
  static final int TRADEOSOSR1 = ++e4;
  static final int TRADEOSOSR2 = ++e4;
  static final int TRADEOSOSR3 = ++e4;
  static final int BEFORETRADEWORTH = ++e4;
  static final int AFTERTRADEWORTH = ++e4;
  static final int TRADEWORTHINCRPERCENT = ++e4;
  static final int TRADEWORTHINCR = ++e4;
  static final int TradeAcceptValuePerGoal = ++e4;
  static final int TradeRejectValuePerGoal = ++e4;
  static final int TradeLostValuePerGoal = ++e4;
  
 
  static final int TradeFirstStrategicGoal = ++e4;
  static final int AlsoTradeStrategicValueLastPercentFirst = ++e4;
  static final int TradeRejectedStrategicGoal = ++e4;
  static final int TradeLostStrategicGoal = ++e4;
  static final int TradeRejectedStrategicValue = ++e4;
  static final int TradeLostStrategicValue = ++e4;
  static final int TradeMissedStrategicGoal = ++e4;
  static final int TradeDeadLostStrategicGoal = ++e4;
  static final int TradeDeadLostStrategicValue = ++e4;
  static final int TradeDeadRejectedStrategicGoal = ++e4;
  static final int TradeDeadStrategicGoal = ++e4;
  static final int TradeDeadRejectedStrategicValue = ++e4;
  static final int TradeDeadStrategicValue = ++e4;
  static final int TradeDeadMissedStrategicGoal = ++e4;
  static final int RCTWORTH = ++e4;
  static final int RCWORTH = ++e4;
  static final int RCTBAL = ++e4;
  static final int RCBAL = ++e4;
  static final int RCTGROWTHPERCENT = ++e4;
  static final int RCWORTHGROWTHPERCENT = ++e4;
  static final int RCGLT10PERCENT = ++e4;
  static final int RCWGLT10PERCENT = ++e4;
  static final int RCGLT100PERCENT = ++e4;
  static final int CRISISINCR = ++e4;
  static final int CRISIS2INCR = ++e4;
  static final int CRISIS3INCR = ++e4;
  static final int NOCRISISINCR = ++e4;
  static final int NOCRISIS2INCR = ++e4;
  static final int NOCRISIS3INCR = ++e4;
  static final int CRISISRESREDUCEPERCENT = ++e4;
  static final int CRISISRESDECAYBONUSPERCENT = ++e4;
  static final int CRISISSTAFFREDUCEPERCENT = ++e4;
  static final int CRISISRESREDUCESURPLUSPERCENT = ++e4;
  static final int CRISISSTAFFREDUCESURPLUSPERCENT = ++e4;
  static final int CRISISSTAFFDECAYBONUSPERCENT = ++e4;
  static final int CRISISSTAFFGROWTHPERCENTINCR = ++e4;
  static final int CRISISSTAFFGROWTHYEARSINCR = ++e4;
  static final int CRISISRESGROWTHPERCENTINCR = ++e4;
  static final int CRISISRESGROWTHYEARSINCR = ++e4;
  static final int CRISISMANUALSPERCENTINCR = ++e4;
  static final int sumCatEffRBen = ++e4;
  static final int sumCatEffSBen = ++e4;
  static final int sumCatEffManualsBen = ++e4;
  static final int sumCatEffKnowBen = ++e4;
  static final int sumCatEffRDecayBen = ++e4;
  static final int sumCatEffSDecayBen = ++e4;
  // static final int TESTWORTH4 = ++e4;
  /// static final int TESTWORTH5 = ++e4;
//  static final int TESTWORTH6 = ++e4;
  // static final int TESTWORTH7 = ++e4;
  // static final int TESTWORTH8 = ++e4;
  static final int rendae4 = e4;

  void defRes() {

    doRes(SCORE, "Score", "Winner must have a score sufficiently larger than any other clan and after sufficient years have passed.  Winner has the highest score the result of combining the different scores set by several value entries which increase the score, Winner is dynamic and can change as individual clan settings are changed and changed results occur", 3, 4, 3,LIST0 | LIST7 | LIST8 | LIST9 | LIST43210YRS | THISYEAR | SUM, 0, 0, 0);   
    doRes(LIVEWORTH, "Live Worth", "Live Worth Value including year end working, reserve: resource, staff, knowledge", 2, 2, 0, LIST0 | LIST6 | LIST7 | LIST8 | LIST9 | THISYEAR  | SUM, LIST0 | LIST6 | LIST7 | LIST8 | THISYEAR | THISYEARUNITS | THISYEARAVE | BOTH, ROWS1 | LIST432 | LIST5 | LIST6 | CUMUNITS | CUM | CUMAVE | BOTH | SKIPUNSET, LIST9 | LIST14 | CUR |  SKIPUNSET);
    doRes(TRADELASTGAVE, "TradelLastGiven", "last goods given strategic worth", 2, 2, 2, LIST40 | LIST15 | THISYEAR | THISYEARAVE | THISYEARUNITS | BOTH | SKIPUNSET, LIST0 | CUM | CUMUNITS | BOTH | SKIPUNSET,   LIST15 | CURAVE | BOTH | SKIPUNSET, 0L);
    doRes(TRADENOMINALGAVE, "TradeNominalGiven", "Nominal worth not strategic worth given in trade ", 2, 2, 2, LIST40  | THISYEAR | THISYEARAVE | THISYEARUNITS | BOTH | SKIPUNSET,  ROWS3 | LIST15 | CURAVE | BOTH | SKIPUNSET, ROWS3 | LIST15 | CURUNITS | CUR | BOTH | SKIPUNSET,0L);
   doRes(TRADESTRATLASTGAVE, "TradeStrategicLastGave", "Percent nominal amount given in trade per sumrcsg may be used for scoreing", 2, 2, 2, LIST40 | LIST15 | THISYEAR | THISYEARAVE | THISYEARUNITS | CUM | CUMUNITS | BOTH | SKIPUNSET, ROWS3 | LIST15 | CURAVE | BOTH | SKIPUNSET, 0L, 0L);
   doRes(WTRADEDINCRMULT, "Trd%IncW", "% Years worth increase at total trades this year/start year worth part of scoring ", 2, 3, 2, LIST0 | LIST1 | LIST4 | THISYEAR | BOTH | SKIPUNSET, 0, 0, 0);
    doRes(DIED, "DIED", "planets or ships died", 2, 2, 3, LIST0 | LIST3 | LIST4 | LIST6 | LIST14 | THISYEARUNITS | CUMUNITS | BOTH, 0, 0, 0);
    doRes(BOTHCREATE, "bothCreations", "new Econs ceated from  game funds and future funds");
    doRes(INITRCSG, "init rcsg", "Initial rcsg Value including year end rcsg", 2, 2, 0,  LIST7 | LIST8 | LIST9 | THISYEARAVE | BOTH, 0, 0, 0);
 //  doRes(RCSG, "rcsg", " rcsg Value at year end rcsg", 2, 2, 0, LIST0 | LIST7 | LIST8 | LIST9 | THISYEAR | THISYEARAVE | BOTH, 0, 0, 0);
    doRes(LIVERCSG, "Live rcsg", "Live rcsg Value including year end rcsg", 2, 2, 0, LIST7 | LIST8 | LIST9 | THISYEARAVE | BOTH, 0, 0, 0);
    doRes(INCRRCSG, "%incr rcsg", "this years incr rcsg Value  year end rcsg - start rcsg", 2, 2, 0, LIST0 | LIST7 | LIST8 | LIST9 | THISYEAR | THISYEARAVE | BOTH, 0, 0, 0);
    doRes(HIGHRCSG, "high rcsg", "high rcsg count ", 2, 2, 0, LIST7 | LIST8 | LIST9 | LIST13 | LIST14 | LIST15 | LIST16 | LIST17 | THISYEARAVE | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes(LOWRCSG, "low rcsg", "low rcsg count", 2, 2, 0, LIST7 | LIST8 | LIST9 | THISYEARAVE | BOTH, 0, 0, 0);
    doRes(MAXRCSG, "max rcsg", "max rcsg Value");
    doRes(MINRCSG, "min rcsg", "min rcsg Value");
    doRes(STARTWORTH, "Starting Worth", "Starting Worth Value including working, reserve: resource, staff, knowledge", 2, 2, 0, LIST7 | LIST8 | LIST9 | ROWS3 | THISYEAR | SUM | SKIPUNSET, ROWS1 | LIST7 | LIST8 | LIST9 | THISYEAR | THISYEARAVE | BOTH | SKIPUNSET, 0L, 0L);
    doRes(WORTHIFRAC, "PercInitWorth ", "Percent increase of Final/Initial Worth Value including working, reserve: resource, staff, knowledge", 2, 2, 0, LIST7 | LIST8 | LIST9 | ROWS3 | THISYEAR | SUM | SKIPUNSET, ROWS1 | LIST7 | LIST8 | LIST9 | THISYEAR | THISYEARAVE | BOTH | SKIPUNSET, 0L, 0L);
    doRes(WORTHINCR, "YrIncWorth", "worth increase this year", 2, 2, 0, LIST0 | LIST1 |LIST2 | LIST7 | LIST8 | LIST9 | THISYEAR | THISYEARAVE | BOTH | SKIPUNSET, 0L, 0L, 0L);
    /*
      static final int HIGHWORTH = ++e4;
  static final int LOWWORTH = ++e4;
  static final int MISCWORTH = ++e4;
   static final int MISCHIGHWORTH = ++e4;
  static final int MISCLOWWORTH = ++e4;
   static final int DWORTH = ++e4;
   static final int DHIGHWORTH = ++e4;
  static final int DLOWWORTH = ++e4;
  static final int DMISCWORTH = ++e4;
   static final int DMISCHIGHWORTH = ++e4;
  static final int DMISCLOWWORTH = ++e4;
     */
    doRes(TRADEWORTH, "Trade worth", "trade accepted Econs average yearly worth", 2, 2, 0, LIST7 | LIST15 | LIST16 | THISYEAR | THISYEARAVE | BOTH, 0, 0, 0);
    doRes(HIGHWORTH, "T high worth", "trade accepted high Econs average yearly worth", 2, 2, 0, LIST7 | LIST16 | THISYEARAVE | BOTH, 0, 0, 0);
    doRes(LOWWORTH, "T low worth", "trade acceptedlow Econs average yearly worth", 2, 2, 0, LIST7 | LIST16 | THISYEARAVE | BOTH, 0, 0, 0);

    doRes(MISCWORTH, " misc worth", "No trade Econs yearly worth", 2, 2, 0, LIST7 | LIST16 | THISYEARAVE | BOTH, 0, 0, 0);
    doRes(MISCHIGHWORTH, "misc hworth", "No trade high Econs average yearly worth", 2, 2, 0, LIST7 | LIST16 | THISYEARAVE | BOTH, 0, 0, 0);
    doRes(MISCLOWWORTH, "misc lworth", "No trade low  Econs average yearly worth", 2, 2, 0, LIST7 | LIST16 | THISYEARAVE | BOTH, 0, 0, 0);
    doRes(DWORTH, "DTworth", "died trade Econs average yearly worth", 2, 2, 0, LIST3 | LIST11 | LIST7 | LIST16 | THISYEARAVE | BOTH, 0, 0, 0);
    doRes(DHIGHWORTH, "DTHworth", "Died trade high Econs average yearly worth");
    doRes(DLOWWORTH, "DTL worth", "Died trade low Econs average yearly worth");

    doRes(DMISCWORTH, " DNworth", "Died No trade Econs yearly worth", 2, 2, 0, LIST4 | LIST10 | LIST7 | LIST16 | THISYEARAVE | BOTH, 0, 0, 0);
    doRes(DMISCHIGHWORTH, "DNHworth", "Died No trade high Econs average yearly worth");
    doRes(DMISCLOWWORTH, "DNLworth", "Died No trade low  Econs average yearly worth");
    doRes(YEARCREATE, "yearCreations", "Econs ceated this year by the game funds");
    doRes(FUTURECREATE, "FutureFund Create", "Econs created from clan Future Funds");
    doRes(DIEDPERCENT, "DIED %", "Percent planets or ships died", 2, 2, 3, LIST0 | LIST2 | LIST3 | LIST4 | LIST6 | THISYEARAVE | BOTH, ROWS2 | LIST3 | LIST4 | LIST5 | LIST10 | LIST11 | CUMAVE | BOTH | SKIPUNSET, 0, 0);
    doRes(HIGHDIEDPERCENT, "HIDIED %", "HI worths Percent planets or ships died trade accepted", 2, 2, 3, ROWS1 | LIST2 | LIST3 | LIST4 | THISYEARAVE | BOTH, ROWS2 | LIST3 | LIST4 | LIST5 | LIST10 | LIST11 | CUMAVE | BOTH | SKIPUNSET, 0, 0);
    doRes(LOWDIEDPERCENT, "LODIED %", "LO worths Percent planets or ships died trade accepted", 2, 2, 3, ROWS1 | LIST2 | LIST3 | LIST4 | THISYEARAVE | BOTH, ROWS2 | LIST3 | LIST4 | LIST5 | LIST10 | LIST11 | CUMAVE | BOTH | SKIPUNSET, 0, 0);
    doRes(MISCDIED, "NT DIED", "planets or ships died no trade accepted", 2, 2, 3, LIST0 | LIST2 | LIST3 | LIST4 | LIST10 | THISYEARUNITS | BOTH, ROWS2 | LIST3 | LIST4 | CUMUNITS | BOTH | SKIPUNSET, 0, 0);
    doRes(MISCDIEDPERCENT, "NT DIED %", "Percent planets or ships died no trade accepted", 2, 2, 3, LIST0 | LIST2 | LIST3 | LIST4 | LIST10 | THISYEARAVE | BOTH, ROWS2 | LIST3 | LIST4 | CUMAVE | BOTH | SKIPUNSET, 0, 0);
    doRes(MISCHIGHDIEDPERCENT, "MHIDIED %", "HI worth Percent planets or ships died no trade accepted", 2, 2, 3, ROWS1 | LIST2 | LIST3 | LIST4 | LIST10 | THISYEARAVE | BOTH, ROWS2 | LIST3 | LIST4 | LIST10 | CUMAVE | BOTH | SKIPUNSET, 0, 0);
    doRes(MISCLOWDIEDPERCENT, "MLODIED %", "LO worth Percent planets or ships died no trade accepted", 2, 2, 3, ROWS1 | LIST2 | LIST3 | LIST4 | LIST10 | THISYEARAVE | BOTH, ROWS2 | LIST3 | LIST4 | LIST10 | CUMAVE | BOTH | SKIPUNSET, 0, 0);
    doRes(RGROWTH, "r growth", "resource gowth as perscent of balance", 1, 2, 3, LIST13 | CURAVE | CUMAVE | SKIPUNSET, 0, 0, 0);
    doRes(CGROWTH, "c growth", "cargo gowth as perscent of balance");
    doRes(SGROWTH, "s growth", "staff gowth as perscent of balance");
    doRes(GGROWTH, "g growth", "guest gowth as perscent of balance");
    doRes("swapRIncr", "swapRIncr", "Uses of R Incr Swap percent of RC", 1, 2, 0, list8 | CUMUNITS | CURUNITS | both | SKIPUNSET, 0, 0, 0);
    doRes("swapSIncr", "swapSIncr", "Uses of S Incr Swap percent of SG");
    doRes("swapSDecr", "swapSDecr", "Uses of S Decr Swap percent of SG");
    doRes("swapRDecr", "swapRDecr", "Uses of R Decr Swap percent of RC");
    doRes("swapRRXchg", "swapRRXchg", "Uses of R Xchg Rcost Swap percent of RC");
    doRes("swapRSXchg", "swapRSXchg", "Uses of R Xchg Scost Swap percent of RC");
    doRes("swapSSXchg", "swapSSXchg", "Uses of S Xchg Scost Swap percent of RC");
    doRes("swapSRXchg", "swapSRXchg", "Uses of S Xchg Rcost Swap percent of RC");
    doRes(MISSINGNAME, "missing name", "tried an unknown name", 6, 2, 0, LIST0 | CUMUNITS | CURUNITS | CURAVE | CUMAVE | SKIPUNSET | both, 0, 0, 0);
     /*
        int[] worthIncrA = {EM.WORTHINCRN0,EM.WORTHINCRN1,EM.WORTHINCRN2,EM.WORTHINCRN3};
        int[] growthsA =  {EM.GROWTHSN0,EM.GROWTHSN1,EM.GROWTHSN2,EM.GROWTHSN3};
        int[] fertilitiesA =  {EM.FERTILITYSN0,EM.FERTILITYSN1,EM.FERTILITYSN2,EM.FERTILITYSN3};
        int[] rcsgIncrA = {EM.RCSGINCRN0,EM.RCSGINCRN1,EM.RCSGINCRN2,EM.RCSGINCRN3};
  */
    doRes(WORTHINCRN0,"AccWorthIncr","yearly increase in worth if trade accepted",2,2,2,LIST15 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(WORTHINCRN1,"noAcc1WorInc","yearly increase in worth if trade not accepted for 1 year",2,2,2,LIST12 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(WORTHINCRN2,"noAcc2WorInc","yearly increase in worth if trade not accepted for 2 years",2,2,2,LIST12 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(WORTHINCRN3,"noAcc3WorInc","yearly increase in worth if trade not accepted for 3 years",2,2,2,LIST12 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(GROWTHSN0,"AccGrowths","yearly growth if trade accepted",2,2,2,LIST15 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(GROWTHSN1,"noAcc1Growths","yearly growth if trade not accepted for 1 year",2,2,2,LIST12 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(GROWTHSN2,"noAcc2Growths","yearly growth if trade not accepted for 2 years",2,2,2,LIST12 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(GROWTHSN3,"noAcc3Growths","yearly growth if trade not accepted for 3 years",2,2,2,LIST12 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(FERTILITYSN0,"AccFertility","fertility if trade accepted",2,2,2,LIST15 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(FERTILITYSN1,"noAcc1Fertility","fertility if trade not accepted for 1 year",2,2,2,LIST12 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(FERTILITYSN2,"noAcc2Fertility","fertility if trade not accepted for 2 years",2,2,2,LIST12 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(FERTILITYSN3,"noAcc3Fertility","fertility if trade not accepted for 3 years",2,2,2,LIST12 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(RCSGINCRN0,"AccRCSGIncr","yearly increase in RCSG if trade accepted",2,2,2,LIST15 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(RCSGINCRN1,"noAcc1RCSGInc","yearly increase in RCSG if trade not accepted for 1 year",2,2,2,LIST12 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(RCSGINCRN2,"noAcc2RCSGInc","yearly increase in RCSG if trade not accepted for 2 years",2,2,2,LIST12 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(RCSGINCRN3,"noAcc3RCSGInc","yearly increase in RCSG if trade not accepted for 3 years",2,2,2,LIST12 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DWORTHINCRN0,"dAccWorthIncr","died yearly increase in worth if trade accepted",2,2,2,LIST10 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DWORTHINCRN1,"dnoAcc1WorInc","died yearly increase in worth if trade not accepted for 1 year",2,2,2,LIST10 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DWORTHINCRN2,"dnoAcc2WorInc","died yearly increase in worth if trade not accepted for 2 years",2,2,2,LIST10 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DWORTHINCRN3,"dnoAcc3WorInc","died yearly increase in worth if trade not accepted for 3 years",2,2,2,LIST10 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DGROWTHSN0,"dAccGrowths","died yearly growth if trade accepted",2,2,2,LIST11 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DGROWTHSN1,"dnoAcc1Growths","died yearly growth if trade not accepted for 1 year",2,2,2,LIST10 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DGROWTHSN2,"dnoAcc2Growths","died yearly growth if trade not accepted for 2 years",2,2,2,LIST10 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DGROWTHSN3,"dnoAcc3Growths","died yearly growth if trade not accepted for 3 years",2,2,2,LIST10 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DFERTILITYSN0,"dAccFertility","died fertility if trade accepted",2,2,2,LIST11 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DFERTILITYSN1,"dnoAcc1Fertility","died fertility if trade not accepted for 1 year",2,2,2,LIST10 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DFERTILITYSN2,"dnoAcc2Fertility","died fertility if trade not accepted for 2 years",2,2,2,LIST10 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DFERTILITYSN3,"dnoAcc3Fertility","died fertility if trade not accepted for 3 years",2,2,2,LIST10 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DRCSGINCRN0,"dAccRCSGIncr","died yearly increase in RCSG if trade accepted",2,2,2,LIST11 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DRCSGINCRN1,"dnoAcc1RCSGInc","died yearly increase in RCSG if trade not accepted for 1 year",2,2,2,LIST10 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DRCSGINCRN2,"dnoAcc2RCSGInc","died yearly increase in RCSG if trade not accepted for 2 years",2,2,2,LIST10 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
    doRes(DRCSGINCRN3,"dnoAcc3RCSGInc","died yearly increase in RCSG if trade not accepted for 3 years",2,2,2,LIST10 | CURAVE |BOTH | SKIPUNSET, 0,0,0);
  
    doRes(rejectTinyRequestsFirst, "rejectRequests1", "trade with a offer too small show Requests first value", 2, 2, 0, LIST4 | LIST5 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes(rejectTinyRequestsPercentFirst, "rejectTReq%First", "trade with a offer too small show request percent of first request", 2, 2, 0, LIST4 | LIST5 | LIST15 | THISYEARAVE | BOTH, 0, 0, 0);
    doRes(rejectTinyRequestsTerm, "rejectTinyReqTerm", "trade with a offer too small show term");
    doRes(rejectTinyRequests, "rejTinyRequests", "Rejected trade with a offer too small show strategicValue percent of strategicGoal");
    doRes(rejectTinyRequestsSv, "statTinyReqSv", "trade with a offer too small show strategicValue ", 2, 2, 0, LIST4 | LIST15 | CURUNITS | SKIPUNSET | BOTH, ROWS2 | LIST15 | CUMAVE | CURAVE | SKIPUNSET | BOTH, 0, 0);
    doRes(rejectNegRequests, "rejectNegRequests", "Rejected trade with a negative offer show strategicValue percent of strategicGoal", 2, 2, 0, 0, ROWS2 | LIST10 | CUMAVE | CURAVE | SKIPUNSET | BOTH, 0, 0);
    doRes(rejectNegRequestsTerm, "rejectNegReqTerm", "Rejected trade with a negative offer show term", 2, 2, 0, 0, ROWS1 | CURAVE | SKIPUNSET | BOTH, 0, 0);
    doRes(rejectGoal0, "rejectGoal0", "Rejected trade when the term 0 offer is too small request show strategicValue percent of strategicGoal");
    doRes(rejectGoal0Term, "rejectGoal0Term", "Rejected trade when the term 0 offer is too small request show term");
    doRes(rejectAtOne, "rejectAtOne", "Rejected forced trade offer is too small show strategicValue percent of strategicGoal");
    doRes(rejectAtOneTerm, "rejectAtOneTerm", "Rejected forced trade offer is too small show previous  term");
    // doRes(DIED, "died", "planets or ships died for any cause",2,2,3,0L,ROWS1 | LIST0 | LIST3 | LISTYRS | THISYEARUNITS | BOTH | SKIPUNSET, ROWS2 |  LIST0 | LIST2 | LIST3  | CUMUNITS | BOTH | SKIPUNSET,0L);
    doRes(DIEDCATASTROPHY, "DiedAfterCrisis", "Died after catastrophy percent worth would have increased", 1, 1, 3, ROWS1 | LIST3 | LIST6 | LIST10 | LIST11 | CUMUNITS | BOTH | SKIPUNSET, 0, 0L, 0L);
    doRes(DIEDSN4, "DIEDSN4", "died s min(3) lt 0");
    doRes(DIEDRN4, "DIEDRN3", "died resource min(2) lt 0");
    doRes(DIEDSN4RM3X5, " DIEDSN4RM3X5", "died 4 r lt 0, && max3 r gt 5 times max3 s");
    doRes(DIEDSN4RM3X4, "DIEDSN4RM3X4", "died 4 r lt 0, && max3 r gt 4 Times max3 s");
    doRes(DIEDSM3X5, "DIEDSM3X5", "died max3 s gt 5 times max3 r");
    doRes(DIEDSN4RN4, "DIEDSN4RN4", "died 4s lt 0 and 4r lt 0");
    doRes(DIEDRM3X4, "DIEDRM3X4", "died r much too large max3 of r gt 4 times max3 of s");
    doRes(DIEDSN3RN3, "diedSN3RN3", "died s.min3 lt 0 and r.min3 lt 0");
    doRes(DIEDSN3RN2, "DIEDSN3RN2", "died s.min3 lt 0 and r.min2lt 0");
    doRes(DIEDSN3RM3X4, "DIEDSN3RM3X4", "died s.min3 lt 0 and r max2 gt 4 times s max2");
    doRes(DIEDSN3RM3X3, "DIEDSN3RM3X3", "died s min3 lt 0 & r max3 gt 3 times s max3");
    doRes(DIEDSN3RN1, "DIEDSN3RN1", "died s min2 lt 0 & r min1 lt 0");
    doRes(DIEDSN3RM3X2, "DIEDSN3RM3X2", "died s min3 < 0 and r max3 2 times s max3");
    doRes(DIEDSN3RM3X1, "DIEDSN3RM3X1", "died s min3 and r max3 1 times s max3");
    doRes(DIEDSN3RM2X4, "DIEDSN3RM2X4", "died s min3 and r max2 4 times s max2");
    doRes(DIEDSN3RM2X3, "DIEDSN3RM2X3", "died s min 3 and r max2 3times s max2");
    doRes(DIEDSN3RM2X2, "DIEDSN3RM2X2", "died", 1, 1, 3, ROWS1 | LIST3 | LIST6 | CUMUNITS | BOTH | SKIPUNSET, 0, 0L, 0L);
    doRes(DIEDSN3RM2X1, "DIEDSN3RM2X1", "died");
    doRes(DIEDSN3RM1X4, "DIEDSN3RM1X4", "died");
    doRes(DIEDSN3RM1X3, "DIEDSN3RM1X3", "died");
    doRes(DIEDSN3RM1X2, "DIEDSN3RM1X2", "died");
    doRes(DIEDSN3RM1X1, "DIEDSN3RM1X1", "died");
    doRes(DIEDSN2RN3, "DIEDSN2RN3", "died");
    doRes(DIEDSN2RN2, "DIEDSN2RN2", "died");
    doRes(DIEDSN2RM3X4, "DIEDSN2RM3X4", "died");
    doRes(DIEDSN2RM3X3, "DIEDSN2RM3X3", "died");
    doRes(DIEDSN2RN1, "DIEDSN2RN1", "died");
    doRes(DIEDSN2RM3X2, "DIEDSN2RM3X2", "died");
    doRes(DIEDSN2RM3X1, "DIEDSN2RM3X1", "died");
    doRes(DIEDSN2RM2X4, "DIEDSN2RM2X4", "died");
    doRes(DIEDSN2RM2X3, "DIEDSN2RM2X3", "died");
    doRes(DIEDSN2RM2X2, "DIEDSN2RM2X2", "died");
    doRes(DIEDSN2RM2X1, "DIEDSN2RM2X1", "died");
    doRes(DIEDSN2RM1X4, "DIEDSN2RM1X4", "died");
    doRes(DIEDSN2RM1X3, "DIEDSN2RM1X3", "died");
    doRes(DIEDSN2RM1X2, "DIEDSN2RM1X2", "died");
    doRes(DIEDSN2RM1X1, "DIEDSN2RM1X1", "died");
    doRes(DIEDSN1RN3, "DIEDSN1RN3", "died");
    doRes(DIEDSN1RN2, "DIEDSN1RN2", "died");
    doRes(DIEDSN1RM3X4, "DIEDSN1RM3X4", "died");
    doRes(DIEDSN1RM3X3, "DIEDSN1RM3X3", "died");
    doRes(DIEDSN1RN1, "DIEDSN1RN1", "died");
    doRes(DIEDSN1RM3X2, "DIEDSN1RM3X2", "died");
    doRes(DIEDSN1RM3X1, "DIEDSN1RM3X1", "died");
    doRes(DIEDSN1RM2X4, "DIEDSN1RM2X4", "died");
    doRes(DIEDSN1RM2X3, "DIEDSN1RM2X3", "died");
    doRes(DIEDSN1RM2X2, "DIEDSN1RM2X2", "died");
    doRes(DIEDSN1RM2X1, "DIEDSN1RM2X1", "died");
    doRes(DIEDSN1RM1X4, "DIEDSN1RM1X4", "died");
    doRes(DIEDSN1RM1X3, "DIEDSN1RM1X3", "died");
    doRes(DIEDSN1RM1X2, "DIEDSN1RM1X2", "died");
    doRes(DIEDSM3X4RN3, "DIEDSM3X4RN3", "died");
    doRes(DIEDSM3X3RN3, "DIEDSM3X3RN3", "died");
    doRes(DIEDSM3X2RN3, "DIEDSM3X2RN3", "died");
    doRes(DIEDSM3X1RN3, "DIEDSM3X1RN3", "died");
    doRes(DIEDSM2X4RN3, "DIEDSM2X4RN3", "died");
    doRes(DIEDSM2X3RN3, "DIEDSM2X3RN3", "died");
    doRes(DIEDSM2X2RN3, "DIEDSM2X2RN3", "died");
    doRes(DIEDSM2X1RN3, "DIEDSM2X1RN3", "died");
    doRes(DIEDSM1X4RN3, "DIEDSM1X4RN3", "died");
    doRes(DIEDSM1X3RN3, "DIEDSM1X3RN3", "died");
    doRes(DIEDSM1X2RN3, "DIEDSM1X2RN3", "died");
    doRes(DIEDSM1X1RN3, "DIEDSM1X1RN3", "died");
    doRes(DIEDSM3X4RN2, "DIEDSM3X4RN2", "died");
    doRes(DIEDSM3X3RN2, "DIEDSM3X3RN2", "died");
    doRes(DIEDSM3X2RN2, "DIEDSM3X2RN2", "died");
    doRes(DIEDSM3X1RN2, "DIEDSM3X1RN2", "died");
    doRes(DIEDSM2X4RN2, "DIEDSM2X4RN2", "died");
    doRes(DIEDSM2X3RN2, "DIEDSM2X3RN2", "died");
    doRes(DIEDSM2X2RN2, "DIEDSM2X2RN2", "died");
    doRes(DIEDSM2X1RN2, "DIEDSM2X1RN2", "died");
    doRes(DIEDSM1X4RN2, "DIEDSM1X4RN2", "died s max > 4 * r max, r min2 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM1X3RN2, "DIEDSM1X3RN2", "died s max > 3 * r max, r min2 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM1X2RN2, "DIEDSM1X2RN2", "died s max > 2 * r max, r min2 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM1X1RN1, "DIEDSM1X1RN1", "died s max > 1 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM3X4RN1, "DIEDSM3X4RN1", "died s max3 > 4 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM3X3RN1, "DIEDSM3X3RN1", "died s max3 > 3 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM3X2RN1, "DIEDSM3X2RN1", "died s max3 > 2 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM3X1RN1, "DIEDSM3X1RN1", "died s max3 > 1 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM2X4RN1, "DIEDSM2X4RN1", "died s max3 > 4 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM2X3RN1, "DIEDSM2X3RN1", "died s max3 > 4 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM2X2RN1, "DIEDSM2X2RN1", "died s max3 > 4 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM2X1RN1, "DIEDSM2X1RN1", "died s max3 > 4 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM1X4RN1, "DIEDSM1X4RN1", "died s max1 > 4 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM1X3RN1, "DIEDSM1X3RN1", "died s max1 > 3 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSM1X2RN1, "DIEDSM1X2RN1", "died s max1 > 2 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DIEDSN1RM1X1, "DIEDSN1RM1X1", "died s max1 > 1 * r max, r min1 is neg try reduce difficulty or staff or resource costs.");
    doRes(DeadNegN, "DeadNegSwapN", "Dead Swaps never entered");
    doRes(DeadLt5, "DeadLt5", "dead no more than 5 swaps");
    doRes(DeadLt10, "DeadLt10", "dead no more than 10 swaps");
    doRes(DeadLt20, "DeadLt20", "dead no more than 20 swaps");
    doRes(DeadNegProsp, "DeadNegProsp", "Died either R or S had a negative");
    doRes(DeadRatioS, "DeadRatioS", "S values simply too small");
    doRes(DeadRatioR, "DeadRatioR", "R values simply too small");
    doRes(DEADRATIO, "diedRatio", "died R or S values too small");
    doRes(DEADHEALTH, "died health", "died,average negative minimum health at death");
    doRes(DEADFERTILITY, "died fertility", "died,average negative minimum fertility at death");
    doRes(DEADSWAPSMOVED, "diedSwapMoves", "died,average Swap Moves at death");
    doRes(DEADSWAPSCOSTS, "diedSwapCosts", "died,average SwapCosts at death");
    doRes(DEADTRADED, "diedTraded", "died,even after trading");
    doRes(RDEPRECIATIONP, "r Depreciation%", "Depreciation in R as the % of unitGrowth", 1, 1, 1, LIST13 | CURAVE, 0, 0, 0);
    doRes(CDEPRECIATIONP, "c Depreciation%", "Depreciation in C as the % of unitGrowth");
    doRes(SDEPRECIATIONP, "s Depreciation%", "Depreciation in S as the % of unitGrowth");
    doRes(GDEPRECIATIONP, "g Depreciation%", "Depreciation in G as the % of unitGrowth");
    doRes(RDDEPRECIATIONP, "r DDepreciation%", "Died Depreciation in R as the % of unitGrowth");
    doRes(CDDEPRECIATIONP, "c DDepreciation%", "Died Depreciation in C as the % of unitGrowth");
    doRes(SDDEPRECIATIONP, "s DDepreciation%", "Died Depreciation in S as the % of unitGrowth");
    doRes(GDDEPRECIATIONP, "g DDepreciation%", "Died Depreciation in G as the % of unitGrowth");
    doRes(RDADEPRECIATIONP, "r DADepreciation%", "Accepted Died Depreciation in R as the % of unitGrowth");
    doRes(CDADEPRECIATIONP, "c DADepreciation%", "Accepted Died Depreciation in C as the % of unitGrowth");
    doRes(SDADEPRECIATIONP, "s DADepreciation%", "Accepted Died Depreciation in S as the % of unitGrowth");
    doRes(GDADEPRECIATIONP, "g DADepreciation%", "Accepted Died Depreciation in G as the % of unitGrowth");
    doRes(SGMTGC, "SGmtgCosts", "SG Maintenance,Travel,Growth Costs / RCSGBal", 1, 1, 1, LIST8 | ROWS2 | CURAVE | both | SKIPUNSET, 0, 0, 0);
    doRes(RCMTGC, "RCmtgCosts", "RC Maintenance,Travel,Growth Costs / RCSGBal");
    doRes(SGREQGC, "SGREQGCosts", "SG REQ G Costs / RCSGBal");
    doRes(RCREQGC, "RCREQGCosts", "RC REQ M Costs / RCSGBal");
    doRes(SGREQMC, "SGREQMCosts", "SG REQ M Costs / RCSGBal");
    doRes(RCREQMC, "RCREQMCosts", "RC REQ G Costs / RCSGBal");

    doRes(RRAWMC, "RMaintCosts", "R Maintenance Costs/ RCSGBal");
    doRes(CRAWMC, "CMaintCosts", "C Maintenance Costs/ RCSGbal");
    doRes(RCRAWMC, "RCMaintCosts", "RC Maintenance Costs/ RCSGBal");
    doRes(SRAWMC, "SMaintCosts", "S Maintenance Costs/ RCSGBal");
    doRes(GRAWMC, "GMaintCosts", "G Maintenance Costs/ RCSGBal");
    doRes(SGRAWMC, "SGMaintCosts", "SG Maintenance Costs/ RCSGBal");
    //   doRes(RCRAWMC, "RCRawMaintCosts", "RC Maintenance Costs/ RCSGBal");
    doRes(RCfrac, "RC/yr Worth", "RC / yr Worth");
    doRes(SGfrac, "SG/yr Worth", "SG / yr Worth");
    //chgd KNOWLEDGEB MANUALSfrac NEWKNOWLEDGEfrac COMMONKNOWLEDGEfrac KNOWLEDGEINCR NEWKNOWLEDGEINCR MANUALSINCR COMMONKNOWLEDGEINCR
    doRes(POORKNOWLEDGEEFFECT, "Dumb csts", "frac Increase in costs due to limited knowledge(ignorance)", 1, 1, 1, LIST14 | ROWS1 | THISYEARAVE | both | SKIPUNSET, LIST1 | LIST7 | LIST14 | ROWS1 | CUMUNITS | both | SKIPUNSET, 0, 0);
    doRes(POORHEALTHEFFECT, "Poor Health Cost", "Increase in costs due to insufficient required resources and staff");
    doRes(NEWKNOWLEDGEFRAC, "New%Old knowledge", "New knowledge / Knowledge ", 2, 3, 1, LIST7 | LIST0 | THISYEARAVE | both | SKIPUNSET, LIST7 | LIST14 | ROWS1 | THISYEARUNITS | BOTH | SKIPUNSET, 0, 0);
    doRes(KNOWLEDGEFRAC, "Knowledge Frac", "Knowledge worth / year worth");
    doRes(COMMONKNOWLEDGEFRAC, "Common Knowledge", "Common knowledge/knowledge");
    doRes(MANUALSFRAC, "Manualsfrac", "Manuals /knowledge, you get manuals in trades, faculty and researcher turn manuals into knowledge");

    doRes(KNOWLEDGEB, "BalanceKnowledge", "Knowledge balance");
    doRes(KNOWLEDGEINCR, "PercIncrKnowledge", "Percent Knowledge Increase per year");
    doRes(NEWKNOWLEDGEINCR, "incNewKnowledge", "Percent New Knowledge Incr/Year");
    doRes(COMMONKNOWLEDGEINCR, "incCommonKnowledge", "Percent Common Knowledge increase by year");
    doRes(MANUALSINCR, "PercIncrManuals", "Percent Manuals increase by years");
    doRes(INCRAVAILFRAC5, "%+AvailFav5", "Percent increase in avail frac after trade at favor 5", 2, 3, 2, 0, ROWS1 | LIST41 | THISYEARAVE | BOTH | SKIPUNSET, 0, 0);
    doRes(INCRAVAILFRAC4, "%+AvailFav4", "Percent increase in avail frac after trade at favor 4");
    doRes(INCRAVAILFRAC3, "%+AvailFav3", "Percent increase in avail frac after trade at favor 3");
    doRes(INCRAVAILFRAC2, "%+AvailFav2", "Percent increase in avail frac after trade at favor 2");
    doRes(INCRAVAILFRAC1, "%+AvailFav1", "Percent increase in avail frac after trade at favor 1");
    doRes(INCRAVAILFRAC0, "%+AvailFav0", "Percent increase in avail frac after trade at favor 0");
    doRes(INCRAVAILFRAC, "%IncrAvail", "Percent increase in avail frac after trade  at any trade");
    doRes(INCRAVAILFRACa, "IncrAvailFracRej", "Percent increase in avail frac trade rejected");
    doRes(INCRAVAILFRACb, "IncrAvailFracb", "Percent increase in avail frac after trade rejected at trade failure");
   
    doRes(TradeBidRequests, "BidRequests", "requested bids");
    doRes(TradeCriticalBidRequests, "BidCriticalRequests", "Critical requested bids");
   
    doRes(TradeAlsoBidRequests, "BidAlsoRequests", "requested bids");
    doRes(TradeAlsoCriticalBidRequests, "BidAlsoCriticalRequests", "Critical requested bids");
    
    doRes(TRADELASTRECEIVE, "Last Received", "Final received goods%tot balance");
    doRes(TRADERECEIVELASTPERCENTFIRST, "received final%first goods", "Final percent of First  amount requested in a trade", 2, 2, 2, LIST7 | LIST15 | THISYEARAVE | BOTH | SKIPUNSET, 0, LIST15 | CURAVE | BOTH | SKIPUNSET, 0L); 
    doRes(TRADESTRATLASTRECEIVE, "StrategicLastReceived", "Final strategic amount eeceived in trade", 2, 2, 2, LIST40 | THISYEAR | THISYEARAVE | THISYEARUNITS | CUM | CUMUNITS | BOTH | SKIPUNSET, ROWS3 | LIST15 | CURAVE | BOTH | SKIPUNSET, 0L, 0L);
    doRes(TradeBidRequestsFirst, "BidFirstRequests", "First requested bids", 2, 2, 2, LIST7 | THISYEARAVE | BOTH | SKIPUNSET, 0, LIST22 | CURAVE | BOTH | SKIPUNSET, 0L);
    doRes(TradeCriticalBidRequestsFirst, "BidFirstCriticalRequests", "First Critical requested bids");   
    doRes(TradeAlsoBidRequestsFirst, "BidAlsoFirstRequests", "First requested bids");
    doRes(TradeAlsoCriticalBidRequestsFirst, "BidAlsoFirstCriticalRequests", "First Critical requested bids");
    doRes(TRADEFIRSTGAVE, "TradeFirstGiven", "First goods given%totBalance", 2, 2, 2, 0, ROWS1 | LIST4 | THISYEARAVE | BOTH | SKIPUNSET, ROWS1 | LIST22 | CURAVE | BOTH | SKIPUNSET, 0L);
    doRes(TRADEFIRSTRECEIVE, "First Received", "First received goods%tot balance"); 
    doRes(TRADESTRATFIRSTRECEIVE, "StrategicFirstReceived", "First strategic amount received in trade", 2, 2, 2, LIST4 | LIST7 | THISYEARAVE | BOTH | SKIPUNSET, ROWS1 | LIST22 | CURAVE | BOTH | SKIPUNSET, 0L, 0L);
     doRes(TRADESTRATFIRSTGAVE, "TradeStrategicFirstGave", "First amount given in trade");
    doRes(TradeNominalReceivePercentNominalOffer, "NomReceive%NomOffer", "% of Nominal Received Per Nominal  Given", 2, 2, 2,  LIST4 | LIST7 | THISYEARAVE | BOTH | SKIPUNSET, ROWS2 | LIST15 | CURAVE | BOTH | SKIPUNSET, 0L, 0L);
    doRes(MaxNominalReceivePercentNominalOffer, "MaxNomReceive%NomOffer", "Max % of Nominal Received Per Nominal  Given", 2, 2, 2, LIST4 | LIST7 | THISYEARAVE | BOTH | SKIPUNSET, ROWS3 | LIST15 | CURAVE | BOTH | SKIPUNSET, 0L, 0L);
    doRes(MinNominalReceivePercentNominalOffer, "MinNomReceive%NomOffer", "Min % of Nominal Received Per Nominal  Given");
    doRes(TradeStrategicReceivePercentStrategicOffer, "StratReceive%StratGiven", "% of Strategic Received Per Strategic  Given");
    doRes(MaxStrategicReceivePercentStrategicOffer, "MaxStratReceive%StratGiven", "Max % of Strategic Received Per Strategic  Given");
    doRes(MinStrategicReceivePercentStrategicOffer, "MinStratReceive%StratGiven", "Min % of Strategic Received Per Strategic  Given");
    doRes(YearTradeNominalReceivePercentNominalOffer, "YearNomReceive%NomOffer", "Year % of Nominal Received Per Nominal  Given");
    doRes(YearMaxNominalReceivePercentNominalOffer, "YearNomReceive%NomOffer", "Year Max % of Nominal Received Per Nominal  Given");
    doRes(YearMinNominalReceivePercentNominalOffer, "YearNomReceive%NomOffer", "Year Min % of Nominal Received Per Nominal  Given");
    doRes(YearTradeStrategicReceivePercentStrategicOffer, "YearStratReceive%StratGiven", "Year % of Strategic Received Per Strategic  Given");
    doRes(YearMaxStrategicReceivePercentStrategicOffer, "YearMaxStratReceive%StratGiven", "Year Max % of Strategic Received Per Strategic  Given");
    doRes(YearMinStrategicReceivePercentStrategicOffer, "YearMinStratReceive%StratGiven", "Year Min % of Strategic Received Per Strategic  Given");
    doRes(BEFORETRADEWORTH, "BeforeTradeWorth", "Worth before A trade");
    doRes(AFTERTRADEWORTH, "AfterTradeWorth", "Worth after a trade");
    doRes(TRADEWORTHINCRPERCENT, "%TradeWorthIncr", "% increase in Worth after trade", 2, 3, 2,  LIST41 | THISYEARAVE | CUMAVE | BOTH | SKIPUNSET, 0, 0, 0);
   doRes(TRADEWORTHINCR, "TradeWorthIncr", "this years increase in Worth after trade", 2, 3, 2,  LIST41 | THISYEAR | THISYEARAVE | CUMAVE | BOTH | SKIPUNSET, 0, 0,0);
    doRes(TradeAcceptValuePerGoal, "AcceptValue%Goal", "Accepted value percent of goal", 2, 3, 2, LIST41 | THISYEARAVE | BOTH | SKIPUNSET, ROWS1 | LIST4 | LIST15 | CURAVE | BOTH | SKIPUNSET, ROWS2 | LIST41 | CUMAVE | BOTH | SKIPUNSET, 0L);
    doRes(TradeRejectValuePerGoal, "RejectValue%Goal", "Rejected percent value per goal");
    doRes(TradeLostValuePerGoal, "LostValue%Goal", "Lost percent value per goal");
    doRes(TradeFirstStrategicGoal, "FirstStrategicGoal", "First Strategic Goal", 2, 3, 2, LIST41 | THISYEARAVE | CUMAVE | BOTH | SKIPUNSET, ROWS1 | LIST4 | LIST15 | CURAVE | BOTH | SKIPUNSET, 0, 0L);
    doRes(TradeLastStrategicGoal, "LastStrategicGoal", "Strategic Goal after trade");
    doRes(TradeFirstStrategicValue, "FirstStrategicValue", "First Strategic Value", 2, 3, 2, LIST41 | THISYEARAVE | CUMAVE | BOTH | SKIPUNSET, ROWS1 | LIST4 | LIST15 | CURAVE | BOTH | SKIPUNSET, 0, 0L);
    doRes(TradeLastStrategicValue, "StrategicValue", "Strategic-Value strategic receive/strategic gave at trade");
    doRes(TradeStrategicValueLastPercentFirst, "Last%FirstStrategicValue", "LastStrategic Value percent of First Strategic Value just before trade");
    doRes(AlsoTradeLastStrategicValue, "AlsoStrategicValue", "AlsoLast strategic Value strategic receive/strategic gave at trade");
    doRes(AlsoTradeStrategicValueLastPercentFirst, "Also%FirstStratVal", "LastStrategic Value percent of First Strategic Value just before trade");
    doRes(TradeRejectedStrategicGoal, "RejectedStrategicGoal", " LiveTrade rejected Strategic Goal", 1, 2, 2, LIST1 | THISYEARAVE | BOTH | SKIPUNSET, ROWS1 | LIST5 | LIST15 | CURAVE | BOTH | SKIPUNSET, ROWS2 | LIST5 | LIST15 | CUMAVE | BOTH | SKIPUNSET, 0L);
    doRes(TradeLostStrategicGoal, "LostStrategicGoal", "Strategic Goal after trade lost");
    doRes(TradeRejectedStrategicValue, "RejStratValue", "Strategic Value after Trade rejected");
    doRes(TradeLostStrategicValue, "LostStrategicValue", "Strategic Value after trade lost");
    doRes(TradeMissedStrategicGoal, "MissedStrategicGoal", "Trade Missed no value");
    doRes(TradeDeadMissedStrategicGoal, "DeadMissedStrategicGoal", "Dead No Strategic Goal no trade", 2, 3, 2, THISYEARAVE | LIST1 | LIST2 | LIST4 | BOTH | SKIPUNSET, ROWS1 | LIST4 | LIST10 | CURAVE | BOTH | SKIPUNSET, 0, ROWS3 | LIST10 | LIST4 | CUMUNITS | BOTH | SKIPUNSET);
    doRes(TradeDeadLostStrategicGoal, "DeadLostStrategicGoal", "Strategic Goal after trade lost and dead");
    doRes(TradeDeadLostStrategicValue, "DeadLostStrategicValue", "Strategic Value after trade lost and dead");
    doRes(TradeDeadRejectedStrategicGoal, "DeadRejectedStrategicGoal", "Strategic Goal after trade rejected and dead");
    doRes(TradeDeadStrategicGoal, "DeadStrategicGoal", "Strategic Goal after trade then died");
    doRes(TradeDeadRejectedStrategicValue, "DeadRejectedStrategicValue", "Strategic Value after trade rejected then died");
    doRes(TradeDeadStrategicValue, "DeadStrategicValue", "Strategic Value after trade then died");
    doRes(TRADESOS0, "SOS0trade%IncW", "Successful trade percent incr worth after starting with SOS0", 2, 3, 2, LIST41 | THISYEARAVE | BOTH | SKIPUNSET, ROWS1 | LIST4 | LIST15 | CURAVE | BOTH | SKIPUNSET, 0L, 0L);
    doRes(TRADESOS1, "SOS1trade%IncW", "Successful trade percent incr worth after starting with SOS1");
    doRes(TRADESOS2, "SOS2trade%IncW", "Successful trade percent incr worth after starting with SOS2");
    doRes(TRADESOS3, "SOS3trade%IncW", "Successful trade percent incr worth after starting with SOS3");
    doRes(TRADEOSOS0, "HlptdS1Acc%IncW", "Helped Successful trade percent incr worth after starting with SOS1", 2, 3, 2, LIST41 | THISYEARAVE | BOTH | SKIPUNSET, ROWS1 | LISTYRS | CUR | BOTH | SKIPUNSET, ROWS2 | LIST4 | LIST15 | CURAVE | CUM | BOTH | SKIPUNSET, 0L);
    doRes(TRADEOSOS1, "HlptdS0Acc%IncW", "Helped Successful trade percent incr worth after starting with SOS0");
    doRes(TRADEOSOS2, "HlptdS2Acc%IncW", "Helped Successful trade percent incr worth after starting with SOS2");
    doRes(TRADEOSOS3, "HlptdS3Acc%IncW", "Helped Successful trade percent incr worth after starting with SOS3");
    doRes(TRADESOSR1, "TrdSOS1%IncW", " trade percent incr worth with SOS1");
    doRes(TRADESOSR2, "TrdSOS2%IncW", " trade percent incr worth with SOS2");
    doRes(TRADESOSR3, "TrdSOS3%IncW", " trade percent incr worth with SOS3");
    doRes(TRADEOSOSR1, "LstS1%IncW", "Other % worth increase when lost for a trade with SOS1");
    doRes(TRADEOSOSR2, "LstS2%IncW", "Other % worth increase when lost for a trade with SOS2");
    doRes(TRADEOSOSR3, "LstS3%IncW", "Other % worth increase when lost for a trade with SOS3");

    doRes(DTRADEACC, "DTrade Acc%IncW", "% worth inc/decrease when Dead after trade accepted", 2, 2, 2, 0, ROWS1 | LIST3 | CUMUNITS | BOTH | SKIPUNSET, 0, 0L);
    doRes(DTRADEOSOSR0, "DHLPS0%IncW", "% worth inc/decrease when Dead after help with SOS0");

    doRes(DTRADEOSOSR1, "DHLPS1%IncW", "% worth inc/decrease when Dead after help with SOS1");
    //  doRes(DLOSTSOSR0, "DLOSTSOS0%IncW", "Dead after lost trade value with SOS0, % Value Incr");
    doRes(DLOSTSOSR1, "DLOSTSOS1%IncW", "Dead after lost trade value with SOS1, % Value Incr");
    doRes(DTRADEOSOSR2, "DHLPS2%IncW", "% worth inc/decrease when Dead after help with SOS2");
    doRes(DTRADEOSOSR3, "DHLPS3%IncW", "% worth inc/decrease when Dead after help with SOS3");
    doRes(DLOSTOSOSR0, "DLostoS0%IncW", "Other Dead after no trade with SOS0, % Value Incr");
    doRes(DLOSTOSOSR1, "DLostoS1%IncW", "Other Dead after no trade with SOS1, % Value Incr");
    doRes(DLOSTOSOSR2, "DLostoS2%IncW", "Other Dead after no trade with SOS2, % Value Incr");
    doRes(DLOSTOSOSR3, "DLostoS3%IncW", "Other Dead after no trade with SOS3, % Value Incr");
    doRes(DTRADESOSR3, "DTradeSOS3R%IncW", "% worth inc/decrease when Dead after Reject with SOS3");
    doRes(DTRADESOSR2, "DTradeSOS2R%IncW", "% worth inc/decrease when Dead after Reject with SOS2");
    doRes(DTRADESOSR1, "DTradeSOS1R%IncW", "% worth inc/decrease when Dead after Reject with SOS1");
    doRes(DTRADESOSR0, "DTradeSOS0R%IncW", "% worth inc/decrease when Dead after Reject with SOS0");
    doRes(sumCatEffRBen, "EffCatRBen", "Catastrophy Effective resource growth benefits", 2, 2, 2, LIST9 | LIST14 | CURAVE | CUM | SKIPUNSET, 0, 0, 0);
    doRes(sumCatEffSBen, "EffCatSBen", "Catastrophy Effective staff growth benefits");
    doRes(sumCatEffManualsBen, "EffCatManBen", "Catastrophy Effective manuals benefits");
    doRes(sumCatEffKnowBen, "EffCatKnoBen", "Catastrophy effective knowledge benefits");
    doRes(sumCatEffRDecayBen, "EffRDecayBen", "Catastrophy effective resource decay decrements");
    doRes(sumCatEffSDecayBen, "EffSDecayBen", "Catastrp[ju effoctove staff decau decrements");
    // repeatlists at "W..." at a later point rn 
    doRes("WTRADEDINCRF5", "Fav5Trd%IncW", "% Years worth increase at Favor5/start year worth", 2, 3, 2, both | SKIPUNSET, ROWS1 | LIST41 | CURAVE | BOTH | SKIPUNSET, ROWS2 | THISYEARUNITS | BOTH | SKIPUNSET, ROWS3 | THISYEARAVE | BOTH | SKIPUNSET);
    doRes("WTRADEDINCRF4", "Fav4Trd%IncW", "% Years worth increase at Favor4/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WTRADEDINCRF3", "Fav3Trd%IncW", "% Years worth increase at Favor3/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WTRADEDINCRF2", "Fav2Trd%IncW", "% Years worth increase at Favor2/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WTRADEDINCRF1", "Fav1Trd%IncW", "Years worth increase at Favor1/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WTRADEDINCRF0", "Fav0Trd%IncW", "% Years worth increase at Favor0/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes(WTRADEDINCRSOS, "incrWSOSTrade", "% Years worth increase at an planet SOS flag trade this year/start year worth", 2, 3, 2, LIST41 | CURAVE | BOTH | SKIPUNSET, 0, 0, 0);
    doRes("WREJTRADEDPINCR", "incrWRejectedTrade", "% W incr if the other had notrejected the trade/start yr worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WLOSTTRADEDINCR", "incrWLostTrade", "% W incr if other rejected the trade/start yr worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("UNTRADEDWINCR", "DEAD no trade Incr", "% no trade offered yearly growth including working, reserve: resource, staff, knowledge", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRAC", "CritReceiptsFrac", "Fraction of Critical receipts W/start totW ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACSYFAV5", "CritReceipts%YF5", "% of critical receipts worth favor5 Trades/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACSYFAV4", "CritReceipts%YF4", "% of critical receipts worth favor4 Trades/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACSYFAV3", "CritReceipts%YF3", "% of critical receipts worth favor3 Trades/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACSYFAV2", "CritReceipts%YF2", "% of critical receipts worth favor2 Trades/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACSYFAV1", "CritReceipts%YF1", "% of critical receipts worth favor1 Trades/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACSYFAV0", "CritReceipts%YF0", "% of critical receipts worth favor0 Trades/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACSYFAVA", "CritReceipts%YF0", "% of critical receipts worth favor0-5 Trades/start year worth", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRADROPT5", "CritTradeFracDropF5", "% of traded critical receipts worth favor5 Trades/start barter C receipts  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRADROPT4", "CritTradeFracDropF4", "% of traded critical receipts worth favor4 Trades/start barter C receipts  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRADROPT3", "CritTradeFracDropF3", "% of traded critical receipts worth favor3 Trades/start barter C receipts  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRADROPT2", "CritTradeFracDropF2", "% of traded critical receipts worth favor2 Trades/start barter C receipts  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRADROPT1", "CritTradeFracDropF1", "% of traded critical receipts worth favor1 Trades/start barter C receipts  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRADROPT0", "CritTradeFracDropF0", "% of traded critical receipts worth favor0 Trades/start barter C receipts  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACWORTHF3", "CritTradeFracF3", "% of critical receipts worth favor3 Trades/start totW  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACWORTHF2", "CritTradeFracF2", "% of critical receipts worth favor2 Trades/start totW  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("CRITICALRECEIPTSFRACWORTHF1", "CritTradeFracF1", "% of critical receipts worth favor1 Trades/start totW  ", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WTRADEDINCR", "WTradedIncr", "% Years worth increase/start year worth", 2, 2, 1, LIST41 | CURAVE | BOTH | SKIPUNSET, 0, 0, 0);
    doRes("WORTHAYRNOTRADEINCR", "IncW aYr NoTrade", "% Year increase Worrth/worth if no trades this year", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WORTH2YRNOTRADEINCR", "IncW 2Yr No Trade", "% Year increase Worrth/worth if  2 years of no trades", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WORTH3YRNOTRADEINCR", "IncW 3Yrs No Trade", "% Year increase W/worth if 3 or more years of no trades", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WORTHAYRTRADEINCR", "IncW aYr Trade", "% Year increase Worrth/worth if a year of trades", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WORTH2YRTRADEINCR", "IncW 2Yr Trade", "% Year increase Worrth/worth if  2 succesive year of trades", 2, 3, 2, DUP, 0, 0, 0);
    doRes("WORTH3YRTRADEINCR", "IncW 3Yr Trade", "% Year increase Worrth/worth if 3 years of trades", 2, 3, 2, DUP, 0, 0, 0);
    //  doRes("WORTH1YRNOTRADEINCR", "IncW 1Yr No Trade", "% Year increase Worrth/worth if at least 1 succesive year of trades",2,2, 1, LIST2 | THISYEAR | THISYEARAVE| BOTH | SKIPUNSET , LISTYRS | SKIPUNSET | BOTH | CUR  | CURAVE, ROWS3 | LIST4 | THISYEARUNITS , 0);
    doRes(SWAPRINCRCOST, "Swap RIncr Cost", "Fraction of R INCR swap cost/sum of R units", 2, 2, 1, list8 | CURAVE | CURUNITS | both | skipUnset, 0, 0, 0);
    doRes(SWAPSINCRCOST, "Swap SIncr Cost", "Fraction of SR INCR swap cost/sum of S units", 2, 2, 1);
    doRes(SWAPRDECRCOST, "Swap RDECR Cost", "Fraction of R DECR swap cost/sum of R units", 2, 2, 1);
    doRes(SWAPSDECRCOST, "Swap SDecr Cost", "Fraction of S Decr swap cost/sum of S units", 2, 2, 1);
    doRes(SWAPRXFERCOST, "Swap RXfer Cost", "Fraction of R XFER swap cost/sum of R units", 2, 2, 1);
    doRes(SWAPSXFERCOST, "Swap SXfer Cost", "Fraction of S XFER swap cost/sum of R units", 2, 2, 1);
    doRes("Redo FutureFund", "Redo FutureFund", "At emergency1 level of resource/staff back out of a saved future fund", 3, 2, 1, ROWS2 | LIST6 | CUM | CUMUNITS | BOTH | SKIPUNSET, 0, 0, 0);
    doRes("EmergFF", "EmergFF", "emergency resource/staff sums tranfer resource to FutureFund", 2, 2, 1, ROWS2 | LIST6 | CUR | CURAVE | CUM | BOTH | SKIPUNSET, 0, 0, ROWS1 | LIST14 | CURAVE | SKIPUNSET);
    doRes("SizeFF", "SizeFF", "Size resource/staff sums tranfer resource to FutureFund", 2, 2, 1, ROWS1 | LIST6 | CUR | CURAVE | BOTH | SKIPUNSET, ROWS1 | LIST14 | CURAVE | SKIPUNSET, 0, 0);
    doRes("FutureFundSaved", "FutureFundsSaved", "Total resource/staff sums tranfered to FutureFund");

    doRes("EmergFF1", "Emerg FutureFund1", "At emergency1 level of resource/staff neg prospects val to FutureFund", 1, 2, 1, ROWS1 | LIST6 | CUR | BOTH | SKIPUNSET, 0, 0, 0);
    doRes("REmergFF1", "R Emerg FutureFund1", "At emergency1 level of resource/staff sums tranfser val to FutureFund");
    doRes("SEmergFF1", "S Emerg FutureFund1", "At emergency1 level of staff/resource sums transfer val to FutureFund");
    doRes("REmergFF2", "R Emerg FutureFund2", "At emergency2 level of resource/staff sums transfer val to FutureFund");
    doRes("SEmergFF2", "S Emerg FutureFund2", "At emergency2 level of staff/resource sums transfer val to FutureFund");
    doRes("RcEmergFF1", "Rc Emerg FutureFund1", "At emergency1 level of resource/staff sums transfer val to FutureFund");
    doRes("SgEmergFF1", "Sg Emerg FutureFund1", "At emergency1 level of staff/resource sums transfer val to FutureFund");
    doRes("RcEmergFF2", "Rc Emerg FutureFund2", "At emergency2 level of resource/staff sums tranfser val to FutureFund");
    doRes("SgEmergFF2", "Sg Emerg FutureFund2", "At emergency2 level of staff/resource sums tranfer val to FutureFund");
    doRes("SizeFFr", "R SizeFutureFund", "At size level of resource/staff sums tranfer val  to FutureFund");
    doRes("SizeFFs", "S SizeFutureFund", "At size level of resource/staff sums tranfer val  to FutureFund");
    doRes("SizeFFEr", "RE SizeFutureFund", "At emergency prospects level of resource/staff sums tranfer val to FutureFund", ROWS1 | LIST14 | CURAVE | CUMUNITS);
    doRes("SizeFFEs", "SE SizeFutureFund", "At size level of resource/staff sums tranfer val  to FutureFund");
    doRes("RSwapFF", "R SwapEmergFF", "At emergency level of resource/staff sums during swaps tranfer val to FutureFund");
    doRes("SSwapFF", "S SwapEmergFF", "At emergency level of staff/resource sums during swaps tranfer val to FutureFund");
    doRes("sWorth", "sWorth", "staff worth as % of total staff uniits, how change by age", 1, 2, 1, ROWS2 | LIST16 | LIST15 | CURAVE | BOTH, ROWS1 | LIST14 | LIST15 | THISYEARUNITS | BOTH, 0, 0);
    doRes("sWork", "s work%", "staff work as % of total staff uniits", 1, 2, 1, ROWS2 | LIST16 | LIST15 | CURAVE | BOTH | SKIPUNSET, 0, 0, 0);
    doRes("sFacultyEquiv", "s facultyEquiv%", "staff faculty equiv part of many grades as % of total staff uniits, faculty equiv governs the staff and guest advance to higher grades");
    doRes("sResearcherEquiv", "s researcherEquiv%", "staff researcher equiv part of many grades as % of total staff uniits, this governs the creation of new knowledge increasing the efficiency of work, reducing the annual costs");
    doRes("sColonists", "s colonists%", "the first 4 grades of new staff as % of total staff uniits");
    doRes("sEngineers", "s engineers%", "the second 4 grades of staff as a % of total staff uniits, these do much of the work, but others also do some work");
    doRes("sFaculty", "s faculty%", "the third 4 grades of staff as a % of total staff units these create much lot of the faculty equiv");
    doRes("sResearchers", "s researchers%", "the fourth 4 grades of staff as a % of total staff, these create much of the researcherEquiv");
    doRes("sKnowledge", "knowledge%", "the current knowledge as a % of the knowledge goal, each increase toward the goal makes work easier");
    doRes("sDWorth", "s D worth", "dead staff worth as % of total staff uniits, how change by age", 1, 2, 1, ROWS2 | LIST4 | LIST10 | CURAVE | BOTH | BOTH, 0, 0, 0);
    doRes("sDWork", "s D work%", "dead staff work as % of total staff uniits", 1, 2, 1, ROWS2 | LIST4 | CURAVE | BOTH | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("sDFacultyEquiv", "s D facultyEquiv%", "staff faculty equiv part of many grades as % of total staff uniits, faculty equiv governs the staff and guest advance to higher grades");
    doRes("sDResearcherEquiv", "s D researcherEquiv%", "dead staff researcher equiv part of many grades as % of total staff uniits, this governs the creation of new knowledge increasing the efficiency of work, reducing the annual costs");
    doRes("sDColonists", "s D colonists%", "the first 4 grades of new dead staff as % of total staff uniits");
    doRes("sDEngineers", "s D engineers%", "the second 4 grades of dead staff as a % of total staff uniits, these do much of the work, but others also do some work");
    doRes("sDFaculty", "s D faculty%", "the third 4 grades of deadstaff as a % of total staff units these create much lot of the faculty equiv");
    doRes("sDResearchers", "s D researchers%", "the fourth 4 grades of deadstaff as a % of total staff, these create much of the researcherEquiv");
    doRes("sDKnowledge", "D knowledge%", "the dead Econ current knowledge as a % of the knowledge goal, each increase toward the goal makes work easier");
    doRes("sDAWorth", "s DA worth", "dead staff worth as % of total staff uniits, how change by age", 1, 2, 1, ROWS2 | LIST11 | LIST3 | CURAVE | BOTH | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("sDAWork", "s DA work%", "accepted dead staff work as % of total staff uniits", 1, 2, 1, ROWS2 | LIST3 | CURAVE | BOTH | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("sDAFacultyEquiv", "s DA facultyEquiv%", "accepted dead staff faculty equiv part of many grades as % of total staff uniits, faculty equiv governs the staff and guest advance to higher grades");
    doRes("sDAResearcherEquiv", "s DA researcherEquiv%", "accepted dead staff researcher equiv part of many grades as % of total staff uniits, this governs the creation of new knowledge increasing the efficiency of work, reducing the annual costs");
    doRes("sDAColonists", "s DA colonists%", "accepted dead staff the first 4 grades of new dead staff as % of total staff uniits");
    doRes("sDAEngineers", "s DA engineers%", "accepted dead staff the second 4 grades of dead staff as a % of total staff uniits, these do much of the work, but others also do some work");
    doRes("sDAFaculty", "s DA faculty%", "accepted dead staff the third 4 grades of deadstaff as a % of total staff units these create much lot of the faculty equiv");
    doRes("sDAResearchers", "s DAresearchers%", "accepted dead staff the fourth 4 grades of deadstaff as a % of total staff, these create much of the researcherEquiv");
    doRes("sDAKnowledge", "DA knowledge%", "accepted dead Econ current knowledge as a % of the knowledge goal, each increase toward the goal makes work easier");
    doRes(CRISISINCR, "IncWorthCrisis", "Percent Year increase Worrth/worth a year with a catastrophy", 1, 2, 1, LIST2 | CUMAVE | BOTH | SKIPUNSET, LIST9 | SKIPUNSET | BOTH | THISYEAR | THISYEARUNITS, ROWS3 | LIST14 | CUR | CUMUNITS | BOTH, 0);
    doRes(CRISIS2INCR, "IncWorthCrisis", "Percent Year increase Worrth/worth a second year after a  catastrophy");
    doRes(CRISIS3INCR, "IncWorthCrisis", "Percent Year increase Worrth/worth a third year after a  catastrophy");
    doRes(NOCRISISINCR, "IncWorth 1Yr No Crisis", "Percent Year increase Worrth/Year initial worth if at least 1  year of no catastrophy");
    doRes(NOCRISIS2INCR, "IncWorth 2Yr No Crisis", "Percent Year increase Worrth/Year initial worth if 2 years of No catastrophy");
    doRes(NOCRISIS3INCR, "IncWorth 3Yr No Crisis", "Percent Year increase Worrth/Year initial worth if 3 years of No catastrophy");
    doRes(CRISISRESREDUCEPERCENT, "reduceResInCrisis", "Crisis reduce resource by percent of balance ", 1, 2, 1, LIST2 | CUMAVE | BOTH | SKIPUNSET, LIST9 | SKIPUNSET | BOTH | THISYEAR | THISYEARUNITS, LIST14 | CURAVE, 0);
    doRes(CRISISRESDECAYBONUSPERCENT, "%ResBounusGrowth", " crisis bonus growth pecent of original potential unit growth");
    doRes(CRISISSTAFFREDUCEPERCENT, "ReduceStaffInCrisis", "Crisis reduce staff by percent of balance");
    doRes(CRISISSTAFFDECAYBONUSPERCENT, "%bounusStaffGrowthCrisis", "Crisis up staff Growth by percent of potential growth");
    doRes(CRISISRESREDUCESURPLUSPERCENT, "PercentSurplusReduceResourceCrisis", "Crisis surplus reduce resource by percent of balance");
    doRes(CRISISSTAFFREDUCESURPLUSPERCENT, "PercentReduceStaffCrisis", "Crisis surplus reduce staff by percent of balance");
    doRes(CRISISSTAFFGROWTHPERCENTINCR, "Crisis%IncreaseStGrow", "Crisis percent increase by staff growth for a few years");
    doRes(CRISISSTAFFGROWTHYEARSINCR, "CrisisIncrStGrowYrs", "Crisis set Years of increased staff growth");
    doRes(CRISISRESGROWTHPERCENTINCR, "Crisis%IncreaseResGrow", "Crisis percent increase by resource growth for a few years");
    doRes(CRISISRESGROWTHYEARSINCR, "CrisisIncrResGrowYrs", "Crisis set Years of increased resource growth");
    doRes(CRISISMANUALSPERCENTINCR, "Crisis%IncrManals", "Crisis percent increase by manuals growth");
    doRes("rCatCosts", "r %Catast Cst ", "ave resource catastrophy costs percent of balances");
    doRes("sCatCosts", "s %Catast Cst ", "ave staff catastrophy costs percent of balances");
    doRes("rCatBonusY", "r Catast B Yr ", "resource catastrophy years added ave per catastrophy");
    doRes("sCatBonusY", "s Catast B Yr ", "staff catastrophy years added ave per catastrophy");
    doRes("rCatBonusVal", "r Catast B Val", "resource catastrophy bonus unit value added ave per catastrophy");
    doRes("sCatBonusVal", "s Catast B Yr ", "staff catastrophy bonus unit value added ave per catastrophy");
    doRes("rCatNegDecay", "r Catast ReduceDecay", "resource catastrophy bonus neg unit value ave per catastrophy");
    doRes("sCatNegDecay", "s Catast ReduceDecay", "staff catastrophy bonus neg unit value  ave per catastrophy");
    doRes("sCatBonusManuals", "s Catast Bonus Manuals", "catastrophy bonus manuals add value ave per catastrophy");
    doRes("sCatBonusNewKnowledge", "s Catast Bonus New Knowledge", "catastrophy bonus newKnowledge add value ave per catastrophy");
    doRes("rCatBonusManuals", "r Catast Bonus Manuals", "catastrophy bonus manuals add value ave per catastrophy");
    doRes("rCatBonusNewKnowledge", "r Catast Bonus New Knowledge", "catastrophy bonus newKnowledge add value ave per catastrophy");
    doRes("potentialResGrowthPercent", "r rawGro%", "raw unit resource growth percent of default unit growth", 2, 2, 1, LIST2 | CUMAVE | BOTH | SKIPUNSET, LIST8 | SKIPUNSET | BOTH | THISYEARAVE | THISYEARUNITS, ROWS1 | LIST15 | CURAVE | BOTH, ROWS3 | LIST15 | CURUNITS | BOTH);
    doRes("potentialStaffGrowthPercent", "s rawGro%", "raw unit staff growth percent of default unit growth");
    doRes("potentialCargoGrowthPercent", "c rawGro%", "raw unit cargo raw growth percent of default unit growth");
    doRes("potentialGuestGrowthPercent", "g rawGro%", "raw unit guest raw growth percent of default unit growth");
    // String[] negRawUnitGrowths = {"rNeg1RawUnitGrowth","cNeg1RawUnitGrowth","sNeg1RawUnitGrowth","gNeg1RawUnitGrowth"};
    doRes("DpotentialResGrowthPercent", "D r rawGro%", "Dead raw unit resource raw growth percent of default unit growth", 2, 2, 1, LIST4 | CUMAVE | BOTH | SKIPUNSET, LIST8 | SKIPUNSET | BOTH | THISYEAR | THISYEARUNITS, ROWS1 | LIST10 | CURAVE | CURUNITS | BOTH | SKIPUNSET, 0);
    doRes("DpotentialStaffGrowthPercent", "D s rawGro%", "Dead raw unit staff raw growth percent of default unit growth");
    doRes("DpotentialCargoGrowthPercent", "D c rawGro%", "Dead raw unit cargo raw growth percent of default unit growth");
    doRes("DpotentialGuestGrowthPercent", "D g rawGro%", "Dead raw unit guest raw growth percent of default unit growth");
    doRes("DApotentialResGrowthPercent", "DA r rawGro%", "Accepted Dead raw unit resource raw growth percent of default unit growth", 2, 2, 1, 0, LIST8 | SKIPUNSET | BOTH | THISYEAR | THISYEARUNITS, ROWS1 | LIST11 | CURAVE | CURUNITS | BOTH | SKIPUNSET, 0);
    doRes("DApotentialStaffGrowthPercent", "DA s rawGro%", "Accepted Dead raw unit staff raw growth percent of default unit growth");
    doRes("DApotentialCargoGrowthPercent", "DA c rawGro%", "Accepted Dead raw unit cargo raw growth percent of default unit growth");
    doRes("DApotentialGuestGrowthPercent", "DA g rawGro%", "Accepted Dead raw unit guest raw growth percent of default unit growth");
    // String[] negRawUnitGrowths = {"rNeg1RawUnitGrowth","cNeg1RawUnitGrowth","sNeg1RawUnitGrowth","gNeg1RawUnitGrowth"};
    doRes("rNeg1RawUnitGrowth", "r neg1 RUGrowth", " r negative raw unit growth in one or more sector", 2, 2, 1, 0, LIST8 | SKIPUNSET | BOTH | THISYEAR | THISYEARUNITS, ROWS1 | LIST15 | CURAVE | CURUNITS | BOTH | SKIPUNSET, 0);
    doRes("cNeg1RawUnitGrowth", "c neg1 RUGrowth", " c negative raw unit growth in one or more  sector");
    doRes("sNeg1RawUnitGrowth", "s neg1 RUGrowth", " s negative raw unit growth in one or more  sector");
    doRes("gNeg1RawUnitGrowth", "g neg1 RUGrowth", "g negative raw unit growth in one or more sector");
    doRes("rNeg2RawUnitGrowth", "r neg2+ RUGrowth", " r negative raw unit growth in 2+ sector");
    doRes("cNeg2RawUnitGrowth", "c neg2+ RUGrowth", " c negative raw unit growth in 2+ sector");
    doRes("sNeg2RawUnitGrowth", "s neg2+ RUGrowth", " s negative raw unit growth in 2+ sector");
    doRes("gNeg2RawUnitGrowth", "g neg2+ RUGrowth", "gnegative raw unit growth in 2+ sector");
    doRes("rDNeg1RawUnitGrowth", "r Dneg1 RUGrowth", "Died r negative raw unit growth in one or more  sector", 2, 2, 1, 0, LIST8 | SKIPUNSET | BOTH | THISYEAR | THISYEARUNITS, ROWS1 | LIST10 | CURAVE | CURUNITS | BOTH | SKIPUNSET, 0);
    doRes("cDNeg1RawUnitGrowth", "c Dneg1 RUGrowth", " Died c negative raw unit growth in one or more sector");
    doRes("sDNeg1RawUnitGrowth", "s Dneg1 RUGrowth", "Died  s negative raw unit growth in one or more  sector");
    doRes("gDNeg1RawUnitGrowth", "g Dneg1 RUGrowth", "Died g negative raw unit growth in one or more  sector");
    doRes("rDNeg2RawUnitGrowth", "r Dneg2+ RUGrowth", " Died r negative raw unit growth in 2+ sector");
    doRes("cDNeg2RawUnitGrowth", "c Dneg2+ RUGrowth", " Died c negative raw unit growth in 2+ sector");
    doRes("sDNeg2RawUnitGrowth", "s Dneg2+ RUGrowth", " Died s negative raw unit growth in 2+ sector");
    doRes("gDNeg2RawUnitGrowth", "g Dneg2+ RUGrowth", "g Died negative raw unit growth in 2+ sector");
    doRes("rADNeg1RawUnitGrowth", "r ADneg1 RUGrowth", "Accepted Died r negative raw unit growth in one sector", 2, 2, 1, 0, LIST8 | SKIPUNSET | BOTH | THISYEAR | THISYEARUNITS, ROWS1 | LIST11 | CURAVE | CURUNITS | BOTH | SKIPUNSET, ROWS1 | LIST13 | CURAVE | CURUNITS | BOTH | SKIPUNSET);
    doRes("cADNeg1RawUnitGrowth", "c ADneg1 RUGrowth", "Accepted  Died c negative raw unit growth in one sector");
    doRes("sADNeg1RawUnitGrowth", "s ADneg1 RUGrowth", "Accepted  Died  s negative raw unit growth in one sector");
    doRes("gADNeg1RawUnitGrowth", "g ADneg1 RUGrowth", "Accepted Died g negative raw unit growth in one sector");
    doRes("rADNeg2RawUnitGrowth", "r ADneg2+ RUGrowth", "Accepted  Died r negative raw unit growth in 2+ sector");
    doRes("cADNeg2RawUnitGrowth", "c ADneg2+ RUGrowth", "Accepted  Died c negative raw unit growth in 2+ sector");
    doRes("sADNeg2RawUnitGrowth", "s ADneg2+ RUGrowth", "Accepted  Died s negative raw unit growth in 2+ sector");
    doRes("gADNeg2RawUnitGrowth", "g ADneg2+ RUGrowth", "Accepted g Died negative raw unit growth in 2+ sector");
    doRes("DEADWTRADEDINCR", "DEAD trade Incr", "DEAD Percent Years worth increase/start year worth", 2, 2, 3, LIST3 | LIST2 | LIST5 | CUMUNITS | BOTH | SKIPUNSET, ROWS3 | LIST3 | SKIPUNSET | THISYEARAVE, ROWS2 | LIST3 | LIST10 | CUMAVE | CURAVE | SKIPUNSET | BOTH, 0);
    doRes("DEADWTRADEDINCRF5", "DEAD fav5 trade Incr", "DEAD Percent Years worth increase at Favor5/start year worth");
    doRes("DEADWTRADEDINCRF4", "DEAD fav4 trade Incr", "Percent Years worth increase at Favor4/start year worth");
    doRes("DEADWTRADEDINCRF3", "DEAD fav3 trade Incr", "Percent Years worth increase at Favor3/start year worth");
    doRes("DEADWTRADEDINCRF2", "DEAD fav2 trade Incr", "Percent Years worth increase at Favor2/start year worth");
    doRes("DEADWTRADEDINCRF1", "DEAD fav1 trade Incr", "Years worth increase at Favor1/start year worth");
    doRes("DEADWTRADEDINCRF0", "DEAD fav0 trade Incr", "Percent Years worth increase at Favor0/start year worth");
    doRes("DEADWTRADEDINCRMULT", "DEAD TradeWIncr", "Percent Years worth increase at trades this year/start year worth");
    doRes("DEADWTRADEDINCRSOS", "DEAD SOS trade incr Worth", "Percent Years worth increase at an planet SOS flag trade this year/start year worth");
    doRes("DEADWREJTRADEDINCR", "DEAD Trade Rejected %Incr", "Percent Worth incr of a rejected trade trade/start yr worth when dead", 2, 2, 3, LIST2 | LIST4 | CUMUNITS | BOTH | SKIPUNSET, ROWS2 | LIST3 | SKIPUNSET | CUMAVE, ROWS3 | LIST4 | LIST5 | SKIPUNSET | THISYEARAVE | SKIPUNSET | BOTH, ROWS2 | LIST10 | CURAVE | CUMUNITS | SKIPUNSET);
    doRes("DEADWLOSTTRADEDINCR", "DEAD Trade Incr Lost", "Percent Worth incr if other rejected the trade/start yr worth when dead");
    doRes("DEADUNTRADEDWINCR", "DEAD incrWorthNoTrade", "no trade offered percent yearly growth when dead including working, reserve: resource, staff, knowledge");
    doRes("WTRADEDF5", "fav5 received/init worth", "Percent received at favor 5 trade/initial worth", 2, 2, 2, (LIST1 | LIST5 | CUMUNITS | BOTH | SKIPUNSET), ROWS2 | LIST5 | SKIPUNSET | THISYEARUNITS | THISYEARAVE | SKIPUNSET | BOTH, 0, 0);
    doRes("WTRADEDF4", "fav4 received/init worth", "Percent received at favor 4 trade/initial worth");
    doRes("WTRADEDF3", "fav3 received/init worth", "Percent received at favor 3 trade/initial worth");
    doRes("WTRADEDF2", "fav2 received/init worth", "Percent received at favor 2 trade/initial worth");
    doRes("WTRADEDF1", "fav1 received/init worth", "Percent received at favor 1 trade/initial worth");
    doRes("WTRADED0", "fav0 received/init worth", "Percent received worthAtFavr0/initial worth");
    doRes("WTRADEDOS", "SOS received/Init Worth", "Percent received worthAtSOS/initial worth");
    doRes("WTRADERCDPERCENT", "received/init worth", "Percent my clan received/initial worth");
    doRes("WTRADERECEIVED", "received worth", "received canonical worth");
    doRes("WOTRADEGAVFRAC", "other gave/initial worth", "other received canonical worth/initial");
    doRes("WOTRADEGAVE", "other gave worth", "Percent worthAtSOS/initial worth");
    doRes("TRADEDRCDF5", "W rcd fav5", "Percent Worth received when trade at fav5/initial worth");
    doRes("TRADEDRCDF4", " W rcd fav4", "Percent Worth received when trade at fav4/initial worth");
    doRes("TRADEDRCDF3", "W rcd fav3", "Percent Worth received when trade at fav3/initial worth");
    doRes("TRADEDRCDF2", "W rcd fav2", "Percent Worth received when trade at fav2/initial worth");
    doRes("TRADEDRCDF1", "W rcd fav1", "Percent Worth received when trade at fav1/initial worth");
    doRes("TRADEDRCDF0", "W rcd fav0", "Percent Worth received when trade at fav 0/initial worth");
    doRes("TRADEDRCD", "W rcd", "Percent Worth received when trade/initial worth");
    doRes("TRADES%", "TRADES %", "Percent of trades per economy");
    doRes("WREJTRADE", "wRejectedTrade", "Percent lost worth  when econ rejected the trade", 2, 2, 2, (LIST1 | LIST4 | CUMUNITS | BOTH | SKIPUNSET), ROWS2 | LIST4 | SKIPUNSET | THISYEARUNITS | THISYEARAVE | SKIPUNSET | BOTH, 0, 0);
    doRes("WLOSTTRADE", "wLostTrade", "Percent lost worth if other clan lost the trade");
    doRes("MISSEDTRADE", "wMissedTrade", "Percent missed/no trade ");

    doRes("DEADTRADES%", "Dead Trades %", "Percent of trades per dead economy", 2, 2, 2, (LIST1 | THISYEARAVE | BOTH | SKIPUNSET), (LIST1 | CURAVE | CUMAVE | BOTH | SKIPUNSET), LIST0 | SKIPUNSET | CUMAVE | BOTH, 0);
    doRes(RCTBAL, "RCBal/TBal", "Percent RC balance/tbal", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes(RCBAL, "RCBal", "RC balance", 1, 1, 0, (LIST7 | CURAVE), 0, 0, 0);
    doRes("SGTBAL", "SG Balance", "Percent SG balance/worth", 1, 1, 0, (LIST7 | CURAVE), 0, 0, 0);
    doRes("RBAL", "RBal", "Percent R balance/ worth", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("SBAL", "S Balance", "Percent S balance/worth", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("CBAL", "CBal", "Percent C balance/worth", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("GBAL", "G Balance", "G balance/worth", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes(RCTWORTH, "RCWorth", "RC Worth/TWorth", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes(RCWORTH, "RCWorth", "RC Worth", 1, 1, 0, (LIST7 | CURAVE), 0, 0, 0);
    doRes("SGTWORTH", "SGWorth", "SG Worth", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("RWORTH", "RWorth", "R Worth", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("SWORTH", "AWorth", "S Worth", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("RCTBINC", "RCBalInc", "RC Balance Increase", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("SGBTINC", "SGBalInc", "SG Balance Increase", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("RCTWINC", "RCWInc", "RC Worth Inc", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("SGTWINC", "SGWInc", "SG Worth Inc", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("RCTREQGROWTHCOSTS3", "RCReqGCosts ", "PercentRC Required Growth Cost/RC Bal", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("SGTREQGROWTHCOSTS3", "SGReqGCosts ", "PercentSG Required Growth Cost/SG Bal", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("RCTGROWTHCOSTS3", "RCGCosts ", "Percent RC Growth Cost/RC Bal", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes(RCTGROWTHPERCENT, "RCGrowth% ", "Percent RC Growth /year start RC Bal", 1, 1, 0, (LIST8 | skipUnset | CURAVE), 0, 0, 0);
    doRes(RCWORTHGROWTHPERCENT, "RCWorthGrowth% ", "Percent RC Worth Growth /year start RC Worth", 5, 1, 0, (LIST8 | skipUnset | CURAVE), 0, 0, 0);
    doRes("RCGLT.5PERCENT", "RCGrowthLT.5%", "Percent RC growth LT .5 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCGLT2PERCENT", "RCGrowthLT2%", "Percent RC growth LT 2 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCGLT5PERCENT", "RCGrowthLT5%", "Percent RC growth LT 5 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes(RCGLT10PERCENT, "RCGrowthLT10%", "Percent RC growth LT 10 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCGLT25PERCENT", "RCGrowthLT25%", "Percent RC growth LT 25 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCGLT50PERCENT", "RCGrowthLT50%", "Percent RC growth LT 50 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes(RCGLT100PERCENT, "RCGrowthLT100%", "Percent RC growth LT 100 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCGGT100PERCENT", "RCGrowthGE100%", "Percent RC growth GE 100 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);

    doRes("RCWGLT.5PERCENT", "RCWGroLT.5%", "Percent RC Worth growth LT .5 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCWGLT2PERCENT", "RCWGroLT2%", "Percent RC Worth growth LT 2 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCWGLT5PERCENT", "RCWGroLT5%", "Percent RC Worth growth LT 5 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes(RCWGLT10PERCENT, "RCWGroLT10%", "Percent RC Worth growth GT 5 LT 10 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCWGLT25PERCENT", "RCWGroLT25%", "Percent Worth RC growth GT 10 LT 25 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCWGLT50PERCENT", "RCWorthGrowthLT50%", "Percent RC Worth growth GT 25 LT 50 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCWGLT100PERCENT", "RCWorthGrowthLT100%", "Percent RC Worth growth GT 50 LT 100 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCWGGT100PERCENT", "RCWorthGrowthGE100%", "Percent RC Worth growth GE 100 %", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("RCWGPERCENT", "RCWorthGrowth%", "Percent RC Worth growth", 5, 1, 0, LIST8 | THISYEARUNITS | BOTH, 0, 0, 0);
    doRes("SGTGROWTHCOSTS3", "SGGCosts ", "SG Growth Cost/SG Bal", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("RCTREQMAINTC3", "RcRQMCosts ", "rc req maintCsts / bal", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("SGTREQMAINTC3", "SgRQMCosts ", "sg req maintCsts / bal", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("RCTMAINTC3", "RCMCosts ", "rc maintCsts / bal", 2, 2, 2, (LIST11 | skipUnset | CURAVE), 0, 0, 0);
    doRes("SGTMAINTC3", "SGMCosts ", "sg maintCsts / bal", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("RCTTRAVC3", "RCTCosts ", "rc travelCsts / bal", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("SGTTRAVC3", "SGTCosts ", "sg travelCsts / bal", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("RCTRAWGROWTHCOSTS3", "rcRawGrowthCsts ", "% r req growthCsts / bal", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("SGTRAWGROWTHCOSTS3", "sgRawGrowthCsts ", "% s req growthCsts / bal", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("RCTMTG3", "rc mtgCsts ", "r mtgCsts / bal", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("SGTMTG3", "sg mtgCsts ", "s mtgCsts / bal", 1, 1, 0, (LIST7 | skipUnset | CURAVE), 0, 0, 0);
    doRes("INCRGSWAPCOSTSB3", "SwapBalCosts", "balance cost involved in repurposing material by Incr to help sectors with inadequate resources", 2, 6, 2, (LIST11 | CURUNITS | skipUnset), 0, 0, 0);
    doRes("DECRGSWAPCOSTS", "DecrSwapBalCosts", "balance cost involved in repurposing material by Decr to help sectors with inadequate resources", 2, 6, 2, (LIST11 | CURUNITS | skipUnset), 0, 0, 0);
    doRes("RXFERGSWAPCOSTS", "RXferSwapCosts", "balance cost involved in repurposing material by Xfer to help sectors with inadequate resources", 2, 6, 2, (LIST11 | CURUNITS | skipUnset), 0, 0, 0);
    doRes("SXFERGSWAPCOSTSB", "SXferSwapCosts", "balance cost involved in repurposing material by Xfer to help sectors with inadequate resources", 2, 6, 2, (LIST11 | CURUNITS | skipUnset), 0, 0, 0);
    doRes("GROWTHSWAP", "growthSwapBalCosts", "% growth loop costs involved in repurposing material to help sectors with inadequate resources", 2, 6, 2, (LIST11 | CURUNITS | skipUnset), 0, 0, 0);
    doRes("HEALTHSWAP", "HealthSwapBalCosts", "% health loop costs involved in repurposing material to help sectors with inadequate resources", 2, 6, 2, (LIST11 | CURUNITS | skipUnset), 0, 0, 0);
// doRes(rWORTH,"Worth", "IGNORE Value including year end working, reserve: resource, staff, knowledge", 6,2, (cur | CURUNITS | CURAVE | sum | both), (LIST14 | curUnitAve | cur | CURUNITS | both | sum), 0, 0);
    rende4 = e4;
    if (E.debugStats) {
      description = "***starting***";
      for (int rrr = 0; rrr < rende4; rrr++) {
        if (resI[rrr] == null) {
          doMyErr("missing resI rN=" + rrr + " prevDescription=" + description);
        }
        if (resV[rrr] == null) {
          doMyErr("missing resV rN=" + rrr + " prevDescription=" + description);
        }
        if (resS[rrr] == null) {
          doMyErr("missing resS rN=" + rrr + " prevDescription=" + description);
        }
        description = resS[rrr][0];
      }
    }
  }

  static volatile int[] yrs1 = {0, 1};
  static volatile int[] yrs2 = {0, 1, 8, 15, 22, 29, 36}; //position of age starts
  static volatile int cnt = 0, curIx = -7, newIx = -7, ccntl = -8, new0 = -11, icur0 = 0;
  static volatile int valid0 = -4, cur0 = -7, yearsGrp = 20, newValid = 10, cura = 0;
  static volatile int rn = 0, rN = 0, statsLim = 0;
  static volatile int lResI = 0, lResV = 0;
  static volatile int mdepth = 22, row0 = -1;
  static volatile long depth = 20, ydepth = 10;

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   * locks are the same as that of the previous doRes
   *
   * @param dName name of setStats recording a result
   * @param desc description of result
   * @param detail detailed description available by clicking description
   * @param depth number of successive years displayed 1-6
   * @param ydepth number of successive years in results separated by years
   * @param fracs number of fraction digits
   *
   * @return rN the index of a vector of result integer index into
   * resI,resV,resS
   */
  private int doRes(String dName, String desc, String detail, int depth, int ydepth, int fracs) {
    return doRes(dName, desc, detail, depth, ydepth, fracs, DUP, 0L, 0L, 0L);
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   * duplicate that of the previous doRes.
   *
   *
   * @param dName name of setStats recording a result
   * @param desc description of result
   * @param detail detailed description available by clicking description set
   * depth=1, ydepth=1
   * @param fracs number of fraction digits
   * @param lock0 0'th lock to match the offered keys
   * @param lock1 1'th lock to match offered keys may be empty
   * @param lock2 2nd lock to match offered keys, may be empty
   * @param lock3 3rd lock to match offered keys may be empty
   *
   * @return rN the index of a vector of result integer index into
   * resI,resV,resS
   */
  private int doRes(String dName, String desc, String detail, int fracs, long lock0, long lock1, long lock2, long lock3) {
    return doRes(dName, desc, detail, 1, 1, fracs, lock0, lock1, lock2, lock3);
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   *
   * @param dName name of setStats recording a result
   * @param desc description of result
   * @param detail detailed description available by clicking description
   * @param depth number of successive years displayed 1-6 ydepth=1
   * @param fracs number of fraction digits
   * @param lock0 0'th lock to match the offered keys
   * @param lock1 1'th lock to match offered keys may be empty
   * @param lock2 2nd lock to match offered keys, may be empty
   * @param lock3 3rd lock to match offered keys may be empty
   *
   * @return rN the index of a vector of result integer index into
   * resI,resV,resS
   */
  private int doRes(String dName, String desc, String detail, int depth, int fracs, long lock0, long lock1, long lock2, long lock3) {
    return doRes(dName, desc, detail, depth, 1, fracs, lock0, lock1, lock2, lock3);
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   *
   * @param dName name of setStats recording a result
   * @param desc description of result
   * @param detail detailed description available by clicking description
   * @param depth number of successive years displayed 1-6
   * @param ydepth number of successive years in results separated by years
   * @param fracs number of fraction digits
   * @param lock0 0'th lock to match the offered keys
   * @param lock1 1'th lock to match offered keys may be empty
   * @param lock2 2nd lock to match offered keys, may be empty
   * @param lock3 3rd lock to match offered keys may be empty
   *
   * @return rN the index of a vector of result integer index into
   * resI,resV,resS
   */
  private int doRes(String dName, String desc, String detail, int depth, int ydepth, int fracs, long lock0, long lock1, long lock2, long lock3) {
    e4++;
    if (e4 > rende3) {
      doMyErr("---------------------Fatal e4=%3d > rende3=%3d size of resI,resV,resS increase size of rende3");
    }
    if(E.debugDoRes)System.out.println("in doRes a, rn=" + e4 + " < rende3=" + rende3 + ", dName=" + dName + ", desc=" + desc + ", detail=" + detail + ",locks=" + lock0 + ", " + lock1 + ", " + lock2 + ", " + lock3);//%o,%o,%o,%o %n", e4, rende3, dName, desc, detail, lock0, lock1, lock2, lock3);
    if (resMap.containsKey(dName)) {
      doMyErr(">>>>>>dName=" + dName + " already exists, e4=" + e4);
    }
    int rN = e4;
    resMap.put(dName, rN);
    return doRes(rN, desc, detail, depth, ydepth, fracs, lock0, lock1, lock2, lock3);
  }

  static volatile int rDepth = 1, rYdepth = 1, rFracs = 0, yrsIx = -7;
  static volatile long rLock0 = 0L, rLock1 = 0L, rLock2 = 0L, rLock3 = 0L;

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   *
   * @param rN number of the result
   * @param desc description
   * @param detail detailed description available by clicking description
   * @param fracs number of fraction digits depth=1, ydepth=1
   * @param lock0 0'th lock to match the offered keys
   * @param lock1 1'th lock to match offered keys may be empty
   * @param lock2 2nd lock to match offered keys, may be empty
   * @param lock3 3rd lock to match offered keys may be empty
   *
   * @return rN the index of a vector of result integer index into
   * resI,resV,resS recall doRes with ydepth=1, depth = 1
   */
  private int doRes(int rN, String desc, String detail, int fracs, long lock0, long lock1, long lock2, long lock3) {
    return doRes(rN, desc, detail, 1, 1, fracs, lock0, lock1, lock2, lock3);
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   *
   * @param rN number of the result display
   * @param desc description title in the stat table
   * @param detail detailed description available by clicking description
   * @param depth number of years displayed in the normal LIST pages groups,
   * pages 10-17
   * @param fracs number of fraction digits
   * @param lock0 0'th lock to match the offered keys
   * @param lock1 1'th lock to match offered keys may be empty
   * @param lock2 2nd lock to match offered keys, may be empty
   * @param lock3 3rd lock to match offered keys may be empty
   *
   * @return rN the index of a vextor of result lock which match call keys
   * recall doRes with ydepth=1;
   */
  int doRes(int rN, String desc, String detail, int depth, int fracs, long lock0, long lock1, long lock2, long lock3) {
    return doRes(rN, desc, detail, depth, 1, fracs, lock0, lock1, lock2, lock3);
  }

  /**
   * Skip the duplicate values from the previous doRes, but leave the duplicates
   * set define method calls creating output lines in the results array when
   * some bits in the locks correspond with bits in the offered key from
   * StarTrader
   *
   * @param rN number of the result display
   * @param desc description title in the stat table
   * @param detail detailed description available by clicking description
   * @param depth number of years displayed in the normal LIST pages
   * @param yDepth number of years displayed for groups, pages 10-17
   * @param fracs number of fraction digits
   * @param lock0 0'th lock to match the offered keys
   * @param lock1 1'th lock to match offered keys may be empty
   * @param lock2 2nd lock to match offered keys, may be empty
   * @param lock3 3rd lock to match offered keys may be empty
   *
   * @return rN the index of a vextor of result lock which match call keys
   * recall doRes with ydepth=1;
   */
  int skipDupDoRes(int rN, String desc, String detail, int depth, int yDepth, int fracs, long lock0, long lock1, long lock2, long lock3) {
    long saveL[] = {rLock0, rLock1, rLock2, rLock3};
    int saveI[] = {rDepth, rYdepth, rFracs};
    int res = doRes(rN, desc, detail, depth, yDepth, fracs, lock0, lock1, lock2, lock3);
    rDepth = saveI[0];
    rYdepth = saveI[1];
    rFracs = saveI[2];
    rLock0 = saveL[0];
    rLock1 = saveL[1];
    rLock2 = saveL[2];
    rLock3 = saveL[3];
    return res;
  }

  /*
  int rDepth = 1, rYdepth = 1, rFracs = 0,yrsIx=-7;
  long rLock0 = 0L, rLock1 = 0L, rLock2 = 0L, rLock3 = 0L;
   */
  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   * the last 4 parameters are filled in with the values from the doRes
   * immediately before this doRes, This allows a quick definition using the
   * previous values which were the same If the immediate previous was
   * shortened, it was filled from the previous doRes Eventually a previous has
   * all the parameters which are used by the following doRes
   *
   * @param rN number of the result
   * @param desc description
   * @param detail detailed description available by clicking description
   * @param depth depth of reports for all years
   * @param ydepth depth for year groups, pages 10-17
   * @param fracs number of fraction digits
   *
   *
   * @return rN the index of a vector of result locks which match call keys
   * recall doRes with ydepth=1;
   */
  int doRes(int rN, String desc, String detail, int depth, int ydepth, int fracs) {
    return doRes(rN, desc, detail, depth, ydepth, fracs, rLock0, rLock1, rLock2, rLock3);
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   *
   * @param rN number of the result
   * @param desc description
   * @param detail detailed description available by clicking description
   * @param depth depth of reports for all years
   * @param ydepth depth for 5 year groups, LIST10,LIST11,LIST12,LIST13,LIST14
   * @param fracs number of fraction digits
   * @param lock0 0'th lock to match the offered keys
   * @param lock1 1'th lock to match offered keys may be empty
   * @param lock2 2nd lock to match offered keys, may be empty
   * @param lock3 3rd lock to match offered keys may be empty
   *
   * @return rN the index of a vector of result locks which match call keys
   * recall doRes with ydepth=1;
   */
  private int doRes(int rN, String desc, String detail, int depth, int ydepth, int fracs, long lock0, long lock1, long lock2, long lock3) {
    if (!((lock0 & SKIPDUP) > 0)) { // don't change mem values if SKIPDUP
      rDepth = depth;  // save the last 7 params for use in calls without them
      rYdepth = ydepth;
      rFracs = fracs;
      rLock0 = lock0;
      rLock1 = lock1;
      rLock2 = lock2;
      rLock3 = lock3;
    } else if ((lock0 & DUP) > 0) { // dup the previous values
      depth = rDepth;
      ydepth = rYdepth;
      fracs = rFracs;
      lock0 = rLock0;
      lock1 = rLock1;
      lock2 = rLock2;
      lock3 = rLock3;

    }
    // long mLocks = (lock0 | lock1 | lock2 | lock3) & (LIST10 | LIST11 | LIST12 | LIST13 | LIST14);
    long mLocks = (lock0 | lock1 | lock2 | lock3) & AGESMASK;
    int bvector2l = mLocks == 0 ? STATSSHORTLEN : STATSLONGLEN; // short if no age years option
    int yrsMax = bvector2l;
    //  int[] yrsAll = mLocks == 0 ? STATSSHORTLEN : STATSLONGLEN;
    if (E.debugStats) {
      if (!(resV[rN] == null) && resV[rN].length > 1) {
        doMyErr("null doRes rN=" + rN + " curDescription=" + (resS[rN] != null && resS[rN][0] != null ? resS[rN][0] : " null cur descrip ") + " prevDescription=" + description);
      }
    }
    resV[rN] = new double[bvector2l][][]; // create the values structure
    resI[rN] = new long[bvector2l][][];
    /**
     * resI
     * [resNum][ICUM,ICUR0,...ICUR6(MAXDEPTH*6rounds+2][PCNTS,SCNTS,CCONTROLD][LCLANS
     * :{over CCONTROLD}ISSET,IVALID,IPOWER:{only for
     * ICUM,CCONTROLD}LOCKS0,LOCKS1,L0CKS2,LOCKS3,IFRACS,IDEPTH] valid number of
     * valid entries 0=unset,1=cur0,2=cur1,7=cur6
     */
    int curIx = 10, ccntl = -5, ageIx = -6, mDepth = -MAXDEPTH;
    int newIx = 56, cur0 = 25;
    // for (int yrsIx = ICUR0 - 1; yrsIx < bvector2l; yrsIx++) { // cum,cur0-6
    // set only the first for a year, doStartYear will move entries up each yr
    for (yrsIx = DCUM; yrsIx < yrsMax; yrsIx += yrsIx == DCUM ? DCUR0 - DCUM : MAXDEPTH) { // ICUM,ICUR0 if yrs2 5 more ages
      newIx = (yrsIx - ICUR0 + 1) % MAXDEPTH;
      curIx = (yrsIx - ICUR0) % MAXDEPTH; //index in relation to CUR0
      cur0 = yrsIx - curIx;  // start of an age group
      ageIx = (yrsIx - ICUR0) / MAXDEPTH; // index into age groups 0f years all <4,<8,<16,<32,32+

      resV[rN][yrsIx] = new double[DVECTOR3L][]; // p,s
      resI[rN][yrsIx] = new long[IVECTOR3L][];// p,s,ctl
      for (int ixPorS = 0; ixPorS < 2; ixPorS++) {
        resV[rN][yrsIx][ixPorS] = new double[E.lclans];
        resI[rN][yrsIx][ixPorS] = new long[E.lclans]; // counts array
        for (int clanIx = 0; clanIx < E.LCLANS; clanIx++) {
          resV[rN][yrsIx][ixPorS][clanIx] = 0.0;
          resI[rN][yrsIx][ixPorS][clanIx] = 0;
        }
      }
      ccntl = ((yrsIx == 0) ? 11 : 3);
      resI[rN][yrsIx][CCONTROLD] = new long[ccntl];
      resI[rN][yrsIx][CCONTROLD][ISSET] = 0;
      resI[rN][yrsIx][CCONTROLD][IVALID] = 0;
      resI[rN][yrsIx][CCONTROLD][IPOWER] = 0;
      if (yrsIx == 0) {
        resI[rN][yrsIx][CCONTROLD][LOCKS0] = lock0;
        resI[rN][yrsIx][CCONTROLD][LOCKS1] = lock1;
        resI[rN][yrsIx][CCONTROLD][LOCKS2] = lock2;
        resI[rN][yrsIx][CCONTROLD][LOCKS3] = lock3;
        resI[rN][yrsIx][CCONTROLD][IFRACS] = fracs;
        resI[rN][yrsIx][CCONTROLD][IDEPTH] = Math.max(minDepth, Math.min(maxDepth, depth));
        resI[rN][yrsIx][CCONTROLD][IYDEPTH] = Math.max(minYDepth, Math.min(maxYDepth, ydepth));
      }
    }
    description = desc;
    resS[rN] = new String[2];
    resS[rN][0] = desc;
    resS[rN][1] = detail;
    return rN;
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   *
   * @param rN number of the result
   * @param desc description
   * @param detail detailed description available by clicking description the
   * last 7 parameters are filled in with the values from the doRes immediately
   * before this doRes, This allows a quick definition using the previous values
   * which were the same If the immediate previous was shortened, it was filled
   * from the previous doRes Eventually a previous has all the parameters which
   * are used by the following doRes
   *
   * @return rN the index of a vector of result locks which match call keys
   * recall doRes with ydepth=1;
   */
  private int doRes(int rN, String desc, String detail) {
    return doRes(rN, desc, detail, rDepth, rYdepth, rFracs, rLock0, rLock1, rLock2, rLock3);
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   * the last 7 parameters are filled in with the values from the doRes
   * immediately before this doRes, This allows a quick definition using the
   * previous values which were the same If the immediate previous was
   * shortened, it was filled from the previous doRes Eventually a previous has
   * all the parameters which are used by the following doRes
   *
   * @param rN number of the result
   * @param desc description
   * @param detail detailed description available by clicking description
   *
   * @return rN the index of a vector of result locks which match call keys
   * recall doRes with ydepth=1;
   */
  private int doRes(String rs, String desc, String detail) {
    return doRes(rs, desc, detail, rDepth, rYdepth, rFracs, rLock0, rLock1, rLock2, rLock3);
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   * the last 7 parameters are filled in with the values from the doRes
   * immediately before this doRes, This allows a quick definition using the
   * previous values which were the same If the immediate previous was
   * shortened, it was filled from the previous doRes Eventually a previous has
   * all the parameters which are used by the following doRes
   *
   * @param rN number of the result
   * @param desc description
   * @param detail detailed description available by clicking description
   * @param lockMore temporarily for this time add lockMore to lock3
   *
   * @return rN the index of a vector of result locks which match call keys
   * recall doRes with ydepth=1;
   */
  private int doRes(String rs, String desc, String detail, Long lockMore) {
    long saveRLock3 = rLock3;
    int res = doRes(rs, desc, detail, rDepth, rYdepth, rFracs, rLock0, rLock1, rLock2, rLock3 | lockMore);
    rLock3 = saveRLock3;
    return res;
  }

  /**
   * define method calls creating output lines in the results array when some
   * bits in the locks correspond with bits in the offered key from StarTrader
   * the last 7 parameters are filled in with the values from the doRes
   * immediately before this doRes, This allows a quick definition using the
   * previous values which were the same If the immediate previous was
   * shortened, it was filled from the previous doRes Eventually a previous has
   * all the parameters which are used by the following doRes
   *
   * @param rN number of the result
   * @param desc description
   * @param detail detailed description available by clicking description
   * @param lockMore2 for this time only add lockMore2 to lock2
   * @param lockMore3 for this time only add lockMore3 to lock3
   *
   * @return rN the index of a vector of result locks which match call keys
   * recall doRes with ydepth=1;
   */
  private int doRes(String rs, String desc, String detail, Long lockMore2, Long lockMore3) {
    long saveRLock2 = rLock2;
    long saveRLock3 = rLock3;
    int res = doRes(rs, desc, detail, rDepth, rYdepth, rFracs, rLock0, rLock1, rLock2 | lockMore2, rLock3 | lockMore3);
    rLock2 = saveRLock2;
    rLock3 = saveRLock3;
    return res;
  }

  /**
   * move the cur values up one year from cur5->cur6 limit the moves by max of
   * depth and ydepth, limit also by valid, valid increments in doEndYear called
   * at the end of doYear
   *
   * @return number of undefined entries
   */
  int doStartYear() {
    // loop through all of the entries

    int maxCopy = MAXDEPTH - 1; //don't copy curIx=6 to above => a curIx=0
    yearErrCnt = 0;

    try {
      clearWH();
      iMaxThreads[0] = (int) maxThreads[0][0];
      for (rN = 0; rN < rende4 && !dfe(); rN++) {
        rn = rN;
        // skip undefined entries without error
        if (resI[rN] != null) {
          cnt++;  // count valid rN entries
          // move each age up 1 5->6,4->5...0->1 skip 6->0,
          // we are moving the Ivector2, Dvector2
          // jj iterates 6,5,4,3,2,1,6,5
          //create new array for each ICUR0,DCUR0
          /**
           * resI [resNum][ICUM,ICUR0,...ICUR6(7*6rounds
           * +2][PCNTS,SCNTS,CCONTROLD][LCLANS :{over
           * CCONTROLD}ISSET,IVALID,IPOWER:{only for
           * ICUM,CCONTROLD}LOCKS0,LOCKS1,L0CKS2,LOCKS3,IFRACS,IDEPTH] valid
           * number of valid entries 0=unset,1=cur0,2=cur1,7=cur6
           */
          lResI = resI[rN].length;
          lResV = resV[rN].length;
          statsLim = lResI >= STATSLONGLIM ? STATSLONGLIM : STATSSHORTLIM; // length of vector 45:10,  start 41:6
          // start moving things up from yrsIx to yrsIx+1, not to next age start if they are the same depth
          valid = 0;
          //YEARS    0,   4,   8,    16,    32,   999999}; // + over 31+ group
          //  CUM CUR0  CUR1 CUR2  CUR3  CUR4  CUR5
          //starts  0      1        8        15       22       29       36       43<45>   
          //   LIST015 LIST10 LIST11 LIST12 LIST13 LIST14 LIST15
          // LIST8-20        
          //              44                0
          // start yrsIx as the top one to copy up one row, 42 or 6
          double[][] dd = new double[DVECTOR3L][]; // p,s
          long[][] ii = new long[IVECTOR3L][]; // p,s,controld
          for (yrsIx = statsLim - 1; yrsIx > ICUM && !dfe(); yrsIx--) { // 6 or 41 not 7 or 42
            newIx = (yrsIx - ICUR0) % MAXDEPTH; // if newIx==0, time for new row0 in this age
            new0 = newIx; // 10:45 yrsIx 8:42,do not copy7 to 8, 35 to 36, 28 to 29, 21 to 22 etc
            // 1,8,15,22,29,36,43 == the  age start, 0,7,14,21,28,35 are do not copy
            yearsGrp = (yrsIx - ICUR0) / MAXDEPTH; // (44-1)= 43/7=1 =6,5...0 should be depth
            curIx = (yrsIx - ICUR0) % MAXDEPTH; //index in relation to 0 of yr grp 43%7 =1:0
            cur0 = cura = yrsIx - curIx;  //43 - 1 = 42 yrsIx-yrsIx + ICUR0
            icur0 = (cur0 - ICUR0) % MAXDEPTH;

            wasHere = "rN=" + rN + ", statsLim=" + statsLim + ", yrsIx=" + yrsIx + " lResI=" + lResI + ", lResV=" + lResV + ", cur0=" + cur0 + ", curIx=" + curIx + ", icur0=" + icur0 + ", Ty=" + (twh = (new Date().getTime() - st.startYear)) + ", thread" + Thread.currentThread().getName() + ", dif=" + (statsLim - yrsIx);

            wasHere += " " + (resI[rN][yrsIx] == null ? " not null" + yrsIx : " is null" + yrsIx);

            if (icur0 != 0) { // (42 -1) = 41%7 = 
              doMyErr(String.format("In doStartYear a thread=" + Thread.currentThread().getName() + ", Error icur0=" + icur0 + " not 0 error rN=%2d,resS[Rn][0]=%5s, resI[rN].length=%3d, curIx=" + curIx + " ICUR0=" + ICUR0 + ",yrsIx=" + yrsIx + ", cur0=" + cur0 + ", cura=" + cura + ", icur0=" + icur0 + "\n", rN, resS[rN][0], resI[rN].length));
            }
            // using ICUM choose depth = YDEPTH if yearsGrp !=0 , IDEPTH if == 0 
            depth = resI[rN][ICUM][CCONTROLD][(yearsGrp == 0 ? IDEPTH : IYDEPTH)];

            if (resI[rN][yrsIx] != null) { // don't try to mov null up one
              // each yearAge has its own isset and valid and power
              if (true || (curIx + 1) <= (depth)) {
                wasHere2 = "rN=" + rN + ", statsLim=" + statsLim + ", yrsIx=" + yrsIx + " lResI=" + lResI + ", lResV=" + lResV + ", cur0=" + cur0 + ", curIx=" + curIx + ", icur0=" + icur0 + ", Ty=" + (twh2 = (new Date().getTime() - st.startYear)) + ", thread" + Thread.currentThread().getName() + ", dif=" + (statsLim - yrsIx);

                wasHere2 += " " + (resI[rN][yrsIx] == null ? " not null" + yrsIx : " is null" + yrsIx);

                // copy everything here, except the 7 to 0
                if (resI[rN][yrsIx][CCONTROLD][ISSET] > 0) {
                  // isset and within depth, keep larger val or set depth nexIx
                  valid = Math.max(valid, (yrsIx + 1) % MAXDEPTH); // highest destination value
                }
                if (curIx < maxCopy) { // copyup  everything except not from curIx==maxCopy(6) to 0
                  //copy ageStart to ageStart+1 etc
//copy reference up, including the current values
                  wasHere3 = "rN=" + rN + ", statsLim=" + statsLim + ", yrsIx=" + yrsIx + " lResI=" + lResI + ", lResV=" + lResV + ", cur0=" + cur0 + ", curIx=" + curIx + ", icur0=" + icur0 + ", Ty=" + (twh2 = (new Date().getTime() - st.startYear)) + ", thread" + Thread.currentThread().getName() + ", dif=" + (statsLim - yrsIx);

                  wasHere3 += " " + (resI[rN][yrsIx] == null ? " not null" + yrsIx : " is null" + yrsIx);
                  long rrr[][] = resI[rN][yrsIx]; // test for null
                  // overwrite any previous reference 
                  // ensure that each reference is in one ICURO+yrsIx
                  resI[rN][yrsIx + 1] = resI[rN][yrsIx];  //moving [pors][clan][CCONTROLD]. up by 1 to depth
                  resV[rN][yrsIx + 1] = resV[rN][yrsIx];
                  //  if (E.debugStatsOut && (rN == 96 || rN <= 3) && (yrsIx % 5 ) == 0 && (resI[rN] != null) && (resI[rN][ICUR0] != null)) {
                  if (E.debugStatsOut && (rN == 96 || rN <= 3) && (resI[rN] != null) && (resI[rN][ICUR0] != null)) {
                    // System.out.printf("In doStartYear have at %s rN%d, yrsIx%d, idepth%d, ydepth%d, valid%d, isseta%d, issetb%d\n", resS[rN][0], rN, yrsIx, resI[rN][ICUM][CCONTROLD][IDEPTH], resI[rN][ICUM][CCONTROLD][IYDEPTH], resI[rN][ICUR0][CCONTROLD][IVALID], resI[rN][ICUR0][CCONTROLD][ISSET], resI[rN][ICUR0 + 1] == null ? -2 : resI[rN][ICUR0 + 1][CCONTROLD][ISSET]);
                    if (E.debugDoStartYear) {
                      System.out.println("In doStartYear  after move up " + resS[rN][0] + " rN" + rN + ", statsLim=" + statsLim + ", yrsIx=" + yrsIx + " lResI=" + lResI + ", lResV=" + lResV + ", cur0=" + cur0 + ", curIx=" + curIx + ", icur0=" + icur0 + ", Ty=" + ((new Date().getTime() - st.startYear)) + ", thread=" + Thread.currentThread().getName() + ", dif=" + (statsLim - yrsIx)
                              + ", idepth" + resI[rN][ICUM][CCONTROLD][IDEPTH]
                              + ", ydepth" + resI[rN][ICUM][CCONTROLD][IYDEPTH]
                              + ", valid" + resI[rN][ICUR0][CCONTROLD][IVALID]
                              + ", isseta" + resI[rN][ICUR0][CCONTROLD][ISSET]);
                    }
                    System.out.flush();
                    if (E.debugDoStartYear) {
                      System.out.println("In doStartYear  set2  after move up after flush" + resS[rN][0] + " rN" + rN + ", statsLim=" + statsLim + ", yrsIx=" + yrsIx + " lResI=" + lResI + ", lResV=" + lResV + ", cur0=" + cur0 + ", curIx=" + curIx + ", icur0=" + icur0 + ", Ty=" + ((new Date().getTime() - st.startYear)) + ", thread=" + Thread.currentThread().getName() + ", dif=" + (statsLim - yrsIx)
                              + ", issetb" + ((resI[rN][ICUR0 + 1] == null) ? -2
                                      : ((resI[rN][ICUR0 + 1][CCONTROLD] == null) ? -4
                                              : resI[rN][ICUR0 + 1][CCONTROLD][ISSET])));
                    }
                    System.out.flush();
                  }// end print if
                }
                if (curIx == 0) {  // 0 instance, create new array objects
                  dd = new double[DVECTOR3L][]; // p,s
                  ii = new long[IVECTOR3L][]; // p,s,controld
                  for (int ixPorS = 0; ixPorS < 2; ixPorS++) {
                    dd[ixPorS] = new double[E.lclans];// make the clans
                    ii[ixPorS] = new long[E.lclans]; // make array for i
                    //resV[rN][yrsIx][ixPorS] = new double[E.lclans];// make the clans
                    //resI[rN][yrsIx][ixPorS] = new long[E.lclans]; // make array for i
                    for (int clanIx = 0; clanIx < E.LCLANS; clanIx++) {
                      // zero the instances of the clan
                      dd[ixPorS][clanIx] = 0.0;
                      ii[ixPorS][clanIx] = 0;
                      //resV[rN][yrsIx][ixPorS][clanIx] = 0.0;
                      //resI[rN][yrsIx][ixPorS][clanIx] = 0;
                    }
                  }
                  ii[CCONTROLD] = new long[3];
                  ii[CCONTROLD][ISSET] = 0;
                  ii[CCONTROLD][IVALID] = valid;
                  // resI[rN][yrsIx][CCONTROLD] = new long[3];
                  //resI[rN][yrsIx][CCONTROLD][ISSET] = 0;
                  //resI[rN][yrsIx][CCONTROLD][IVALID] = valid;
                  ii[CCONTROLD][IPOWER] = 0;
                  valid = 0; // reset for the next row
                  //resI[rN][yrsIx][CCONTROLD][IPOWER] = 0;
                }
              }// not null
              wasHere4 = "rN=" + rN + ", statsLim=" + statsLim + ", yrsIx=" + yrsIx + " lResI=" + lResI + ", lResV=" + lResV + ", cur0=" + cur0 + ", curIx=" + curIx + ", icur0=" + icur0 + ", Ty=" + (twh4 = (new Date().getTime() - st.startYear)) + ", thread" + Thread.currentThread().getName() + ", dif=" + (statsLim - yrsIx);

              wasHere4 += " " + (resI[rN][yrsIx] == null ? " not null" + yrsIx : " is null" + yrsIx);
            //  long resLock[][] = resI[rN][yrsIx];
            //  if (resLock != null) synchronized (resLock) {
                resV[rN][yrsIx] = dd;
                resI[rN][yrsIx] = ii;
              //}
            } // end loop on yrsIx

            boolean doYears = statsLim >= STATSSHORTLIM;
            if (E.debugStatsOut && ((rN == 96 || rN <= 1) && resI[rN] != null && resI[rN][ICUR0] != null)) {
              //System.out.printf("In doStartYear at %s rN%d, idepth%d, ydepth%d, valid%d, isseta%d, issetb%d\n", resS[rN][0], rN, resI[rN][ICUM][CCONTROLD][IDEPTH], resI[rN][ICUM][CCONTROLD][IYDEPTH], resI[rN][ICUR0][CCONTROLD][IVALID], resI[rN][ICUR0][CCONTROLD][ISSET], resI[rN][ICUR0 + 1] == null ? -2 : resI[rN][ICUR0 + 1][CCONTROLD][ISSET]);
              if (E.debugDoStartYear) {
                System.out.println("In doStartYear at new zero=" + resS[rN][0] + " rN" + rN + sfe()
                        + "Ty" + (new Date().getTime() - st.startYear) + ", th=" + Thread.currentThread().getName() + " yrsIx" + yrsIx
                        + ", idepth" + resI[rN][ICUM][CCONTROLD][IDEPTH]
                        + ", ydepth" + resI[rN][ICUM][CCONTROLD][IYDEPTH]
                        + ", valid" + resI[rN][ICUR0][CCONTROLD][IVALID]
                        + ", isseta" + resI[rN][ICUR0][CCONTROLD][ISSET]);
              }
              System.out.flush();
              if (E.debugDoStartYear) {
                System.out.println("In doStartYear at new zero after flush()=" + resS[rN][0] + " rN" + rN
                        + "Ty" + (new Date().getTime() - st.startYear) + ", th=" + Thread.currentThread().getName() + " yrsIx" + yrsIx
                        + ", issetb" + ((resI[rN][ICUR0 + 1] == null) ? -2
                                : (resI[rN][ICUR0 + 1][CCONTROLD] == null) ? -4
                                        : resI[rN][ICUR0 + 1][CCONTROLD][ISSET]));
              }
              System.out.flush();
              if (doYears) {
                // System.out.print(+ ", " + resI[rN][lockC][0][rValid2] + ", " + resI[rN][lockC][0][rValid3] + ", rSets=" + resI[rN][lockC][0][rSet] + ", " + resI[rN][lockC][0][rSet1] + ", " + resI[rN][lockC][0][rSet2] + ", " + resI[rN][lockC][0][rSet3] + ", rcnts=" + resI[rN][lockC][0][rcnt] + ", " + resI[rN][lockC][0][rcnt1] + ", " + resI[rN][lockC][0][rcnt2] + ", " + resI[rN][lockC][0][rcnt3]);
              }
            }// end print if
          }// end if not null
          //      if (((resI[rN][ICUM][CCONTROLD][IDEPTH] > 1) && resI[rN][ICUM][CCONTROLD][IVALID] > resI[rN][ICUM][CCONTROLD][IDEPTH]) || resI[rN][rcur0 + resI[rN][lockC][0][rValid] - 1] == null) {
          //     E.myTest(true, "doStartYear rcur" + (resI[rN][lockC][0][rValid] - 1) + " is null" + " rValid=" + resI[rN][lockC][0][rValid] + (resI[rN][rcur0] == null ? " !!!" : " " + "rcur0") + (resI[rN][rcur0 + 1] == null ? " !!!" : " " + "rcur1") + (resI[rN][rcur0 + 2] == null ? " !!!" : " " + "rcur2") + (resI[rN][rcur0 + 3] == null ? " !!!" : " " + "rcur3") + (resI[rN][rcur0 + 4] == null ? " !!!" : " " + "rcur4") + (resI[rN][rcur0 + 5] == null ? " !!!" : " " + "rcur5"));
          //        }
        }
      }// end rN loop

    } catch (trade.WasFatalError ex) {
      ex.printStackTrace(pw);
      thirdStack = sw.toString();
      flushes();
      System.err.println(curEconName + " " + Econ.nowThread + " Caught Exception " + ex.toString() + "  cause=" + ex.getCause() + " message=" + ex.getMessage() + andMore());
//      ex.printStackTrace(System.err);
      System.exit(-5);
    } catch (Exception | Error ex) {
      newError = true;
      firstStack = secondStack + "";
      ex.printStackTrace(pw);
      secondStack = sw.toString();
      flushes();
      System.err.println("doStartYear" + year + curEconName + " " + Thread.currentThread().getName() + " Caught Exception " + ex.toString() + "  cause=" + ex.getCause() + " message=" + ex.getMessage() + "lRes I" + resI[rN].length + ", V" + resV[rN].length + andMore());
//      ex.printStackTrace(System.err);
      st.setFatalError(); // throws WasFatalError
      //throw new WasFatalError(curEconName + " " + Thread.currentThread().getName() + " Caught Exception " + ex.toString() + "  cause=" + ex.getCause() + " message=" + ex.getMessage() + andMore());
      flushes();
      System.exit(-27);

    }
    return rende4 - cnt;
  }// end doStartYear

  static volatile int didEndYear = 0; //a count of active endYears

  /**
   * do end of year processing, determine if values need to be divided by power
   * of 10 and then shown in the display of the result process the score
   * processor getWinner() before the rest of doEndYear
   *
   * @return
   */
  int doEndYear() {
    doResSpecial();
    getWinner();
    int cnt = 0, curIx = -7, newIx = -7, ccntl = -8;
    int valid0 = -4, cur0 = -7, yearsGrp = 20, valid = 10;
    int mdepth = 22;
    long depth = 20, ydepth = 10;
    yearErrCnt = 0;
    for (int rN = 0; rN < rende4; rN++) {
      // skip undefined entries without error
      if (resI[rN] != null) {
        cnt++;

        /**
         * resI [resNum][ICUM,ICUR0,...ICUR6(7*6rounds
         * +2][PCNTS,SCNTS,CCONTROLD][LCLANS :{over
         * CCONTROLD}ISSET,IVALID,IPOWER:{only for
         * ICUM,CCONTROLD}LOCKS0,LOCKS1,L0CKS2,LOCKS3,IFRACS,IDEPTH] valid
         * number of valid entries 0=unset,1=cur0,2=cur1,7=cur6
         */
        int statsLen = resI[rN].length; // length of yrsIx vector I think
        int[] spots = {0, 1};  // ICUM,ICUR0 short spots no LISTYRS
        // 8 for LIST10 
        int[] spotsl = {0, 1, 8, 15, 22, 29, 36};// long cur0 index
        int statsLim = statsLen > STATSSHORTLEN ? STATSLONGLIM : STATSSHORTLIM; // pick the right spots
        boolean doYears = statsLen > STATSSHORTLEN;
        boolean didPower = false;
        int maxCumP = -10;

        //YEARS 0-99999   0,   4,   8,    16,    32,   999999}; // + over 31+ group
        //  CUM CUR0  CUR1 CUR2  CUR3  CUR4  CUR5
        //spots  0   1     8    15    22    29    36       43(44)    
        //   LIST012 LIST10 LIST11 LIST12 LIST13 LIST14
        // LIST8-20
        for (int yrsIx = ICUM; yrsIx < statsLim; yrsIx += yrsIx == ICUM ? ICUR0 - ICUM : MAXDEPTH) {
          newIx = (yrsIx + 1) % MAXDEPTH;;
          yearsGrp = yrsIx / MAXDEPTH;
          depth = resI[rN][ICUM][CCONTROLD][(yearsGrp == 0 ? IDEPTH : IYDEPTH)];
          curIx = (yrsIx) % MAXDEPTH; //index in relation to CUR0
          cur0 = yrsIx - curIx;

          // find the max value in cum and cur
          // all entries 
          if (!didPower) {
            double maxCum = -99999999.;
            maxCumP = -10;
            int maxCumixPorS = 0, m;
            // find the largest abs value in aclansIx values in resV
            for (int ixPorS = 0; ixPorS < 2; ixPorS++) {
              for (int clansIx = 0; clansIx < E.LCLANS; clansIx++) {
                prevLine = "rN" + rN + ", statsLim" + statsLim + ", yrsIx" + yrsIx + ", ixPorS" + ixPorS + ", clansIx" + clansIx;
                maxCum = Math.max(maxCum, Math.abs(resV[rN][yrsIx][ixPorS][clansIx]));
              }
            }
            prevLine = "";

            m = maxCumP = -12;
            if (maxCum > E.PZERO) {
              for (m = MAX_PRINT.length - 1, maxCumP = -10; m > -1 && maxCumP < -1; m--) {
                if (MAX_PRINT[m] < maxCum) {
                  maxCumP = m;   // -1,0 for no reduction in  value
                }
              }
              if (maxCumP < 0) {  // -10 bigger than the largest print divisor
                //     System.out.print("in doEndYear:" + resS[rN][0] + ", maxCumP=" + maxCumP + " choose largest = ");
                maxCumP = 0;
              }

            } else {
              maxCumP = 0;
            }
            if (maxCumP > 0) {
              didPower = true;
              if (E.debugStatsOut) {
                System.out.printf("-----EY-----EM.doEndYear %s, m=%d,MAX_PRINT[%1d]=%e, maxCum=%e\n", resS[rN][0], m, maxCumP, MAX_PRINT[maxCumP], maxCum);
              }
            }
          }
          //     if(E.debugStatsOut)System.out.println( "Max_PRINT[" + maxCumP + "]=" + MAX_PRINT[maxCumP] + " maxCum=" + maxCum );
          resI[rN][yrsIx][CCONTROLD][IPOWER] = maxCumP;

          // now compute the valid rows for stats for this age
          long isset1 = resI[rN][yrsIx][CCONTROLD][ISSET];
          int maxyrsIxj = 6;
          valid = 0;
          long maxd = Math.min(depth, maxyrsIxj);
          for (int yrsIxj = 1; yrsIxj <= maxd && yrsIx > 0; yrsIxj++) {
            valid = resI[rN][yrsIx + yrsIxj - 1] != null && (isset1 = resI[rN][yrsIx + yrsIxj - 1][CCONTROLD][ISSET]) > 0 ? yrsIxj : valid;
            if (maxd > 1 && rN < 0) {
              if (E.debugStatsOut) {
                System.out.printf("----EYb----EM.doEndYear %s rN%d, valid%d,isseta%d,issetb%d, issetc%d depth%d, maxd%d, yrsIxj%d,yrsIx%d\n", resS[rN][0], rN, valid, isset1, (yrsIx + yrsIxj < resI[rN].length ? resI[rN][yrsIx + yrsIxj] != null ? resI[rN][yrsIx + yrsIxj][CCONTROLD][ISSET] : -1 : -2), (yrsIx + MAXDEPTH + yrsIxj < resI[rN].length ? resI[rN][yrsIx + MAXDEPTH + yrsIxj] != null ? resI[rN][yrsIx + MAXDEPTH + yrsIxj][CCONTROLD][ISSET] : -1 : -2), depth, maxd, yrsIxj, yrsIx);
              }
            }

          }// end of yrsIxj
          //  int[][][] resclanIx = resI[rN];
          // int[][] resi2 = resii[ICUR0+yrsIx];
          // int[] resi3 = resi2[2];
          // int resValid = resi3[IVALID];
          resI[rN][yrsIx][CCONTROLD][IVALID] = valid;

          if (didEndYear < 3) {
            // if(E.debugStatsOut)System.out.printf("in doEndYear econ rN=%3d, desc=%5s, MAX_PRINT[0]=%6f,%8.2e,%e,%e, (MAX_PRINT[m=%d]= < maxCum=%5f   < MAX_PRINT[m+1=%3d]=\n", rN, resS[rN][0], 729845219.331, 729845219.331, 8729845219.331, 57729845219.331, m, maxCum, m + 1);
          }
        }// end yrsIx

        int rn = rN;
        if ((rN == 96 || rN < 1) && resI[rN] != null && resI[rN][ICUR0] != null) {
          if (E.debugStatsOut) {
            System.out.printf("----EYc------In EM.doEndYear at res %s rN%d, idepth%d, ydepth%d, valid%d, isseta%d, issetb%d\n", resS[rN][0], rN, resI[rN][ICUM][CCONTROLD][IDEPTH], resI[rN][ICUM][CCONTROLD][IYDEPTH], resI[rN][ICUR0][CCONTROLD][IVALID], resI[rN][ICUR0][CCONTROLD][ISSET], resI[rN][ICUR0 + 1] == null ? -2 : resI[rN][ICUR0 + 1][CCONTROLD][ISSET]);
          }
        }// end print if
      }
    }
    didEndYear++;
    return rende4 - cnt; // number of slots left
  }

  /**
   * set a statistic value and possibly a count
   *
   * @param rn the name of this statistic
   * @param pors planet=0 ship=1
   * @param clan clan of the request
   * @param v the value to be set
   * @return v
   */
  double oldsetStat(int rn, int pors, int clan, double v) {
    int age = curEcon.age;
    return oldsetStat(rn, pors, clan, v, 1, age);
  }

  /**
   * set a statistic value and possibly a count
   *
   * @param rn the name of this statistic
   * @param pors planet=0 ship=1
   * @param clan clan of the request
   * @param v the value to be set
   * @param cnt cnt of occurances usually 0 or 1
   * @return v
   */
  double oldsetStat(int rn, int pors, int clan, double v, int cnt) {
    int age = curEcon.age;
    return oldsetStat(rn, pors, clan, v, cnt, age);
  }

  /**
   * set a statistic value and a count
   *
   * @param rn the name of this statistic
   * @param pors planet=0 ship=1
   * @param clan clan of the request
   * @param v the value to be set
   * @param cnt greater than 0 if this set is to be counted
   * @param age years since creation of the Econ for this stat
   * @return v
   */
  int cntStatsPrints = 0;
  static volatile int ste = 0, lstk = 0, a = -5, b = -5, curm = 0;

  //synchronized 
  double oldsetStat(int rn, int pors, int clan, double v, int cnt, int age
  ) {
    try {
      //long resLock[][][] = resI[rn];
      synchronized (syncRes) {
        int le = lStatsWaitList;
        int prevIx = ixStatsWaitList;
        ixStatsWaitList = (++ixStatsWaitList) % lStatsWaitList;
        prevIx = prevIx >= lStatsWaitList ? 0 : prevIx;// I don't know why there was out of bounds sometimes
        int atCnt = 0;
        long nTime = (new Date()).getTime();
        long moreT = nTime - doYearTime;

        //  int sClan = curEcon.clan;
        // int pors = curEcon.pors;
        addlErr = "inSetStat a rn=" + rn + " pors=" + pors + " clan=" + clan + " ";
        String desc = resS[rn][0];
        wasHere = "inSetStat b rn=" + rn + " desc=" + desc + " pors=" + pors + " clan=" + clan + " ";
        addlErr = "";
        a = -5;
        b = -5;
        curm = 0;
        lResI = resI[rn].length;
        if (lResI > STATSSHORTLEN) {
          for (a = 1; a < 6 && b < 0; a++) {
            //AGESTARTS   0,   4,   8,    16,    32,   999999}; 
            //  CUM CUR0  CUR1 CUR2  CUR3  CUR4  CUR5
            //   MAXDEPTH = 7
            if (age < AGESTARTS[a]) { //0, 4, 8, 16, 32, 999999,9999999}; 
              b = a; //ageIx: b1 = 0-3,B2 4-7,B3 8-15,B4 16-31 B5 32-999999
            }
          }
          if (b > 0) {
            curm = ICUR0 + MAXDEPTH * b;//age < 4 makes ageIx=1 curm>0 0 of age group
          }
        } // end if
        // select an object only big enough to work for the code for all sub objects that could be changed

        double resL[][][] = resV[rn];
        int ycnt = cnt > -2 ? cnt : 0;// allow -1

        // calculate the needed changes for ICUM  ICUR0  and possible ICURx
        double[] resVCum = resV[rn][ICUM][pors]; //array object 
        double resVCumC = resVCum[clan]; // value in 
        resVCumC += v; // doesn't change resVCum[clan]
        long[] resICum = resI[rn][ICUM][pors];
        double[] resVCur = resV[rn][ICUR0][pors];
        long[] resICur = resI[rn][ICUR0][pors];
        long[] resICurCC = resI[rn][ICUR0][CCONTROLD];
        long[] resICumCC = resI[rn][ICUM][CCONTROLD];
        /* now set the values in the appropriate age group */
        EM.wasHere4 = "setStat rn=" + rn + " curm=" + curm + " pors=" + pors + " clan=" + clan;
        EM.wasHere4 += " resI len=" + resI[rn].length; // prev value is saved
        EM.wasHere4 += " resI[rn][curm]L=" + resI[rn][curm].length;
        EM.wasHere4 += " +porsL=" + resI[rn][curm][pors].length;       
        double[] resVCurm = resV[rn][curm][pors];//cur addresses
        long[] resICurm = resI[rn][curm][pors];
        long[] resICurmCC = resI[rn][curm][CCONTROLD];
        //wasHere = "inSetStat rn=" + rn + " desc=" + desc + " pors=" + pors + " clan=" + clan + " ";
        //only one thread at a time gets resLock  and can enter this code
        //volatile flag tells execution must not save value in cpu memory only, all cpu's see values
        if (E.debugStatsOut) {
          statsWaitList[prevIx] = "setStat in thread " + Thread.currentThread().getName() + " sinceDoYear " + moreT + " at ";
          StackTraceElement[] prevCalls = new StackTraceElement[le];
          StackTraceElement[] stks = Thread.currentThread().getStackTrace();
          lstk = stks.length - 1;
          for (ste = 1; ste < le && atCnt < 5 && ste < lstk; ste++) {
            if (stks[ste + 1] != null) {
              prevCalls[ste] = stks[ste + 1];
              if (prevCalls[ste].getMethodName() != null
                      && prevCalls[ste].getFileName() != null
                      && prevCalls[ste].getLineNumber() != 0
                      && !prevCalls[ste].getMethodName().contentEquals("setStat")) {
                if (atCnt == 0) {
                  statsWaitList[prevIx] += prevCalls[ste].getMethodName() + " ";
                }
                String pcs = prevCalls[ste].getFileName();
                int pci = prevCalls[ste].getLineNumber();
                statsWaitList[prevIx] += " "
                        + " at "
                        + pcs
                        + "." + pci;
              } // parts !null
            } // !null
            atCnt++;
          }//for
        }//if debugStatsOut

        resVCum[clan] += v;
        resICum[clan] += ycnt;
        resVCur[clan] += v;
        resICur[clan] += ycnt;
        resICurCC[ISSET] += 1;
        resICumCC[ISSET] += 1;
        if (curm > 0) {
          /* now set the values in the appropriate age group */
          resVCurm[clan] += v;
          resICurm[clan] += ycnt;
          resICurmCC[ISSET] += 1;
        }

        statsWaitList[prevIx] = "";
        if (E.debugStatsOut) {
          if (rn > 0) {
            long endSt = (new Date()).getTime();
            long moreTT = endSt - doYearTime;
            int rN = rn;
            int yrsIx = 1;
            int yrsIxj = 1;
            long resICumClan = resI[rn][ICUM][pors][clan];
            long resIcur0Clan = resI[rn][ICUR0][pors][clan];
            double resVcur0Clan = resV[rn][ICUR0][pors][clan];
            EM.wasHere4 = "setStat rn=" + rn + " curm=" + curm + " pors=" + pors + " clan=" + clan;
            EM.wasHere4 += " resI len=" + resI[rn].length; // prev value is saved
            EM.wasHere4 += " resI[rn][curm]L=" + resI[rn][curm].length;
            EM.wasHere4 += " +porsL=" + resI[rn][curm][pors].length;       
            long resICurmClan = resI[rn][curm][pors][clan];
            double resVCurmClan = resV[rn][curm][pors][clan];
            long resIcur0Isset = resI[rn][ICUR0][CCONTROLD][ISSET];
            long resICumIsset = resI[rn][ICUM][CCONTROLD][ISSET];

            long isset1 = (yrsIx - 1 + yrsIxj < resI[rN].length ? resI[rN][yrsIx - 1 + yrsIxj] != null ? resI[rN][yrsIx - 1 + yrsIxj][CCONTROLD][ISSET] : -1 : -2);

            // if (E.debugStatsOut1) {
            if (cntStatsPrints < E.ssMax) {
              cntStatsPrints += 1;
              System.out.println(
                      "EM.setStat " + Econ.nowName + " " + Econ.doEndYearCnt[0] + " since doYear" + year + "=" + moreT + "=>" + moreTT + " " + resS[rN][0] + " rN" + rN + ", valid" + valid + ", " + " resIcum=" + resICumClan + ", age" + age + ", curEcon.age" + curEcon.age + ", pors=" + pors + ", clan=" + clan + ", resIcur0Isset=" + resIcur0Isset + ", resICumIsset=" + resICumIsset + ", resVCur0Clan=" + mf(resVcur0Clan) + ", resVCurmClan=" + mf(resVCurmClan));
              System.out.flush();
            } //ssMax
            //  }

          };
        }
      } // end of lock on res..[rn]
      long[][][] resii = resI[rn];  //for values if using debug
      double[][][] resvv = resV[rn];
      return v;
    } catch (Exception | Error ex) {
      firstStack = secondStack + "";
      ex.printStackTrace(pw);
      secondStack = sw.toString();
      System.out.flush();
      System.err.flush();
      System.err.println(tError = ("Caught " + ex.toString() + ", cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName() + andMore()));
      System.err.println("rn=" + rn + ", desc=" + resS[rn][0]);
      //     ex.printStackTrace(System.err);
      st.setFatalError();
      throw new WasFatalError(tError);
    }
  }

  /**
   * set a maxStatistic value and a count
   *
   * @param rn the name of this statistic
   * @param pors planet=0 ship=1
   * @param clan clan of the request
   * @param v the value to be set
   * @param cnt greater than 0 if this set is to be counted
   * @param age years since creation of the Econ for this stat
   * @return v
   */
  // int cntStatsPrints = 0;
  double oldsetMaxStat(int rn, int pors, int clan, double v, int cnt, int age
  ) {
    try {
      synchronized (syncRes) {
        int le = 10;
        int prevIx = ixStatsWaitList;
        ixStatsWaitList = (++ixStatsWaitList) % lStatsWaitList;
        int atCnt = 0;
        long nTime = (new Date()).getTime();
        long moreT = nTime - doYearTime;
        if (E.debugStatsOut) {
          statsWaitList[prevIx] = "setMaxStat in thread " + Thread.currentThread().getName() + " sinceDoYear " + moreT + " at ";
          StackTraceElement[] prevCalls = new StackTraceElement[le];
          int lstk = Thread.currentThread().getStackTrace().length - 1;
          for (int ste = 1; ste < le && atCnt < 5 && ste < lstk; ste++) {
            prevCalls[ste] = Thread.currentThread().getStackTrace()[ste + 1];
            if (!prevCalls[ste].getMethodName().contentEquals("setMaxStat")) {
              if (atCnt == 0) {
                statsWaitList[prevIx] += prevCalls[ste].getMethodName() + " ";
              }
              statsWaitList[prevIx] += " at " + prevCalls[ste].getFileName() + "." + prevCalls[ste].getLineNumber();
            }
            atCnt++;
          }//for
        }//if out

        //  int sClan = curEcon.clan;
        // int pors = curEcon.pors;
        addlErr = "inSetMaxStat a rn=" + rn + " pors=" + pors + " clan=" + clan + " ";
        String desc = resS[rn][0];
        wasHere = "inSetMaxStat b rn=" + rn + " desc=" + desc + " pors=" + pors + " clan=" + clan + " ";
        addlErr = "";
        int a = -5, b = -5, curm = 0;
        // check if we do agelist
        if (resI[rn].length > STATSSHORTLEN) {
          for (a = 1; a < 6 && b < 0; a++) {
            //AGESTARTS   0,   4,   8,    16,    32,   999999}; 
            //  CUM CUR0  CUR1 CUR2  CUR3  CUR4  CUR5
            //   LIST0-LIST9 LIST10 LIST11 LIST12 LIST13 LIST14 LIST15-LIST20
            //   MAXDEPTH = 7
            if (age < AGESTARTS[a]) {
              b = a;
            }
          }
          curm = ICUR0 + MAXDEPTH * b;
        } // end if
        double resL[][][] = resV[rn];
        int ycnt = cnt > 0 ? cnt : 0;

        double[] resVCum = resV[rn][ICUM][pors]; //array object 
        double resVCumC = resVCum[clan]; // value in 
        resVCumC += v; // won't work
        long[] resICum = resI[rn][ICUM][pors];
        double[] resVCur = resV[rn][ICUR0][pors];
        long[] resICur = resI[rn][ICUR0][pors];
        long[] resICurCC = resI[rn][ICUR0][CCONTROLD];
        long[] resICumCC = resI[rn][ICUM][CCONTROLD];
        /* now set the values in the appropriate age group */
        double[] resVCurm = resV[rn][curm][pors];
        long[] resICurm = resI[rn][curm][pors];
        long[] resICurmCC = resI[rn][curm][CCONTROLD];
        //wasHere = "inSetStat rn=" + rn + " desc=" + desc + " pors=" + pors + " clan=" + clan + " ";

        if (resICumCC[ISSET] < 1) {
          for (int m = 0; m < 2; m++) {
            double aresVC[] = resV[rn][ICUM][m];
            for (int n = 0; n < E.LCLANS; n++) {
              aresVC[n] = -999999999999.;
            }
          }
        }
        resVCum[clan] = v > resVCum[clan] ? v : resVCum[clan];
        resICum[clan] += ycnt;
        if (resICurCC[ISSET] < 1) {
          for (int m = 0; m < 2; m++) {
            double aresVC[] = resV[rn][ICUR0][m];
            for (int n = 0; n < E.LCLANS; n++) {
              aresVC[n] = -999999999999.;
            }
          }
        }
        resVCur[clan] = v > resVCur[clan] ? v : resVCur[clan];
        resICur[clan] += ycnt;
        resICurCC[ISSET] = 1;
        resICumCC[ISSET] = 1;
        if (curm > 0) {
          if (resICurmCC[ISSET] < 1) {
            for (int m = 0; m < 2; m++) {
              double aresVC[] = resV[rn][curm][m];
              for (int n = 0; n < E.LCLANS; n++) {
                aresVC[n] = -999999999999.;
              }
            }
          }
          /* now set the values in the appropriate age group */
          resVCurm[clan] = v > resVCurm[clan] ? v : resVCurm[clan];
          resICurm[clan] += ycnt;
          resICurmCC[ISSET] = 1;
        }

        statsWaitList[prevIx] = "";
        if (E.debugStatsOut) {
          if (rn > 0) {
            long endSt = (new Date()).getTime();
            long moreTT = endSt - doYearTime;
            int rN = rn;
            int jj = 1;
            int jjj = 1;
            long resICumClan = resI[rn][ICUM][pors][clan];
            long resIcur0Clan = resI[rn][ICUR0][pors][clan];
            double resVcur0Clan = resV[rn][ICUR0][pors][clan];
            long resICurmClan = resI[rn][curm][pors][clan];
            double resVCurmClan = resV[rn][curm][pors][clan];
            long resIcur0Isset = resI[rn][ICUR0][CCONTROLD][ISSET];
            long resICumIsset = resI[rn][ICUM][CCONTROLD][ISSET];

            long isset1 = (jj - 1 + jjj < resI[rN].length ? resI[rN][jj - 1 + jjj] != null ? resI[rN][jj - 1 + jjj][CCONTROLD][ISSET] : -1 : -2);

            if (E.debugStatsOut1) {
              if (cntStatsPrints < E.ssMax) {
                cntStatsPrints += 1;
                System.out.println(
                        "EM.setStat " + Econ.nowName + " " + Econ.doEndYearCnt[0] + " since doYear" + year + "=" + moreT + "=>" + moreTT + " " + resS[rN][0] + " rN" + rN + ", valid" + valid + ", " + " resIcum=" + resICumClan + ", age" + age + ", curEcon.age" + curEcon.age + ", pors=" + pors + ", clan=" + clan + ", resIcur0Isset=" + resIcur0Isset + ", resICumIsset=" + resICumIsset + ", resVCur0Clan=" + mf(resVcur0Clan) + ", resVCurmClan=" + mf(resVCurmClan));
                System.out.flush();
              } //ssMax
            }

          };
        }
      } // end of lock on res..[rn]
      long[][][] resii = resI[rn];  //for values if using debug
      double[][][] resvv = resV[rn];
      return v;
    } catch (Exception | Error ex) {
      firstStack = secondStack + "";
      ex.printStackTrace(pw);
      secondStack = sw.toString();
      System.out.flush();
      System.err.flush();
      System.err.println(tError = ("Caught " + ex.toString() + ", cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName() + andMore()));
      System.err.println("rn=" + rn + ", desc=" + resS[rn][0]);
      //     ex.printStackTrace(System.err);
      st.setFatalError();
      throw new WasFatalError(tError);
    }
  }

  /**
   * set a min Statistic value and a count
   *
   * @param rn the name of this statistic
   * @param pors planet=0 ship=1
   * @param clan clan of the request
   * @param v the value to be set
   * @param cnt greater than 0 if this set is to be counted
   * @param age years since creation of the Econ for this stat
   * @return v
   */
  // int cntStatsPrints = 0;
  double oldsetMinStat(int rn, int pors, int clan, double v, int cnt, int age
  ) {
    try {
      synchronized (syncRes) {
        int le = 10;
        int prevIx = ixStatsWaitList;
        ixStatsWaitList = (++ixStatsWaitList) % lStatsWaitList;
        int atCnt = 0;
        long nTime = (new Date()).getTime();
        long moreT = nTime - doYearTime;
        if (E.debugStatsOut) {
          statsWaitList[prevIx] = "setMinStat in thread " + Thread.currentThread().getName() + " sinceDoYear " + moreT + " at ";
          StackTraceElement[] prevCalls = new StackTraceElement[le];
          int lstk = Thread.currentThread().getStackTrace().length - 1;
          for (int ste = 1; ste < le && atCnt < 5 && ste < lstk; ste++) {
            prevCalls[ste] = Thread.currentThread().getStackTrace()[ste + 1];
            if (!prevCalls[ste].getMethodName().contentEquals("setMinStat")) {
              if (atCnt == 0) {
                statsWaitList[prevIx] += prevCalls[ste].getMethodName() + " ";
              }
              statsWaitList[prevIx] += " at " + prevCalls[ste].getFileName() + "." + prevCalls[ste].getLineNumber();
            }
            atCnt++;
          }//for
        }//if out

        //  int sClan = curEcon.clan;
        // int pors = curEcon.pors;
        addlErr = "inSetMinStat a rn=" + rn + " pors=" + pors + " clan=" + clan + " ";
        String desc = resS[rn][0];
        wasHere = "inSetMinStat b rn=" + rn + " desc=" + desc + " pors=" + pors + " clan=" + clan + " ";
        addlErr = "";
        int a = -5, b = -5, curm = 0;
        // check if we do agelist
        if (resI[rn].length > STATSSHORTLEN) {
          for (a = 1; a < 6 && b < 0; a++) {
            //AGESTARTS   0,   4,   8,    16,    32,   999999}; 
            //  CUM CUR0  CUR1 CUR2  CUR3  CUR4  CUR5
            //   LIST0-LIST9 LIST10 LIST11 LIST12 LIST13 LIST14 LIST15-LIST20
            //   MAXDEPTH = 7
            if (age < AGESTARTS[a]) {
              b = a;
            }
          }
          curm = ICUR0 + MAXDEPTH * b;
        } // end if
        double resL[][][] = resV[rn];
        int ycnt = cnt > 0 ? cnt : 0;

        double[] resVCum = resV[rn][ICUM][pors]; //array object 
        double resVCumC = resVCum[clan]; // value in 
        resVCumC += v; // won't work
        long[] resICum = resI[rn][ICUM][pors];
        double[] resVCur = resV[rn][ICUR0][pors];
        long[] resICur = resI[rn][ICUR0][pors];
        long[] resICurCC = resI[rn][ICUR0][CCONTROLD];
        long[] resICumCC = resI[rn][ICUM][CCONTROLD];
        /* now set the values in the appropriate age group */
        double[] resVCurm = resV[rn][curm][pors];
        long[] resICurm = resI[rn][curm][pors];
        long[] resICurmCC = resI[rn][curm][CCONTROLD];
        //wasHere = "inSetStat rn=" + rn + " desc=" + desc + " pors=" + pors + " clan=" + clan + " ";
        if (resICumCC[ISSET] < 1) {
          for (int m = 0; m < 2; m++) {
            double aresVC[] = resV[rn][ICUM][m];
            for (int n = 0; n < E.LCLANS; n++) {
              aresVC[n] = 999999999999999999999999.;
            }
          }
        }
        resVCum[clan] = v < resVCum[clan] ? v : resVCum[clan];
        resICum[clan] += ycnt;
        if (resICurCC[ISSET] < 1) {
          for (int m = 0; m < 2; m++) {
            double aresVC[] = resV[rn][ICUR0][m];
            for (int n = 0; n < E.LCLANS; n++) {
              aresVC[n] = 999999999999999999999999.;
            }
          }
        }
        resVCur[clan] = v < resVCur[clan] ? v : resVCur[clan];
        resICur[clan] += ycnt;
        resICurCC[ISSET] = 1;
        resICumCC[ISSET] = 1;
        if (curm > 0) {
          /* now set the values in the appropriate age group */
          if (resICurmCC[ISSET] < 1) {
            for (int m = 0; m < 2; m++) {
              double aresVC[] = resV[rn][curm][m];
              for (int n = 0; n < E.LCLANS; n++) {
                aresVC[n] = 999999999999999999999999.;
              }
            }
          }
          resVCurm[clan] = v < resVCurm[clan] || resICurmCC[ISSET] < 1 ? v : resVCurm[clan];
          resICurm[clan] += ycnt;
          resICurmCC[ISSET] = 1;
        }

        statsWaitList[prevIx] = "";
        if (E.debugStatsOut) {
          if (rn > 0) {
            long endSt = (new Date()).getTime();
            long moreTT = endSt - doYearTime;
            int rN = rn;
            int jj = 1;
            int jjj = 1;
            long resICumClan = resI[rn][ICUM][pors][clan];
            long resIcur0Clan = resI[rn][ICUR0][pors][clan];
            double resVcur0Clan = resV[rn][ICUR0][pors][clan];
            long resICurmClan = resI[rn][curm][pors][clan];
            double resVCurmClan = resV[rn][curm][pors][clan];
            long resIcur0Isset = resI[rn][ICUR0][CCONTROLD][ISSET];
            long resICumIsset = resI[rn][ICUM][CCONTROLD][ISSET];

            long isset1 = (jj - 1 + jjj < resI[rN].length ? resI[rN][jj - 1 + jjj] != null ? resI[rN][jj - 1 + jjj][CCONTROLD][ISSET] : -1 : -2);

            if (E.debugStatsOut1) {
              if (cntStatsPrints < E.ssMax) {
                cntStatsPrints += 1;
                System.out.println(
                        "EM.setStat " + Econ.nowName + " " + Econ.doEndYearCnt[0] + " since doYear" + year + "=" + moreT + "=>" + moreTT + " " + resS[rN][0] + " rN" + rN + ", valid" + valid + ", " + " resIcum=" + resICumClan + ", age" + age + ", curEcon.age" + curEcon.age + ", pors=" + pors + ", clan=" + clan + ", resIcur0Isset=" + resIcur0Isset + ", resICumIsset=" + resICumIsset + ", resVCur0Clan=" + mf(resVcur0Clan) + ", resVCurmClan=" + mf(resVCurmClan));
                System.out.flush();
              } //ssMax
            }

          };
        }
      } // end of lock on res..[rn]
      long[][][] resii = resI[rn];  //for values if using debug
      double[][][] resvv = resV[rn];
      return v;
    } catch (Exception | Error ex) {
      firstStack = secondStack + "";
      ex.printStackTrace(pw);
      secondStack = sw.toString();
      System.out.flush();
      System.err.flush();
      System.err.println(tError = ("Caught " + ex.toString() + ", cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName() + andMore()));
      System.err.println("rn=" + rn + ", desc=" + resS[rn][0]);
      //     ex.printStackTrace(System.err);
      st.setFatalError();
      throw new WasFatalError(tError);
    }
  }

  /**
   * set a statistic value and possibly a count
   *
   * @param rn the name of this statistic
   * @param v the value to be set
   * @param cnt greater than 0 if this set is to be counted
   * @return v
   */
  double oldsetStat(int rn, double v, int cnt) {
    int age = curEcon.age;
    int clan = curEcon.clan;
    int pors = curEcon.pors;
    return oldsetStat(rn, pors, clan, v, cnt, age);
  }

  static int putRowsPrint1Count = 0;
  static int putRowsPrint2Count = 0;
  static int putRowsPrint3Count = 0;
  static int putRowsPrint4Count = 0;
  static int putRowsPrint5Count = 0;
  static int putRowsPrint6Count = 0;
  static int putRowsPrint6aCount = 0;
  static int putRowsPrint7Count = 0;
  static int putRowsPrint8Count = 0;
  static int putRowsPrint9Count = 0;
  static int prpc1 = 0;
  static int prpc2 = 0;
  static int prpc3 = 0;
  private long[][][] curRes;
  private long[][][] res3;
  //  values for doRes

  private boolean doUnitsDivide = false;  // units ave
  private boolean doValidDivide = false; // divide sum by valid years  ave
  private boolean divBy = false;

  private static String nextCol = "";
  private static boolean ctlFnd = false;
  private static boolean inNum = false;
  private static int colCnt = 0, colMax = 10, lTit = 41, lCol = 15;
  private static int lMax = 0, lCnt = 0;
  private static final int colAdd = 0;
  private static final int colAddBrk = 1;
  private static final int colBrkAdd = 2;
  private static final int colHlfAddBrk = 3;
  private static final int colHlfBrkAdd = 4;
  private static final int colBrkEnd = 5;

  /**
   * possibly put a row into table if the key aop matches a lock in rn Called
   * from StarTrader.listRes(page,resloops,fullres) listres adds the set of keys
   * for a given page from resloops and the keys one by one for each of 4
   * possible rows. putrows loops through each doRes (results) entry calling
   * putRows2
   * <p>
   * putRows2 is called to do most of the work, it loops through all 4 locks,
   * for each lock it loops through the possible agelist, if there is a set of
   * ages, it then loops through the years in the age block. The individual
   * locks in the doRes are matched against the keys from StarTrader.listRes,
   * check first for a page match, then a rows match then command matches. The 9
   * possible commands that match are obeyed in order to create a line in the
   * table.
   *
   * @param table table in Stats
   * @param resExt this is the detail and (tip text) accessed by the title in
   * the table
   * @param row next row in the display table
   * @param aop the key to fit locks in resI, describes which page(LIST..) and
   * types of output, rows
   * @return next row
   */
  public int putRows(JTable table, String[] resExt, int row, long aop) {
    if (putRowsPrint1Count++ < 10) {
      if (E.debugPutRowsOut) {
        System.out.println(">>>>>>putRows1 rende4=" + rende4 + "," + rendae4 + ", count=" + putRowsPrint1Count + "<<<<<<");
      }

    }
    int rn = 0;
    String prevDesc = "none";
    long depth = -2, listMatch = 0, prevListMatch = 0, hLMp = 0, myValid = 0;
    for (rn = 0; rn < rende4; rn++) { // traverse all doRes entries
      if (putRowsPrint2Count++ < 10) {
        if (E.debugPutRowsOut) {
          System.out.printf(">>>>>>putRows2 count=" + putRowsPrint2Count + " rn=" + rn + " row=" + row + "aop=%o  <<<<<<\n", aop);
        }
      }
      if (resI[rn] == null) {
        System.out.println("null resI[" + rn + "] prev desc=" + prevDesc);
      } else { // not null
        prevDesc = resS[rn][0];
        boolean myUnset;
        long[][][] resii = resI[rn];
        long[][] resiii = resI[rn][ICUM];
        long[] resiic = resI[rn][ICUM][CCONTROLD];
        int c = 0, ageIx = 0;
        // duplication is also in doRes with short versions doing duplication of previous values
        if (res3 != null) { //use prev locks by default
          curRes = res3;
          res3 = null;
        }
        // check for SKIPDUP in locks0, save curRes in res3 don't use dup, use this rn
        if ((resI[rn][ICUM][CCONTROLD][LOCKS0 + 0] & SKIPDUP) > 0L) {
          res3 = curRes;  // save prev value for use above
          curRes = resI[rn];
        } else // use the previous locks at curRes if DUP is in the first lock this rn
        // there can be a long string of DUP, always use the last no DUP
        if ((resI[rn][ICUM][CCONTROLD][LOCKS0 + 0] & DUP) == 0L) { // if no DUP
          curRes = resI[rn];  //save this rn locks as current if no dup, then use previous locks
        }

        // now see if this rn has an acceptable lock,
        //find the index into the lowest age group of all the locks
        long allLocks = resiic[LOCKS0] | resiic[LOCKS1] | resiic[LOCKS2] | resiic[LOCKS3];
        // pick ages  again in putRows2, this ageIx is not used
        int maxc = resI[rn].length <= STATSSHORTLEN ? shortLength : longLength;
        for (c = 3, ageIx = 0; c < maxc && ageIx == 0; c++) {
          if (((aop & allLocks) & AGELISTS[c]) > 0) { // pick the lfirst age
            ageIx = c - shortLength + 1; // LIST10 = 1
          }
        }
        // short look only at ICUR0 unset, long look at ICUR0 for the first 3 than at the rest
        // so change ageIx to do the above
        ageIx = resI[rn].length <= STATSSHORTLEN ? 0 : ageIx <= shortLength ? 0 : Math.min(ageIx - shortLength + 1, MAXAGES);
        myAgeIx = resI[rn].length < STATSSHORTLEN ? 0 : ageIx <= shortLength ? 0 : 1;
        //       myUnset = unset = resI[rn][ICUR0 + ageIx * MAXDEPTH][CCONTROLD][ISSET] < 1; // flag for age
        //       myValid = valid = resI[rn][ICUR0 + ageIx * MAXDEPTH][CCONTROLD][IVALID];
        //     depth = resI[rn][ICUR0 + ageIx * MAXDEPTH][CCONTROLD][IVALID];
        // set lla true to do a print below
        boolean lla = (rn > (rende4 - 2) ? true
                : (rn == RCGLT10PERCENT) ? true
                        : (rn == RCTGROWTHPERCENT) ? true
                                : ((aop & (LIST14)) > 0L)
                                        ? true : false);
        if (E.debugPutRows6aOut) {
          if ((((aop & (LIST14)) > 0L) || ((putRowsPrint6aCount % 75) == 0)) && (putRowsPrint6aCount++ < 100)) {
            System.out.flush();
            System.out.printf("EM.putrow6aa rn=%d ageIx%d %s, %s, aop%o, list%d, depth%d, valid%d, cum%d, rende4=%d,%d putRowsPrint6aCount= " + putRowsPrint6aCount + " \n", rn, myAgeIx, (unset ? "UNSET" : "ISSET") + " = " + resI[myRn][ICUM][CCONTROLD][ISSET] + ":" + resI[myRn][ICUR0 + myAgeIx * MAXDEPTH][CCONTROLD][ISSET], resS[rn][0], aop, ((aop & LIST14) > 0 ? 14 : (aop & LIST1) > 0 ? 1 : (aop & LIST3) > 0 ? 3 : (aop & LIST8) > 0 ? 8 : aop), depth, valid, resI[rn][ICUM][0][0], rende4, rendae4);
          }
        }
        row = putRows2(table, resExt, rn, row, aop, allLocks, ageIx);
      } // end not null
    }
    return row;
  }

  private long haveOpsMatch;
  private long haveAllOpsMatch;
  private long haveListsMatch;
  private long haveRows;  //StarTrader rows key
  private long haveRowsMatch;
  private long haveCmdMatch;
  private long haveAgesMatch;
  private long listMatch = 0, prevListMatch = 0, hLMp = 0, myValid = 0;
  private boolean isCur;
  private boolean isCurAve;
  private boolean isCurUnits, isACur;
  private boolean isThisYear, isAThisYear;
  private boolean isThisYearAve;
  private boolean isThisYearUnits;
  private boolean isCum, isACum;
  private boolean isCumAve;
  private boolean isCumUnits;
  private boolean isAve;
  private boolean isUnits;
  private boolean doSum = false;
  private boolean didSum = false; //a previous lock offered sum, so without both do sum
  private boolean doBoth = false;
  private long doPower = -5;
  private String powers = "";  // the power append to name and desc string
  private boolean doSkipUnset = false;
  private boolean doZeroUnset = false;
  private boolean didUnset = false;
  private boolean doCum = false;
  private boolean doValues = false;
  private boolean doUnits = false;
  private boolean isVal, isAge0, isAgeMore, isAges, isAgeCmd, isAgeLength;
  private static boolean tstr = false;   // a tstring line
  private static long lStart = 0L;
  private static long lEnd = 1L;  // o-1
  private static int startAgeYearsValues = 0;
  private static int endAgeYearsValues = 1;
  private static int ageYearsIx = -3;
  private static long cmd = 0L;
  // these are only in one process so they can be static
  private static int myRow = 0, myLock = 0, myAgeIx = 0, myPors = 0;
  private static int lockIx = -2, ageIx = -2, yearsIx = -2, nineIx = -5;
  private static String suffix = "";
  private static String extSuffix = "";
  private boolean isAgeList = false, myIsSet = false, isAAge0;
  private static final long NEVER = 0000000L; //  unused
  private static final long[] NINECMDS = {CUR, CURUNITS, CURAVE, THISYEAR, THISYEARUNITS, THISYEARAVE, CUM, CUMUNITS, CUMAVE};
  private static final long[] NINENOTS = {NEVER, NEVER, NEVER, CUR, CURUNITS, CURAVE, NEVER, NEVER, NEVER};
  private static final long[] NINEAGECMDS = {CUR, CURUNITS, CURAVE, NEVER, NEVER, NEVER, NEVER, NEVER, NEVER};
  private static final String[] NINESTRS = {"CUR", "CURUNITS", "CURAVE", "THISYEAR", "THISYEARUNITS", "THISYEARAVE", "CUM", "CUMUNITS", "CUMAVE"};
  private static final String[] ninesSuffix = {" cur$", " curU", " curAv$", " thisYr", " thisYrU", " thisYrAv", " cum", " cumU", " cumAv"};
  private static final String[] ninesExtSuffix = {" current years sums", " current year units", " currrent year ave", " this  year sum", " this year units", " this year ave", " cumulative sum", " cumulative units", " cumulative ave"};
  private static final int[] CURAVEAgesYrs = {MAXDEPTH, 4, 4, 6, 6, 6}; //yrs to scan for ageIx

  private String getOpsNames(Long ops) {
    String rtn = "";
    rtn += ((ops & CUR) > 0 ? " cur" : "");
    rtn += ((ops & CURAVE) > 0 ? " CURAVE" : "");
    rtn += ((ops & CURUNITS) > 0 ? " CURUNITS" : "");
    rtn += ((ops & CUM) > 0 ? " cum" : "");
    rtn += ((ops & CUMAVE) > 0 ? " CUMAVE" : "");
    rtn += ((ops & CUMUNITS) > 0 ? " CUMUNITS" : "");
    rtn += ((ops & THISYEAR) > 0 ? " thisYear" : "");
    rtn += ((ops & THISYEARUNITS) > 0 ? " thisYearUnits" : "");
    rtn += ((ops & THISYEARAVE) > 0 ? " thisYearAve" : "");
    rtn += (isAge0 ? " age0" : "");
    rtn += (isAAge0 ? " aage0" : "");
    rtn += (isAgeMore ? " ageMore" : "");
    rtn += (isAgeCmd ? " ageCmd" : "");
    rtn += (isAges ? " ages" : "");
    return rtn;
  }

  //long haveRows = 0L;
  /**
   * test whether to output this special line of output
   *
   * @param myCmd the cmd being proessed
   * @return true if a print done
   */
  private boolean ifPutRow6(String myCmd) {
    // if ((resS[myRn][rDesc].contentEquals("S Worth")) && ((haveListsMatch & LIST6) > 0)) {
    if (((haveListsMatch & LIST6) > 0)) {
      if (putRowsPrint6Count < 20
              || (((putRowsPrint6Count % 25) == 0)) && (putRowsPrint6Count < 200)) {
        System.out.flush();
        System.out.printf("EM.putrow6 " + myCmd + "  rn=" + myRn + ", " + resS[myRn][0] + ", row=" + myRow
                + ", aop=%o, isset=" + (myUnset ? myCumUnset ? "CumUnset" : "unset" : "isSet") + " = " + resI[myRn][ICUM][CCONTROLD][ISSET] + ":" + resI[myRn][ICUR0 + myAgeIx * MAXDEPTH][CCONTROLD][ISSET]
                + ", lock#%d, nine%d, age%d, years%d" + ", list" + ((haveListsMatch & LIST0) > 0 ? "0," : (haveListsMatch & LIST1) > 0 ? "1," : (haveListsMatch & LIST2) > 0 ? "2," : (haveListsMatch & LIST6) > 0 ? "6," : (haveListsMatch & LIST10) > 0 ? "10," : (haveListsMatch & LIST11) > 0 ? "11," : (haveListsMatch & LIST12) > 0 ? "12," : (haveListsMatch & LIST13) > 0 ? "13," : (haveListsMatch & LIST14) > 0 ? "14," : "??,")
                + ", CURAVEAgesYears=" + CURAVEAgesYrs[ageIx]
                + ", ops=" + getOpsNames(haveCmdMatch)
                + ", suffix=" + suffix
                + ", depth%d, valid%d"
                + ", cum00=" + resI[myRn][ICUM][0][0]
                + ", rende4=" + rende4 + "," + rendae4 + " putRowsPrint6Count=" + putRowsPrint6Count + " \n", myAop, lockIx, nineIx, ageIx, yearsIx, depth, valid);
        return true;
      }
    }
    return false;
  }

  /**
   * possibly put a row into table if the key aop matches a lock in results for
   * this rn
   *
   * @param table table in Stats
   * @param resExt this is the detail and (tip text)
   * @param rn defined number of the stat called in order
   * @param row next row in the display table
   * @param aop the key to fit the locks in resI
   * @param allLocks the or of all the locks
   * @param ageIx the age category of the item must be recalculated
   * @return next row
   */
  private int putRows2(JTable table, String[] resExt, int rn, int row, long aop, long allLocks, int ageIx3) {
    myRn = rn;
    myRow = row;
    myAop = aop;
    myAgeIx = ageIx;
    if (E.debugPutRowsOut) {
      if (putRowsPrint3Count++ < 12) {
        System.out.println(">>>>>>putRows3 count=" + putRowsPrint3Count + " rn=" + rn + " row=" + row + ", rende4=" + rende4 + "," + rendae4 + " <<<<<<");
      }
    }
    if (resV[rn] == null) { // skip undefined rows
      return row;
    }

    try {
      int tend = table.getSize().height;
      long opr = 0;
      //int opx = 0;
      long[][][] resii = curRes;
      long[][] resiii = curRes[ICUM];
      long[] resiic = curRes[ICUM][CCONTROLD];

      haveAllOpsMatch = (aop & allLocks) & CMDSONLYMASK;
      haveListsMatch = (aop & allLocks) & LMASK;
      if (E.debugPutRowsOut) {
        if (putRowsPrint4Count++ < 12) {
          System.out.println(">>>>>>putRows4 count=" + putRowsPrint4Count + " haveOpsMatch=" + haveOpsMatch + " haveListsMatch=" + haveListsMatch + " rn=" + rn + " row=" + row + " <<<<<<");
        }
      }
      //skip rows with not LISTx and no CMDS
      if (haveAllOpsMatch == 0L || haveListsMatch == 0L) { // check if EM rn missing list or do (command)
        return row;
      }

      didSum = false; // initialize didSum, remember sum across locks
      // process each LOCKS0-3 thru each command and list not zero
      // use previous hlMp (LISTs) if this lock has no list
      if (E.debugPutRowsOut) {
        if (putRowsPrint5Count++ < 12) {
          System.out.printf(">>>>>>putRows5 count=" + putRowsPrint5Count + " haveOpsMatch=%o, haveListsMatch=%o," + " rn=" + rn + " <<<<<<\n", haveOpsMatch, haveListsMatch);
        }
      }
      int rowAtStart = row;
      isAgeLength = (resI[rn].length > STATSSHORTLEN);
      // printing done now move through the 4 locks
      for (lockIx = 0; lockIx < 4; lockIx++) {
        myLock = lockIx;
        // check for a LISTx match put in prevListMatch
        //  prevListMatch = (long) (aop & curRes[ICUM][CCONTROLD][LOCKS0 + lockIx]) & lmask;
        //look for ROWS1 ROWS2 ROWS3
        haveRows = aop & ROWSMASK;  //StarTrader rows key
        // any ROWSx in this lock
        haveRowsMatch = curRes[ICUM][CCONTROLD][LOCKS0 + lockIx] & ROWSMASK;
        // match either no ROWS or some ROWS
        boolean okRows = (haveRows == 0L && haveRowsMatch == 0L) || ((haveRows & haveRowsMatch) > 0L);
        //use current  LISTx match if present else use previous listMatch putRows2
        listMatch = curRes[ICUM][CCONTROLD][LOCKS0 + lockIx] & aop & LMASK;
        listMatch = (listMatch == 0L ? prevListMatch : listMatch);
        prevListMatch = 0L; // one use only
        isAgeList = (aop & AGESMASK) > 0L; // The LISTx that do againg
        // is there a command match
        haveCmdMatch = (aop & curRes[ICUM][CCONTROLD][LOCKS0 + lockIx]) & CMDSMASK;
        cmd = opr = haveCmdMatch;  //commands in this lock

        // must have at least 1 matching list and 1 matching do type and okRows
        if ((listMatch > 0L) && (haveCmdMatch > 0L) && okRows) {
          prevListMatch = listMatch; // save a good list option for possible use in the next lockIx

          // aop from StarTrader should containt no more than 1 age list
          int maxList = isAgeLength ? shortLength : longLength;

          // now go thru the 9 possible commands in NINECMDS putRows2
          for (nineIx = 0; nineIx < 9; nineIx++) {
            isCur = nineIx == 0; //(cmd & CUR) > 0;
            isCurAve = nineIx == 2; //(cmd & CURAVE) > 0;
            isCurUnits = nineIx == 1; //(cmd & CURUNITS) > 0;
            isCum = nineIx == 6; //(cmd & CUM) > 0;
            isCumAve = nineIx == 8; //(cmd & CUMAVE) > 0;
            isCumUnits = nineIx == 7; //(cmd & CUMUNITS) > 0;
            isThisYear = nineIx == 3; //(cmd & THISYEAR) > 0;
            isThisYearAve = nineIx == 5; //(cmd & THISYEARAVE) > 0;
            isThisYearUnits = nineIx == 4; //(cmd & THISYEARUNITS) > 0;
            isUnits = isCurUnits | isCumUnits | isThisYearUnits;
            isAve = isCurAve | isCumAve | isThisYearAve;
            isVal = isCur | isCum | isThisYear;
            isACur = isCur | isCurUnits | isCurAve;
            isACum = isCum | isCumUnits | isCumAve;
            isAThisYear = isThisYear | isThisYearUnits | isThisYearAve;
            //dont THISYEAR if a corresponding CUR exists
            if (((NINECMDS[nineIx] & haveCmdMatch) > 0L) && !((NINENOTS[nineIx] & haveCmdMatch) > 0L)) {
              isAgeCmd = isACur;// cur curu curave
              isAges = (isAgeLength && isACur && isAgeList);
              int ageLim = isAges ? MAXAGES : 1;
              //        String sstring = (isVal?" values " : isUnits? " units " : isAve ? " ave " : "???");
              // loop through possible sets of ages, most commands have only 1 age
              for (ageIx = 0; ageIx < ageLim; ageIx++) { //
                myAgeIx = ageIx;
                cmd = opr = haveCmdMatch;  //commands in this lock
                isAAge0 = (ageIx == 0) && isAges;
                isAge0 = (ageIx == 0);
                isAgeMore = ageIx > 0 && isAges;
                int unsetCnt = 0;
                int yrsMax = MAXDEPTH; //CURAVEAgesYrs[ageIx];
                for (int ageYrsIx = 0; ageYrsIx < yrsMax; ageYrsIx++) {
                  prevLine = "rN" + myRn + " " + resS[myRn][0] + ", length" + resI[rn].length + ", nineIx" + nineIx + ", extSuffix=" + extSuffix + (isAges ? ", isAges " : " notAges ") + ", ageIx" + ageIx + ", ageYrsIx" + ageYrsIx + ", extSuffix=" + ninesExtSuffix[nineIx];
                  if (resI[rn].length < ICUR0 + ageIx * MAXDEPTH + ageYrsIx) {
                    if (E.debugPutRows2) {
                      doMyErr("Null in putRows2 rn=" + rn + ", desc=" + resS[rn][0] + (isAges ? ", isAges " : ", not Ages ") + " ageIx" + ageIx + ", ageYrsIx" + ageYrsIx + ", len" + ICUR0 + ageIx * MAXDEPTH + ageYrsIx + ":" + resI[rn].length);
                    }
                  } else if (resI[rn][ICUR0 + ageIx * MAXDEPTH + ageYrsIx] == null) {
                    ageYrsIx = MAXDEPTH + 2; // stop the loop
                    // OR do nothing, but not an error
                  } else {
                    if (resI[rn][ICUR0 + ageIx * MAXDEPTH + ageYrsIx].length > 0) {
                      unsetCnt += resI[rn][ICUR0 + ageIx * MAXDEPTH + ageYrsIx][CCONTROLD][ISSET];
                      // go up the ageYrsIx even if not set, only set valid if year isset
                      valid = resI[rn][ICUR0 + ageIx * MAXDEPTH + ageYrsIx][CCONTROLD][ISSET] > 0 ? ageYrsIx + 1 : valid;
                      resI[rn][ICUR0 + ageIx * MAXDEPTH][CCONTROLD][IVALID] = valid;

                      if (E.debugPutRows6abOut) {
                        if (isAgeList || ((putRowsPrint6aCount++ < 400) && (putRowsPrint6aCount % 25) == 0)) {
                          System.out.flush();
                          System.out.printf("EM.putrow6ab rn=%d %s,lockIx%d ageIx%d ageLim%d, nineIx%d ageYrsIx%d yrsMax%d, depth%d, valid%d,%s, %s, aop%o, opr%o, cmd%o, list%d,cum%d, rende4=%d,%d putRowsPrint6aCount= " + putRowsPrint6aCount + " \n", rn, resS[rn][0], lockIx, myAgeIx, ageLim, nineIx, ageYrsIx, yrsMax, depth, valid, extSuffix, (unset ? "UNSET" : "ISSET") + " = " + resI[myRn][ICUM][CCONTROLD][ISSET] + ":" + resI[myRn][ICUR0 + myAgeIx * MAXDEPTH][CCONTROLD][ISSET], aop, opr, cmd, ((aop & LIST14) > 0 ? 14 : (aop & LIST1) > 0 ? 1 : (aop & LIST3) > 0 ? 3 : (aop & LIST8) > 0 ? 8 : aop), resI[rn][ICUM][0][0], rende4, rendae4);
                        }
                      }
                    }
                  }
                }  // for
                myUnset = unset = unsetCnt < 1; // flag for age
                myCumUnset = myUnset && resI[rn][ICUM][CCONTROLD][ISSET] < 1;
                myUnset = unset = isACum ? myCumUnset | unset : unset;
                myValid = valid = resI[rn][ICUR0 + ageIx * MAXDEPTH][CCONTROLD][IVALID];
                depth = resI[rn][ICUM][CCONTROLD][IDEPTH];

                doUnits = isThisYearUnits || isCurUnits || isCumUnits;
                // set didSum if ever doSum and not both
                didSum |= doSum = (opr & sum) > 0;
                doBoth = (opr & both) > 0 || !didSum;  // default both if neither, but sum overrides both
                didSum &= !doBoth;  // doBoth clears didSum
                doSkipUnset = (haveAllOpsMatch & skipUnset) > 0;
                doZeroUnset = (haveAllOpsMatch & zeroUnset) > 0;
                //           tstr = (opr & tstring) > 0;
                didUnset = false;
                prevLine = "";
                if (E.debugPutRowsOut6) {
                  if (ifPutRow6("start")) {
                    putRowsPrint6Count++;
                  }
                } // if debug

                //String[] agesStr = {"", "0-3", "4-7", "8-15", "16-31", "32+"};
                boolean isYears = isACur;
                int yearsMax = isAge0 && isACur ? (int) Math.min(depth, valid) : 1;
                for (yearsIx = 0; yearsIx < yearsMax; yearsIx++) {
                  boolean isYear0 = yearsIx == 0;
                  boolean isMoreYears = yearsMax > 1;
                  boolean scanMoreYears = isCurAve && !isMoreYears;
                  startAgeYearsValues = yearsIx;// for curave values in getRowEntryValues
                  //scan and average if not displaying year by year values
                  endAgeYearsValues = myMin(MAXDEPTH, (yearsIx + (scanMoreYears ? CURAVEAgesYrs[ageIx] : 1)));
                  suffix = ninesSuffix[nineIx];
                  extSuffix = ninesExtSuffix[nineIx];
                  // treat isMoreYears later
                  suffix += ((isYear0 && isAAge0 && !isMoreYears) ? (scanMoreYears ? " scan: " : " all: ") : "");
                  extSuffix += ((isYear0 && isAAge0 && !isMoreYears) ? (scanMoreYears ? " scanAges: " : " allAges: ") : "");
                  suffix += isAgeMore ? " age" + AGESTR[ageIx] : "";
                  extSuffix += isAgeMore ? " age" + AGESTR[ageIx] : "";
                  suffix += isMoreYears ? (scanMoreYears ? " scanAges: " : " all: ") + (yearsMax - yearsIx) + "/" + yearsMax : "";
                  extSuffix += isMoreYears ? (scanMoreYears ? " scanAllAges " : " all ") + " latest year first: " + (yearsMax - yearsIx) + "/" + yearsMax : "";
                  //  suffix = (isVal ? isAAge0 ? sstring + " allAges" + thisOrCur + ":" + (yearsIx + 1) + "/" + valid
                  //    : isAgeMore ? sstring + " ages" + agesStr[ageIx]
                  //           : isACur ? sstring + thisOrCur + ":" + (yearsIx + 1) + "/" + valid
                  //                  : sstring
                  //  : isAve ? isAAge0 ? sstring + " allAges" + thisOrCur + ":" + (yearsIx + 1) + "/" + valid
                  //                  : isAgeMore ? sstring + " ages" + agesStr[ageIx]
                  //                          : isACur ? sstring + thisOrCur +
                  //                                 : " "
                  //       : " ??? ");
                  cmd = NINECMDS[nineIx];
                  lStart = yearsIx;
                  lEnd = yearsIx + 1;
                  if (E.debugPutRows6acOut) {
                    if (isAgeList || ((putRowsPrint6aCount++ < 400) && (putRowsPrint6aCount % 25) == 0)) {
                      System.out.flush();
                      System.out.printf("EM.putrow6ac rn=%d %s,lockIx%d ageIx%d ageLim%d, nineIx%d yearsIx%d yearsMax%d, depth%d, valid%d,%s, %s, aop%o, opr%o, cmd%o, list%d,cum%d, rende4=%d,%d putRowsPrint6aCount= " + putRowsPrint6aCount + " \n", rn, resS[rn][0], lockIx, myAgeIx, ageLim, nineIx, yearsIx, yearsMax, depth, valid, extSuffix, (unset ? "UNSET" : "ISSET") + " = " + resI[myRn][ICUM][CCONTROLD][ISSET] + ":" + resI[myRn][ICUR0 + myAgeIx * MAXDEPTH][CCONTROLD][ISSET], aop, opr, cmd, ((aop & LIST14) > 0 ? 14 : (aop & LIST1) > 0 ? 1 : (aop & LIST3) > 0 ? 3 : (aop & LIST8) > 0 ? 8 : aop), resI[rn][ICUM][0][0], rende4, rendae4);
                    }
                  }
                  row = putRowInTable(table, rn, row, ageIx, cmd, suffix, resExt, extSuffix);

                  if (E.debugPutRowsOut6) {
                    ifPutRow6("allCmds");
                  } // if debug
                } //yearsIx
              } // ageIx

              if (false && E.debugPutRowsOut6) {
                if ((resS[rn][rDesc].contentEquals("EmergFF")) && ((haveListsMatch & LIST6) > 0)) {
                  if (putRowsPrint6Count < 20
                          || (((putRowsPrint6Count % 25) == 0)) && (putRowsPrint6Count < 200)) {

                    System.out.flush();
                    System.out.printf("EM.putrow6Start rn=" + rn + ", " + resS[rn][0] + ", row=" + row
                            + ", isset=" + (myUnset ? myCumUnset ? "CumUnset" : "unset" : "isSet")
                            + ", lock#" + lockIx + ", list" + ((haveListsMatch & LIST0) > 0 ? 0 : (haveListsMatch & LIST1) > 0 ? 1 : (haveListsMatch & LIST2) > 0 ? 2 : (haveListsMatch & LIST6) > 0 ? 6 : "??")
                            + ", ops=" + getOpsNames(haveCmdMatch)
                            + ", depth=" + depth + ", valid" + valid + ", ageIx" + ageIx
                            + ", cum00=" + resI[rn][ICUM][0][0]
                            + ", rende4=" + rende4 + "," + rendae4 + " putRowsPrint6Count=" + putRowsPrint6Count + " \n");
                  }
                }
              }
              if (false && E.debugPutRowsOut6) { // after last process
                if ((resS[rn][rDesc].contentEquals("sWorth")) && ((haveListsMatch & LIST10) > 0)) {
                  if (putRowsPrint6Count < 20
                          || (((putRowsPrint6Count % 25) == 0)) && (putRowsPrint6Count < 200)) {
                    System.out.flush();
                    System.out.printf("EM.putrow6Start rn=" + rn + ", " + resS[rn][0] + ", row=" + row
                            + ", isset=" + (myUnset ? myCumUnset ? "CumUnset" : "unset" : "isSet")
                            + ", lock#" + lockIx + ", list" + ((haveListsMatch & LIST0) > 0 ? 0 : (haveListsMatch & LIST1) > 0 ? 1 : (haveListsMatch & LIST2) > 0 ? 2 : (haveListsMatch & LIST6) > 0 ? 6 : "??")
                            + ", ops=" + getOpsNames(haveCmdMatch)
                            + ", depth=" + depth + ", valid" + valid + ", ageIx" + ageIx
                            + ", cum00=" + resI[rn][ICUM][0][0]
                            + ", rende4=" + rende4 + "," + rendae4 + " putRowsPrint6Count=" + putRowsPrint6Count + " \n");
                  }

                }
              }// for ageix
            } // if nine
          } // for nine
        }// end of match

      } // end of loop on doRes locks0-3
      if (E.debugPutRowsOut) {
        if (putRowsPrint9Count < 12) {
          System.out.println("xit rn=" + rn + " row=" + row + ", desc=" + resS[rn][0] + " suffix=" + suffix + " putRowsPrint9Count" + putRowsPrint9Count++);
        }
      }
      return row;
    } catch (Exception | Error ex) {
      firstStack = secondStack + "";
      ex.printStackTrace(pw);
      secondStack = sw.toString();
      System.out.flush();
      System.err.flush();
      System.err.println(tError = ("Caught " + ex.toString() + ", cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName() + andMore()));
      System.err.println("rn=" + rn + ", desc=" + resS[rn][0]);
      //     ex.printStackTrace(System.err);
      st.setFatalError();
      throw new WasFatalError(tError);

    }
//    return row;
  }

  boolean yesPercent = false;

  /**
   * convert val to a shorter string if % present
   *
   * @param val value to convert
   * @return string with possible % and shorter
   */
  private String valForTable(double val) {
    return (yesPercent ? mf2(val) + "%" : mf2(val));
  }

  /**
   * set values into a row of the table
   *
   * @param table where rows are stored
   * @param resExit reference for the detail string
   * @param rn the index of the value being written
   * @param myCmd part of the key being offered for the type of line element
   * @param row the row in the table to receive a line
   * @param desc the description line for the start of the line
   * @param ageIx the index to the cur agegroup to use
   * @return the row
   */
  private int putRowInTable(JTable table, int rn, int row, int ageIx9, long myCmd, String suffix9, String resExt[], String extSuffix9) {
    // String s[] = {"planets ", "ships ", "sum "};
    String ss[] = {"999999.", "ave of the", "P and S", "sums", ">>>>>>>>>>"};
    String ww[] = {"color", "Winner", "999999", "99.0%", ">>>>>>>>>>"};
    long d[] = {getP, getS};
    long dd[] = {getP, getS};
    description = resS[rn][rDesc];
    myRn = rn;

    String detail = myDetail = resS[rn][rDetail];
    String dds = description + detail;
    String isPercent = (dds.contains("%") || dds.contains("Percen") || dds.contains("percen")) && ((myCmd & (THISYEARAVE | CURAVE | CUMAVE | THISYEAR | CUR | CUM)) > 0L) ? "%" : "";
    yesPercent = isPercent.contains("%");
    double aVal;
    double sums;
    dFrac.setMaximumFractionDigits(doUnits || doPower > 0 ? 0 : (int) resI[rn][ICUM][CCONTROLD][IFRACS]);
    dd[1] = doSum ? sum : getS; // force D1 request to sum for second round
    detail += ":: " + extSuffix;
    if (doPower > 0 && ((myCmd & (THISYEARAVE | CURAVE | CUMAVE | THISYEAR | CUR | CUM)) > 0L)) {
      detail += " add " + doPower + " 0 digits added before the period for each number";
    }
    if (unset && E.debugPutRowsOutUnset && !doSkipUnset) {
      suffix = ">>>UNSET<<<<";
    } else if (row > 96) {
      suffix = "full";
    }
    if (row < 98) {
      table.setValueAt(description + suffix, row, 0);
      resExt[row] = detail;

      if (E.debugPutRowsOut6) {
        if (resS[rn][rDesc].contains("Score")) {
          System.out.println("in putRowInTable. do Score " + Thread.currentThread().getName() + " .putRowInTable" + (doSum ? " doSum" : doBoth ? " doBoth" : "") + ", suffix=" + suffix + " myCmd=" + Long.toOctalString(myCmd) + " lStart=" + lStart + " lEnd=" + lEnd + ", valid=" + valid + (myUnset ? " myUnset" : "") + (myCumUnset ? " myCumUnset" : ""));
        }
        //   System.out.println("in EM.gameRes." + toString() + ".putRowInTable" + dFrac.format(values[0][0]) + " " + dFrac.format(values[0][6]));
      }
      // process values never set, particularly skip adding row if unset
      if ((myCumUnset || myUnset || unset) && (doSkipUnset || didUnset || !E.debugPutRowsOutUnset)) {
        return row;  // do not update row, do not write row
      } // or set a zero row if stat is unset
      else if (myCumUnset && myUnset && doZeroUnset) {
        for (int mm = 1; mm < 11; mm++) {
          table.setValueAt("0.0", row, mm);
        }
        row++;
        didUnset = true;
      } // or set row values to ---
      else if (myCumUnset && myUnset) {
        for (int mm = 1; mm < 11; mm++) {
          table.setValueAt("---", row, mm);
        }
        row++;
        didUnset = true;
      } else {
        if (resS[rn][rDesc].contains("Score") && row == 0) {
          int i = E.S;
          // String ww[] = {"color","Winner","999999","99.0%",">>>>>>>>>>" }; `
          //  Set second half of the row in ships 5
          if (isWinner) {
            table.setBackground(new java.awt.Color(E.backGroundColors[winner]));
          }
          aVal = sums = 0;
          double myWin = 0;
          for (int m = 0; m < E.lclans; m++) {
            table.setValueAt(((aVal = getRowEntryValue(rn, dd[(int) i] + myCmd, m, ageIx)) < -93456789.0 ? "------" : mf(aVal)), row, (int) (i * E.lclans + m + 1));
            sums += aVal;
            if (m == winner && isWinner) {
              table.setValueAt(aVal, row, 3);
              myWin = aVal;
            }
          }
          if (isWinner) {
            table.setValueAt(E.groupNames[winner], row, 1);
            table.setValueAt("Winner", row, 2);
            table.setValueAt("   by   ", row, 3);
            table.setValueAt((mf(aVal * 100 / sums * .2)) + "%", row, 4); // qubbwe
            table.setValueAt(">>>>>>>>>>", row, 5);
          } else { // winner pending
            table.setValueAt(mf(curDif) + "%", row, 1);
            table.setValueAt("yet till", row, 2);
            table.setValueAt("a Winner", row, 3);
            table.setValueAt("for the", row, 4);
            table.setValueAt("game", row, 5);
          }

          table.setValueAt(description + suffix + powers, row, 0);
          resExt[row] = detail;
          row++;
        } else if (doSum) {
          sums = 0.;
          for (long i : d) {
            if (doSum && (i == getP)) { // only do the first half of sums
              for (int mm = 1; mm < E.lclans; mm++) {
                //String ss[] = {">>>This", "row sums", "planets", "and ships", ">>>>>>>>>>"};
                //String ss[] = {"999999.", "ave of the", "P and S", "sums", ">>>>>>>>>>"};
                table.setValueAt(ss[mm], row, mm + 1);
              }
            } else { // second half of sum
              for (int m = 0; m < E.lclans; m++) {
                table.setValueAt(((sums += aVal = getRowEntryValue(rn, dd[(int) i] + myCmd, m, ageIx)) < -93456789.0 ? "------" : valForTable(aVal)), row, (int) i * E.lclans + m + 1);
              }
              table.setValueAt(valForTable(sums/E.LCLANS), row, 1); // average
            }
          }
          table.setValueAt(description + suffix + powers, row, 0);
          resExt[row] = detail;
          row++;
        } else if (doBoth) {
          boolean didSum = doSum;
          doSum = false; // prevent getRowEntryValue from suming values
          table.setValueAt(description + suffix + " both", row, 0);
          resExt[row] = detail;
          for (long ij : d) {
            for (int m = 0; m < E.lclans; m++) {
              table.setValueAt((((aVal = getRowEntryValue(rn, (int) dd[(int) ij] + myCmd, m, ageIx)) < -93456789.
                      ? aVal < -94567895.
                              ? "--------"
                              : "-----"
                      : valForTable(aVal))),
                      row, (int) ij * E.lclans + m + 1);
            }
          }
          table.setValueAt(description + suffix + powers, row, 0);
          resExt[row] = detail;
          row++;
          doSum = didSum;  // restore doSum
        }
      }
    }

    return row;
  }

  /**
   * return value of one value, value designated by opr
   *
   * @param rn the index of the statistic being listed
   * @param opr1 key flags for pors other commands
   * @param dClan the dClan to be processed
   * @param ageIx index of age in request
   * @return value a value sumsV, sumsI or sumsV/sumsI
   */
  private double getRowEntryValue(int rn, long opr1, int dClan, int ageIx) {
    double sum = 0.;
    double cnts = 0;
    // doSum doBoth global variables
    String ops = "unset";
    String doingSum = (doSum ? "doingSum" : "notDoingSum");

    int pors = (int) (opr1 & PSMASK);
    // int curIx = 0;
    try {
      doPower = resI[myRn][ICUM][CCONTROLD][IPOWER];
      powers = "";
      if (((CUM | CUMAVE | CUMUNITS) & opr1) > 0) {
        if ((CUM & opr1) > 0) {
          ops = "cum";
          //              sum                                            both
          sum = doSum ? resV[rn][ICUM][0][dClan] + resV[rn][ICUM][1][dClan] : resV[rn][ICUM][pors][dClan];
        } else if ((CUMUNITS & opr1) > 0) {
          sum = doSum ? resI[rn][ICUM][0][dClan] + resI[rn][ICUM][1][dClan] : resI[rn][ICUM][pors][dClan];
          ops = "CUMUNITS";
          doPower = 0;
        } else if ((CUMAVE & opr1) > 0) {
          sum = (doSum ? resV[rn][ICUM][0][dClan] + resV[rn][ICUM][1][dClan] : resV[rn][ICUM][pors][dClan]) / (doSum ? resI[rn][ICUM][0][dClan] + resI[rn][ICUM][1][dClan] : resI[rn][ICUM][pors][dClan]);
          ops = "cumUnitAve";
        }
        if (doPower > 0) {
          sum = sum / Math.pow(10., doPower);
          powers = " *10**" + doPower + " ";
        }
        return sum;

      } else if ((CURAVE & opr1) > 0) {
        // for curave sum averages
        //   int yrsMax = CURAVEAgesYrs[ageIx];
        sum = 0.;
        int didCnt = 0;
        for (int ageYrsIx = startAgeYearsValues; ageYrsIx < endAgeYearsValues; ageYrsIx++) {
          double aaa = 0., bbb = 0., ccc = 0., ddd = 0.;
          int iii = ageYrsIx;
          int jjj = ICUR0 + (ageIx * MAXDEPTH + ageYrsIx);
          long cntSum = 0;
          int cntS = 0;  // ignore zero counts
          // skip empty entries
          if (resV[rn][ICUR0 + (ageIx * MAXDEPTH + ageYrsIx)] == null) {
            // no error but don't count in average
          } else if (resV[rn][ICUR0 + (ageIx * MAXDEPTH + ageYrsIx)].length > 0) {
            cntSum = doSum ? resI[rn][ICUR0 + ageIx * MAXDEPTH + ageYrsIx][0][dClan] + resI[rn][ICUR0 + ageIx * MAXDEPTH + ageYrsIx][1][dClan] : resI[rn][ICUR0 + ageIx * MAXDEPTH + ageYrsIx][pors][dClan];
            cntS = cntSum > 0 ? (int) cntSum : 1;  // ignore zero counts
            sum += cntS > 0 ? (aaa = (ccc = doSum ? ((bbb = resV[rn][ICUR0 + (ageIx * MAXDEPTH + ageYrsIx)][0][dClan]) + resV[rn][ICUR0 + ageIx * MAXDEPTH + ageYrsIx][1][dClan]) : (bbb = resV[myRn][ICUR0 + ageIx * MAXDEPTH + ageYrsIx][pors][dClan])) / cntS) : 0.;
            didCnt += cntSum > 0 ? 1 : 0;  // only count actural sums
            if (E.debugPutRows6agOut) {
              if ((resS[rn][rDesc].contentEquals("s worth")) && (pors == 0) && (dClan == 0)) {
                System.out.flush();
                //                  long opr1 = opr1;
                System.out.printf("EM.putrow6ag rn=" + rn + " " + rn, resS[rn][0] + " lockIx" + lockIx + " ageIx" + ageIx + " nineIx" + nineIx + " yearsIx" + yearsIx + " = " + mf(bbb) + " depth%d, valid%d,%s, %s, opr1%o, cmd%o, list%d, ageYrsIx%d, icur%d , rende4=%d,%d putRowsPrint6aCount= " + putRowsPrint6aCount + " \n", depth, valid, extSuffix, (unset ? "UNSET" : "ISSET") + " = " + resI[myRn][ICUM][CCONTROLD][ISSET] + ":" + resI[myRn][ICUR0 + myAgeIx * MAXDEPTH][CCONTROLD][ISSET], opr1, cmd, ((opr1 & LIST14) > 0 ? 14 : (opr1 & LIST1) > 0 ? 1 : (opr1 & LIST3) > 0 ? 3 : (opr1 & LIST8) > 0 ? 8 : opr1), iii, jjj, bbb, rende4, rendae4);

              }
            }
          }
        }
        // now average for years found
        sum = didCnt > 0 ? sum / didCnt : sum;
        ops = "CURAVE";
        if (doPower > 0) {
          sum = sum / Math.pow(10., doPower);
          powers = " *10**" + doPower + " ";
        }
        return sum;
      } else if (!(resI[rn][ICUR0 + (int) lStart] == null
              || resI[rn][ICUR0 + (int) lStart][CCONTROLD] == null
              || resI[rn][ICUR0 + (int) lStart][CCONTROLD][IPOWER] < 0)) {  // cur current values for up to 6 successive years
        sum = 0.;
        cnts = 0.;
        if (resI[rn] == null) {
          doMyErr(">>>>>>in curIxgetRowEntryValue null at resI[" + rn + "]=" + description);
          return -88888888.;
        }
        if (resI[rn][ICUR0 + (int) lStart] != null && resI[rn][ICUR0 + (int) lStart][CCONTROLD] != null && resI[rn][ICUR0 + (int) lStart][CCONTROLD][IPOWER] < 0) {
          doMyErr(">>>>>>in curIxgetRowEntryValue null at resI[rn][ICUR0 + (int) lStart=" + lStart + "] resI[" + rn + "]=" + description);
          return -9999998888.;
        }
        long resia[][] = resI[rn][ICUR0 + (int) lStart];
        if (resia[CCONTROLD] == null) {
          doMyErr(">>>>>>in curIxgetRowEntryValue null at resI[rn][ICUR0 + (int) lStart=" + lStart + "][CCONTROLD] resI[" + rn + "]=" + description);
          return -99999999.;
        }
        long resib[] = resia[CCONTROLD];
        long dp = resib[IPOWER];
        doPower = resI[rn][ICUR0 + (int) lStart][CCONTROLD][IPOWER];
        powers = "";
        lEnd = Math.min(lEnd, valid); //restrict year 0 to 1 year,1 prior reset
        ops = "some Cur";
        for (int curIx = (int) lStart + ageIx * MAXDEPTH; curIx < lEnd + ageIx * MAXDEPTH; curIx++) {
          if (resV[rn] == null) {
            if (pors == 0 && dClan == 0) { //complain only once
              doMyErr(">>>>>>in curIxgetRowEntryValue null at resV[" + rn + "] desc=" + resS[rn][0]);
            }
            return -98.;
          } else if (resV[rn][ICUR0 + curIx] == null) {
            if (pors == 0 && dClan == 0) {
              doMyErr(">>>>>>in curIxgetRowEntryValue null at resV[" + rn + "] [cur0+ " + curIx + "] desc=" + resS[rn][0]);
            }
            return -97.;
          } else if (resV[rn][ICUR0 + curIx][0] == null) {
            doMyErr(">>>>>>in curIxgetRowEntryValue null at resV[" + rn + "] [cur0 + " + curIx + "][0] desc=" + resS[rn][0]);
            return -98.78;
          }
          if ((rn == 96 || rn == 0 || rn == 2 || rn == 3) && pors == 0 && dClan == 0) {
            //  System.out.println(">>>>>>in curIxgetRowEntryValue  at resV[" + rn + "][rcur0 +" + curIx + "]" + ", desc=" + resS[rn][0]);
          }
          sum += doSum ? resV[rn][ICUR0 + curIx][0][dClan] + resV[rn][ICUR0 + curIx][1][dClan] : resV[rn][ICUR0 + curIx][pors][dClan];
          cnts += doSum ? resI[rn][ICUR0 + curIx][0][dClan] + resI[rn][ICUR0 + curIx][1][dClan] : resI[rn][ICUR0 + curIx][pors][dClan];
        }
        if (((THISYEAR | CUR) & cmd) > 0 && sum != 0.0) {   // values/yrs
          ops = "cur";
          sum = sum / (lEnd - lStart);
          if (doPower > 0) {
            sum = sum / Math.pow(10., doPower);
            powers = " *10**" + doPower + " ";
          }
          return sum;
        } else if ((((THISYEARAVE | CURAVE) & cmd) > 0) && (sum != 0.0) && (cnts > 0)) {
          ops = "curUnitAve";
          sum = sum / cnts; // values / units  for whatever years
          if (doPower > 0) {
            sum = sum / Math.pow(10., doPower);
            powers = " *10**" + doPower + " ";
          }
          return sum;
        } else if (((CURUNITS | THISYEARUNITS) & cmd) > 0) {
          ops = "CURUNITS";
          return cnts;
        } else if (((THISYEAR | CURUNITS | thisYearUnitAve | curUnitAve) & cmd) > 0) {
          ops = "sum or cnts 0";
          if (doPower > 0) {
            sum = sum / Math.pow(10., doPower);
            powers = " *10**" + doPower + " ";
          }
          // return sum > 0 ? sum : -7788.66; // sum or cnts 0
          return sum;
        }

      }
      return -93456789.;  // if a strange option
    } catch (Exception ex) {
      newError = true;
      firstStack = secondStack + "";
      ex.printStackTrace(pw);
      secondStack = sw.toString();
      System.err.println(tError = ("putRows2 " + curEconName + " " + Econ.nowThread + "Caught " + ex.toString() + ", cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName() + andMore()));
      System.err.println("rn=" + rn + ", desc=" + resS[rn][0]);
      //ex.printStackTrace(System.err);
      System.exit(-31);
      throw new WasFatalError(tError);
    }
  }

  int rememberYear = -1;
  int rememberList = -1;
  int rememberRow = -1;
  int detailYear = -1;
  String rememberDetail = "xx";

  /**
   * write to the keep file if rememberFromPage is set rememberFromPage is
   * cleared at each new page
   *
   * @param list
   * @param table
   * @param theRow
   * @param theDetail
   */
  void doRememberValues(int list, JTable table, int theRow, String theDetail) {
    try {
      String ll = " ";
      if (rememberFromPage) {
        if (year != rememberYear || !st.statsRememberWhy.getText().matches(prevRememberCmt)) { // need another year comment page
          rememberYear = year;
          prevRememberCmt = st.statsRememberWhy.getText() + ""; // force a copy
          String dateString = MYDATEFORMAT.format(new Date());
          //    String rOut = "New Game " + dateString + "\r\n";
          ll = "renenberYear" + year + " version " + st.versionText + " " + dateString + " " + prevRememberCmt + "\r\n";
          bKeep.write(ll, 0, ll.length());
        }
        if (!theDetail.matches(rememberDetail) || year != detailYear) {
          rememberDetail = theDetail + "";
          ll = "title " + theDetail + "\r\n"; // the detail description of the remember
          bKeep.write(ll, 0, ll.length());
          ll = "remember ";
          // add the string value of the row entries
          for (int i = 0; i < 11; i++) {
            ll += table.getValueAt(theRow, i).toString() + " ";
          }
          ll += "\r\n";
          bKeep.write(ll, 0, ll.length());
          keepBuffered = true;
        }//rememberDetail

      }//remember from page
    } catch (Exception ex) {
      firstStack = secondStack + "";
      ex.printStackTrace(pw);
      secondStack = sw.toString();
      flushes();
      System.err.println(tError = ("Remember " + curEconName + " " + Econ.nowThread + "Ignore this error " + new Date().toString() + " " + (new Date().getTime() - startTime) + " cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName() + ", addlErr=" + eM.addlErr + andMore()));
      System.exit(-23);
      throw new WasFatalError(tError);
    }
  }//doWriteRememberVals

  /**
   * set the columns of a title row, and end the row as needed commands colAdd
   * colAddBrk colBrkAdd colHlfAddBrk colHlfBrkAdd colBrkEnd String nextCol =
   * ""; static int colCnt = 0, colMax = 10, lTit = 41, lCol = 15; static int
   * lMax=0,lCnt=0;
   *
   * @param cmd command for processing the chr
   * @param table table for the columns
   * @param chr
   * @param resExit
   * @param row
   * @return
   */
  int savT(int cmd, JTable table, String chr, String[] resExit, int row) {
    switch (cmd) {
      case colAdd:
        nextCol += chr;
        break;

      default:
    }
    return row;
  }

  /**
   * loop through the description for row titles
   *
   * @calls savT static int colCnt = 0, colMax = 10, lTit = 41, lCol = 15;
   * static int lMax=0,lCnt=0;
   */
  int getT(JTable table, int rn, String[] resExit, int row) {
    String ctlChr = "&", ctlBrk = "-", ctlHBrk = "_", numBrk = ",.$", oBrk = ":;<>";
    String nextCol = "", chunk1 = "", chunk2 = "";
    int lNextCol = 0, lChunk1 = 0, lChunk2 = 0, lNextNChunk1 = 0;
    colCnt = 0;
    int maxNextCol = 41, lCols = 15;
    boolean ctlFnd = false;
    boolean inNum = false;
    int lMax = colCnt == 0 ? lTit : lCol;
    String next = "";
    String description = resS[rn][rDesc];
    int maxM = description.length();
    for (int m = 0; m < maxM; m++) {
      next = description.substring(m, m + 1);
      if (ctlFnd) {
        if (ctlBrk.contains(next)) {

        }
      } else if (inNum) {
      } else if (ctlChr.contains(next)) {
        ctlFnd = true;
      } else {
      }
    }
    return row++;
  }

  /**
   * get the detail string
   *
   * @return
   */
  //   public String getDetail() {
  //    return detail;
  //  }
  /**
   * enum to hold and sum result values, flags indicate at which rounds a given
   * enum will produce a row in the table. (title, 5 clans)
   *
   *
   */
  private static boolean game = true;
  private static boolean clan = false;
  private static boolean printVal = true;

  /**
   * Throw a MyErrException error, list the message sent with the error
   *
   * @param form format of the error message
   * @param oargs arguments of the error
   */
  void aErr(String form, Object... oargs) {
    // StringBuffer m = "Exception";
    //throw MyTestException()
    Object v[] = new Object[21];
    for (int i = 0; i < v.length; i++) {
      v[i] = 0.;
    }
    for (int i = 0; i < oargs.length && i < v.length; i++) {
      v[i] = oargs[i];
    }

    System.out.flush();
    System.err.flush();
    System.err.format("name=" + (curEcon == null ? "" : curEcon.name) + ":" + form + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    System.err.format((curEcon == null ? "" : curEcon.name) + ":" + form + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    String vvv = "".format((curEcon == null ? "" : curEcon.name) + ":" + form + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    new Throwable().printStackTrace(System.err);
    System.err.flush();
    System.out.flush();

    myTestDone = true;
    doMyErr(vvv); // throw as part of enclosing if statment
    //System.exit(5);
    //return 0.;
  }

  /**
   * send a set of error messages, only throw a MyErrException if too many
   * errors
   *
   * @param form format for the messages
   * @param oargs arguments for the format
   */
  void bErr(String form, Object... oargs) {
    // StringBuffer m = "Exception";
    //throw MyTestException()
    Object v[] = new Object[21];
    for (int i = 0; i < v.length; i++) {
      v[i] = 0.;
    }
    for (int i = 0; i < oargs.length && i < v.length; i++) {
      v[i] = oargs[i];
    }

    System.out.flush();
    System.err.flush();
    System.err.format((curEcon == null ? "" : curEcon.name) + ":" + form + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    String vvv = "".format((curEcon == null ? "" : curEcon.name) + ":" + form + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    System.out.print(vv);
    new Throwable().printStackTrace(System.err);
    System.err.flush();
    System.out.flush();

    if (++allGameErrCnt >= allGameErrMax || ++gameErrCnt >= gameErrMax || ++yearErrCnt > yearErrMax) {
      System.err.print(">>>>> fatal Error " + (allGameErrCnt >= allGameErrMax ? "allGameErr" : gameErrCnt >= gameErrMax ? "gameErr" : yearErrCnt >= yearErrMax ? "yearErr" : "unknown") + " count exceeded=" + "allGameErrCnt=" + allGameErrCnt + ", gameErrCnt=" + gameErrCnt + ", yearErrCnt=" + yearErrCnt + "<<<<<<<<<");
      myTestDone = true;
      doMyErr(vvv); // throw as part of enclosing if statment
    }
    //System.exit(5);
    //return 0.;
  }

  void cErr(String form) {
    // StringBuffer m = "Exception";
    //throw MyTestException()

    System.out.flush();
    System.err.flush();
    System.err.println(form);
    new Throwable().printStackTrace(System.err);
    System.err.flush();
    System.out.flush();

    if (++allGameErrCnt >= allGameErrMax || ++gameErrCnt >= gameErrMax || ++yearErrCnt > yearErrMax) {
      System.err.print(">>>>> fatal Error " + (allGameErrCnt >= allGameErrMax ? "allGameErr" : gameErrCnt >= gameErrMax ? "gameErr" : yearErrCnt >= yearErrMax ? "yearErr" : "unknown") + " count exceeded=" + "allGameErrCnt=" + allGameErrCnt + ", gameErrCnt=" + gameErrCnt + ", yearErrCnt=" + yearErrCnt + "<<<<<<<<<");
      myTestDone = true;
      doMyErr("cErr=" + form); // throw as part of enclosing if statment
    }
    //System.exit(5);
    //return 0.;
  }

  /**
   * generate a string with names of Econ, Thread, file.line.method,
   * file.line.method why
   *
   * @param ec the Econ of the caller
   * @param why the message
   * @return filled out message line
   */
  static String here(String ww,Econ ec, String why) {
    String isHere = "---" + ww + "----" + ec.name + "  " + Thread.currentThread().getName();
    StackTraceElement[] aa = Thread.currentThread().getStackTrace();
    int stLen = aa.length;
    if (stLen == 2) {
      isHere += " " + aa[2].getFileName() + "." + aa[2].getLineNumber() + "." + aa[2].getMethodName() + " " + why;
    } else if (stLen >= 3) {
     // StackTraceElement ab = Thread.currentThread().getStackTrace()[3];
      // StackTraceElement ac = Thread.currentThread().getStackTrace()[5];
      isHere += " " + aa[2].getFileName() + "." + aa[2].getLineNumber() + "." + aa[2].getMethodName() + " from " + aa[3].getFileName() + "." + aa[3].getLineNumber() + " " + aa[3].getMethodName() + " " + why;
      return isHere;
    }
    return isHere;
  }
  
   /**
   * generate a string with names of Econ, Thread, file.line.method,
   * file.line.method why Place the string at variable wasHere
   *
   * @param is the flag of the caller
   * @param ec the Econ of the caller
   * @param why the message
   * @return filled out message line
   */
  static String isHere(String is,Econ ec, String why) {
    wasHere6 = here(is,ec, why);
    if(E.debugIsHere1Out){
      System.out.println(wasHere6);
    }
    return wasHere6;
  }


  /**
   * generate a string with names of Econ, Thread, file.line.method,
   * file.line.method why Place the string at variable wasHere
   *
   * @param ec the Econ of the caller
   * @param why the message
   * @return filled out message line
   */
  static String isHere1(Econ ec, String why) {
    String wasHere = here("IS1",ec, why);
    if(E.debugIsHere1Out){
      System.out.println(wasHere);
    }
    return wasHere;
  }

  /**
   * generate a string with names of Econ, Thread, file.line.method,
   * file.line.method why Place the string at variable wasHere2
   *
   * @param ec the Econ of the caller
   * @param why the message
   * @return filled out message line
   */
  static String isHere2(Econ ec, String why) {
    wasHere2 = here("IS2",ec, why);
    if(E.debugIsHere2Out){
      System.out.println(wasHere2);
    }
    return wasHere2;
  }

  /**
   * generate a string with names of Econ, Thread, file.line.method,
   * file.line.method why Place the string at variable wasHere
   *
   * @param ec the Econ of the caller
   * @param why the message
   * @return filled out message line
   */
  static String isHere3(Econ ec, String why) {
    wasHere3 = here("IS3",ec, why);
    if(E.debugIsHere3Out){
      System.out.println(wasHere3);
    }
    return wasHere3;
  }

  /**
   * write to System.err a message using form with oargs as arguments
   *
   * @param form the form of the message
   * @param oargs a set of up to 20 arguments
   */
  void aMsg(String form, Object... oargs) {
    // StringBuffer m = "Exception";
    //throw MyTestException()
    Object v[] = new Object[21];
    for (int i = 0; i < v.length; i++) {
      v[i] = 0.;
    }
    for (int i = 0; i < oargs.length && i < v.length; i++) {
      v[i] = oargs[i];
    }

    System.out.flush();
    System.err.flush();
    // StringBuffer m = "Exception";
    //throw MyTestException()
    StackTraceElement aa = Thread.currentThread().getStackTrace()[3];
    StackTraceElement ab = Thread.currentThread().getStackTrace()[4];
    StackTraceElement ac = Thread.currentThread().getStackTrace()[5];

    String Fname = aa.getFileName();
    int Fline = aa.getLineNumber();
    String Fname2 = ab.getFileName();
    int Fline2 = ab.getLineNumber();
    String Mname2 = ab.getMethodName();
    String Fname3 = ac.getFileName();
    int Fline3 = ac.getLineNumber();
    String Mname3 = ac.getMethodName();
    String Cname = aa.getClassName();
    String Mname = aa.getMethodName();
    //System.err.format("name=" + (curEcon == null ? "" : curEcon.name) + ":" + form + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);
    System.err.format((curEcon == null ? "" : curEcon.name) + "." + Mname3 + "." + Mname2 + "." + Mname + ":" + form + "%n", v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8], v[9], v[10], v[11], v[12], v[13], v[14], v[15], v[16], v[17], v[18], v[19], v[20]);

    new Throwable().printStackTrace(System.err);
    System.err.flush();
    System.out.flush();
  }

  /**
   * pending
   *
   */
  static void doResSpecial() {

  }

  /**
   * save the percent of rN2*100./rN1 runs inside doResSpecial before getWinner
   * inside doEndYear The results go into ICUR0 of the result The units for rN1
   * are placed in ICUR0 of the result unless rn2==result rN The ISSET of ICUR0
   * is set in result The results are added to ICUM The units of rN1 are added
   * to ICUM unless rN1 == result rN
   *
   * @param rNPer The doRes result of the percent
   * @param rN1 the first doRes generally earliest value
   * @param rN2 the second doRes generally last value
   */
  void doResVPercent(int rNPer, int rN1, int rN2) {
    if (!(resI[rN1] == null || resI[rN1][ICUR0] == null || resI[rN1][ICUR0][CCONTROLD] == null || resI[rN1][ICUR0][CCONTROLD][ISSET] != 1 || resI[rN2] == null || resI[rN2][ICUR0] == null || resI[rN2][ICUR0][CCONTROLD] == null || resI[rN2][ICUR0][CCONTROLD][ISSET] != 1)) {
      for (int m = 0; m < 2; m++) {
        for (int n = 0; n < 5; n++) {
          resV[rNPer][ICUM][m][n] += resV[rNPer][ICUR0][m][n] = (resV[rN2][ICUR0][m][n] - resV[rN1][ICUR0][m][n]) * 100. / (resV[rN1][ICUR0][m][n] + .00001);
          if (rNPer != rN1) {
            resI[rNPer][ICUM][m][n] += resI[rNPer][ICUR0][m][n] = resI[rN1][ICUR0][m][n];
          }//if
        }//n
      }//m
      resI[rNPer][ICUR0][CCONTROLD][ISSET] = 1;
    }
  }

  /**
   * save the percent increase of (rN2 -rN1)*100./rN1 runs inside doResSpecial
   * before getWinner inside doEndYear The results go into ICUR0 of the result
   * The units for rN2 are placed in ICUR0 of the result unless rn2==result rN
   * The ISSET of ICUR0 is set The results are added to ICUM The units of rN1
   * are added to ICUM unless rN1 == result rN
   *
   * @param rNPer The doRes result of the incr percent
   * @param rN1 the first doRes generally earliest value
   * @param rN2 the second doRes generally last value
   */
  void doResVIncrPercent(int rNPer, int rN1, int rN2) {
    if (!(resI[rN1] == null || resI[rN1][ICUR0] == null || resI[rN1][ICUR0][CCONTROLD] == null || resI[rN1][ICUR0][CCONTROLD][ISSET] != 1 || resI[rN2] == null || resI[rN2][ICUR0] == null || resI[rN2][ICUR0][CCONTROLD] == null || resI[rN2][ICUR0][CCONTROLD][ISSET] != 1)) {
      if (!(resI[rN1] == null || resI[rN1][ICUR0] == null || resI[rN1][ICUR0][CCONTROLD] == null || resI[rN1][ICUR0][CCONTROLD][ISSET] != 1 || resI[rN2] == null || resI[rN2][ICUR0] == null || resI[rN2][ICUR0][CCONTROLD] == null || resI[rN2][ICUR0][CCONTROLD][ISSET] != 1)) {
        for (int m = 0; m < 2; m++) {
          for (int n = 0; n < 5; n++) {
            resV[rNPer][ICUM][m][n] += resV[rNPer][ICUR0][m][n] = (resV[rN2][ICUR0][m][n] - resV[rN1][ICUR0][m][n]) * 100. / resV[rN1][ICUR0][m][n];
            if (rNPer != rN1) {
              resI[rNPer][ICUM][m][n] += resI[rNPer][ICUR0][m][n] = resI[rN1][ICUR0][m][n];
            }//if
          }//n
        }//m
        resI[rNPer][ICUR0][CCONTROLD][ISSET] = 1;
      }
    }
  }

  /**
   * save the sum of rNAdd = (rN2 + rN1) runs inside doResSpecial before
   * getWinner inside doEndYear The results go into ICUR0 of the result The
   * units for rN2 are placed in ICUR0 of the result unless rn2==result rN The
   * ISSET of ICUR0 is set The results are added to ICUM The units of rN1 are
   * added to ICUM unless rN2 == result rN
   *
   * @param rNAdd The doRes result of the add
   * @param rN1 the first doRes generally earliest value
   * @param rN2 the second doRes generally last value
   */
  void doResAdd(int rNAdd, int rN1, int rN2) {
    if (!(resI[rN1] == null || resI[rN1][ICUR0] == null || resI[rN1][ICUR0][CCONTROLD] == null || resI[rN1][ICUR0][CCONTROLD][ISSET] != 1 || resI[rN2] == null || resI[rN2][ICUR0] == null || resI[rN2][ICUR0][CCONTROLD] == null || resI[rN2][ICUR0][CCONTROLD][ISSET] != 1)) {
      for (int m = 0; m < 2; m++) {
        for (int n = 0; n < 5; n++) {
          resV[rNAdd][ICUM][m][n] += resV[rNAdd][ICUR0][m][n] = (resV[rN2][ICUR0][m][n] + resV[rN1][ICUR0][m][n]);
          if (rNAdd != rN2) {
            resI[rNAdd][ICUM][m][n] += resI[rNAdd][ICUR0][m][n] = resI[rN2][ICUR0][m][n];
          }// if
        }//n
      }//m
      resI[rNAdd][ICUR0][CCONTROLD][ISSET] = 1;
    }
  }

  /**
   * save the difference of rNAdd = (rN2 - rN1) runs inside doResSpecial before
   * getWinner inside doEndYea The results go into ICUR0 of the result The units
   * for rN2 are placed in ICUR0 of the result unless rn2==result rN The ISSET
   * of ICUR0 is set The results are added to ICUM The units of rN1 are added to
   * ICUM unless rN2 == result rN
   *
   * @param rNSub The doRes result of the add
   * @param rN1 the first doRes generally earliest value
   * @param rN2 the second doRes generally last value
   */
  void doResSub(int rNSub, int rN1, int rN2) {
    if (!(resI[rN1] == null || resI[rN1][ICUR0] == null || resI[rN1][ICUR0][CCONTROLD] == null || resI[rN1][ICUR0][CCONTROLD][ISSET] != 1 || resI[rN2] == null || resI[rN2][ICUR0] == null || resI[rN2][ICUR0][CCONTROLD] == null || resI[rN2][ICUR0][CCONTROLD][ISSET] != 1)) {
      for (int m = 0; m < 2; m++) {
        for (int n = 0; n < 5; n++) {
          resV[rNSub][ICUM][m][n] += resV[rNSub][ICUR0][m][n] = (resV[rN2][ICUR0][m][n] - resV[rN1][ICUR0][m][n]);
          if (rN2 != rNSub) {
            resI[rNSub][ICUM][m][n] += resI[rNSub][ICUR0][m][n] = resI[rN2][ICUR0][m][n];
          }// if
        }//n
      }//m
      resI[rNSub][ICUR0][CCONTROLD][ISSET] = 1;
    }
  }

  /**
   * save the division of rN2 by rN1 of rNDivUnits = rN1 / rN2 runs inside
   * doResSpecial before getWinner inside doEndYea The results go into ICUR0 of
   * the result The units for rN2 are placed in ICUR0 of the result unless
   * rn1==result rN The ISSET of ICUR0 is set in result The results are added to
   * ICUM The units of rN1 are added to ICUM unless rN1 == result rN
   *
   * @param rNDivUnits The doRes result of the division
   * @param rN1 the first doRes generally earliest value
   * @param rN2 the second doRes generally last value units
   */
  void doResVDivUnits(int rNDivUnits, int rN1, int rN2) {
    if (!(resI[rN1] == null || resI[rN1][ICUR0] == null || resI[rN1][ICUR0][CCONTROLD] == null || resI[rN1][ICUR0][CCONTROLD][ISSET] != 1 || resI[rN2] == null || resI[rN2][ICUR0] == null || resI[rN2][ICUR0][CCONTROLD] == null || resI[rN2][ICUR0][CCONTROLD][ISSET] != 1)) {
      for (int m = 0; m < 2; m++) {
        for (int n = 0; n < 5; n++) {
          resV[rNDivUnits][ICUM][m][n] += resV[rNDivUnits][ICUR0][m][n] = (resV[rN1][ICUR0][m][n] / (resI[rN2][ICUR0][m][n] + .00001));
          if (rN1 != rNDivUnits) {
            resI[rNDivUnits][ICUM][m][n] += resI[rNDivUnits][ICUR0][m][n] = resI[rN1][ICUR0][m][n];
          }// if
        }//n
      }//m
      resI[rNDivUnits][ICUR0][CCONTROLD][ISSET] = 1;
    }
  }

  /**
   * save the percent of rN2 by rN1 of rNDivUnits = rN1 * 100. / rN2 runs inside
   * doResSpecial before getWinner inside doEndYea The results go into ICUR0 of
   * the result The units for rN2 are placed in ICUR0 of the result unless
   * rn1==result rN The ISSET of ICUR0 is set in result The results are added to
   * ICUM The units of rN1 are added to ICUM unless rN1 == result rN
   *
   * @param rNDivUnits The doRes result of the division
   * @param rN1 the first doRes units generally earliest value
   * @param rN2 the second doRes generally last value units
   */
  void doResIPercemtUnits(int rNDivUnits, int rN1, int rN2) {
    if (!(resI[rN1] == null || resI[rN1][ICUR0] == null || resI[rN1][ICUR0][CCONTROLD] == null || resI[rN1][ICUR0][CCONTROLD][ISSET] != 1 || resI[rN2] == null || resI[rN2][ICUR0] == null || resI[rN2][ICUR0][CCONTROLD] == null || resI[rN2][ICUR0][CCONTROLD][ISSET] != 1)) {
      for (int m = 0; m < 2; m++) {
        for (int n = 0; n < 5; n++) {
          resV[rNDivUnits][ICUM][m][n] += resV[rNDivUnits][ICUR0][m][n] = (resI[rN1][ICUR0][m][n] * 100. / (resI[rN2][ICUR0][m][n] + .00001));
          if (rN1 != rNDivUnits) {
            resI[rNDivUnits][ICUM][m][n] += resI[rNDivUnits][ICUR0][m][n] = resI[rN1][ICUR0][m][n];
          }// if
        }//n
      }//m
      resI[rNDivUnits][ICUR0][CCONTROLD][ISSET] = 1;
    }
  }

  static boolean isWinner = false;
  static double myScore[] = {400., 400., 400., 400., 400.};
  static double myScoreSum = 0.0;
  static int isV = 0;
  static int isI = 1;
  static int isScoreAve = 2;
  static int sValsCnt = -1;
  static double difPercent = 0.0;
  static double difMult = 0.0;
  static double winDif[][] = {{6.000}};  //set by doVal "years to win"
  static double mwinDif[][] = {{2.2, 40.0}, {2.2, 40.0}};
  static double curDif = 0.0;

  int getWinner() {
    // initialize curDif,difMult if year < 2
    curDif = year < 2 ? winDif[0][0] : curDif;
    difMult = year < 2 ? 1.0 / winDif[0][0] : difMult;
    for (int n = 0; n < 5; n++) {
      myScore[n] = 400.;  // allow negatives to reduce it
    }
    winner = scoreVals(TRADELASTGAVE, iGiven, ICUM, isI);
    winner = scoreVals(TRADELASTGAVE, wGiven, ICUM, isV);
    winner = scoreVals(TRADENOMINALGAVE, wGiven2, ICUM, isV);
    winner = scoreVals(TRADESTRATLASTGAVE, wGenerous, ICUM, isV);//%given
    winner = scoreVals(LIVEWORTH, wLiveWorthScore, ICUR0, isV);
    winner = scoreVals(LIVEWORTH, iLiveWorthScore, ICUR0, isI);
    winner = scoreVals(WTRADEDINCRMULT, wYearTradeV, ICUR0, isV);
    // winner = scoreVals(WTRADEDINCRMULT, wYearTradeI, ICUR0, isI);
    winner = scoreVals(DIED, iNumberDied, ICUM, isI);
    winner = scoreVals(BOTHCREATE, iBothCreateScore, ICUM, isI);

    // winner = scoreVals(getStatrN("WTRADEDINCRMULT"), wYearTradeI, ICUR0, isI);
    resI[SCORE][ICUR0][CCONTROLD][ISSET] = 1;
    myScoreSum = 0.0;
    for (int n = 0; n < 5; n++) {
      myScoreSum = myScore[n] * 2;
      resV[SCORE][ICUR0][0][n] = myScore[n];
      resV[SCORE][ICUR0][1][n] = myScore[n];
      resI[SCORE][ICUR0][1][n] = 1;
      resI[SCORE][ICUR0][0][n] = 1;
    }
    double dif = 0.0, wDif = 0.0;
    // dif = max - myScore.ave
    // wDif = dif/myScore.av - curDif
    // isWinner if wDif > 0.0 that is max  is enough greater than ave
    int prevWinner = winner;
    boolean badbad = winner < 0 || winner > 4 ? true : false; // set legal winner
    winner = badbad ? 0 : winner; // set winner legal
    difPercent = (wDif = (dif = (myScore[winner] - myScoreSum * .1) / myScoreSum * .1) - curDif) * 100.0;
    if (wDif > 0.0) {  // wDif is frac (max-ave)/ave - curDif
      isWinner = true;
    } else {  // wDif < 0.0 reduce curDif
      // reduce curDif the amt needed max >= ave
      curDif += (wDif * difMult);
      // reduce curDif each year but don't go negative
      // redices amt max/ave >  curDif
      curDif -= (curDif - curDif * difMult) > 0.0 ? curDif * difMult : 0.0;
    }
    System.out.println("getWinner " + resS[SCORE][0] + " =" + mf(resV[SCORE][ICUR0][0][0]) + " , " + mf(resV[SCORE][ICUR0][0][1]) + " , " + mf(resV[SCORE][ICUR0][0][2]) + "," + mf(resV[SCORE][ICUR0][0][3]) + "," + mf(resV[SCORE][ICUR0][0][4]) + " : " + mf(resV[SCORE][ICUR0][1][0]) + " , " + mf(resV[SCORE][ICUR0][1][1]) + " , " + mf(resV[SCORE][ICUR0][1][2]) + "," + mf(resV[SCORE][ICUR0][1][3]) + "," + mf(resV[SCORE][ICUR0][1][4]) + ", myScore=" + mf(myScore[0]) + " , " + mf(myScore[1]) + " , " + mf(myScore[2]) + "," + mf(myScore[3]) + "," + mf(myScore[4]) + ", winner=" + winner + (badbad ? " illegal winner=" + prevWinner : ""));
    return winner;
  }

  /**
   * score a doRes into myScore and set winner
   *
   * @param dRn count of the stats
   * @param mult pointer to multiplier entry, neg mult, neg to score
   * @param cumCur ICUM or ICUR0
   * @param isN isV =0,isI=1,isScoreAve = 2
   * @return 0-4 number of winner
   */
  int scoreVals(int dRn, double[][] mult, int cumCur, int isN) {
    double inScore[] = {0., 0., 0., 0., 0.};
    int winner = -1;
    double mm = mult[0][0];  // bottom value of limits
    double min = 9999999999.E+20; //reduce to mins
    double max = -min; // increase to max
    double max1 = max - 1.;// an almost max amount
    int ii = 0;
    int m = 0, n = 0;
    for (m = 0; m < 2; m++) {
      for (n = 0; n < 5; n++) {

        if (m == 0) {
          // initialize with planet values for each clan
          if (isN == isI) {
            inScore[n] = resI[dRn][cumCur][m][n] * mm;
          } else if (isN == isV) {
            inScore[n] = resV[dRn][cumCur][m][n] * mm;
          } else { // isScoreAve
            ii = (int) resI[dRn][cumCur][m][n];
            inScore[n] = mm * (ii == 0 ? 1. : resV[dRn][cumCur][m][n] / ii);
          }
        } else { // Than add ship values per clan
          if (isN == isI) {
            inScore[n] += resI[dRn][cumCur][m][n] * mm;
          } else if (isN == isV) {
            inScore[n] += resV[dRn][cumCur][m][n] * mm;
          } else {
            ii = (int) resI[dRn][cumCur][m][n];
            inScore[n] += mm * (ii == 0 ? 1. : resV[dRn][cumCur][m][n] / ii);
          }
          // than find the min value of all clans
          if (inScore[n] < min) {
            min = inScore[n];  // update min for all clanSums
          }
          // find the max value of all clans
          if (inScore[n] > max) {
            max1 = max; // update the previous max
            max = inScore[n];  // update max for all clanSums
          }
        }
      }//n
    }//m
    double smax = -9999999999.E+10;// ?? reset max
    // increase myScore for each clan by clanSum/min
    // neg inScore reduces myScore
    // neg min reduces myScore
    // neg min neg inScore increase myScore
    // plus min plus inScore increases myScore
    // plus inScore large min small increase
    // plus large inScore small plus min large increase
    // plus large inScore small neg min large decrease
    for (n = 0; n < 5; n++) {
      myScore[n] += (inScore[n] + .00001) / (min * .3 + .00001); //update to max greater than max1
      if (myScore[n] > smax) {
        smax = myScore[n];
        winner = n; // current winner
      }
    }
    System.out.println("scoreVals " + resS[dRn][0] + ", mult=" + mm + ", inScore=" + mf(inScore[0]) + " , " + mf(inScore[1]) + " , " + mf(inScore[2]) + "," + mf(inScore[3]) + "," + mf(inScore[4]) + ", myScore=" + mf(myScore[0]) + " , " + mf(myScore[1]) + " , " + mf(myScore[2]) + "," + mf(myScore[3]) + "," + mf(myScore[4]) + ", winner=" + winner);
    return winner;
  }

  int scoreVals(String rName, double[][] mult, int cumCur, int isN) {
    return scoreVals(getStatrN(rName), mult, cumCur, isN);
  }

  int getCurrentShipUnits(String dname) {
    return getCurCumPorsClanUnitSum(getStatrN(dname), ICUR0, E.S, E.S + 1, 0, E.LCLANS);
  }

  int getCurrentShipUnits(int rN) {
    return getCurCumPorsClanUnitSum(rN, ICUR0, E.S, E.S + 1, 0, E.LCLANS);
  }

  int getCurrentPlanetUnits(String dname) {
    return getCurCumPorsClanUnitSum(getStatrN(dname), ICUR0, E.P, E.P + 1, 0, E.LCLANS);
  }

  int getCurrentPlanetUnits(int rN) {
    return getCurCumPorsClanUnitSum(rN, ICUR0, E.P, E.S + 1, 0, E.LCLANS);
  }

  int getCurrentSumUnits(String dname) {
    return getCurCumPorsClanUnitSum(getStatrN(dname), ICUR0, E.P, E.P + 1, 0, E.LCLANS);
  }

  int getCurrentSumUnits(int dname) {
    return getCurCumPorsClanUnitSum(dname, ICUR0, E.P, E.S + 1, 0, E.LCLANS);
  }

  /**
   * get the current clan units
   *
   * @param rN the index of the result
   * @param cl the clan
   * @return the current units for the specified clan
   */
  int getCurrentClanUnits(int rN, int cl) {
    return getCurCumPorsClanUnitSum(rN, ICUR0, E.P, E.S + 1, cl, cl + 1);
  }

  /**
   * get the cumulative clan units
   *
   * @param rN the index of the result
   * @param pors the p or s of the clan
   * @param cl the clan
   * @return the cumulative units for the specified clan
   */
  int getCumulativeClanUnits(int rN, int pors, int cl) {
    return getCurCumPorsClanUnitSum(rN, ICUM, pors, pors + 1, cl, cl + 1);
  }

  /**
   * get the cumulative clan units
   *
   * @param rN the index of the result
   * @param pors the p or s of the clan
   * @param cl the clan
   * @return the cumulative units for the specified clan
   */
  int getCumulativeClanUnits(String rN, int pors, int cl) {
    return getCurCumPorsClanUnitSum(getStatrN(rN), ICUM, pors, pors + 1, cl, cl + 1);
  }

  /**
   * get the count of current clan planets units
   *
   * @param rN index of the result element
   * @param cl the number of the clan
   * @return The current number of planets for this clan
   */
  int getCurrentClanPlanetsUnits(String rN, int cl) {
    return getCurCumPorsClanUnitSum(getStatrN(rN), ICUR0, E.P, E.P + 1, cl, cl + 1);
  }

  /**
   * get the cumulative count of clan planet units
   *
   * @param rN index of the result element
   * @param cl the number of the clan
   * @return The current number of planet units for this clan
   */
  int getCumulativeClanPlanetUnits(String rN, int cl) {
    return getCurCumPorsClanUnitSum(getStatrN(rN), ICUM, E.P, E.P + 1, cl, cl + 1);
  }

  /**
   * get the cumulative count of clan ship units
   *
   * @param rN index of the result element
   * @param cl the number of the clan
   * @return The cumulative count of ship units for this clan
   */
  int getCumulativeClanShipUnits(String rN, int cl) {
    return getCurCumPorsClanUnitSum(getStatrN(rN), ICUM, E.S, E.S + 1, cl, cl + 1);
  }

  /**
   * get the cumulative count of clan planet units
   *
   * @param rN index of the result element
   * @param cl the number of the clan
   * @return The cumulative count of planet units for this clan
   */
  int getCumulativeClanPlanetUnits(int rN, int cl) {
    return getCurCumPorsClanUnitSum(rN, ICUM, E.P, E.P + 1, cl, cl + 1);
  }

  /**
   * get the cumulative count of clan ship units
   *
   * @param rN index of the result element
   * @param cl the number of the clan
   * @return The current number of ship units for this clan
   */
  int getCumulativeClanShipUnits(int rN, int cl) {
    return getCurCumPorsClanUnitSum(rN, ICUM, E.S, E.S + 1, cl, cl + 1);
  }

  /**
   * get the cumulative sum of planet units
   *
   * @param rN index of the result element
   * @return The current sum of planet units
   */
  int getCumulativeSumOfPlanets(int rN) {
    return getCurCumPorsClanUnitSum(rN, ICUM, E.P, E.P + 1, 0, E.LCLANS);
  }

  /**
   * get the cumulative sum of ship units
   *
   * @param rN index of the result element
   * @return The current sum of planet units
   */
  int getCumulativeClanShipUnits(int rN) {
    return getCurCumPorsClanUnitSum(rN, ICUM, E.S, E.S + 1, 0, E.LCLANS);
  }

  /**
   * get the rN for the stats name
   *
   * @param dname string stats name to be found
   * @return the integer related to that name
   */
  int getStatrN(String dname) {
    addlErr = "setStat dname=" + dname;
    Object o1 = resMap.get(dname);
    if (o1 == null) {
      System.out.printf("setStat a cannot find \"%s\" \n", dname);
      o1 = resMap.get("missing name");
    }
    return (int) o1;
  }

  /**
   * get a units sum from the stats database, it could be for the current year
   * or for the cunulative sum of all the years
   *
   * @param rN the index into the stats database<br>
   * use getStatrN(name) as rN to select by string name
   * @param curCum either ICUM or ICUR0 from EM
   * @param porsStart 0:start with planets, 1 start with ships
   * @param porsEnd 1. 0:1 sum just planets, 2. 0:2 sum planets & ships
   * @param clanStart 0-4 sum of the clan to start with
   * @param clanEnd 1-5 end of clan sum, 0:1,1:2 etc. 1 clan<br>
   * 0:5 sum all of the clans
   * @return the sum of units as filtered by the selectors
   */
  int getCurCumPorsClanUnitSum(int rn, int curCum, int porsStart, int porsEnd, int clanStart, int clanEnd) {
    synchronized (syncRes){
    String anErr = "";
    if (E.PAINTDISPLAYOUT) {
      anErr = (curCum < ICUM ? "curCum=" + curCum + " is less than ICUM"
              : curCum > ICUR0 ? "curCum " + curCum + " is greater than ICUR0"
                      : porsStart < 0 ? "porsStart=" + porsStart + " is less than 0 E.P"
                              : porsStart > 1 ? "porsStart=" + porsStart + " is greater than 1 E.S"
                                      : porsEnd < 1 ? "porsEnd=" + porsEnd + " is less than 1 E.P+1"
                                              : porsEnd > 2 ? "porsEnd=" + porsEnd + " is greater than 2 E.S+1"
                                                      : clanStart < 0 ? "clanStart=" + clanStart + " is less than 0"
                                                              : clanStart > 4 ? "clanStart=" + clanStart + " is greater than 4"
                                                                      : clanEnd < 1 ? "clanEnd=" + clanEnd + " is less than 1"
                                                                              : clanEnd > 5 ? "clanEnd=" + clanEnd + " is greater than 5"
                                                                                      : "");
      if (anErr.length() > 0) {
        throw (new MyErr("ERR: " + anErr + " stats:" + rn + ":" + resS[rn][0]
        ));
      }
    }
    int mPors = 0, nClan = 0, iSum = 0;
    
      for (mPors = porsStart; mPors < porsEnd; mPors++) {
        for (nClan = clanStart; nClan < clanEnd; nClan++) {
          iSum += (int) (long) resI[rn][curCum][mPors][nClan];
        }
      }
    
    return iSum;
  } // syncRes
  } //getCurCumPorsClanUnitSum
  /**
   * get a val sum from the stats database, it could be for the current year or
   * for the cunulative sum of all the years
   *
   * @param rN the index into the stats database<br>
   * use getStatrN(name) as rN to select by string name
   * @param curCum either ICUM or ICUR0 from EM
   * @param porsStart 0:start with planets, 1 start with ships
   * @param porsEnd 1. 0:1 sum just planets, 2. 0:2 sum planets & ships
   * @param clanStart 0-4 sum of the clan to start with
   * @param clanEnd 1-5 end of clan sum, 0:1,1:2 etc. 1 clan<br>
   * 0:5 sum all of the clans
   * @return the sum of values as filtered by the selectors
   */
  double getCurCumPorsClanValSum(int rn, int curCum, int porsStart, int porsEnd, int clanStart, int clanEnd) {
    synchronized(syncRes){
    String anErr = "";
    if (E.PAINTDISPLAYOUT) {
      anErr = (curCum < ICUM ? "curCum=" + curCum + " is less than ICUM"
              : curCum > ICUR0 ? "curCum " + curCum + " is greater than ICUR0"
                      : porsStart < 0 ? "porsStart=" + porsStart + " is less than 0 E.P"
                              : porsStart > 1 ? "porsStart=" + porsStart + " is greater than 1 E.S"
                                      : porsEnd < 1 ? "porsEnd=" + porsEnd + " is less than 1 E.P+1"
                                              : porsEnd > 2 ? "porsEnd=" + porsEnd + " is greater than 2 E.S+1"
                                                      : clanStart < 0 ? "clanStart=" + clanStart + " is less than 0"
                                                              : clanStart > 4 ? "clanStart=" + clanStart + " is greater than 4"
                                                                      : clanEnd < 1 ? "clanEnd=" + clanEnd + " is less than 1"
                                                                              : clanEnd > 5 ? "clanEnd=" + clanEnd + " is greater than 5"
                                                                                      : "");
      if (anErr.length() > 0) {
        throw (new MyErr("ERR: " + anErr + " stats#:" + rn + ":" + resS[rn][0]
        ));
      }
    } // if DEBUG
    double vSum = 0.;
    int mPors = 0, nClan = 0, iSum = 0;
      for (mPors = porsStart; mPors < porsEnd; mPors++) {
        for (nClan = clanStart; nClan < clanEnd; nClan++) {
          vSum += resV[rn][curCum][mPors][nClan];
        }
      }
    return vSum;
  }// syncRes
  }

  /**
   * get an average from the stats database, it could be for the current year or
   * for the cunulative sum of all the years, or some of the saved years
   *
   * @param rN the index into the stats database<br>
   * use getStatrN(name) as rN to select by string name
   * @param curCum either ICUM or ICUR0 thrue ICUR6 from EM
   * @param nYears number of years in area to sum units and vals
   * @param porsStart 0:start with planets, 1 start with ships
   * @param porsEnd 1. 0:1 sum just planets, 2. 0:2 sum planets & ships
   * @param clanStart 0-4 sum of the clan to start with
   * @param clanEnd 1-5 end of clan sum, 0:1,1:2 etc. 1 clan<br>
   * 0:5 sum all of the clans
   * @return the average of values/units as filtered by the selectors
   */
  double getCurCumPorsClanAve(int rn, int curCum, int nYears, int porsStart, int porsEnd, int clanStart, int clanEnd) {
    synchronized(syncRes){
    String anErr = "";
    if (E.PAINTDISPLAYOUT) {
      anErr = (curCum < ICUM ? "curCum=" + curCum + " is less than ICUM"
              : (curCum + MAXDEPTH) > resI.length ? "curCum " + curCum + " gt"
                      : nYears < 1 ? "nYears" + nYears + " is less than 1"
                              : nYears > MAXDEPTH ? "nYears" + nYears + " is greater than MAXDEPTH" + MAXDEPTH
                                      : (nYears + curCum + 1) > resI.length ? "nYears + curCum" + (nYears + curCum + 1) + " gt length" + resI.length
                                              : porsStart < 0 ? "porsStart=" + porsStart + " is less than 0 E.P"
                                                      : porsStart > 1 ? "porsStart=" + porsStart + " is greater than 1 E.S"
                                                              : porsEnd < 1 ? "porsEnd=" + porsEnd + " is less than 1 E.P+1"
                                                                      : porsEnd > 2 ? "porsEnd=" + porsEnd + " is greater than 2 E.S+1"
                                                                              : clanStart < 0 ? "clanStart=" + clanStart + " is less than 0"
                                                                                      : clanStart > 4 ? "clanStart=" + clanStart + " is greater than 4"
                                                                                              : clanEnd < 1 ? "clanEnd=" + clanEnd + " is less than 1"
                                                                                                      : clanEnd > 5 ? "clanEnd=" + clanEnd + " is greater than 5"
                                                                                                              : "");
      if (anErr.length() > 0) {
        throw (new MyErr("ERR: " + anErr + " stats#:" + rn + ":" + resS[rn][0]
        ));
      }
    } // if DEBUG
    double ave = 0., sum = 0., val = 0.;
    int units = 0, sumUnits = 0;
    int mPors = 0, nClan = 0, iSum = 0;
      for (mPors = porsStart; mPors < porsEnd; mPors++) {
        for (nClan = clanStart; nClan < clanEnd; nClan++) {
          for (int yIx = 0; yIx < nYears; yIx++) {
            int lll = 0;
            wasHere = "getCurCumPorsClanAve rn" + rn + " curCum" + curCum + " yIx" + yIx + " mPors" + mPors + " nClan" + nClan + " cnt" + lll++;
            long[][][] rrr = resI[rn];
            long[][] ccc = rrr[curCum];
            long[][] ccc1 = rrr[curCum + yIx];
            long[] ppp = ccc1[mPors];
            long clll = ppp[nClan];
            if ((units = (int) resI[rn][curCum + yIx][mPors][nClan]) > 0) {
              sumUnits += units;
              sum += val = resV[rn][curCum + yIx][mPors][nClan];
            }
          }
        }
      } // mPors

    return units > 0 ? sum / units : 0.;
  }
    }
  /**
   * get a val min from the stats database, it could be for the current year or
   * for the cunulative mins of all the years
   *
   * @param rN the index into the stats database<br>
   * use getStatrN(name) as rN to select by string name
   * @param curCum either ICUM or ICUR0 from EM
   * @param porsStart 0:start with planets, 1 start with ships
   * @param porsEnd 1. 0:1 sum just planets, 2. 0:2 sum planets & ships
   * @param clanStart 0-4 sum of the clan to start with
   * @param clanEnd 1-5 end of clan sum, 0:1,1:2 etc. 1 clan<br>
   * 0:5 sum all of the clans
   * @return the min of values as filtered by the selectors
   */
  double getCurCumPorsClanValMin(int rn, int curCum, int porsStart, int porsEnd, int clanStart, int clanEnd) {
    synchronized(syncRes){
    String anErr = "";
    if (E.PAINTDISPLAYOUT) {
      anErr = (curCum < ICUM ? "curCum=" + curCum + " is less than ICUM"
              : curCum > ICUR0 ? "curCum " + curCum + " is greater than ICUR0"
                      : porsStart < 0 ? "porsStart=" + porsStart + " is less than 0 E.P"
                              : porsStart > 1 ? "porsStart=" + porsStart + " is greater than 1 E.S"
                                      : porsEnd < 1 ? "porsEnd=" + porsEnd + " is less than 1 E.P+1"
                                              : porsEnd > 2 ? "porsEnd=" + porsEnd + " is greater than 2 E.S+1"
                                                      : clanStart < 0 ? "clanStart=" + clanStart + " is less than 0"
                                                              : clanStart > 4 ? "clanStart=" + clanStart + " is greater than 4"
                                                                      : clanEnd < 1 ? "clanEnd=" + clanEnd + " is less than 1"
                                                                              : clanEnd > 5 ? "clanEnd=" + clanEnd + " is greater than 5"
                                                                                      : "");
      if (anErr.length() > 0) {
        throw (new MyErr("ERR: " + anErr + " stats#:" + rn + ":" + resS[rn][0]
        ));
      }
    } // if DEBUG
    double vMin = 999999999999999999.;
    int mPors = 0, nClan = 0, iSum = 0;
    for (mPors = porsStart; mPors < porsEnd; mPors++) {
      for (nClan = clanStart; nClan < clanEnd; nClan++) {
        vMin += resV[rn][curCum][mPors][nClan] < vMin ? resV[rn][curCum][mPors][nClan] : vMin;
      }
    }
    return vMin;
  }
  }
} // Class EM
