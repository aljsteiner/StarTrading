/*
 Copyright (C) 2012 Albert Steiner
 Copyright (C) 2024 Albert Steiner

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

 Assets holds the goodies for each economy.  The economies are divided into 7
 sectors, E.lsecs,  The log displays only hold 7 sectors width.  Assets hold the values that continue from one year to the next, it also hold temporary values between the multiple times that CashFlow is instantiated for bartering with different ecconomies, and finally to swap or repurposing values from one financial sector to another and determining the possible survival and possible growth of an economy.  Year temporary values are deleted at the end of year in method Assets.yearEnd in particular the CashFlow instance and all inner class instances are deleted, any remembered values must be kept in Assets, preparing Assets for starting the next year.

 Assets contains a subClass  CashFlow  which contains the yearly processing of assets.  CashFlow instances are also used to contain values for previous years and previous "n" in the swap or rearrange asset sectors process.

 CashFlow  contains subclasses SubAssets for the resource and reserved resoure cargo, and for staff and reserved staff guests.  In addition, CashFlow contains the subclass Trades, this class uses many members of CashFlow, but is only instantiated during the trading process this economy and another economy.  Within the game no more than 2 Economies are in the process of trade at any one time.

Assets.CashFlow.DoTotalWorths saves worths for a later comparison.

Assets.CashFlow.HSwaps holds history of swaps to be used in an udo,redo of the swap over with different vals.

The game attempts to minimize the storage used by the game by allocatting full storage for no more than two economies at a time.
 */
/** EM, E, StarTrader contains this set of stats descriptors
 *
 * static final public String statsButton0Tip = "0: Cum Game Worths,";
 * static final public String statsButton1Tip = "1: cum Favors and trade effects";
 * static final public String statsButton2Tip = "2: catastrophes, deaths, randoms, forwardfund";
 * static final public String statsButton3Tip = "3: deaths. trades acc";
 * static final public String statsButton4Tip = "4: deaths, Rej misd Trades";
 * static final public String statsButton5Tip = "5: trades accepted, rejected, missed ";
 * static final public String statsButton6Tip = "6: forwardFunds, deaths";
 * static final public String statsButton7Tip = "7: Resource, staff, knowledge values";
 * static final public String statsButton8Tip = "8: creates. growth, forwardFunds and costs details";
 * static final public String statsButton9Tip = "9: Catastrophes, Fertility, health and effects";
 * static final public String statsButton10Tip = "10: list by ages deaths with trades missed, rejected, lost";
 * static final public String statsButton11Tip = "11: list by ages deaths with trades accepted ";
 * static final public String statsButton12Tip = "12: list by ages deaths with negative prospects";
 * static final public String statsButton13Tip = "13: list by ages affects with growths depreciation";
 * static final public String statsButton14Tip = "14: list by ages affects with catastrophies, forwardFunds ";
 * static final public String statsButton15Tip = "15: list by ages live trades";
 * static final public String statsButton16Tip = "16: list by ages worths, work, faculty,research interns";
 * static final public String statsButton17Tip = "17: list by ages helps, creations ";
 * static final public String statsButton18Tip = "18: Swaps years xfer skips, redos and dos";
 * static final public String statsButton19Tip = "19: Swaps years Forward Fund imbalance or save";
 * static final public String statsButton20Tip = "20: TB assigned";
 * static final public String statsButton21Tip = "21: TB assigned";
 * static final public String statsButton22Tip = "22: TB assigned";
 * static final public String statsButton23Tip = "23: display table";
 *
 */
/* 0:worths,1:trade favor,2:random,crisis,deaths,forward,3:deaths,4:trades,5:creates,6:forwardFund,7:resource,staff,knowledge,8:growth,costs,9:Fertility,health,effects,10 11 12 13 14 1years,15:swaps,16:swapincr,17:swapdecr,18:xfer, 19:swap forwardFund balance orsave, 20:Swaps cum
 */
package trade;

import java.io.UnsupportedEncodingException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import org.junit.platform.engine.support.hierarchical.Node;

/**
 *
 * @author albert Steiner
 */
public class Assets {

  Assets as0;
  StarTrader st;
  String name;  //name of econ
  Econ ec;
  EM eM;
  ;
  CashFlow cur;
  int clan;
  int pors;
  // int oclan;
  //int opors;
  int oClan = -5, oPors = -6;  //in Assets preInstantiation
  // int year;  // copy of eM.year
  int myEconCnt;

  //Assets
  static final boolean subAssetsIsStaff[] = {false, false, true, true};
  static final boolean subAssetsIsReserve[] = {false, true, false, true};
  // now gather some cumulative counts and statistic
  static final int[] alock1 = {0};
  static final int[] alock2 = {0};
  static final int[] alock3 = {0};

  //Assets AI pointers into   //Assets AI pointers into  moved to EM
  // static int pTradeFrac = -1;
  static int pUserCatastrophyFreq = -1;
  /* now add some AI variables for this economy */
  // consolidate key's reading adding cnt, greatest age up to 51, greates scoreIx
  int cnts[] = new int[3]; // cnt=occurrances,age>>age of Econ,scoreIx>>scoreix
  // use Double to catch infinity and nan
  Double aiERScore = 2., prevAIERScore = -3., prevPrevAIERScore = -4.;
  Double aiERScoreI = -5., prevAIERScoreI = -5., prevPrevAIERScoreI = -3.;
  Double aiEScore = 2., prevAIEScore = -3., prevPrevAIEScore = -4.;
  Double aiEScoreI = -5., prevAIEScoreI = -5., prevPrevAIEScoreI = -3.;
  Double aiScore = 2., prevAIScore = -3., prevPrevAIScore = -4.;
  Double aiScoreI = -5., prevAIScoreI = -5., prevPrevAIScoreI = -3.;
  Double aiWorth = -11., prevAIWorth = -12., prevPrevAIWorth = -13.;
  Double prevAIWorthI = -14., aiWorthI = -11.;
  static final int nudV = 0;
  static final int nudSet = 1;
  static final int nudBoth = 2;
  static final int nudStrt = 3;
  static final int nudChgs = 5;
  static final int nudLen = 8;
  double tradeFracNudge[] = {0., 0., .0, .009, .012, 0.015, 0.018, 0.021};//tradeFrac dif  .43-.73::.2--.5   *.003
  double ffTFracNudge[] = {0., 0., .0, 0.042, 0.056, 0.070, 0.084, 0.098};  //futureFundTransferFrac 3.0--5.4  014
  static final double bMin = 11.; //if bCnt is less than bCnt<bMax?-999999:bVal/bCnt
  static final double bSmall = -.0999;// if setCntDr < bSmall treat as invalid
  double aiNudges[][] = {tradeFracNudge, ffTFracNudge};
  int ranInt = -7, rIn = -9;
  int aiPos = -7, prevAIPos = -7, prevPrevAIPos = -7;
  Boolean acct = false; // saveAI set this to the last year tradeAccepted
  Boolean y = true;
  // values without prefix prev are last years values
  double aiOffer = -10., prevAIOffer = -11., prevPrevAIOffer = -11., prevAIOfferI;
  double aiProsM = -12., aiProsA = -15., aiProsI = -3., aiProsAI = -9., aiProsMI = -9.;
  double prevAIProsM = -8., prevAIProsA = -11., prevAIProsI = -11., prevAIProsMI = -7.;
  double prevAIProsAI = -7., prevPrevAIProsM = -8., prevAIPrevProsA = -11.;
  double prevPrevAIProsI = -7.;
  int sliderVala = -15, prevSliderVala = -17, sliderValb = -9, prevSliderValb = -19;
  double aiOper = -7., prevAIOper = -9.;

  //double prevAIOffer = -5., prevAIOfferI = -5.;
  //double prevPrevAIOffer = -7., prevIncAIOperW = -9., prevAIOperW = -9.;
  // int sliderValc = -15, prevSliderValc = -17, sliderVald = -9, prevSliderVald = -19;
  double prevAIKnowledge = -3., aiKnowledgeA = -7., aiKnowledgeI = -17.;
  double prevPrevAIKnowledge = -1., prevAIKnowledgeI = -3., prevPrevAIProsA = -7.;

  // double aiKnowledge = 3., aiKnowledgeInc = 7.;
  // double prevAIResilience = -8., aiResilience = 8., prevIncAIResilience = -7.;
  // double prevPrevAIResilience = -3., hope = 7., prevAIHope = -9., prevPrevAIHope = -7.;
  // double prevIncAIHope = -7., aiHope = -6.;
  // trade values kept in assets
  double strategicGoal = 0., rGoal0 = 0., strategicValue = 0., goodFrac = 0.;
  double prevStrategicGoal = 0, prevStrategicValue = 0., prevGoodFrac = 0.;
  double prevPrevStrategicGoal = 0, prevPrevStrategicValue = 0., prevPrevGoodFrac = 0.;
  double sf = 0., sv = 0.;
  static double firstStrategicGoal = 0., firstStrategicValue = 0.;
  // String myAIvalC = "soon";
  // String myAIbalances = "1234567";
  // String myAIprosperity = "1234567";
// String myAIjoys = "1234567";
  int prevScorePos = -1;
  double prevClanScore = -0.1;
  // volatile static char[] EM.psClanChars[pors][clan] = {0, 0}; // extended in eM.buildMyAICvals
  volatile static String myAICvals = "coming soon"; // converted for EM.psClanChars[pors][clan]
  //Character myChars[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};
// Assets these AI entries have  2types, 2PS , 5Clan, 9much, 3sVal,3rRes
  static volatile int aEntries[] = {0, 0, 0, 0, 0}; // 2typein CashFlow.yearEnd()
  static volatile int aWaits = 0; //CashFlow.yearEnd()
  // static volatile int aTAMEntries[][][] = {{{0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}}, {{0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}}, {{0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}}, {{0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}}}; // 2type, 2acct, 2 much
  // static volatile int aATEntries[][] = {{0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}}; // 2type, 7 much
  // static volatile int aPSMEntries[][][][] = {/*type*/{/*ps*/{/*acct*/{/*much*/0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}}, {/*clan*/{/*much*/0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}}}, {/*ps*/{/*acct*/{/*much*/0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}}, {/*clan*/{/*much*/0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}}}};//  2type 2acct 2Ps 7MUCH
  // static volatile int bClanEntries[][][][][] = {{/*type*/{/*PS*/{/*clan*/{/*much*/0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}}, {/*clan*/{/*much*/0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}}}, {/*PS*/{/*clan*/{/*much*/0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}}, {/*clan*/{/*much*/0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0/*clan*/}/*PS*/}/*type*/}/*much*/}, {/*type*/{/*PS*/{/*clan*/{/*much*/0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}}, {/*clan*/{/*much*/0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}}}, {/*PS*/{/*clan*/{/*much*/0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}}, {/*clan*/{/*much*/0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0/*clan*/}/*PS*/}/*type*/}/*much*/}}; //2type, 2acct, 2PS,5Clan,7much
//  char[] aiMask1 = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
  static int aType = 0, aPorS = 0, aClan = 0, aSize = 0, aMuch = 0, aCnts = 0;
  static int aKeys = 0, aNums = 0;
  String aKey = "";
  Integer aVal[] = {1, -0, 0, 0};
  static final int nTypes = 3; //accepted/also,rejected/lost,missed
  static final int nMuches = 9;
  static final int nSVals = 4; // n source values Wealth, other off/w,prevStratGoal
  static int[] sVals = new int[nSVals]; // nMuches divisions

  int aSVal = 1, aRRes = 1;
  static final int nRRes = 3; // number of result pointer to values in array
  static int[] rRes = new int[nRRes];// pointers to result kept nMuches division
  static volatile int aClanEntries[][][][][][];

  static volatile int atEntries = 0; //traded active, failed or lost
  static volatile int atPSEntries[] = {0, 0};
  static volatile int atClanEntries[][] = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
  static volatile float cumOffersPerWorth = 0;
  static volatile float[] cumOffersPerWorthPS = {0, 0};
  static volatile float[][] cumOffersPerWorthClan = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
  static volatile float cumOffers = 0;
  static volatile float[] cumOffersPS = {0, 0};
  static volatile float[][] cumOffersClan = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
  static volatile float cumBTWorth = 0;
  static volatile float[] cumBTWorthPS = {0, 0};
  static volatile float[][] cumBTWorthClan = {{0, 0, 0, 0, 0}, {0, 0, 0, 0, 0}};
  boolean dead = false;  //CFyearEnd if rawProspects2.min < 0 = true
  boolean sos = false;
  boolean didGoods = false;
  //Assets do deterioration in calcGrowth only first time each year, set false in endYear
  boolean iCF = false; // did initCashFlow;
  boolean didInitRawProspects = false;
  boolean didStart = false; //CF.start=true CF.yearEnd=false;
  boolean didDepreciation = false; // CF.SA calcGrowth=true, CFyearEnd=false
  boolean didCashFlowStart = false; //CF=true CFyearEnd=false
  boolean didCashFlowInit = false; //assetsInit.CF=true no false
  boolean didCatastrophy = false; // set in Assets.CashFlow.calcCatastrophy
  boolean endYearEnd = false; //CFyearEnd=true
  boolean assetsInitialized = false; //assetsInit=true
  double health = 2.;
  double sumTotWorth = 0.; // sum of SubAsset worth + cash + knowledge

  String otherName;   // blank at year start, name of trading partner
  ArrayList<History> hist;
  //double[] sectorPri;
  String aPre = "@A";
  static final int LSECS = E.LSECS;
  static final String[] aChar = E.aChar; //{"r", "c", "s", "g"};
  static String[] rcsg = aChar;
  static String[] aNames = E.aNames; //{"resource", "cargo", "staff", "guests"};
  static String[] rcsqName = E.aNames;
  static int[] spluss = E.spluss; //{0, 0, LSECS, LSECS};
  static String[] sChar = E.sChar; //{"r", "r", "s", "s"};
  static String[] rcNsq = E.rcNsq; //{"rc", "sq"};
  static String[] rNc = E.rNc; //{"r", "c"};
  static String[] sNg = E.sNg; //{"s", "g"};
  static String[] rNs = E.rNs; //{"r", "s"};
  static String[] cNg = E.cNg; //{"c", "g"};
  static int[] rorss = E.rorss; //{0, 0, 2, 2};  // resorce or staff, also place or costs see
  static int[] d01 = E.d01; //{0, 1};
  static int[] balsIxA = E.balsIxA; //{0, 1, 2, 3};
  static final int[] IA01 = {0, 1};
  static final int[] A01 = {0, 1};
  static final int[] MR = {0, LSECS};
  static int[] dlsecs = E.alsecs;
  static final int[] ASECS = E.alsecs;
  static int[] d2lsecs = E.a2lsecs;
  static final int[] I2ASECS = E.a2lsecs;
  static int[] d25 = {2, 3, 4, 5};
  static final int[] IA25 = d25;
  static final int[] IA03 = {0, 1, 2, 3};
  static final int[] IA4 = IA03;
  static final double NZERO = E.NZERO;
  static final double PZERO = E.PZERO;
  static final int[] ms = {0, 1};
  static final int[] mr = {0, E.lsecs};
  static final int aDl = 9;
  int secCnt = 1;// default count for setSats

  public enum SwpCmd {
    NOT("nothing assigned"), NONE("no Cmd"), NOOP("no operation"), GROW("in grow section"), HEALTH("Health swapping"), UINCR("un Incr"), RSINCR("pre Incr"), SINCR("increase staff guest to staff"), SINCR1("second incr staff G to S"), SINCR2("3 incr staff G to S"), SINCR3("4 incr staff G to S"), RINCR("increase available from cargo"), RINCR1("another increase available C to A"), RINCR2("another increase available C to A"), RINCR3("another increase available C to A"), RSINCR1("seconPre Incr"), /* AXINCR("increase available by transmute from another resource Cargo or Avail"), */ UDECR("un Decr"), RSDECR("pre DECR"), SDECR("decrease a large staff to Guests to help another resource staff"), RDECR("decrease large resource to help another resource"), RDECR1, RDECR2, RDECR3, SDECR1, SDECR2, SDECR3, UNXDECR("un XDECR"), RFUTUREFUND, REMERGFUTUREFUND, SFUTUREFUND, SEMERGFUTUREFUND, RFUTUREFUND1, REMERGFUTUREFUND1, SFUTUREFUND1, SEMERGFUTUREFUND1, RFUTUREFUND2, REMERGFUTUREFUND2, SFUTUREFUND2, SEMERGFUTUREFUND2, RFUTUREFUND3, REMERGFUTUREFUND3, SFUTUREFUND3, SEMERGFUTUREFUND3, RSXDECR("pre XDECR"), RXDECR("swap resources/staff between sectors, high resource cost"), RXDECR1("Second RXDECR"), RXDECR2("Second RXDECR"), RXDECR3("Second RXDECR"), SXDECR("swap resource with high staff costs"), SXDECR1("Second SXDECR"), SXDECR2("Second SXDECR"), SXDECR3("Second SXDECR"), TXDECR("reverse charge r sector"), TXDECR1, TXDECR2, TXDECR3, UXDECR, UXDECR1, UXDECR2, UXDECR3, TRADE("prepare for Trade"), TRADEG("Trade Guests between Planet and Ship"), TRADEC("Trade cargo between Ship and Planeet");
    private String desc;
    private double val;
    private double low, high;
    private String desTip;

    private SwpCmd() {
      this.desc = "";
    }

    private SwpCmd(String desc) {
      this.desc = desc;
    }

    private SwpCmd(String desc, double ival, double plow, String dtip, Consumer sav) {
      sav.accept(ival);
    }

    public int n() {
      return ordinal();
    }

  }

  SwpCmd[][] tst = incrs;
  static SwpCmd[][] incrs = {{SwpCmd.RINCR, SwpCmd.RINCR1, SwpCmd.RINCR2, SwpCmd.RINCR3}, {SwpCmd.SINCR, SwpCmd.SINCR1, SwpCmd.SINCR2, SwpCmd.GROW.SINCR3}};
  static SwpCmd[][] decrs = {{SwpCmd.RDECR, SwpCmd.RDECR1, SwpCmd.RDECR2, SwpCmd.RDECR3}, {SwpCmd.SDECR, SwpCmd.SDECR1, SwpCmd.SDECR2, SwpCmd.SDECR3}};
  static SwpCmd[][] futureFunds = {{SwpCmd.RFUTUREFUND, SwpCmd.REMERGFUTUREFUND, SwpCmd.RFUTUREFUND1, SwpCmd.REMERGFUTUREFUND1, SwpCmd.RFUTUREFUND2, SwpCmd.REMERGFUTUREFUND2, SwpCmd.RFUTUREFUND3, SwpCmd.REMERGFUTUREFUND3}, {SwpCmd.SFUTUREFUND, SwpCmd.SEMERGFUTUREFUND, SwpCmd.SFUTUREFUND1, SwpCmd.SEMERGFUTUREFUND1, SwpCmd.SFUTUREFUND2, SwpCmd.SEMERGFUTUREFUND2, SwpCmd.SFUTUREFUND3, SwpCmd.SEMERGFUTUREFUND3}};
  static SwpCmd[][][] dlecrs = {decrs, futureFunds};
  static SwpCmd[][] xdecrs = {{SwpCmd.RXDECR, SwpCmd.RXDECR1, SwpCmd.RXDECR2, SwpCmd.RXDECR3}, {SwpCmd.SXDECR, SwpCmd.SXDECR1, SwpCmd.SXDECR2, SwpCmd.SXDECR3}};
  static SwpCmd[][] udecrs = {{SwpCmd.TXDECR, SwpCmd.TXDECR1, SwpCmd.TXDECR2, SwpCmd.TXDECR3}, {SwpCmd.UXDECR, SwpCmd.UXDECR1, SwpCmd.UXDECR2, SwpCmd.UXDECR3}};
  static SwpCmd[][][] uxdecrs = {xdecrs, udecrs};

  EnumSet<SwpCmd> setTst = setIncr;
  static EnumSet<SwpCmd> setIncr = EnumSet.of(SwpCmd.RINCR, SwpCmd.SINCR, SwpCmd.RINCR1, SwpCmd.SINCR1, SwpCmd.RINCR2, SwpCmd.SINCR2, SwpCmd.RINCR3, SwpCmd.SINCR3);
  static EnumSet<SwpCmd> setDecr = EnumSet.of(SwpCmd.SDECR, SwpCmd.SDECR1, SwpCmd.SDECR2, SwpCmd.SDECR3, SwpCmd.RDECR, SwpCmd.RDECR1, SwpCmd.RDECR2, SwpCmd.RDECR3);
  static EnumSet<SwpCmd> setRDecr = EnumSet.of(SwpCmd.RDECR, SwpCmd.RDECR1);
  static EnumSet<SwpCmd> setSDecr = EnumSet.of(SwpCmd.SDECR, SwpCmd.SDECR1);
  static EnumSet<SwpCmd> setXXdecr = EnumSet.of(SwpCmd.RXDECR, SwpCmd.RXDECR1, SwpCmd.RXDECR2, SwpCmd.RXDECR3, SwpCmd.SXDECR, SwpCmd.SXDECR1, SwpCmd.SXDECR2, SwpCmd.SXDECR3, SwpCmd.TXDECR, SwpCmd.TXDECR1, SwpCmd.TXDECR2, SwpCmd.TXDECR3, SwpCmd.UXDECR, SwpCmd.UXDECR1, SwpCmd.UXDECR2, SwpCmd.UXDECR3);
  static EnumSet<SwpCmd> setRXdecr = EnumSet.of(SwpCmd.RXDECR, SwpCmd.RXDECR1);
  static EnumSet<SwpCmd> setSXdecr = EnumSet.of(SwpCmd.SXDECR, SwpCmd.SXDECR1);
  public static final int swpIncrv[] = {SwpCmd.RINCR.n(), SwpCmd.SINCR.n(), SwpCmd.RINCR1.n(), SwpCmd.SINCR1.n()};
  static final int swpXXdecr[] = {SwpCmd.RXDECR.n(), SwpCmd.RXDECR1.n(), SwpCmd.SXDECR.n(), SwpCmd.SXDECR1.n()};
  static final int swpSXdecr[] = {SwpCmd.SXDECR.n(), SwpCmd.SXDECR1.n()};
  static final int swpRXdecr[] = {SwpCmd.RXDECR.n(), SwpCmd.RXDECR1.n()};
  public static final int swpSIncrv[] = {SwpCmd.SDECR.n()};
  public static final int swpDecrv[] = {SwpCmd.SDECR.n()};
  public static final int swpADecrv[] = {SwpCmd.RINCR.n(), SwpCmd.RINCR1.n()};
  public static final int swpSDecrv[] = {SwpCmd.SDECR.n(), SwpCmd.SINCR.n(), SwpCmd.SINCR1.n()};
  // !swpXXdecrs.contains(cmd.n())
  static Set swpXXdecrs = new HashSet(Arrays.asList(swpXXdecr));
  static Set swpSXdecrs = new HashSet(Arrays.asList(swpSXdecr));
  static Set swpRXdecrs = new HashSet(Arrays.asList(swpRXdecr));
  public static Set swpIncrs = new HashSet(Arrays.asList(swpIncrv));
  public static Set swpDecrs = new HashSet(Arrays.asList(swpDecrv));

  // Assets  range 0. - 1.
  static final double decrMostGapMult[] = {1 / (14 * 16), 1 / (14 * 15), 1 / (14 * 14), 1 / (14 * 13), 1 / 14 * 1 / 12, 1 / 14 * 1 / 11, 1 / 14 * 1 / 10, 1 / 14 * 1 / 9};

  // The following Ix are 2 to 18 are starting indexs for the 4 SubAsset Ix
  // A6Row[] needsArray;
  ARow aSectorPriority;
  ARow difficulty;
  double iwealth;
  double wealth;
  double colonists;
  double cash = 0.;
  double res;
  double aknowledge;
  double percentDifficulty;
  // int[] sLoops = new int[E.hcnt];  //count of swap loops (health only);
  static int maxn = 40;
  int i = -4, j = -4, k = -4, l = -4, m = -4, n = -4, splus = -4, term = -4;
  int rowIx = -4, secIx = -4;
  double clanRisk;

  A2Row initRawProspects2;   // Assets variables

  double econsCnt;
  double worthIncrPercent = 0.;
  double worthIncr = 0.;

  static double[][][] maintRequired = E.maintRequired;
  static double[][][] mxCosts = E.maintCost;
  static double[][][] tCosts = E.shipTravelLightyearCostsBySourcePerConsumer;
  static double[][][] gReqs = E.resourceGrowthRequirementBySourcePerConsumer;
  static double[][][] gCosts = E.resourceGrowthCostBySourcePerConsumer;
  double rs[][][]; //[5 reqM reqG m t g][2 r s][4 rcsg]
  double[] trand;   // reference to random numbers in Econ
  History h1, h2, h3, h4, h5, h6, h7, h8; // pointers to history lines

// Assets variables last trade value if multiple trades this year
  // int term = -4;  // term or level of trade
  double preTradeSum4 = 0., preTradeAvail = 0., postTradeSum4 = 0., postTradeAvail = 0.;
  int tradedSuccessTrades; // successful trades this year
  double tradedStrategicRequests; // sum of strategic requests
  double tradedStrategicOffers; // sum of strategic offers
  double tradedOffers = -.5; // sum of strategic offers
  double tradedPrevOffers = -.5; // sum of strategic offers
  double tradedPrevPrevOffers = -.5; // sum of strategic offers
  double tradedNominalRequests;// sum of nominal requests
  double tradedNominalOffers; // sum of nominal offers
  double tradedTotalStrategicRequests; // sum of strategic requests+cash+manuals
  double tradedRequests = -7.;
  double tradedTotalStrategicOffers; // sum of strategic offers+cash+manuals
  double tradedTotalNominalRequests;// sum of nominal requests+cash+manuals
  double tradedTotalNominalOffers; // sum of nominal offers+cash+manuals
  double tradedManualsWorths;  // worth of manuals received in trades

  // if multiple ships trade in a year, this is for the last ship
  int tradedShipOrdinal = 0; // count of ships traded this year
  int visitedShipOrdinal = 0;
  int econVisited = 0; // count of econs trying trade this year
  String tradingShipName = "none";
  int yearTradeAccepted = -20;
  int yearTradeRejected = -10;
  int yearTradeLost = -20;
  int yearTradeMissed = -20;
  int yearCatastrophy = -20;
  int yearSwapForwardFundEmergency = -20;
  int yearSOS = -20;
  int yearCreated = -20;
  int lastAcceptedYear = -20;  // set near end of endYear
  int prevNotAcceptedYear = -20;
  boolean newTradeYear1 = false; // set by Assets.barter

  // save ship maint and travel cost of pre barter, for year end costs
  boolean newTradeYear2 = false; // set after maint  & travel saved

  // in Assets
  int sumGradesUp = 0;
  double iyWTotWorth = 0., syWTotWorth = 0., btWTotWorth = 0., tWTotWorth = 0.;
  double preSwapWorth = 0.;
  // save by yearEnd for years and increment by years
  int yrTradesStarted = -20;  // -1 if no trade this year, set in yearEnd
  int yrTradedMissedStarted = -20;
  double worthFirstTradedYear = -70.;
  double worthFirstMissedYear = -50.;
  A2Row tradeStrategicVars; // goes to Assets and Econ reset to null
  A2Row tradeGoodsNeeds; // goes to Assets and Econ reset to null
  A10Row tradeTravelCosts10;
  A10Row tradeTravelMaintCosts10;
  double sumTradeTravelCosts = 0.;
  double sumTradeTravelMaintCosts = 0;
  double sumTrade1YearTravelMaintCosts = 0.0;
  // int[] tradedShipAccepted = new int[E.hcnt];
  int lTradedEcons = 20;
  Econ[] oTradedEcons = new Econ[lTradedEcons];
  int oTradedEconsNext = 0;
  double fav = -5, oFav = -5;  //in assets
  double tradedFav = -4;
  double tradedOFav = -4;
  /*
  double tradedFirstStrategicReceipts;
  double tradedFirstReceipts;
  double tradedFirstSends;
  double tradedFinalStrategicReceipts;
  double tradedFinalReceipts;
  double tradedFinalSends;

   */
  double tradedFirstNegProspectsSum = 0.;
  // double[] tradedGoodBal = new double[E.hcnt];
  // double[] tradedGoodWorth = new double[E.hcnt];
  boolean tradeAccepted = false;  // in Assets
  boolean tradeRejected = false;
  boolean tradeLost = false;
  boolean tradeMissed = false; // no trade tried
  // int acceptedTrade = -5;  // barter number of tradeOK
  // int rejectedTrade = -6; // barter number of rejected trad
  A2Row tradedBid;
  double tradedStrategicValue;
  double tradedStrategicFrac;
  ARow tradedMoreManuals;
  double lightYearsTraveled = 0.;
  //in Assets preInstantiation
  // up to 10 visited ship names
  String visitedShipNames[][] = {{"A", "B", "C", "D", "E", "f", "g", "h", "i", "j"}, {"A", "B", "C", "D", "E"}, {"A", "B", "C", "D", "E"}, {"A", "B", "C", "D", "E"}, {"A", "B", "C", "D", "E"}};
  //double strategicGoal = 0., rGoal0 = 0., strategicValue = 0., goodFrac = 0.;
  // double firstStrategicGoal=0.,firstStrategicValue = 0.;
  double endTradeWorth = -200.;
  /**
   * in trade.Assets bids positive are offers, negative are requests, positive
   * are what I can give, negative what I can send
   */
  A2Row bids;
  int lev = History.loopMinorConditionals5;

  // Assets preInstantiation permanent values, flags and unit values
  ABalRows bals;

  A6Row balances; // balances
  A6Row cashFlowSubAssetBalances; // assume no recreate of each ARow
  A6Row growths;  // Subset of ABalRows
  A6Row cashFlowSubAssetsGrowths;
  A6Row mtgNeeds6;  //needs are positive, avails negative  Assets
  A6Row cashFlowSubAssetUnitsNeededToSurvive;
  A6Row mtgAvails6;// the negative of mtgNeeds6
  A6Row LimitedBonusYearUnitGrowth; // for Assets.CashFlow.SubAssets.calcGrowth

  A6Row cashFlowSubAssetUnitsAvailableToSwap;
  A2Row rawFertilities2;
  A2Row rawProspects2;
  A2Row prevProspects2;
  A6Row prevBalances;
  A6Row invMEfficiency;
  A6Row invGEfficiency;
  //more Assets definitions
  ARow commonKnowledge;
  ARow newKnowledge;
  ARow knowledge;
  ARow manuals;
  ARow moreK; // in doGrow incr knowledge
  ARow lessM; // in doGrow The manual made commonKnowledge
  ARow ydifficulty;
  double initialSumWorth = -200.;
  double startYrSumWorth = -200.;
  double prevYrSumWorth = -300.;
  double initialSumKnowledge, prevYrSumKnowledge, initialSumKnowledgeWorth;
  double startYrSumKnowledge, startYrSumKnowledgeWorth, prevYrSumKnowledgeWorth;

  // Assets years future fund, only done in yearEnd method
  //Assets forward fund, zero at start of end, stat at end of end
  // double resEmergencyFutureFundAssigned = 0.;
  // double staffEmergencyFutureFundAssigned = 0.;
  //double resEmergencyFutureFundRequired = 0.;
  // double staffEmergencyFutureFundRequired = 0.;
  double yearsFutureFund = 0.;
  double emergeFutureFund = 0;
  double excessFutureFund = 0;
  int yearsFutureFundTimes = 0;
  String resTypeName = "anot";
  double rsval1 = 0., rsval2 = 0.;
  Double rsval = 0.;
  // a place for local Assets references to local EM. first installation values
  String[][] resS; //space for desc, comment
  double[][][][] resV;
  long[][][][] resI;
  // int rende3 = 700;  // Assets for setStats
  //int lStatsWaitList = 10;
  // String[] statsWaitList;
  int ISSET, ICUR0, MAXDEPTH, ICUM, CCONTROLD;

  long doYearTime;
  double resFutureFundRequired = 0.;
  double staffFutureFundRequired = 0.;
  double resRorwardFundAssigned = 0.;
  double staffFutureFundAssigned = 0.;
  double totalFutureFundAssignedj = 0;
  double remainingFF = 0., excessForFF = 0.;

  double poorKnowledgeAveEffect = 4., poorHealthAveEffect = 1.5;
  int ixWRSrc = -2;  // 0,1 source balances, bals Working and Reserved, rc sg
  int ixWSrc; // 2,4 source Working ARows index ixWRSrc *2 + 2
  int ixRSrc; // 3,5 source Reserved ARows index, ixWRSrc*2 + 3
  // warning the following duplicates A6Rowa or ABalRows and must not be changed
  private static final int TCOST = A6Rowa.tcost;
  private static final int TBAL = A6Rowa.tbal;
  private static final int RIX = ABalRows.RIX, CIX = ABalRows.CIX, SIX = ABalRows.SIX;
  private static final int GIX = ABalRows.GIX, SGIX = ABalRows.SGIX, RCIX = ABalRows.RCIX;
  // warning the following duplicates ABalRows and must not be changed
  private static final int RCIX2 = ABalRows.RCIX2, LSUMS = ABalRows.LSUMS;
  ;
  private static final int BALANCESIX = ABalRows.BALANCESIX, LSUBS = ABalRows.LSUBS;
  private static final int GROWTHSIX = ABalRows.GROWTHSEFFIX;
  private static final int BONUSUNITSIX = ABalRows.BONUSUNITSIX;

  private static final int DEPRECIATIONIX = ABalRows.DEPRECIATIONIX;
  private static final int BALSLENGTH = ABalRows.BALSLENGTH;
  private static final int balancesSums[] = {BALANCESIX + RCIX, BALANCESIX + SGIX};
  private static final int balancesSubSum1[] = {BALANCESIX + RIX, BALANCESIX + CIX};
  private static final int balancesSubSum2[] = {BALANCESIX + SIX, BALANCESIX + GIX};
  private static final int balancesSubSums[][] = {balancesSubSum1, balancesSubSum2};
  Assets.CashFlow.DoTotalWorths syW, iyW, pyW; //predefine references to worths

  // choose lt values
  // double myAiPHELimits[] = {E.PZERO, 0.05, 0.3, 1.7};// 5 choice other
  //double[] myAIrawProspectsMinLimits = {E.PZERO, 0.1, 0.7, 3.0};
  //double[] myAIWorthGrowthLimits = {-.5, -.1, 0.5, 2.0};
  //char[] myAIjoy = {'a', 'b', 'c', 'd', 'e'};// PHE, rawProspectsMins, worthGrowth
  /**
   * The history versions of CashFlow are always copied to involving a new
   * HAssets(), than copy of the cur values that are of interest. The history
   * HCashFlow is a limited copy of CashFlow, only containing declarations of
   * members that will be used in some historical result or decision. the
   * SubAsset within HCashFlow is likewise limited
   */
  //CashFlow initial, started, noTrade, noTrade2;
  // CashFlow ysgLooped, ysgCosts, ysGrowed, ysEndyr, traded, growLooped, growCosts, growed, endyr;
  // ARow sectorPriority = new ARow(ec);
  // ARow priorityYr = new ARow(ec);
  enum yrphase {

    START, PRESEARCH, SEARCH, TRADE, DOLOOPS, SWAPING, GROW, PAY, HEALTH, END;

    int n() {
      return ordinal();
    }
  } // end yrphase

  yrphase yphase = yrphase.START;
  boolean em;  // print error message

  double tmin;  // temporary min

  NumberFormat dFrac;
  NumberFormat whole;
  NumberFormat dfo;

  /**
   * constructor to instantiate Assets
   *
   */
  public Assets() {
  }

  /**
   * initiator for Assets
   *
   * @param aeconCnt econ count
   * @param aaec hold Econ instance
   * @param stx holds StarTrader instance
   * @param eem holds EM instance
   * @param aaname name of economy
   * @param aaclan clan of economy
   * @param aapors 0==planet, 1==Ship
   * @param aahist history pointer
   * @param iwealth initial wealth before distribution
   * @param aawealth value of wealth
   * @param aasectorPri ARow of sector Priorities
   * @param aares number of resource units
   * @param aacolonists number of colonist units
   * @param aaknowledge units of common knowledge
   * @param aapercentDifficulty difficult percent
   * @param aatranda points to an array of random values between 0. - 2.
   *
   * Terminate with bals set to balances and other things set in assets, null
   * cur leave assetsInitialized
   */
  public void assetsInit(int aeconCnt, Econ aaec, StarTrader stx, EM aeM, String aaname, int aaclan, int aapors, ArrayList<History> aahist, double iwealth, double aawealth, ARow aasectorPri, double aares, double aacolonists, double aaknowledge, double aapercentDifficulty, double[] aatranda) {
    System.out.println("AssetsInit 357 start=" + aaname);
    as0 = this;
    dFrac = NumberFormat.getNumberInstance();
    whole = NumberFormat.getNumberInstance();
    dfo = dFrac;
    assert tradeFracNudge.length == nudLen : "error tradeFracNudge length=" + tradeFracNudge.length + " not=" + nudLen;
    assert ffTFracNudge.length == nudLen : "error ffTFracNudge length=" + ffTFracNudge.length + " not=" + nudLen;
    // define aClanEntries only once before any use, ignore clan
    /*
   static final int nTypes = 2;
  static final int nMuches = 9;
  static final int nSVals = 3; // n source values
  int aSval=1,aRRes=1;
  static int[] sVals = new int[nSVals]; // nMuches divisions
  static final int nRRes = 3; // number of result pointer to values in array
  static int[] rRes = new int[nRRes];// pointers to result kept nMuches division
  static volatile int aClanEntries[][][][][][]
     */
    int asIx = 0;
    if (aClanEntries == null) {
      aClanEntries = new int[nTypes][][][][][];
      for (aType = 0; aType < nTypes - 1; aType++) {
        aClanEntries[aType] = new int[2][][][][];
        for (asIx = 0; asIx < 2; asIx++) {
          aClanEntries[aType][asIx] = new int[2][][][];
          for (aPorS = 0; aPorS < 2; aPorS++) {
            aClanEntries[aType][asIx][aPorS] = new int[nSVals][][];
            for (aSVal = 0; aSVal < nSVals; aSVal++) {
              aClanEntries[aType][asIx][aPorS][aSVal] = new int[nRRes][];
              for (aRRes = 0; aRRes < nRRes; aRRes++) {
                aClanEntries[aType][asIx][aPorS][aSVal][aRRes] = new int[nMuches];
                for (aMuch = 0; aMuch < nMuches - 1; aMuch++) {
                  aClanEntries[aType][asIx][aPorS][aSVal][aRRes][aMuch] = 0;
                }
              }
            }
          }
        }
      }
    }
    //  EM.psClanChars[pors][clan] = new byte[E.bValsStart + eM.vvend]; // new array
    //  aiMask1 = new byte[E.bValsStart + eM.vvend];
    //   startYrs = new HCashFlow[7]; // might use instead of name 1,2 ...
    //   prevns = new HCashFlow[7];
    double sumPri = 0.;
    myEconCnt = aeconCnt;
    ec = aaec;
    st = stx;
    eM = aeM;
    name = aaname;
    yearCreated = eM.year;
    st.startYear = EM.doYearTime = (new Date().getTime());
    clan = aaclan;
    pors = aapors;
    hist = aahist;
    this.iwealth = iwealth;
    wealth = aawealth;
    trand = aatranda;
    aSectorPriority = aasectorPri;
    res = aares;
    colonists = aacolonists;
    aknowledge = aaknowledge;
    percentDifficulty = aapercentDifficulty;
    difficulty = new ARow(ec);
    ydifficulty = new ARow(ec);
    // move the definitions here so they can reference a defined Assets
    bals = new ABalRows(ec, ABalRows.BALSLENGTH, ABalRows.tbal, History.valuesMajor6, "bals");
    balances = new A6Row(ec, History.valuesMajor6, "balances");
    //. if (true) { // I think unneeded
    cashFlowSubAssetBalances = balances; // assume no recreate of each ARow
    growths = new A6Row(ec, History.valuesMajor6, "growths");
    cashFlowSubAssetsGrowths = growths;
    mtgNeeds6 = new A6Row(ec, lev, "mtgNeeds6");
    invMEfficiency = new A6Row(ec, lev, "invMEfficiency");
    invGEfficiency = new A6Row(ec, lev, "invMEfficiency");
    cashFlowSubAssetUnitsNeededToSurvive = mtgNeeds6;
    mtgAvails6 = new A6Row(ec, lev, "mtgAvails6");
    cashFlowSubAssetUnitsAvailableToSwap = mtgAvails6;
    //}
    bids = new A2Row(ec, History.valuesMajor6, "bids");
    commonKnowledge = new ARow(ec);
    newKnowledge = new ARow(ec);
    knowledge = new ARow(ec);
    manuals = new ARow(ec);
    moreK = new ARow(ec); // in doGrow incr knowledge
    lessM = new ARow(ec); // in doGrow The manual made commonKnowledge
    // rende3 = eM.rende3;
    //create a local reference not static reference to nonstatic EM values
    resS = eM.resS; //space for desc, comment
    resV = eM.resV;
    resI = eM.resI;
    prevProspects2 = new A2Row(ec);
    rawProspects2 = new A2Row(ec);
    prevBalances = new A6Row(ec);
    // lStatsWaitList = eM.lStatsWaitList;
    // statsWaitList = eM.statsWaitList;
    ISSET = eM.ISSET;
    doYearTime = eM.doYearTime;
    ICUR0 = eM.ICUR0;
    MAXDEPTH = eM.MAXDEPTH;
    ICUM = eM.ICUM;
    CCONTROLD = eM.CCONTROLD;
    //ydifficulty = new ARow(ec);  set by priority
    double sumWealth = res * eM.nominalWealthPerResource[pors] + colonists * eM.nominalWealthPerStaff[pors] + aknowledge * eM.nominalWealthPerCommonKnowledge[0] + wealth;
    hist.add(new History("aa", History.loopIncrements3, "InitAs " + EM.year + " i$" + EM.mf(iwealth), "r" + EM.mf(res), "r$" + EM.mf(res * eM.nominalWealthPerResource[pors]), "s" + EM.mf(colonists), "s$" + EM.mf(colonists * eM.nominalWealthPerStaff[pors]), "K" + EM.mf(aknowledge), "K$" + EM.mf(aknowledge * eM.nominalWealthPerCommonKnowledge[0]), "$" + EM.mf(wealth), "i$" + EM.mf(iwealth), "difficulty=", EM.mf(percentDifficulty)));
    //  System.out.println("Assets() 623 end constructor");
    System.out.println("Assets.assetsInit 407 more" + name);
    //  needsArray = new A6Row[5];

    cur = new CashFlow(this);
    cur.aStartCashFlow(this);
    assetsInitialized = true;
    bals.copy4BtoC(ABalRows.BALANCESIX, ABalRows.INITIALASSETSBALANCESIX);
    cur = null;
  } // end Assets.assetsInit

  /**
   * generate string of r or s source and index scrIx
   *
   * @param ixSrc 0,1 index of source r or s
   * @param srcIx index of sector
   * @return rNs[ixSrc] + srcIx
   */
  String rNsIx(int ixSrc, int srcIx) {
    return rNs[ixSrc] + srcIx;
  }

  /**
   * generate String of r or s source and index
   *
   * @param n
   * @return
   */
  String rNsIx(int n) {
    return rNs[(int) n / LSECS] + n % LSECS;
  }

  /**
   * get the raw value 0,1 value of the source, depends on ixWRSrc
   *
   * @return ixWRSrc
   */
  int getSrcWRix() {
    return ixWRSrc;
  }

  /**
   * get the index for Working ARows in Bals or Balances
   *
   * @return ixWRSrc*2+2
   */
  int getSrcWix() {
    return ixWRSrc * 2 + 2;
  }
  /**
   * get the index for Reserved ARows in Bals or Balances
   *
   * int getSrcRix() { return ixWRSrc*2+3; }
   *
   *
   * /**
   * set hist Title line
   *
   * @param lTitle
   */
  int prevTitleLine = 0;

  void histTitles(String lTitle) {
    histTitles(aPre, lTitle);
  }

  /**
   * set Titles for the following lines Don't set a title if the previous line
   * was a title
   *
   * Titles have the special level 20
   *
   * @param aPre Prefix for the titles
   * @param lTitle content of the title column
   */
  void histTitles(String aPre, String lTitle) {
    // eliminate duplicates in hist, remove previous title
    if (prevTitleLine > 0 && prevTitleLine == hist.size()) {
      hist.remove(hist.size() - 1);
    }
    if (prevTitleLine != hist.size()) {
      if (History.dl > History.valuesMajor6) {
        hist.add(new History(aPre, History.headers20, lTitle, "0Lifeneed", "1Structs", "2Energy", "3Propel", "4Defense", "5Gov", "6Colinize", "Min", "Sum", "Ave"));
        prevTitleLine = hist.size();
      }
    }
  }

  /**
   * put a number into a byte array, throw an error is ASCII is not supported
   *
   * @param res the byte array to receive the number
   * @param bias the place to start putting into the array
   * @param size the max size of characters in the number, call EM..doMyErr it
   * number exceeds size
   * @param val number to be stored
   * @throws UnsupportedEncodingException
   */
  public void putNums(byte[] res, int bias, int size, int val) throws UnsupportedEncodingException {
    String valS = String.valueOf(val);
    byte aNum[] = valS.getBytes("ASCII");//[10];
    int blength = aNum.length, ix = 0;
    int bstart = size - blength;
    if (blength < size) {
      for (ix = 0; ix < size; ix++) {
        if (ix < bstart) {
          res[bias + ix] = ' ';
        }
        else {
          res[bias + ix] = aNum[ix - bstart];
        } // else
      } //ix
    }
    else { // blength >= size
      if (bstart < 0) {
        EM.doMyErr("----AERR3--- putNums val too large=" + val);
      }
      for (ix = 0; ix < size; ix++) {
        res[bias + ix] = aNum[ix - bstart]; // bstart negative
      }
    } // if blength
    //  res[bias] = (byte) ((int) (val / 100 + E.aByte)); // less than 999
  }

  /**
   * put a ixVal into a byte array,
   *
   * @param res the byte array to receive the number
   * @param bias the place to start putting into the array
   * @param ixVal index value to convert to Alpha character
   */
  public void putIxVal(byte[] res, int bias, int ixVal) {
    if (ixVal > 52 || ixVal < 0) {
      EM.doMyErr("----AERR6--- putIxVal ixVal too large=" + ixVal);
    }
    if (ixVal > 26) {
      res[bias] = (byte) ('a' + ixVal - 26);
    }
    else {
      res[bias] = (byte) ('A' + ixVal);
    }
  }

  /**
   * put a byte value from a series of tests to put into res[bias+ix]; called in
   * EM.buildAICvals and in Assets.CashFlow.yearEnd() with tests specified in
   * Assets
   *
   * @param res the array to set
   * @param bias the bias into the array
   * @param value the source value
   * @param tests an array of tests that a may be greater than
   *
   */
  static void putValueChar(char[] res, int bias, double value, double[] tests) {
    assert tests.length <= E.keysXMax : " tests values array is too long=" + tests.length + ">" + E.keysXMax;
    int ix = 0, rix = 0;
    char ret = E.getAIResChar(ix);
    int testsLen = tests.length;
    for (ix = testsLen - 2; ix > -1; ix--) {  //.skip highest value
      if (value > tests[ix]) {
        rix = ix < testsLen - 1 ? ix + 1 : ix;
        ret = E.getAIResChar(rix);
        ix = -2; // exit test loop
      }
    }
    int againx = E.getAIMuch(ret);
    assert againx == rix : "----PVe---error againx" + againx + " not equal to original" + rix + " value=" + EM.mf(value);
    res[bias] = ret;

  }
  static int putValCnt = 0;
  static String putValStr = "coming soon";

  /**
   * put a char value from a series of tests to put into res[bias+ix]; called in
   * buildAICvals and in Assets.CashFlow.yearEnd() with tests specified in
   * Assets
   *
   * @param res the array to set
   * @param bias the bias into the array
   * @param value the source value
   * @param tests an array of tests that a may be greater than
   * @param what a descriptor string for this value
   * @param ifPrint print message to System.out if true
   * @return ix index greatest value of test, put resChar into res[bias] an
   * 'b'+0 if greater that the smallest test 'a' if equal or less than smallest
   * test
   */
  static int putValueChar(char[] res, int bias, double value, double[] tests, String what, boolean ifPrint) {
    assert tests.length <= E.keysXMax : " tests values array is too long=" + tests.length + ">" + E.keysXMax;
    //byte ret[] = {'A'};
    char ret = E.getAIResChar(0);
    int ix = 0;
    int retIx = 0, retIu = 0;
    int testsLen = tests.length;
    int testsLess = testsLen - 1; //largest index
    //go greatest to smallest value, if not found then ix =0 = E.getAIResChar(0)
    for (ix = testsLen - 2; ix > -1; ix--) { // skip highest value
      if (value > tests[ix]) {
        retIx = ix + 1; //avoid ix larger than largest value testsLen-1
        retIu = ix;
        ret = E.getAIResChar(retIx);
        ix = -2; // exit test loop
      }
    }
    res[bias] = ret;
    int againx = E.getAIMuch(ret);

    EM.wasHere6 = " entryCnt" + aEntries[0] + "  size" + EM.myAIlearnings.size() + "   what=" + what + "  bias" + bias + "V" + EM.mf(value) + ":" + retIx + ":" + retIu + "C" + ret + "L" + testsLen + " putValCnt" + putValCnt;
    assert againx == retIx : "----PVe---error againx" + againx + " not equal to original" + retIx + " value=" + EM.mf(value);
    if ((ifPrint && (++putValCnt % 47) == 0) || bias == E.pPrevEScW || bias == E.pPrevERScW) {
      String ss = new String(res);
      char rr = ret;
      System.out.println("----PVB3---- putValByte what=" + what + " bias =" + bias + "V" + EM.mf(value) + ":" + retIx + "C" + ret + "L" + testsLen + "Vx" + EM.mf2(tests[retIx]) + "->" + EM.mf2(tests[retIu]) + ", putValCnt" + putValCnt + " key.length" + res.length + " entryCnt" + aEntries[0] + "  keys=" + EM.myAIlearnings.size() + " key=" + ss);
    }
    return retIx;
  }

  /**
   * get the smaller int from testing a value against an array of test values
   *
   * @param value value to be tested
   * @param tests array of test values
   * @param what
   * @return
   */
  static int getTestVal(double value, double[] tests, String what) {
    assert tests.length <= E.keysXMax : " tests values array is too long=" + tests.length + ">" + E.keysXMax;
    int ret = 0;
    int ix = 0;
    int testsLen = tests.length;
    for (ix = testsLen - 1; ix > -1; ix--) {
      if (value > tests[ix]) {
        ret = ix < testsLen - 1 ? ix + 1 : ix;  // 2.,3.,4.,5. for value=4.::4.>3.[1] but want [2]
        ix = -2; // exit test loop
      }
    }
    return ret;

  }

  ;
  /**
   * convert the number to a string representation
   *
   * @param v input value
   * @return value as a string
   */
  protected String df2(double v) {
    return eM.mf(v);
  }

  /**
   * format the value
   *
   * @param v input value
   * @return value as a string
   */
  protected String df(double v) {
    return EM.mf(v);
  }

  /**
   * format the value
   *
   * @param v input value
   * @param n ignored
   * @return value as a string
   */
  protected String df(double v, int n) {
    return EM.mf(v);
  }

  /**
   * format the value
   *
   * @param v input value
   * @return value as a string
   */
  String mf(double v) {
    return EM.mf(v);
  }

  /**
   * format the value
   *
   * @param v input value
   * @return value as a string
   */
  protected String df7(double v) {
    return EM.mf(v);
  }

  /**
   * convert double to a string
   *
   * @param n double value
   * @return string value
   */
  String whole(double n) {
    return EM.mf(n);
  }

  /**
   * convert double to a string
   *
   * @param n double value
   * @return string value
   */
  String wh(double n) {
    return EM.mf(n);
  }

  /**
   * convert an int to string raw java
   *
   * @param n int value
   * @return string value of int
   */
  String wh(int n) {
    return n + "";
  }

  /**
   * test and return true if n is a number is not very near zero
   *
   * @param n the number for a test
   * @return true if number is a number and is not very close to 0.0
   */
  public static boolean nz(Double n) {
    return !(n.isNaN() || (n > E.NNZERO && n < E.PPZERO));
  }

  /**
   * throw an error if the value is not a number, or is infinite
   *
   * @param trouble number to be tested
   * @return return only a good number
   */
  double doubleTrouble(Double trouble) {
    return ec.doubleTrouble(trouble, "");
  }

  /**
   * throw an error if the value is not a number, or is infinite
   *
   * @param trouble number to be tested
   * @param string part of the error message for a bad number
   * @return return only a good number
   */
  double doubleTrouble(Double trouble, String vs) {
    return ec.doubleTrouble(trouble, vs);
  }

  /**
   * throw an error if the value is not a number, or is infinite
   *
   * @param trouble number to be tested
   * @return return only a good number
   */
  double doubleTroubled(Double trouble) {
    if (trouble.isNaN()) {
      if (E.debugDouble) {
        throw new MyErr(String.format("Not a number found, term%d, i%d, j%d, m%d, n%d", term, i, j, m, n));
      }
      else {
        return 0.0;
      }
    }
    if (trouble.isInfinite()) {
      if (E.debugDouble) {
        throw new MyErr(String.format("Infinite number found, term%d,i%d,j%d,m%d,n%d", term, i, j, m, n));
      }
      else {
        return 100.0;
      }
    }
    return (double) trouble;
  }

  /**
   * use ec.cRand(randx,mRand) in Econ get Random number by index modified by
   * fraction an array of random numbers is generated at the start of each
   * financial year this array is constant for that economy until the next year.
   * Each economy has it own array
   *
   * @param randx random index, converted to positive value
   * @param mRand modifying fraction
   * @return ec.cRand(randx,mRand);
   */
  protected double cRand(int randx, double mRand) { //Assets.cRand
    return ec.cRand(randx, mRand);
  }

  /**
   * return a random number constant for this year, this index use
   * Econ.cRand(randIx,1. if eM.randFrac[pors] == 0 than a 1.0 is always the
   * returned random number
   *
   *
   * @param trand ignored , but ec.trand is the array of random numbers
   * @param randIx index into ec.trand
   * @return a random number around 1.0
   */
  protected double cRand(double[] trand, int randIx) { // Assets.cRand
    return ec.cRand(randIx, 1.0);
  }

  /**
   * return a random number constant for this year, this index get random number
   * for randIx use cRand(randIx,1.)
   *
   * @param randIx index into years random numbers
   * @return random number always the same if year, randIx are the same
   */
  double cRand(int randIx) {
    return ec.cRand(randIx, 1.);
  }

  /**
   * calculate percent and if divisor &lt; value 0
   *
   * @param divisor divisor value
   * @param dividend to be divided percented value
   * @return dividend/divisor unless divisor &lt; 0 then return 0
   */
  double calcPercent(double divisor, double dividend) {
    double rtn = 0;
    doubleTroubled(divisor);
    doubleTroubled(dividend);
    rtn = divisor > E.PZERO ? 100. * dividend / divisor : 0.;
    return rtn;
  }

  /**
   * calculate percent increase of second to first value
   *
   * @param first value
   * @param second value find the increase from first
   * @return first >%gt; 0 then 100. * second-first/ first otherwise 0.;
   */
  double calcIncrease(double first, double second) {
    double rtn = 0;
    doubleTroubled(first);
    doubleTroubled(second);
    rtn = first > E.PZERO ? 100. * second - first / first : 0.;
    return rtn;
  }

  /**
   * add to Assets.cash
   *
   * @param aCash amount to add
   * @return total of cash + aCash
   */
  double addCash(double aCash) {
    return cash += aCash;
  }

  /**
   * get the count of number of trades with ships this year
   *
   * @return tradedShipOrdinal
   */
  int getShipOrdinal() {
    return tradedShipOrdinal;
  }

  /**
   * get the count of trades started this year
   *
   * @return yrTradesStarted
   */
  int getYrTradesStarted() {
    return yrTradesStarted;
  }

  /**
   * Assets get tradingGoods from CashFlow' if CashFlow (cur) is null get
   * CashFlow get the value then null cashFlow otherwise leave CashFlow but get
   * goods
   *
   * @return goods bid for Trading
   */
  A2Row getTradingGoods() {
    if (E.tradeInitOut) {
      System.out.println("Assets.getTradingGoods " + name + "Y" + EM.year);
    }
    if (cur == null) {
      yphase = yrphase.PRESEARCH;
      cur = new CashFlow(this);
      cur.aStartCashFlow(this);
      A2Row ret = cur.getTradingGoods();
      doNullCur("getTradingGoods");
    }
    else {
      A2Row ret = cur.getTradingGoods();
    }
    return bids;
  }

  /**
   * get Worth this Assets critical bids to trade force calculation of bids if
   * needed
   *
   * @return worth to trade
   *
   * double getTradingWorth() { //Assets getTradingGoods(); return
   * tradingOfferWorth; }
   */
  /**
   * get the number of trades tried this year
   *
   * @return trades tried this year
   */
  int getTradedShipsTried() {
    int jjj = econVisited;
    return econVisited;
  }

  /**
   * get the number of successful trades this year
   *
   * @return the number of successful trades
   */
  int getTradedSuccessTrades() {
    return tradedSuccessTrades;
  }

  /**
   * get knowledge
   *
   * @return knowledge
   */
  ARow getKnowledge() {
    return knowledge;
  }

  /**
   * get the name of the Econ
   *
   * @return name of Econ
   */
  String getName() {
    return name;
  }

  /**
   * get common knowledge
   *
   * @return common knowledge
   */
  ARow getCommonKnowledge() {
    return commonKnowledge;
  }

  /**
   * get new knowledge
   *
   * @return new knowledge
   */
  ARow getNewKnowledge() {
    return newKnowledge;
  }

  /**
   * get manuals
   *
   * @return manuals
   */
  ARow getManuals() {
    return manuals;
  }

  /**
   * eliminated 1/15/2016, use only make(ARow A) new an ARow if it was not
   * instantiated save space in auxiliary CashFlow only using a few fields
   *
   * @param a ARow if it already exists
   * @return a or a new ARow
   */
  ARow makeNewNot(ARow a) {   //Assets.makeNew
    if (a != null) {
      return a;
    }
    ARow AA = new ARow(ec);
    return AA;
  }

  /**
   * return a set ARow from old using anew if it exists save space in auxiliary
   * CashFlow only using a few fields
   *
   * @param anew possible existing ARow
   * @param old source ARow to set
   * @return this set to old
   */
  ARow makeSetNot(ARow anew, ARow old) {   //Assets.makeSet
    if (anew != null && old != null) {
      return anew.set(old);
    }
    else {
      if (old == null) {
        old = makeZero(old);
      }
      if (anew == null || anew.getValues() == null) {
        anew = makeZero(anew);
      }
      anew.set(old);
      return anew;
    }
  }

  /**
   * make a new A2Row and make the 2 ARows from the invoking A2Row
   *
   * @param a the input A2Row
   * @return the copy of rowsin including the 2 copied ARow
   */
  A2Row copy(A2Row a) {
    A2Row tem = new A2Row(ec);
    if (a == null) {
      a = new A2Row(ec);
    }
    for (int m : IA01) {
      tem.getRow(m).set(a.getRow(m));
    }
    tem.lev = a.lev;
    tem.titl = "cp" + a.titl;
    return tem;
  }

  /**
   * make a copy of the old ARow or a zero ARow if old is null
   *
   * @param old
   * @return copy of ARow old or a zero ARow if old is null
   */
  ARow copy(ARow old) {
    if (old == null) {
      ARow tem = new ARow(ec).zero();
      tem.get(0);
      return tem;
    }
    else {
      return new ARow(ec).set(old);
    }
  }

  /**
   * make a copy of an A6Row instance
   *
   * @param a the old A6Row
   * @return new A6Row with all the values of A
   */
  A6Row copy(A6Row a) {
    A6Row tem = new A6Row(ec);
    if (a == null) {
      a = new A6Row(ec);
    }
    int[] d15 = {0, 1, 2, 3, 4, 5};
    for (int m : d15) {
      tem.getRow(m).set(a.getRow(m));
    }
    tem.lev = a.lev;
    tem.balances = a.balances;
    tem.costs = a.costs;
    tem.titl = "cp" + a.titl;
    return tem;
  }

  /**
   * make a copy of an A10Row instance
   *
   * @param a A10Row original
   * @return A10Row copy
   */
  A10Row copy(A10Row a) {
    A10Row tem = new A10Row(ec);
    if (a == null) {
      a = new A10Row(ec);
    }
    int[] d19 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    for (int m : d19) {
      tem.getRow(m).set(a.getRow(m));
    }
    tem.lev = a.lev;
    tem.balances = a.balances;
    tem.costs = a.costs;
    tem.titl = "cp" + a.titl;
    return tem;
  }

  /**
   * 1/15/2016 Eliminate this in favor of make, set means something different
   * return the old ARow or a new zero ARow if old is null ARow.set... generally
   * means modify and existing ARow ARow.mult ARow.divby ... does not modify
   * ARow but creates a new one for a stacked set of methods
   *
   * @param old
   * @return old or a new zero ARow if old is null
   */
  ARow setNot(ARow old) {   // Assets.set
    if (old == null) {
      return new ARow(ec).zero();
    }
    else if (old.getValues() == null) {
      old.fill();
      return old;
    }
    else {
      return old;
    }
  }

  /**
   * get the rN for the stats name
   *
   * @param dname string stats name to be found
   * @return the integer related to that name
   */
  int getStatrN(String dname) {
    EM.addlErr = "setStat dname=" + dname;
    Object o1 = eM.resMap.get(dname);
    if (o1 == null) {
      System.out.printf("setStat a cannot find \"%s\" \n", dname);
      o1 = eM.resMap.get("missing name");
    }
    return (int) o1;
  }

  /**
   * get a units sum from the stats database, it could be for the current year
   * or for the cunulative sum of all the years
   *
   * @param rN the index into the stats database
   * @param curCum either ICUM or ICUR0 from EM
   * @param porsStart 0:start with planets, 1 start with ships
   * @param porsEnd 1. 0:1 sum just planets, 2. 0:2 sum planets & ships
   * @param clanStart 0-4 sum of the clan to start with
   * @param clanEnd 1-5 end of clan sum, 0:1,1:2 etc. 1 clan<br>
   * 0:5 sum all of the clans
   * @return the sum of units as filtered by the selectors
   */
  int getCurCumPorsClanUnitSum(int rN, int curCum, int porsStart, int porsEnd, int clanStart, int clanEnd) {
    return eM.getCurCumPorsClanUnitSum(rN, curCum, porsStart, porsEnd, clanStart, clanEnd);
  }

  /**
   * get the sum of ship costs to travel 1 year force the calculation of the
   * value if not currently set called in Econ
   *
   * @return the sum of 1 Lightyear of travel and Maint Costs
   */
  double getSumTrade1YearTravelMaintCosts() {
    if (E.tradeInitOut) {
      System.out.println("Assets.getSumTrade1YearTravelMaintCosts " + name + "Y" + EM.year);
    }
    getTradeInit(sumTrade1YearTravelMaintCosts < NZERO);
    return sumTrade1YearTravelMaintCosts;
  }

  /**
   * if tradeGoodsNeeds unset get the initial trade bid (goods) with the
   * getNeeds calculated
   *
   * @return the the initial trade bid (goods) with
   */
  A2Row getTradeGoodsNeeds() {
    if (E.tradeInitOut) {
      System.out.println("Assets.getTradeGoodsNeeds " + name + "Y" + EM.year);
    }
    getTradeInit(tradeGoodsNeeds == null);
    return tradeGoodsNeeds;
  }

  /**
   * get the trade strategic vars, high values greatest needs force the
   * calculation of the value if not currently set called in Econ
   *
   * @return the trade strategic vars, high values greatest needs
   */
  A2Row getTradeStrategicVars() {  //Assets.getTradeStrategicVars()
    if (E.tradeInitOut) {
      eM.printHere("---AGTS----", ec, "Assets.getTradeStrategicVars before " + (tradeStrategicVars == null ? " getTradeInit" : " !getTradeInit"));
    }
    getTradeInit(tradeStrategicVars == null);
    eM.printHere("---AGTS2----", ec, "Assets.getTradeStrategicVars AFTER" + (tradeStrategicVars == null ? " getTradeInit" : " !getTradeInit"));
    return tradeStrategicVars;
  }

  /**
   * run initTrade if forceInit is true
   *
   * @param forceInit set true if a desired trade value is unset
   */
  String didTradeInitCF = "not Init CF";

  void getTradeInit(boolean forceInit) {
    didTradeInitCF = "not Init CF ";
    boolean startedCF = false;
    yphase = yrphase.TRADE;
    if (forceInit) {
      if (cur == null || !didCashFlowStart) {
        eM.printHere("----AITA----", ec, "Assets.getTradeInit befpre start cashflow" + (didCashFlowInit ? " didCashFlowInit" : " !didCashFlowInit") + (didCashFlowStart ? " didCashFlowStart" : " !didCashFlowStart"));
        cur = new CashFlow(this);
        eM.wasHere = "Assets.getTradeInit before aStartCashFlow";
        cur.aStartCashFlow(this);
        startedCF = true;
        didTradeInitCF = "yesTICF";
        eM.wasHere = "Assets.getTradeInit after aStartCashFlow";
      }
      eM.printHere("----AITG----", ec, "Assets.getTradeInit after start cashflow" + (didCashFlowInit ? " didCashFlowInit" : " !didCashFlowInit") + (didCashFlowStart ? " didCashFlowStart" : " !didCashFlowStart"));
      cur.getTradeInit(forceInit); // pass to cur CashFlow
      eM.printHere("----AGTI----", ec, " after Assets.getTradeInit " + (tradeStrategicVars == null ? " getTradeInit" : " !getTradeInit"));
      if (startedCF) {
        doNullCur(" from getTradeInit");
      }
    } // forceInit
    eM.printHere("----ATIc----", ec, " Assets.getTradeInit after doNullCur");
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
  double setStat(int rn, int pors, int clan, double v) {
    int age = ec.age;
    return setStat(rn, pors, clan, v, 1, age);
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
  double setStat(int rn, int pors, int clan, double v, int cnt) {
    return setStat(rn, pors, clan, v, cnt, ec.age);
  }

  /**
   * set a statistic value
   *
   * @param rn the name of this statistic
   * @param v the value to be set
   * @return v
   */
  double setStat(int rn, double v) {
    return setStat(rn, pors, clan, v, 1, ec.age);
  }

  /**
   * set a statistic percent value
   *
   * @param rn the name of this statistic
   * @param divisor divisor value
   * @param dividend dividend value
   * @return v
   */
  double setPercentStat(int rn, double divisor, double dividend) {
    double v = divisor > E.PPZERO ? 100 * dividend / divisor : 0.0;
    return setStat(rn, pors, clan, v, 1, ec.age);
  }

  int cntStatsPrints = 0;
  volatile int ste = 0, lstk = 0, a = -5, b = -5, curm = 0;

  /**
   * set a statistic value and a count
   *
   * @param rn the name of this statistic
   * @param v the value to be set
   * @param cnt greater than 0 if this set is to be counted
   *
   * @return v
   */
  double setStat(int rn, int pors, int clan, Double v, int cnt, int age
  ) {
    try {
      //long resLock[][][] = resI[rn];
      assert !v.isInfinite() : "setStat error infinite v=" + v;
      assert !v.isNaN() : "setStat error nan v=" + v;
      int le = eM.lStatsWaitList;
      int prevIx = eM.ixStatsWaitList;
      eM.ixStatsWaitList = (++eM.ixStatsWaitList) % eM.lStatsWaitList;
      prevIx = prevIx >= eM.lStatsWaitList ? 0 : prevIx;// I don't know why there was out of bounds sometimes
      int atCnt = 0;
      long nTime = (new Date()).getTime();
      long moreT = nTime - eM.doYearTime;

      //  int sClan = curEcon.clan;
      // int pors = curEcon.pors;
      eM.addlErr = "inSetStat a rn=" + rn + " pors=" + pors + " clan=" + clan + " ";
      String desc = eM.resS[rn][0];
      eM.wasHere = "inSetStat b rn=" + rn + " desc=" + desc + " pors=" + pors + " clan=" + clan + " ";
      eM.addlErr = "";
      a = -5;
      b = -5;
      curm = 0;
      int lResI = eM.resI[rn].length;
      if (lResI > eM.STATSSHORTLEN) {  //did it have ages
        for (a = 1; a < 6 && b < 0; a++) {
          //AGESTARTS   0,   4,   8,    16,    32,   999999};
          //  CUM CUR0  CUR1 CUR2  CUR3  CUR4  CUR5
          //   MAXDEPTH = 7
          if (age < eM.AGESTARTS[a]) { //0, 4, 8, 16, 32, 999999,9999999};
            b = a; //ageIx: b1 = 0-3,B2 4-7,B3 8-15,B4 16-31 B5 32-999999
          }
        }
        if (b > 0) {
          curm = eM.ICUR0 + eM.MAXDEPTH * b;//age < 4 makes ageIx=1 curm>0 0 of age group
          // curm =1 + 7*b = 1 8 15 22 29 36 43
        }
      } // end if
      // select an object only big enough to work for the code for all sub objects that could be changed

      double resL[][][] = eM.resV[rn];
      int ycnt = cnt > -2 ? cnt : 0;// allow -1
      int ICUM = eM.ICUM;
      int ICUR0 = eM.ICUR0;
      int CCONTROLD = eM.CCONTROLD;
      // calculate the needed changes for ICUM  ICUR0  and possible ICURx
      double[] resVCum = eM.resV[rn][ICUM][pors]; //clan array object
      double prevResVCumC = resVCum[clan] + 0.; // don't point into array
      double nextResVCumC = resVCum[clan] + 0. + v; // don't point into array
      // resVCumC += v; // doesn't change resVCum[clan]
      long[] resICum = resI[rn][ICUM][pors];
      //  long resICumP = resICum[clan] + 0;
      double[] resVCur = resV[rn][ICUR0][pors];// clan array
      //  double resVCur0P = resVCur[clan]+0;
      long[] resICur = resI[rn][ICUR0][pors];
      long resICur0P = resICur[clan];
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
      //   synchronized (resL) {
      EM.wasHere8 = "---ELa3--- Assets res has lock";
      if (E.debugStatsOut) {
        String sList = "----SSLa----setStat " + name + "Y" + EM.year + " in  " + Thread.currentThread().getName() + " sinceDoYear " + moreT + " at ";
        StackTraceElement[] prevCalls = new StackTraceElement[le];
        StackTraceElement[] stks = Thread.currentThread().getStackTrace();
        lstk = stks.length - 1;
        // do stack element history
        for (ste = 1; ste < le && atCnt < 5 && ste < lstk; ste++) {
          if (stks[ste + 1] != null) {
            prevCalls[ste] = stks[ste + 1];
            if (prevCalls[ste].getMethodName() != null
                && prevCalls[ste].getFileName() != null
                && prevCalls[ste].getLineNumber() != 0
                && !prevCalls[ste].getMethodName().contentEquals("setStat")) {
              if (atCnt == 0) {
                sList += prevCalls[ste].getMethodName() + " ";
              }
              String pcs = prevCalls[ste].getFileName();
              int pci = prevCalls[ste].getLineNumber();
              sList += " " + " at "
                       + pcs
                       + "." + pci;
            } // parts !null
          } // !null
          atCnt++;
        }//for
        //Econ.keyList[Econ.ixKeyList = ++Econ.ixKeyList < Econ.lKeyList ? Econ.ixKeyList : 0]
        synchronized (ARow.lock) {
          EM.statsWaitList[EM.ixStatsWaitList = ++EM.ixStatsWaitList < EM.lStatsWaitList ? EM.ixStatsWaitList : 0] = sList;
        }
        //EM.addStatsWaitList(sList);
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

      // if (E.debugStatsOut && cntStatsPrints++ > E.ssMax) {
      if (E.debugStatsOut) {
        //  cntStatsPrints = 0;
        if (rn > 0) {
          long endSt = (new Date()).getTime();
          long moreTT = endSt - doYearTime;
          int rN = rn;
          int yrsIx = 1;
          int yrsIxj = 1;
          long resICumClan = resI[rn][ICUM][pors][clan];
          double resVCumClan = resV[rn][ICUM][pors][clan];
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
          boolean T = EM.mfShort;
          boolean Q = EM.mfSS;
          EM.mfShort = EM.mfSS = true;
          if (E.debugStatsOut) {
            System.out.println("---SSTat--- " + " " + resS[rN][0] + " rN" + rN + ", valid" + eM.valid + "S" + resIcur0Isset + resICumIsset + EM.mf("V", v) + EM.mf("PMV", prevResVCumC));
            //" " + name + "Y" + EM.year + "K" + clan + "A" + age + + EM.mf("NM", nextResVCumC) + EM.mf(resVCumClan) + "::" + resVCumClan + " resIcum = " + resICumClan + " , cur0ClanV = " + mf(resVcur0Clan) + ", cur++Clan =" + mf(resVCurmClan));
            /*      System.out.println(
                      "EM.setStat " + Econ.nowName + " " + Econ.doEndYearCnt[0] + " since doYear" + EM.year + "=" + moreT + "=>" + moreTT + " " + resS[rN][0] + " rN" + rN + ", valid" + eM.valid + ", " + " resIcum=" + resICumClan + ", age" + age + ", curEcon.age" + eM.curEconAge + ", pors=" + pors + ", clan=" + clan + ", resIcur0Isset=" + resIcur0Isset + ", resICumIsset=" + resICumIsset + ", resVCur0Clan=" + mf(resVcur0Clan) + ", resVCurmClan=" + mf(resVCurmClan));

              System.out.flush();
             */
          }
        }
      }
      //   } // end of lock on res..[rn]
      long[][][] resii = resI[rn];  //for values if using debug
      double[][][] resvv = resV[rn];
      return v;
    }
    catch (Exception | Error ex) {
      eM.firstStack = eM.secondStack + "";
      ex.printStackTrace(eM.pw);
      eM.secondStack = eM.sw.toString();
      System.out.flush();
      System.err.flush();
      EM.newError = true;
      EM.tError = ("----STSa---- setStat Caught " + ex.toString() + ", cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName());
      System.err.println(EM.tError + eM.andMore());
      System.err.println("rn=" + rn + ", desc=" + resS[rn][0]);
      ex.printStackTrace(System.err);
      st.setFatalError();
      return v;
      //throw new WasFatalError(eM.tError);
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
  double setMaxStat(int rn, int pors, int clan, double v, int cnt, int age
  ) {
    try {

      int le = 10;
      int prevIx = eM.ixStatsWaitList;

      int atCnt = 0;
      long nTime = (new Date()).getTime();
      long moreT = nTime - doYearTime;
      if (E.debugStatsOut) {
        String sList = "----SSLb----setMaxStat " + name + "Y" + EM.year + " in thread " + Thread.currentThread().getName() + " sinceDoYear " + moreT + " at ";
        StackTraceElement[] prevCalls = new StackTraceElement[le];
        int lstk = Thread.currentThread().getStackTrace().length - 1;
        for (int ste = 1; ste < le && atCnt < 5 && ste < lstk; ste++) {
          prevCalls[ste] = Thread.currentThread().getStackTrace()[ste + 1];
          if (!prevCalls[ste].getMethodName().contentEquals("setMaxStat")) {
            if (atCnt == 0) {
              sList += prevCalls[ste].getMethodName() + " ";
            }
            sList += " at " + prevCalls[ste].getFileName() + "." + prevCalls[ste].getLineNumber();
          }
          atCnt++;
        }//for
        synchronized (ARow.lock) {
          EM.statsWaitList[EM.ixStatsWaitList = ++EM.ixStatsWaitList < EM.lStatsWaitList ? EM.ixStatsWaitList : 0] = sList;
        }
        // EM.addStatsWaitList(sList);
      }//if out

      //  int sClan = curEcon.clan;
      // int pors = curEcon.pors;
      eM.addlErr = "inSetMaxStat a rn=" + rn + " pors=" + pors + " clan=" + clan + " ";
      String desc = resS[rn][0];
      eM.rememberDetail = eM.wasHere = "inSetMaxStat b rn=" + rn + " desc=" + desc + " pors=" + pors + " clan=" + clan + " ";
      eM.addlErr = "";
      int a = -5, b = -5, curm = 0;
      // check if we do agelist
      if (resI[rn].length > eM.STATSSHORTLEN) {
        for (a = 1; a < 6 && b < 0; a++) {
          //AGESTARTS   0,   4,   8,    16,    32,   999999};
          //  CUM CUR0  CUR1 CUR2  CUR3  CUR4  CUR5
          //   LIST0-LIST9 LIST10 LIST11 LIST12 LIST13 LIST14 LIST15-LIST20
          //   MAXDEPTH = 7
          if (age < eM.AGESTARTS[a]) {
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
      //synchronized (resL) {
      EM.wasHere8 = "---ELa4--- Assets resMax has lock";
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

          if (E.debugStatsOut) {
            if (cntStatsPrints < E.ssMax) {
              cntStatsPrints += 1;
              System.out.println("---SMXb---"
                                 + "EM.setMaxStat " + name + " " + Econ.doEndYearCnt[0] + " since doYear" + EM.year + "=" + moreT + "=>" + moreTT + " " + resS[rN][0] + " rN" + rN + ", valid" + eM.valid + ", " + " resIcum=" + resICumClan + ", age" + age + ", curEcon.age" + eM.curEconAge + ", pors=" + pors + ", clan=" + clan + ", resIcur0Isset=" + resIcur0Isset + ", resICumIsset=" + resICumIsset + ", resVCur0Clan=" + mf(resVcur0Clan) + ", resVCurmClan=" + mf(resVCurmClan));
              System.out.flush();
            } //ssMax
          }

        };
      } //E.debugStatsOut1
      //  } // end of lock on res..[rn]
      long[][][] resii = resI[rn];  //for values if using debug
      double[][][] resvv = resV[rn];
      return v;
    }
    catch (Exception | Error ex) {
      eM.firstStack = eM.secondStack + "";
      ex.printStackTrace(eM.pw);
      eM.secondStack = eM.sw.toString();
      System.out.flush();
      System.err.flush();
      EM.newError = true;
      EM.tError = ("----MXSa--- Caught " + ex.toString() + ", cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName());
      System.err.println(EM.tError + eM.andMore());
      System.err.println("rn=" + rn + ", desc=" + resS[rn][0]);
      ex.printStackTrace(System.err);
      st.setFatalError();
      // throw new WasFatalError(eM.tError);
      return v;
    }
  }

  /**
   * set a maxStatistic value and a count
   *
   * @param rn the name of this statistic
   * @param v the value to be set
   * @return v
   */
  // int cntStatsPrints = 0;
  double setMaxStat(int rn, double v) {
    return setMaxStat(rn, pors, clan, v, 1, ec.age);
  }

  // int cntStatsPrints = 0;
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
  double setMinStat(int rn, int pors, int clan, double v, int cnt, int age
  ) {
    try {
      //synchronized (syncRes) {
      int le = 10;
      int prevIx = eM.ixStatsWaitList;
      int atCnt = 0;
      long nTime = (new Date()).getTime();
      long moreT = nTime - doYearTime;
      if (E.debugStatsOut) {
        String sList = "----SSLc----setMinStat " + name + " in thread " + Thread.currentThread().getName() + " sinceDoYear " + moreT + " at ";
        StackTraceElement[] prevCalls = new StackTraceElement[le];
        int lstk = Thread.currentThread().getStackTrace().length - 1;
        for (int ste = 1; ste < le && atCnt < 5 && ste < lstk; ste++) {
          prevCalls[ste] = Thread.currentThread().getStackTrace()[ste + 1];
          if (!prevCalls[ste].getMethodName().contentEquals("setMinStat")) {
            if (atCnt == 0) {
              sList += prevCalls[ste].getMethodName() + " ";
            }
            sList += " at " + prevCalls[ste].getFileName() + "." + prevCalls[ste].getLineNumber();
          }
          atCnt++;
        }//for
        synchronized (ARow.lock) {
          EM.statsWaitList[EM.ixStatsWaitList = ++EM.ixStatsWaitList < EM.lStatsWaitList ? EM.ixStatsWaitList : 0] = sList;
        }
      }//if out

      //  int sClan = curEcon.clan;
      // int pors = curEcon.pors;
      eM.addlErr = "inSetMinStat a rn=" + rn + " pors=" + pors + " clan=" + clan + " ";
      String desc = resS[rn][0];
      eM.wasHere = "inSetMinStat b rn=" + rn + " desc=" + desc + " pors=" + pors + " clan=" + clan + " ";
      eM.addlErr = "";
      int a = -5, b = -5, curm = 0;
      // check if we do agelist
      if (resI[rn].length > eM.STATSSHORTLEN) {
        for (a = 1; a < 6 && b < 0; a++) {
          //AGESTARTS   0,   4,   8,    16,    32,   999999};
          //  CUM CUR0  CUR1 CUR2  CUR3  CUR4  CUR5
          //   LIST0-LIST9 LIST10 LIST11 LIST12 LIST13 LIST14 LIST15-LIST20
          //   MAXDEPTH = 7
          if (age < eM.AGESTARTS[a]) {
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
      //synchronized (resL) {
      EM.wasHere8 = "---ELa5 --- Assets res Min has lock";
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
              System.out.println("---SMINLc---"
                                 + "EM.setMinStat " + name + " " + Econ.doEndYearCnt[0] + " since doYear" + EM.year + "=" + moreT + "=>" + moreTT + " " + resS[rN][0] + " rN" + rN + ", valid" + eM.valid + ", " + " resIcum=" + resICumClan + ", age" + age + ", curEcon.age" + eM.curEconAge + ", pors=" + pors + ", clan=" + clan + ", resIcur0Isset=" + resIcur0Isset + ", resICumIsset=" + resICumIsset + ", resVCur0Clan=" + mf(resVcur0Clan) + ", resVCurmClan=" + mf(resVCurmClan));
              System.out.flush();
            } //ssMax
          }

        };
      }
      //  } // end of lock on res..[rn]
      long[][][] resii = resI[rn];  //for values if using debug
      double[][][] resvv = resV[rn];
      return v;
    }
    catch (Exception | Error ex) {
      eM.firstStack = eM.secondStack + "";
      ex.printStackTrace(eM.pw);
      eM.secondStack = eM.sw.toString();
      System.out.flush();
      System.err.flush();
      EM.newError = true;
      EM.tError = ("----MUBaa---Caught in setMinStat " + ex.toString() + ", cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName());
      System.err.println(EM.tError + eM.andMore());
      System.err.println("rn=" + rn + ", desc=" + resS[rn][0]);
      ex.printStackTrace(System.err);
      st.setFatalError();
      throw new WasFatalError(eM.tError);
    }
  }

  /**
   * set a min Statistic value and a count
   *
   * @param rn the name of this statistic
   * @param v the value to be set
   * @return v
   */
  double setMin(int rn, double v) {
    return setMinStat(rn, pors, clan, v, 1, ec.age);
  }

  /**
   * set a min Statistic value and a count
   *
   * @param rn the name of this statistic
   * @param v the value to be set
   * @return v
   */
  double setMinStat(int rn, double v) {
    return setMinStat(rn, pors, clan, v, 1, ec.age);
  }

  /**
   * set a statistic value and possibly a count
   *
   * @param rn the name of this statistic
   * @param v the value to be set
   * @param cnt greater than 0 if this set is to be counted
   * @return v
   */
  double setStat(int rn, double v, int cnt) {
    int age = ec.age;
    int clan = ec.clan;
    int pors = ec.pors;
    return setStat(rn, pors, clan, v, cnt, age);
  }

  /**
   * set a statistical value for later viewing assume a count of 1
   *
   * @param dname name of the statistic
   * @param pors 0=planet, 1=ship
   * @param clan clan 0-4
   * @param val the value of the statistic
   */
  void setStat(String dname, int pors, int clan, double val) {
    EM.addlErr = "setStat dname=" + dname;
    Object o1 = eM.resMap.get(dname);
    if (o1 == null) {
      System.out.printf("setStat a cannot find \"%s\" \n", dname);
      o1 = eM.resMap.get("missing name");
    }
    setStat((int) o1, pors, clan, val, 1, ec.age);
    EM.addlErr = "";
  }

  /**
   * set a statistical value for later viewing
   *
   * @param dname name of the statistic
   * @param pors 0=planet, 1=ship
   * @param clan clan 0-4
   * @param val the value of the statistic
   * @param cnt the count of units 1 or 0 in certain comditions
   */
  void setStat(String dname, int pors, int clan, double val, int cnt) {
    EM.addlErr = "setStat dname=" + dname;
    int o1 = eM.resMap.get(dname);
    setStat(o1, pors, clan, val, cnt, ec.age);
    EM.addlErr = "";
  }

  /**
   * set a statistical value for later viewing
   *
   * @param dname name of the statistic
   * @param val the value of the statistic
   * @param cnt the count of units 1 or 0 in certain comditions
   */
  void setStat(String dname, double val, int cnt) {
    EM.addlErr = "setStat dname=" + dname;
    int o1 = eM.resMap.get(dname);
    setStat(o1, pors, clan, val, cnt, ec.age);
    EM.addlErr = "";
  }

  /**
   * set a statistical value for later viewing
   *
   * @param dname name of the statistic
   * @param val the value of the statistic
   */
  void setStat(String dname, double val) {
    EM.addlErr = "setStat dname=" + dname;
    int o1 = eM.resMap.get(dname);
    setStat(o1, pors, clan, val, 1, ec.age);
    EM.addlErr = "";
  }

  /**
   * set a statistical value for later viewing if the value is not very close to
   * zero
   *
   * @param dname name of the statistic
   * @param val the value of the statistic
   */
  void nzStat(String dname, double val) {
    if (!nz(val)) {
      return;
    }
    setStat(dname, val);
  }

  /**
   * set a statistical value for later viewing if the value is not very close to
   * zero
   *
   * @param dname name of the statistic
   * @param val the value of the statistic
   */
  void nzStat(int dname, double val) {
    if (!nz(val)) {
      return;
    }
    setStat(dname, val, 1);
  }

  /**
   * set a Min result statistical value for later viewing
   *
   * @param rN number of the stat
   * @param pors planet or ship of the stat
   * @param clan clan of the stat
   * @param val value of the stat
   * @param cnt count 1 or 0 if another setStat will set cnt ec.age age of this
   * economy
   */
  void setMin(int rN, int pors, int clan, double val, int cnt) {
    if (rN < NZERO) {
      eM.bErr("unknown result Name");
      return;
    }
    setMinStat(rN, pors, clan, val, cnt, ec.age);
  }

  /**
   * set a max result statistical value for later viewing
   *
   * @param rN number of the stat
   * @param val value of the stat
   * @param cnt count 1 or 0 if another setStat will set cnt ec.age age of this
   * economy
   */
  void setMax(int rN, double val) {
    if (rN < NZERO) {
      eM.bErr("unknown result Name");
      return;
    }
    setMaxStat(rN, pors, clan, val, 1, ec.age);
  }

  /**
   * return the old ARow or a new zero ARow if old is null This allows declaring
   * many ARow'sas that are used for only some SubAssets most of the use is in
   * CashFlow calculating costs, swaps, remnants etc.
   *
   * @param old
   * @return old or a new zero ARow if old is null
   */
  ARow make(ARow old) {   //Assets.make
    if (old == null) {
      return new ARow(ec).zero();
    }
    else if (old.values == null) {
      old.fill();
      return old;
    }
    else {
      return old;
    }
  }

  /**
   * return old if it exists, otherwise create a new one with a level of
   * History.valuesMinor7
   *
   * @param old reference to a possibly existing A6Row
   * @param tit title for a new A6Row
   * @return
   */
  A6Row make6(A6Row old, String tit) {
    if (old == null) {
      return new A6Row(ec, History.valuesMinor7, tit);
    }
    else {
      return old;
    }
  }

  /**
   * compress the values in an A10Row to an A6Row to calculate costs for each
   * SubAsset
   *
   * @param tit title of the new A6Row
   * @param b A10Row file to compress to A6Row
   * @return the reference to the owning A6Row
   */
  A6Row make6(A10Row b, String tit) {
    A6Row ret = new A6Row(b.ec, A6Rowa.tbal, b.lev, tit);
    //lev = b.lev;
    //balances = b.balances;
    //costs = b.costs;
    // titl = b.titl;
    ret.blev = b.blev;
    for (int m = 2; m < 6; m++) {
      for (int n = 0; n < E.LSECS; n++) {
        ret.set(m, n, b.get(m, n) + b.get(m + 4, n));
      }
    }
    return ret;
  }

  /**
   * return old if it exists, otherwise create a new one with a level of
   * History.valuesMinor7
   *
   * @param old reference to a possibly existing A6Row
   * @param tit title for a new A6Row
   * @return
   */
  A2Row make2(A2Row old, String tit) {
    if (old == null) {
      return new A2Row(ec, History.valuesMinor7, tit);
    }
    else {
      return old;
    }
  }

  /**
   * return old if it exists, otherwise create a new one with a level of
   * History.valuesMinor7
   *
   * @param old reference to a possibly existing A10Row
   * @param tit title of a created A10Row
   * @return
   */
  A10Row make10(A10Row old, String tit) {
    if (old == null) {
      return new A10Row(ec, History.valuesMinor7, tit);
    }
    else {
      return old;
    }
  }

  /**
   * return a zero ARow, make one only if it is null
   *
   * @param a
   * @return this zero
   */
  ARow makeZero(ARow a) {
    if (a != null) {
      return a.zero();
    }
    ARow aa = new ARow(ec);
    return aa;
  }

  /**
   * make and existing a zero, else create a zero valued one
   *
   * @param a
   * @return
   */
  A2Row makeZero(A2Row a) {
    if (a != null) {
      a.getARow(E.lsecs).zero();
      a.getARow(0).zero();
      return a;
    }
    A2Row aa = new A2Row(new ARow(ec).zero(), new ARow(ec).zero());
    return aa;
  }

  /**
   * make and existing a zero, else create a zero valued one
   *
   * @param a
   * @param titl title of the object
   * @return
   */
  A2Row makeZero(A2Row a, String titl) {
    if (a != null) {
      a.getARow(E.lsecs).zero();
      a.getARow(0).zero();
      return a;
    }
    A2Row aa = new A2Row(new ARow(ec).zero(), new ARow(ec).zero());
    aa.titl = titl;
    return aa;
  }

  A4Row makeZero(A4Row a) {
    if (a == null) {
      return new A4Row();
    }
    int[] d4 = {0, 1, 2, 3};
    for (int m : d4) {
      a.A[m] = make(a.A[m]).zero();
    }
    return a;
  }

  /**
   * zero or make and zero a and set titl
   *
   * @param a reference to A10Row object, if null create new object
   * @param titl title of object
   * @return A10Row object with all values zero
   */
  A10Row makeZero(A10Row a, String titl) {
    if (a == null) {
      return new A10Row(ec, History.valuesMinor7, titl);
    }
    a.zero();
    return a;
  }

  /**
   * zero or make and zero a and set titl
   *
   * @param a reference to A6Row object, if null create new object
   * @param titl title of object
   * @return A6Row object with all values zero
   */
  A6Row makeZero(A6Row a, String titl) {
    if (a == null) {
      return new A6Row(ec, History.valuesMinor7, titl);
    }
    a.zero();
    return a;
  }

  /**
   * flip the rc and sg row signs in a new A2Row
   *
   * @param B
   * @return flipped values of rc and sg
   */
  A2Row flip(A6Row B) {
    A2Row aa = new A2Row(ec);
    for (int m : E.d2) {
      for (int n : ASECS) {
        aa.set(m, n, -B.get(m, n));
      }
    }
    return aa;
  }

  /**
   * receive yearStart from trade.Econ, set trand for cRand
   *
   * @param atrand the array of random numbers set in Econ by\
   * @param ahist the hist for the new year
   */
  void yearStart(double[] atrand, ArrayList<History> ahist) { // trade.Assets.yearStart
    //  year = eM.year;
    otherName = "";
    trand = atrand;   // set history array
    hist = ahist;   // set hist
    tradedStrategicRequests = 0.; // sum of strategic requests
    tradedStrategicOffers = 0.;// sum of strategic offers
    tradedNominalRequests = 0.;// 2-3 least strategic value traded
    tradedNominalOffers = 0.; // 2-3 real costs of trades
    tradedOffers = 0.; // 2-3 real costs (offers) of trades
    // tradedPrevOffers = 0.; // prev real costs (offers) of trades
    // tradedPrevPrevOffers = 0.; // prev prev real costs (offers) of trades
    tradedManualsWorths = 0.; // worth of manuals received in trades
    strategicGoal = 0;
    // prevStrategicGoal = strategicGoal; // from a trade
    //   lightYearsTraveled = lYears;
    if (E.tradeInitOut) {

      eM.printHere("---ASYa----", ec, " yearStart");
      //   System.out.println("Assets.yearStart " + name + "Y" + EM.year + " thread=" + Thread.currentThread().getName());
    }
    if (cur == null) {
      cur = new CashFlow(this);
      cur.aStartCashFlow(this);
      doNullCur(" Assets.yearStart ");
    }
    initRawProspects2 = null;
    didInitRawProspects = false;

  }

  double getHealth() {
    return health;
  }

  double getWorth() {
    return sumTotWorth;
  }

  boolean getDie() {
    return dead;
  }

  int getAge() {    // Assets.getAge
    return ec.age;
  }

  /**
   * list bids
   *
   * @param lev level of listing
   * @param pre prefix
   */
  void listBids(int lev, String pre
  ) {  // Assets.CashFlow.Trades
    hist.add(h1 = new History(pre, lev, "T" + term + " bidC", bids.getARow(0)));
    hist.add(h2 = new History(pre, lev, "T" + term + " bidG", bids.getARow(E.lsecs)));
  }

  /**
   * list bids was goods
   *
   * @param lev level of listing
   * @param pre prefix
   */
  void listGoods(int lev, String pre) {
    listBids(lev, pre);
  }

  int aaadd1 = 0, aaadd2 = 0, aaadd3 = 0, aaadd4 = 0;

  /**
   * year end, for end of year from Econ from StarTrader
   *
   */
  void yearEnd() {  //trade.Assets
    EM.mfShort = false;
    if (cur == null) {
      cur = new CashFlow(this);
      cur.aStartCashFlow(this);
      System.out.println("--CEYEa--  before CashFlow.yearEnd " + name + " aaadd1 " + aaadd1++);
      if (E.debugEconCnt) {
        if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
          throw new MyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
        }

      }
    }
    cur.yearEnd();

    System.out.println("-----YEDPm ---- in Assets.yearEnd() near end " + name + (dead ? " DEAD" : " LIVE") + " myWidth" + EM.myWidth + (EM.mfShort ? " ++mfShort" : " --mfShort"));
    EM.mfShort = false;
    assert cur.c.balance == bals.A[2 + 1] : getName() + " c != bals.A[3], c=" + EM.mf(cur.c.balance.sum()) + " != bals c=" + EM.mf(bals.A[1 + 2].sum());
    if (false && E.debugMisc && !dead && !EM.dfe() && syW != null) {
      EM.newError = true;
      throw new MyErr("Assets.yearEnd says CashFlow.yearEnd did not null syW, probably skipped some code");
    }
    //

    cashFlowSubAssetBalances.copyValues(balances);
    cashFlowSubAssetsGrowths.copyValues(growths);
    // cashFlowSubAssetUnitsNeededToSurvive = mtgNeeds6;
    //cashFlowSubAssetUnitsAvailableToSwap = mtgAvails6;
    // in Assets.yearEnd
    EM.isHere("--CEYf--", ec, "after decrement of deterioration etc aaadd3=" + aaadd3++);
    if (E.debugEconCnt) {
      if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
        EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
      }
    }
    doNullCur(" yearEnd");
    if (E.debugEconCnt) {
      if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
        EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
      }

    }

    EM.wasHere = "end Assets.yearEnd aaadd4=" + aaadd4++;
  }

  void doNullCur(String where) {
    // Assets.doNullCur, zero yearly counters before yearStart
    eM.printHere("----DNC----", ec, " Assets.doNullCur at " + where);
    // tradedSuccessTrades = 0; // successful trades this year
    // tradedStrategicWorths = 0.; // positive strategic worths
    // tradedStrategicRealWorths = 0.; // real worths of successful trades
    //tradedStrategicCosts = 0.;// 2-3 least strategic value traded
    //  tradedStrategicRealCosts = 0.; // 2-3 real costs of trades
    // tradedManualsWorths = 0.;  // worth of manuals received in trades
    // tradingOfferWorth = 0;
    // tradeTravelCosts10 = null;
    // tradeTravelMaintCosts10 = null;
    // sumTradeTravelCosts = -1.;
    //  sumTradeTravelMaintCosts = -1.;
    //  sumTrade1YearTravelMaintCosts = -1.;
    //  tradeStrategicVars = null;
    // tradeGoodsNeeds = null;
    // if multiple ships trade in a year, this is for the last ship
    // tradedShipOrdinal = 0;
    // visitedShipOrdinal = 0;
    // econVisited = 0;
    // yrTradesStarted = -1;  // -1 if no trade this year
    // int[] tradedShipAccepted = new int[E.hcnt];
    tradedFav = 0.;
    tradedOFav = 0.;
    //   tradedFirstStrategicReceipts = 0.;
    // tradedFirstReceipts = 0.;
    // tradedFirstSends = 0.;
    //  tradedFinalStrategicReceipts = 0.;
    // tradedFinalReceipts = 0.;
    //tradedFinalSends = 0.;
    tradedFirstNegProspectsSum = 0;
    // double[] tradedGoodBal = new double[E.hcnt];
    // double[] tradedGoodWorth = new double[E.hcnt];
    //tradeAccepted = false;
    //tradeRejected = false;
    //tradeLost = false;
    // int acceptedTrade = -5;  // barter number of tradeOK
    // int rejectedTrade = -6; // barter number of rejected trad
    tradedBid = null;
    tradedStrategicValue = 0.;
    tradedStrategicFrac = 0.;
    tradedMoreManuals = null;
    // move names of traded ships up one year
    visitedShipNames[4] = visitedShipNames[3];
    visitedShipNames[3] = visitedShipNames[2];
    visitedShipNames[2] = visitedShipNames[1];
    visitedShipNames[1] = visitedShipNames[0];
    // String ttt[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
    // visitedShipNames[0] = ttt;

    //  now do prep for the next year with cur still valid
    //  endYearEnd = true;
    if (E.debugEconCnt) {
      if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
        EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
      }
    }
    EM.econCountsTest();
    //   endYearEnd = false;
    // bals.nullEndRows(); // free unneeded rows
    if (E.debugEconCnt) {
      if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
        EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
      }

    }

    cur = null; // release all CashFlow storage
  } // doNullCur

  /**
   * get the SOS flag
   *
   * @return Assets.sos
   */
  boolean getSOS() {
    return sos;
  }

  String getOtherName() {
    return otherName;
  }

  /**
   * return the guests balance ARow
   *
   * @return guests balance ARow
   */
  ARow getGuests() {
    return bals.getRow(ABalRows.BALANCESIX + ABalRows.GIX);
  }

  /**
   * get the guest grades reference from bals
   *
   * @return reference to guest grades
   */
  double[][] getGuestGrades() {
    return bals.getGuestGrades();
  }

  /**
   * return the cargo balances ARow reference
   *
   * @return cargo balance ARow
   */
  ARow getCargo() {
    return bals.getRow(ABalRows.BALANCESIX + ABalRows.CIX);
  }

  /**
   * pass on request to barter to CashFlow to Assets.CashFlow.Trades instantiate
   * CashFlow if needed
   *
   * @param inOffer the input offer
   * @return the output offer after processed by Assets.CashFlow.Trades
   */
  Offer barter(Offer inOffer) {  // Assets.barter

    newTradeYear1 = lastAcceptedYear != eM.year;
    yrphase yphase = yrphase.TRADE;
    if (lastAcceptedYear != eM.year) { //a new year barter
      newTradeYear1 = true;
      // lastAcceptedYear = eM.year; moved to Assets.CashFlow
      tradingShipName = inOffer.cnName[1]; // the ship name
      visitedShipNames[0][visitedShipOrdinal] = tradingShipName;
      int vl = visitedShipNames[0].length - 1;
      // don't overfill the array of names;
      visitedShipOrdinal = visitedShipOrdinal < vl ? visitedShipOrdinal + 1 : vl;
    }
    // lastAcceptedYear = eM.year; moved to Assets.CashFlow
    if (cur == null) {

      if (E.debugBarterOut) {
        eM.printHere("----ABR----", ec, " AsBarter create CashFlow");
      }
      cur = new CashFlow(this);
      cur.aStartCashFlow(this);
    }
    if (!didCashFlowStart) {
      cur.aStartCashFlow(this);
    }
    if (E.debugBarterOut) {
      eM.printHere(E.tradeInitOut, "----ABR2-----", ec, " Assets.barter tradeInitOut term=" + inOffer.getTerm());
    }
    Offer myIn = cur.barter(inOffer);
    if (EM.dfe()) {
      return inOffer;
    }
    // if exit trade exit cur
    if (cur.myTrade == null) {
      if (E.debugBarterOut) {
        eM.printHere("----ABR3----", ec, " myTrade null, delete CashFlow");
      }
      doNullCur(" Assets Barter");
      if (ec.clearHist()) {
        hist.clear();
      }

      // sumTrade1YearTravelMaintCosts = -1.;
      // tradeStrategicVars = null;
      // tradeGoodsNeeds = null;
    }
//    otherName = myIn.getOtherName();
    return myIn;
  }

  /**
   * return the current loop n
   *
   * @return
   */
  int getN() {
    if (cur == null) {
      return 0;
    }
    return cur.getN();
  }

  /**
   * return the saved swap n for the last calls in an array newest to oldest if
   * not instance of CashFlow exists return array of =8 else return array of ns
   * with -5 as an unset value newest values first
   *
   * @return the array of swap n
   */
  int[] getNSavd() {
    int ret[] = {-6, -6, -6, -6, -6};
    if (cur != null) {
      ret = cur.getNSavd();
    }
    return ret;
  }

  // end of Assets only methods
  // start subclasses
  /**
   * Assets.CashFlow This object holds one year of an economies values and the
   * processing that occurs in a year. The values are cumulative and hopefully
   * increase each year. import random values from Econ, and use them for the
   * entire year.
   *
   * Ships, select the next planet to travel to and trade. They may try to
   * caculate several years ahead, but of course planets change, so plans may
   * need revision
   *
   * At startYear The efficiencies for this year is calculated based on the
   * difficulty, priorities and amount of knowledge for each sector of the
   * economy, Minimal files are created to pass to Trades or yearEnd
   *
   * StarTrader initiates the barters (Trades) cycling through ships, which
   * selecting eligible planets. The trades trade some of the required resources
   * and staff for planets and ships, if Trades.accepted than both planet and
   * ship are strategically better with resources and staff.
   *
   * Then the Grow values are calculated for this year. With nominal grow
   * values, Raw costs are now calculated.
   *
   * The health and raw fertility are calculated. The health is the least healt
   * for any subsection of the economy Now a health penalty is applied to the
   * costs, increasing costs for health &lt; 1. decarasing costs for health &gt;
   * 1..
   *
   * Now swapping may occur, first move working resource or staff to or from
   * reserves. in emergency (health too low or fertility too low) staff can be
   * moved between sectors. Moved staff require some training to be back to
   * their former productivity repurposing resources is much more costly,
   * because repurposing is always inefficient, and costs both resources and
   * staff.
   *
   * The purpose of the growth stage is to try to end up with more value than
   * what you started with in this year. Usually, each economy has some very
   * limited (low priority) sectors with limited staff and resources, and some
   * very plentiful resources. Trades are supposed to be a way to even some of
   * this out, but that depends on where the ship was previously, and whether it
   * has a trade that the planet wants. Ships arrive with a random set of
   * resources and staff, widely varying in usefulness of Trade
   *
   * Maintenance travel and growth always cost both resource and staff. They
   * cost more when difficulty is high or knowledge is low. In addition, random
   * factors change costs from year to year.
   *
   * @author albert Steiner
   */
  class CashFlow {  // Assets.CashFlow

    Assets as2 = as0;
    // Cashflow relate to subassets
    SubAsset resource = new SubAsset(0);
    SubAsset staff = new SubAsset(2);
    SubAsset r = resource, s = staff;
    SubAsset guests = new SubAsset(3, true, staff); //reserves set partner.partner to themself
    SubAsset cargo = new SubAsset(1, true, resource);
    SubAsset c = cargo, g = guests;
    SubAsset[] sys = {resource, cargo, staff, guests};
    SubAsset[] partners = {cargo, resource, guests, staff};
    SubAsset[] others = {staff, guests, resource, cargo};
    SubAsset[] workers = {resource, staff};
    String[] subStrs = {"r", "c", "s", "g"};

    double sumBonusUnitGrowth = 0.;
    double ddepreciation = 0.;
    double sumCumulativeUnitBonus = 0.;
    double catEffRGBen = 0.;
    double catEffSGBen = 0.;
    double catEffManualsBen = 0.;
    double catEffKnowBen = 0;
    double catEffRDepreciationBen = 0.;
    double catEffSDepreciationBen = 0.;
    ARow ySectorPriorityYr = new ARow(ec);
    A6Row invMEfficiency = new A6Row(ec, lev, "invMEfficiency");// in bals
    A6Row invGEfficiency = new A6Row(ec, lev, "invMEfficiency");
    // start CashFlow Swap loop variables
    boolean debugSumGrades2 = false;
    boolean flagg, flagh, flagf, flagm, prevFlagg, prevFlagh, prevFlagf, prevFlagm;
    boolean emergHs, emergHr, emergency, preveHs, preveHr, notDone;
    boolean doFailed, doLoop, done;
    boolean hFlag, gFlag, gfFlag, fFlag, geFlag, nheFlag, heFlag, gmFlag, hmFlag;
    boolean emergFs, emergFr, failed = false, hTrue = false, gTrue = false;
    boolean hEmerg = false, gEmerg = false;
    boolean incrMinProsperity, incrMinFertility, incrWorth;
    double prevMinProsperity = -100., prevMinFertility = -100, prevTotWorth = -100.;
    //0-2 inc, 3-5 decr, 6-8 xfer
    double maxMult[] = {1., .8, .5, 1., .8, .5, 1., .8, .6};
    int swapLoopMax = 3;
    int nSavd[] = {-5, -5, -5, -5, -5}; // saved n values from swaps
    int unDo = 0, nn = 0, reDo = 0;
    A10Row doNot = new A10Row(ec, lev, "doNot");
    int stopped[] = {0, 0, 0, 0, 0}, stopX[] = {5, 5, 0, 0, 0};
    boolean useMTCosts = false;
    // end loop variables
    boolean tEmerg = false;

    String yrName = "startYr";
    String lTitle = "Init";
    //  String[] sysSs = {"r", "c", "s", "g", "null"};
    //  String[] sysS = {"resource", "cargo", "staff", "guests", "null"};
    int rlev, blev; // flags for level of History and if statements for it
    int swapAlt = 0; // swapLoops%2  0,1
    int costsComp = -10;
    int costsUse = -10;  // recompute costs of costsUse > costsComp;
    double yearStartHealth = 2.0;
    double fertility = 2., minH = -.5, minFert = -.3, phe = -1, poorHealthEffect = -1;
    double resilience = -1, hope = -1;
    int ixArow;
    int srcIx = -2, destIx = -2, forIx = -2, ixWRFor = -2, chrgIx = -2, needIx = -2, need4Ix = -2, need3Ix = -2, sourceIx = -2;
    int rChrgIx = -2, sChrgIx = -2;
    double rChrg, sChrg;

    int swapLoops = -2, swap4Step = -2, swap7Step = -2;
    double[] catastrophyBalIncr = new double[E.hcnt];
    double[] catastrophyPBalIncr = new double[E.hcnt];
    double[] catastrophyDepreciationBalDecr = new double[E.hcnt];
    //double[] catastrophyDepreciationPBalDecr = new double[E.hcnt];
    /**
     * now define ARows to carry history, see swapResults
     */
    final double doNotDays2 = 2;
    final double doNotDays3 = 3.;
    final double doNotDays5 = 5.;
    final double doNotDays100 = 100.;
    ARow yprevGrowth = new ARow(ec);   // last years actual growth
    ARow yprevUnitGrowth = new ARow(ec);

    ARow ylimLowSGbyR = new ARow(ec);
    ARow ylimMidRCbyR = new ARow(ec);
    ARow ylimMidSGbyR = new ARow(ec);
    ARow ylimHiRCbyR = new ARow(ec);
    ARow ylimHiSGbyR = new ARow(ec);
    /**
     * the following used by Assets.CashFlow.swaps
     */
    final int lPrevns = 9;// in trade.Assets.CashFlow
    HSwaps[] prevprevns; // previous number swap values
    HSwaps[] prevns = new HSwaps[lPrevns];
    HSwaps[] prevgood = new HSwaps[lPrevns];
    boolean negProspects = false, negOutlook = false, negNeeds = false;
    boolean wrongIxSrc = false, doneIncr = false;
    ARow swapRtoC = new ARow(ec);
    ARow swapCtoR = new ARow(ec);
    ARow swapStoG = new ARow(ec);
    ARow swapGtoS = new ARow(ec);
    ARow xferRCtoR = new ARow(ec);
    ARow xferSGtoS = new ARow(ec);
    A2Row xferRCSG = new A2Row(xferRCtoR, xferSGtoS);
    ARow tneed = new ARow(ec);
    ARow tMove = new ARow(ec);
    ARow tRneed = new ARow(ec);
    ARow tRMove = new ARow(ec);
    ARow tSneed = new ARow(ec);
    ARow tSMove = new ARow(ec);
    ARow tRcost = new ARow(ec);
    ARow tScost = new ARow(ec);
    double fmov = 0, smov = 0, rmov = 0, movMin = 0, mkeep = 0;
    double rmov1 = 0., smov1 = 0.;
    double xferMovMin;

    double swapMins[] = new double[2];
    double swapMaxs[] = new double[2];
    boolean tests = true, testr = true, doxfer = false;
    History errHistory;
    SubAsset dest = staff;
    SubAsset source = guests;
    SubAsset osource = cargo;
    SubAsset rchrg = resource;
    SubAsset schrg = staff;
    SubAsset prevrchrg = resource;
    SubAsset prevschrg = staff;
    SubAsset forRes;
    /*   double fFrac = E.usrFutureMaxn[pors][clan];
     double gFrac = E.usrGrowthMaxn[pors][clan];
     double gfFrac = E.usrGrowFirstMaxn[pors][clan];
     double hFrac = E.usrHealthMaxn[pors][clan];
     double hgFrac = E.usrHealthGrowMaxn[pors][clan];
     */

    // Assets holds iyW,syW
    // in Assets.CashFlow
    DoTotalWorths btW, tW, rawCW, preSwapW, gSwapW, gGrowW, gCostW, fyW;
    double rawCWTotWorth = 0., preSwapWTotWorth = 0., gSwapWTotWorth = 0., gGrowWTotWorth = 0., gCostWTotWorth = 0.;
    double gSwapIncr = 0.;  // Incr of worth over swaps
    double btWrcsgSum;
    double NeedsPlusSum, NeedsNegSum, rawProspectsMin, rawProspectsMin2, rawProspectsNegSum;
    double curSum, needsSum;
// Assets.CashFlow declarations
    double rFutureFundDue = 0., sFutureFundDue = 0., rEmergFutFund = 0., sEmergFutFund = 0.;
    double movd = 0., rcost = 0., scost = 0., need = 0., tmin = 0.;
    double prevsrc = 0., prevdest = 0., prevosrc = 0., prevodest = 0.;
    double fracN = n / eM.maxn[pors];
    int prevn;
    double prevNextN;
    double nextN;
    double yHealthPenalty;
    double ysumPriority;

    double sumRCWorth = 0.;
    double sumSGWorth = 0.;
    double preTradeWorth = 0.;
    double sendSum = 0.;
    double postNHealth;
    double postNHealthWorth;
    double tradeAvailIncrPercent;
    double fracPostTrade;
    double fracPreTrade;
    double percentValuePerGoal;
    double worthIncrPercent;
    double worthIncr;
    double additionToKnowledgeBiasForSumKnowledge
            = eM.additionalKnowledgeGrowthForBonus[0] / 1.5;
    double multiplierForEfficiencyFromRequirements
            = eM.additionalKnowledgeGrowthForBonus[0] / 1.4;

    // required for  Assets.CashFlow.getNeeds
    int bLev = History.dl;
    double totNeeds = -999999.;
    int swapType = 7;
    double[] redoFrac = {1., 1., .85, .75, .5, .3, .1, .05562};
    double mov = -1., mov1 = -1., mov2 = -2., mov3 = -1., mov4 = -1.;
    int myEconCnt = 0;

    double minRH, minRF;
    int minRHIx = -1, minRFIx = -1;
// in Assets.CashFlows  declarations
    A6Row worths = new A6Row(ec, lev, "worths");
    A6Row yrStrtWorths = new A6Row(ec, lev, "yrStrtWorths");
    // swapCosts 0,1 incr, 2,3 decr, 4,5 exchange
    int rxfers = 0, sxfers = 0; //count of continuous r or s xfers
    A6Row swapCosts = new A6Row(ec, lev, "swapCosts");
    A2Row stratVarsHG = new A2Row(ec);
    A2Row stratMult = new A2Row(ec);
    A2Row stratVars = new A2Row(ec);

    //   A6Row reqMaintRemnants = new A6Row(ec,lev, "reqMaintRemnants");
    //   A6Row reqMaintEmergNeeds = new A6Row(ec,lev, "reqMaintEmergNeeds");
//    A6Row reqMaintNeeds = new A6Row(ec,lev, "reqMaintNeeds");
    A6Row invMEff = new A6Row(ec, History.valuesMinor7, "invMEff");
    A6Row invGEff = new A6Row(ec, History.valuesMinor7, "invGEff");
    //   A6Row reqMaintFractions = new A6Row(ec,lev, "reqMaintFractions");
    //   A6Row reqMaintLimitedFractions = new A6Row(ec,lev, "reqMaintLimitedFractions");

    int decrCnt = 0;
    double decrGain = 0;
    double decrCost = 0;
    int typeGrow = 2;
    int typeTrade = 0;
    int typeHealth = 1;
    int flowType = 0;
    double curGrowGoal, adjGrowGoal, adjVal;
    double curMaintGoal, adjMaintGoal;
    // from initTrade
    A6Row preTradeMtgAvails6; // avails in initTrade
    A6Row preTradeBalances; // from initTrade
    /**
     * now Assets.CashFlow variables for yCalcCosts
     */
    A6Row rawGrowths = new A6Row(ec, lev, "rawGrowths");
    A6Row reqMaintCosts = new A6Row(ec, lev, "reqMaintCosts");
    A6Row maintCosts = new A6Row(ec, lev, "maintCosts");
    A6Row aYrTravelCosts = new A6Row(ec);
    A6Row travelCosts = new A6Row(ec, lev, "travelCosts");
    A6Row healths = new A6Row(ec, lev, "healths");
    A6Row reqGrowthCosts = new A6Row(ec, lev, "reqGrowthCosts");
    A6Row rawGrowthCosts = new A6Row(ec, lev, "rawGrowthCosts");
    A6Row mtgCosts = new A6Row(ec, lev, "mtgCosts");
    A6Row bonusGrowthFrac6 = new A6Row(ec);

    //Assets.CashFlow for getNeeds(
    A6Row goalmtg1Needs6 = new A6Row(ec, lev, "goalmtg1Needs6");
    A10Row goalmtg1Neg10 = new A10Row(ec, lev, "goalmtg1Neg10");
    A6Row mNeeds = new A6Row(ec, History.valuesMajor6, "mNeeds");
    A10Row mtggCosts10 = new A10Row(ec, lev, "mtggCosts10");    //   A6Row mtggRemnants = new A6Row();
    //   A6Row mtggEmergNeeds = new A6Row(ec,lev, "mtggEmergNeeds");
    A6Row mtgFertilities = new A6Row(ec, lev, "mtgFertilities");
    //  A6Row rawGoalFertilities = new A6Row(ec,lev, "rawGoalFertilities");
    //  A6Row rawGoalHealths = new A6Row(ec,lev, "rawGoalHealths");
    A6Row mtggRawHealths = new A6Row(ec, lev, "rawMTGGHealths");
    A6Row mtggRawFertilities = new A6Row(ec, lev, "rawMTGGFertilities");
    A2Row fertilities = new A2Row(ec, lev, "fertilities");  // mtggRemnants/reqGrowthCosts
    A6Row growthCosts = new A6Row(ec, lev, "growthCosts");  // rawGrowthCost*fertilities
    A6Row mtggGrowthCosts = new A6Row(ec, lev, "mtggGrowth");  // rawGrowthCost*fertilities
    A6Row mtgReqFertilities = new A6Row(ec);
    //Assets.CashFlow for getNeeds(
    A10Row reqMaintCosts10;
    A10Row reqGrowthCosts10;
    A10Row rawGrowthCosts10;
    A10Row maintCosts10;
    A10Row travelCosts10, mtgCosts10, mtCosts10, growthCosts10;
    A10Row consumerHealthMTGCosts10, consumerTrav1YrCosts10, consumerMaintCosts10;
    A10Row consumerReqGrowthCosts10, consumerReqMaintCosts10, consumerTravelCosts10, consumerFertilityMTGCosts10;
    A10Row consumerHealthEMTGCosts10, consumerFertilityEMTGCosts10;
    A10Row consumerRawGrowthCosts10;
    A10Row nTrav1Yr;
    A6Row consumerMTC6, consumerEMTC6, consumerEMTGC6;
    A10Row pmNegs = new A10Row(ec, History.valuesMajor6, "pmNegs");
    A10Row ptNegs = new A10Row(ec, History.valuesMajor6, "ptNegs");
    // A2Row rawHealths2;
    A2Row fertilities2;
    A2Row mtggRawProspects2 = new A2Row(ec);
    A2Row mtggRawFertilities2;
    A2Row mtggRawHealths2;
    A6Row mtggGrowths6 = new A6Row(ec, lev, "mtggGrowths6");
    A6Row mtNeeds6 = new A6Row(ec, lev, "mtNeeds6");
    //int lResults = 40;
    // double[] mtgResults = new double[lResults];
    //double[] mtggResults = new double[lResults];
    //double[] hgResults = new double[lResults];
    //double[] heResults = new double[lResults];
    //double mtggSumRemnant = 0;
    // double mtgSumRemnant = 0;
    // A6Row reqMaintRawFractions = new A6Row();
    // double mtgResults10[] = new double[lResults];
    //double mtggResults10[] = new double[lResults];
    //Assets.CashFlow for getNeeds(
    A6Row growths10;
    A6Row mtggNeeds6, mtGNeeds6;

    double maxAvail = 0.; // max available for a given swap
    double maxavail1, maxavail2, maxavail3, maxavail4;

    //values Assets.CashFlow for Assets.CashFlow.Trades
    double requests, offers, unitRequests, unitOffers, unitGets;
    double totalReceipts, totalSend, totalRequests, totalOffers, needs;
    double strategicRequests = 0., strategicOffers = 0., strategicFrac = 0., totalStrategicRequests = 0.;
    double sumNominalRequests = 0., sumNominalOffers = 0.;
    double sumCriticalStrategicRequests = 0., criticalStrategicOffers = 0., criticalStrategicFrac = 0.;
    double sumCriticalNominalRequests = 0;
    double totalStrategicOffers = 0;
    double totalStrategicFrac = 0;
    double nominalRequests = 0., nominalOffers = 0., nominalFrac = 0.;
    double criticalNominalRequests = 0., criticalNominalOffers = 0., criticalNominalFrac = 0.;
    double totalStrategicRequestsFirst = totalStrategicRequests;
    double totalStrategicOffersFirst = totalStrategicOffers;
    double totalNominalOffers = 0.;
    double totalStrategicFracFirst = totalStrategicFrac;
    double strategicFracFirst, strategicValueFirst;
    double offeredManualsValue = 0., requestedManualsValue = 0.;
    double requestsFirst, offersFirst, sumRequestsFirst, totalSendFirst, totalRequestsFirst, totalOffersFirst, needsFirst, sendSumFirst, sumCriticalStrategicRequestsFirst;
    double strategicReceiptsFirst = 0., strategicOffersFirst = 0., totalStrategicReceiptsFirst = 0.;
    double sumNominalRequestsFirst = 0.;
    double criticalStrategicRequestsFirst, criticalStrategicOffersFirst, criticalStrategicFracFirst;
    double sumCriticalBidRequests, sumBidRequests, sumCriticalBidRequestsFirst, sumBidRequestsFirst;
    double nominalRequestsFirst = 0., nominalOffersFirst = 0., nominalFracFirst = 0.;
    double nominalRequestsSum = 0., nominalRequestsSumFirst = 0.;
    double criticalNominalRequestsFirst = 0., criticalNominalOffersFirst = 0., criticalNominalFracFirst = 0.;
    double pCash = cash;
    double availCash = rawProspects2 == null ? cash : rawProspects2.min() < .2 ? rawProspects2.min() < NZERO ? cash - 0 : cash - 500 : cash - 1000;
    double excessOffers = 0.;
    double offeredManuals, requestedManuals;
    /**
     * Assets.CashFlow start to add costs that apply to the R and S balances
     * first the maintenance and travel costs with the health penalty applied
     * yRMTNPCost etc Than the growth cost with RawFertility for this sector
     * applied then the healthPenalty give yRMTLimitedGNoPenaltyCost etc
     * remnants are these costs subtracted from R and S, note that costs for C
     * apply to yR... and costs for G apply to yS... finally r.tRemnant and
     * s.tRemnant is the balance left after the costs
     */
    ARow yR2MTNPRemnant = new ARow(ec);
    ARow yS2MTNPRemnant = new ARow(ec);
    ARow yRMTLimitedGNoPenaltyCost = new ARow(ec);
    ARow ySMTLimitedGNoPenaltyCost = new ARow(ec);
    ARow yRMTCostsHPenRemnant = new ARow(ec);
    ARow ySMTCostsHPenRemnant = new ARow(ec);
    ARow ySReqGrowthMTNPCost = new ARow(ec);
    ARow yWReqGrowthMTNPCost = new ARow(ec);
    ARow yRReqGrowthMTNPRemnant = new ARow(ec);
    ARow yWReqGrowthMTNPRemnant = new ARow(ec);
    ARow yWtoSFrac = new ARow(ec);  // balance divby work
    ARow yRRawGrowthMTNPRemnant = new ARow(ec);
    ARow ySRawGrowthMTNPRemnant = new ARow(ec);
    ARow yRRawGrowthHPenRemnant = new ARow(ec);
    ARow ySRawGrowthHPenRemnant = new ARow(ec);
    ARow yIovrJRRawGMTCosts = new ARow(ec);
    ARow yIovrJSRawGMTCosts = new ARow(ec);
    ARow yRRawMTGHPenRemnant = new ARow(ec); // full growth remnant
    ARow ySRawMTGHPenRemnant = new ARow(ec);
    ARow yRMTFRemnant = new ARow(ec); // fertility growth remnant
    ARow ySMTFRemnant = new ARow(ec);
    ARow ySLimitedGHPenCosts = new ARow(ec);
    ARow yRMTLimitedGHPenRemnant = new ARow(ec);
    ARow yS2MTLimitedGHPenRemnant = new ARow(ec);
    ARow yR2MTLimitedGHPenRemnant = new ARow(ec);
    ARow yRReqGrowthMTHPenCost = new ARow(ec);
    ARow ySReqGrowthMTHPenCost = new ARow(ec);
    ARow yRReqGrowthMTHPenRemnant = new ARow(ec);
    ARow ySReqGrowthMTHPenRemnant = new ARow(ec);
    ARow yWReqMaintHealth = new ARow(ec);
    ARow yCCostsRemnant = new ARow(ec);
    ARow ySVCostsRemnant = new ARow(ec);
    ARow yRFuture1 = new ARow(ec);   // future 1 more year
    ARow ySFuture1 = new ARow(ec);
    ARow yRFuture2 = new ARow(ec);
    ARow ySFuture2 = new ARow(ec);

    SwpCmd cmd = SwpCmd.NONE;
    boolean swapped = false;
    ARow rneed = new ARow(ec); // temp value for a given swap
    ARow sneed = new ARow(ec);  // temp value for a given swap
    ARow yrneed = new ARow(ec);
    ARow ysneed = new ARow(ec);
    A2Row yNeed = new A2Row(yrneed, ysneed);
    ARow ymove = new ARow(ec);

    // Assets.CashFlow
    boolean donext = false, sEmerg = false, rEmerg = false;
    double rresmult, sresmult, rresmult2, sresmult2;
    ARow wtdRtoC = new ARow(ec);
    ARow wtdStoG = new ARow(ec);
    A2Row wtdRS = new A2Row(wtdRtoC, wtdStoG);
    A6Row rawUnitGrowths;
    // HCashFlow prev1n, prev2n, prev3n, prev4n, prev5n, prev6n, prev7n;
    // HCashFlow[] startYrs; // might use instead of name 1,2 ...

    // in Assets.CashFlow
    HSwaps xitCalcCosts;
//    A2Row yRemnant = new A2Row(r.tRemnant, ySRemnant);

    Offer myOffer;
    Offer oOffer;
    Offer newOffer;

    ARow yRAvail = new ARow(ec);
    ARow ySAvail = new ARow(ec);
    //Assets.CashFlow
    Assets as3, as;
    NumberFormat dFrac = NumberFormat.getNumberInstance();
    NumberFormat whole = NumberFormat.getNumberInstance();
    NumberFormat dfo = dFrac;

    /**
     * CashFlow handle the yearly cashflow of assets
     *
     * @param aas reference to Assets
     */
    CashFlow(Assets aas) {
      System.out.flush();
      System.out.flush();
      System.err.flush();
      StackTraceElement[] stk = Thread.currentThread().getStackTrace();
      StackTraceElement a1 = stk[1];
      StackTraceElement a2 = stk[2];
      StackTraceElement a3 = stk[3];
      StackTraceElement a4 = stk[4];
      eM.printHere(E.DEBUGASSETSOUT, "-----CFc----", ec, "construct  " + E.ROYGB.charAt(clan) + " " + yphase + " at " + stk[4].getMethodName() + ", " + stk[3].getMethodName() + ", " + stk[2].getMethodName() + " wealth=" + EM.mf(wealth));
      //if(E.DEBUGASSETSOUT)System.out.println("-----CFc----CF construct  " + E.ROYGB.charAt(clan) + " " + name + " " + yphase + " at " + stk[4].getMethodName() + ", " + stk[3].getMethodName() + ", " + stk[2].getMethodName() + " wealth=" + EM.mf(wealth));
      //    System.out.println("CashFlow(Assets) " + name + " constructor");
      as = as3 = as2 = aas;
    } // end constructor of CashFlow

    String df(double v) {
      return EM.mf(v);
    }

    /**
     * an object which contains many total sums at the time of instantiation but
     * it does not contain a reference to CashFlow so that although it
     * calculates values with expressions using Assets and Assets.CashFlow
     * references to DoTotalWorths do not hold the memory that a reference to
     * Assets.CashFlow would.
     *
     * @note sumTotWorth
     * @note sumRCWorth
     * @note sumSGWorth
     * @note sumCommonKnowledgeBal
     * @note sumCommonKnowledgeWorth
     * @note sumeNewKnowledgeWorth
     * @note sumKnowledgeWorth
     * @note sumManualsWorth
     * @note sumRBal;
     * @note sumCBal;
     * @note sumRCBal;
     * @note sumSBal;
     * @note sumGBal;
     * @note sumSGBal; Wnote sumRCSGBal;
     */
    class DoTotalWorths { // Assets.CashFlow.DoTotalWorths

      DoTotalWorths now, prev; // connection to previous and current DoTotalWorths
      // process to remember the previous
      double sumSBal = staff.sumGrades();
      double sumGBal = guests.sumGrades();
      //  double minSGBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.SGIX).min();
      // double minRCBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.RCIX).min();
      double sumRBal = bals.getRow(bals.BALANCESIX + bals.RIX).sum();
      double sumCBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.CIX).sum();
      double sumRCBal = sumRBal + sumCBal;
      double sumSGBal = sumSBal + sumGBal;
      //  double minGBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.GIX).min();
      //  double minCBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.CIX).min();
      //  double minSBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.SIX).min();
      //  double minRBal = bals.getRow(ABalRows.BALANCESIX + ABalRows.RIX).min();
      double[] sumBals = {sumRCBal, sumSGBal, sumRBal, sumCBal, sumSBal, sumGBal};
      //   double[] sumMins = {minRCBal, minSGBal, minRBal, minCBal, minSBal, minGBal};
      double sumRCSGBal = sumRCBal + sumSGBal;
      double minFertility = rawFertilities2.curMin();
      double sumFertility = rawFertilities2.curSum();
      double aveFertility = sumFertility / 14.;
      double sumTotBalances = sumRCSGBal;
      double prospectsAve = rawProspects2.ave();
      double prospectsMin = rawProspects2.min();
      double sumSWorth = staff.worth.sum();
      double sumGWorth = guests.worth.sum();
      double sumRWorth = sumRBal * eM.nominalWealthPerResource[pors];
      double sumCWorth = sumCBal * eM.nominalWealthPerResource[pors] * eM.cargoWorthBias[0];
      double sumRCWorth = sumRWorth + sumCWorth;
      double sumSGWorth = sumSWorth + sumGWorth;
      double[] sumWorths = {sumRCWorth, sumSGWorth, sumRWorth, sumCWorth, sumSWorth, sumGWorth};
      A6Row myBalances = bals.copyBalances(History.valuesMinor7, "balances");
      double[] worthPerSubBal = {eM.nominalWealthPerResource[pors], eM.nominalWealthPerResource[pors] * eM.cargoWorthBias[0], eM.nominalWealthPerStaff[pors], eM.nominalWealthPerStaff[pors] * eM.guestWorthBias[0]}; //
      double myCash = cash;
      //ARow myCommonKnowledge = commonKnowledge.copy();
      double sumCommonKnowledgeBal = make(commonKnowledge).sum();
      double sumCommonKnowledgeWorth = sumCommonKnowledgeBal * eM.nominalWealthPerCommonKnowledge[0];
      // ARow myNewKnowledge = newKnowledge.copy();
      double sumNewKnowledgeBal = make(newKnowledge).sum();
      double sumNewKnowledgeWorth = sumNewKnowledgeBal * eM.fracNewKnowledge[0] * eM.nominalWealthPerCommonKnowledge[0];
      //   ARow myKnowledge = knowledge.copy();
      double sumKnowledgeBal = sumCommonKnowledgeBal + sumNewKnowledgeBal;
      double sumKnowledgeWorth = sumCommonKnowledgeWorth + sumNewKnowledgeWorth;
      ARow myManuals = manuals.copy();
      double sumManualsBal = manuals.sum();
      double sumManualsWorth = sumManualsBal * eM.manualFracKnowledge[pors] * eM.nominalWealthPerCommonKnowledge[0];
      ;
      double sumTotWorth = sumRCWorth + sumSGWorth + cash + sumKnowledgeWorth + sumManualsWorth;
      double myResilience = resilience; // isolate from Assets
      double myHope = hope;
      //   double costs =
      // int aLev = History.valuesMinor7;
      //  A6Row myGrowths = bals.getGrowths(History.valuesMinor7, "myGrowths");
      //  A6Row myGrowths = growths.copy(ec);
      //double sumTotGrowths = growths.curSum();
      //A6Row totRawGrowths; // = A6Rowa.copy6(ABalRows.rawGrowths, "rawGrowths").copy(History.valuesMinor7);
      //    double difGrowthsWorth;
      //  A10Row myGrowthCosts10 = rawGrowthCosts10.copy10(aLev,"myGrowC10");
      //A10Row myReqGrowthCosts10 = reqGrowthCosts10.copy10(aLev,"myReqGrowC10");
      // A10Row myReqMaintCosts10 = reqMaintCosts10.copy10(aLev,"myReqMaintC10");
      //A10Row myMaintCosts10 = maintCosts10.copy10(aLev,"myMaintC10");
      //A10Row myTravCosts10 = travelCosts10.copy10(aLev,"myTravC10");
      //  double sumTotGrowthCosts = make10(growthCosts10, "growthCosts10").curSum();
      double difWorth;
      //     A2Row myRawProspects2 = rawProspects2.copy();
      //     A6Row myAvails6 = mtgAvails6.copy(ec);
//      A6Row myInvMEfficiency = invMEfficiency.copy(ec);
      //   double sumTotInvMEff = totInvMEff.curSum();
      //     A6Row myInvGEfficiency = invGEfficiency.copy(ec);
      //  double sumTotInvGEff = totInvGEff.curSum();
      //   A6Row myCumDepreciation = bals.getCumDepreciation(History.valuesMinor7, "totCumDepreciation").copy(ec);
      //   double sumTotCumDepreciation = totCumDepreciation.curSum();
      //   double[] sumTotBonusValues = {r.bonusUnitGrowth.sum(), s.bonusUnitGrowth.sum()};
      //   int sumTotBonusYears = (int) (r.bonusYears.sum() + s.bonusYears.sum());
//DoTotalWorths iyW, syW, tW, rawCW,gSwapW, gGrowW, gCostW, fyW;

      DoTotalWorths() {

        // set prev to the previous now = this; if null prev = this;
        if (now != null) {
          prev = now;
        } // set up prev pointer
        else {
          prev = this;
        }
        now = this;
        double aMin = rawProspects2.min();
        ARow resil = new ARow(ec);
        for (int resilIx = 0; resilIx < E.LSECS; resilIx++) {
          resil.set(resilIx, rawProspects2.get(0, resilIx) * rawProspects2.get(1, resilIx));
        }
        resilience = resil.max() * aMin / resil.ave();
        hope = resilience * phe;
        //       assert sumRCBal > 0.0:"error " + name + " Neg RCbal=" + EM.mf(sumRCBal);
      } // end DoTotalWorth()

      /**
       * get fraction of sum of SG balances divided by sum of RC balances
       *
       * @return sumSGBal/sumRCBal
       */
      //   double getSGFracRCSum() {
      //     return sumSGBal / sumRCBal;
      //   }
      /**
       * get the measure of SG min against SG ave
       *
       * @return ((sumSGBal*E.invL2secs - minSGBal)/sumSGBal)*E.invL2secs;
       */
      //    double getSGMinFracAve() {
      //     return ((sumSGBal * E.invL2secs - minSGBal) / sumSGBal) * E.invL2secs;
      //   }
      /**
       * get the fraction of the difference of a balance since difPrev by
       * fracPrev
       *
       * @param iX index of element: RCIX,SGIX,RIX,CIX,SIX,GIX
       * @param difPrev the previous DoTotalWorths
       * @param fracPrev the standard divisor
       * @return (sumBals[2+iX] - difPrev.sumBals[2+iX])/
       * fracPrev.sumBals[2+iX];
       */
      //     double getSumIXDifPrevFracPrev(int iX, DoTotalWorths difPrev, DoTotalWorths fracPrev) {
      //     return (sumBals[2 + iX] - difPrev.sumBals[2 + iX]) / fracPrev.sumBals[2 + iX];
      //   }
      /**
       * get the fraction of the difference of a minimum since difPrev by
       * fracPrev
       *
       * @param iX index of element: RCIX,SGIX,RIX,CIX,SIX,GIX
       * @param difPrev the previous DoTotalWorths
       * @param fracPrev the standard divisor
       * @return (sumMins[2+iX] - difPrev.sumMins[2+iX])/ fracPrev.sumMins[2+iX]
       */
      //   double getMinIXDifPrevFracPrev(int iX, DoTotalWorths difPrev, DoTotalWorths fracPrev) {
      //    return (sumMins[2 + iX] - difPrev.sumMins[2 + iX]) / fracPrev.sumMins[2 + iX];
      //  }
      /**
       * get the fraction of the difference of a worth since difPrev by fracPrev
       *
       * @param iX index of element: RCIX,SGIX,RIX,CIX,SIX,GIX
       * @param difPrev the previous DoTotalWorths
       * @param fracPrev the standard divisor
       * @return sumWorths[2+iX] - difPrev.sumWorths[2+iX])/
       * fracPrev.sumWorths[2+iX]
       */
      //     double getWorthIXDifPrevFracPrev(int iX, DoTotalWorths difPrev, DoTotalWorths fracPrev) {
      //      return (sumWorths[2 + iX] - difPrev.sumWorths[2 + iX]) / fracPrev.sumWorths[2 + iX];
      //     }
      /**
       * get the sum of total worth
       *
       * @return sumTotWorth
       */
      double getTotWorth() {
        return sumTotWorth;
      }

      /**
       * get the sum of all knowledge balances
       *
       * @return knowledge sum
       */
      double getSumKnowledgeBal() {
        return sumKnowledgeBal;
      }

      /**
       * get the sum of all commonKnowledge balances
       *
       * @return commonKnowledge sum
       */
      double getSumCommonKnowledgeBal() {
        return sumCommonKnowledgeBal;
      }

      /**
       * get the sum of all newKnowledge Balances
       *
       * @return newKnowledge sum
       */
      double getSumNewKnowledgeBal() {
        return sumNewKnowledgeBal;
      }

      /**
       * get the sum of all manuals Balances
       *
       * @return manuals sum
       */
      double getSumManualsBal() {
        return sumManualsBal;
      }

      /**
       * get the sum of all manuals Worth
       *
       * @return sumManualsWorth sum
       */
      double getSumManualsWorth() {
        return sumManualsWorth;
      }

      /**
       * get the sum of all newKnowledge Worth
       *
       * @return sumNewKnowledgeWorth sum
       */
      double getSumNewKnowledgeWorth() {
        return sumNewKnowledgeWorth;
      }

      /**
       * get the sum of all KnowledgeWorth Balance
       *
       * @return sumNewKnowledgeWorth sum
       */
      double getSumKnowledgeWorth() {
        return sumKnowledgeWorth;
      }

      /**
       * get the sum of all commonKnowledge Worths
       *
       * @return sumCommonKnowledgeWorth sum
       */
      double getSumCommonKnowledgeWorth() {
        return sumCommonKnowledgeWorth;
      }

      /**
       * get the sum of all SG Balances
       *
       * @return SG Balance Sum
       */
      double getSumSGBal() {
        return sumSGBal;
      }

      /**
       * get the sum of all S Balancs
       *
       * @return S Balance Sum
       */
      double getSumSBal() {
        return sumSBal;
      }

      /**
       * get the sum of all G Balances
       *
       * @return G Balance Sum
       */
      double getSumGBal() {
        return sumGBal;
      }

      /**
       * get the sum of all RC Balances
       *
       * @return RC Balance Sum
       */
      double getSumRCBal() {
        if (E.debugMisc && sumRCBal == 0.0) {
          throw new MyErr("sumRCBal should not be zero = " + E.mf(sumRCBal));
        }
        return sumRCBal;
      }

      /**
       * get the sum of all RCSG Balances
       *
       * @return RC Balance Sum
       */
      double getSumRCSGBal() {
        if (E.debugMisc && sumRCSGBal == 0.0) {
          throw new MyErr("sumRCSGBal should not be zero = " + E.mf(sumRCSGBal));
        }
        return sumRCSGBal;
      }

      /**
       * get the sum of all R Balances
       *
       * @return R Balance Sum
       */
      double getSumRBal() {
        return sumRBal;
      }

      /**
       * get the sum of all C Balances
       *
       * @return C Balance Sum
       */
      double getSumCBal() {
        return sumCBal;
      }

      /**
       * get sum of RC Worth
       *
       * @return sum RC Worth
       */
      double getSumRCWorth() {
        return sumRCWorth;
      }

      /**
       * get sum of SG Worth
       *
       * @return sum SG Worth
       */
      double getSumSGWorth() {
        return sumSGWorth;
      }

      /**
       * get RC sum difference from the prev instance
       *
       * @return (rc - prev rc) / prev rc
       */
      double getRCDif() {
        return (getSumRCBal() - prev.getSumRCBal()) / prev.getSumRCBal();
      }

      /**
       * get R sum difference from the prev instance
       *
       * @return (r - prev r) / prev r
       */
      double getRDif() {
        return (getSumRBal() - prev.getSumRBal()) / prev.getSumRBal();
      }

      /**
       * get C sum difference from the prev instance
       *
       * @return (c - prev c) / prev c
       */
      double getCDif() {
        return (getSumCBal() - prev.getSumCBal()) / prev.getSumCBal();
      }

      /**
       * get SG sum difference from the prev instance
       *
       * @return (sg - prev sg) / prev sg
       */
      //    double getSGDif() {
      //      int n = 1;
      //       return (getSumSGBal() - prev.getSumSGBal()) / prev.getSumSGBal();
      //  }
      /**
       * get S sum difference from the prev instance
       *
       * @return (s - prev s) / prev s
       */
      double getSDif() {
        int n = 1;
        return (getSumSBal() - prev.getSumSBal()) / prev.getSumSBal();
      }

      /**
       * get G sum difference from the prev instance
       *
       * @return (g - prev g) / prev g
       */
      double getGDif() {
        int n = 1;
        return (getSumGBal() - prev.getSumGBal()) / prev.getSumGBal();
      }

      /**
       * get a reference to Balances
       *
       * @return internal Balances
       */
      A6Row getTBalances() {
        return myBalances;
      }

      DoTotalWorths setPrev(DoTotalWorths aPrev) {
        return prev = aPrev;
      }

    }  // end class DoTotalWorth

    /**
     * start declaration of subclass SubAsset the subclass has access to all
     * methods and objects in Assets and Assets.CashFlow, but does not contain
     * them in each instance. as if it were and extension of Assets.
     * <p>
     * The SubAssets are contained by Assets.CashFlow both class instances are
     * short lived so that all of their variables are instanciated for the whole
     * game. Enduring values are held in the Assets class and are then
     * referenced in instances of Assets.CashFlow and Assets.CashFlow.SubAsset.
     * <P>
     * Four instances of SubAssets are created in each instance of CashFlow.
     * They are <dl><dt>resource or r</dt><dd>working resources like steel,
     * corn,oxygen,coal etc. Annual depreciation of resource occurs primarily on
     * working resources, and working resources may be increased each year if
     * growth is possible. Only working resources can be used to pay costs.</dd>
     * <dt>cargo or c</dt><dd>reserved resource, not part of a building or
     * actively part of commerce. However, it is the resource that can is
     * traded. Only a smaller amount of annual depreciation and growth. The
     * cargo unit worth is less than the working resource, and unit costs are
     * less than working.</dd>
     * <dt>staff or s</dt><dd>Working colonists, nothing is done without staff,
     * Staff are in 16 different grades. The grades represent stages of
     * development of staff, usually advancing 1 stage a year. There are 4
     * groups of grades with 4 grades in each group.
     * <ul><dt>children </dt><dd>start as babies which do no work, up to intern
     * learning how to work.</dd>
     * <dt>Engineer</dt><dd>able to do increasingly complex work</dd>
     * <dt>Faculty</dt><dd>can work, but mainly teach enabling staff to advance
     * one or more grades each year</dd>
     * <dt>Researcher</dt><dd>some work, but their primary job is to increase
     * knowledge. Increased knowledge reduces depreciation each year and
     * increases growth<dd>
     * </ul>Blame this process on my 50 years working as IT engineer at
     * universities</dd>
     * <dt>guests or g</dt><dd>reserved staff with a parallel set of grades
     * which do not contribute to any SubAsset value except for unit worth, but
     * only working staff can pay costs. As a reserve the associated unit costs
     * are less. Trades are done with guests not staff.</dd>
     * <dt>knowledge </dt><dd>The goal of the game is to increase units of
     * resource and staff, and also increase the forms of knowledge for each
     * financial sector. Common knowledge is essentially the knowledge most
     * planets have, new knowledge is developed by faculty and researchers and
     * becomes common knowlege in the next year, manuals have worth but must be
     * transformed into common knowledge by faculty and researchers. Knowledge
     * is the sum of common knowledge and new knowledge, increases in knowlege
     * make years more efficient reducing the cost of travel, maintenencce and
     * growth. Knowledge cannot be traded to travel between stars, only
     * knowledge as manuals can be transported, and must then to be learned so
     * that becomes common knowledge.</dd>
     * <dt>random activity</dt><dd>A level of random activity is set the the
     * game master, but each clan master can also set a clan random activity
     * level which is added to that of the game. The two levels influence the
     * size of random activities, and remains constant throughout a given year.
     * Random activity changes costs, growth, evaluation of trades an
     * catastrophies. A catastrophy involves some size of loss of resources and
     * staff</dd></dl>
     */
    class SubAsset { // Assets.CashFlow.SubAsset

      Assets as1 = as2;
      String asname = as2.name;
      boolean sstaff = false;
      boolean reserve = false;
      int sIx, subIx; // the index number for some of the tables
      String aschar;
      String aName = "";
      SubAsset partner; // the other reserve or working subyr with this year
      SubAsset other; // sas <-> partner, sac <-> sag
      SubAsset oPartner;

      ARow balance = new ARow(ec);
      //   ARow nnbalanceWithPartner = new ARow(ec);
      //Assets.CashFlow.SubAsset  sectors
      ARow bonusUnitGrowth = new ARow(ec);
      ARow fracGrowths = new ARow(ec);
      ARow rawBiasedUnitGrowth = new ARow(ec);
      // ARow rawSectorPriorityUnitGrowth = new ARow(ec);
      ARow rawYearlyUnitGrowth = new ARow(ec);
      ARow maxRawYearlyUnitGrowth = new ARow(ec);
      ARow rawGrowth = new ARow(ec);
      ARow yearlyUnitGrowth = new ARow(ec);
      ARow rawUnitGrowth = new ARow(ec);
      // ARow rawUnitGrowthAfterDepreciation = new ARow(ec);
      //rawYearlyUnitGrowth=rawSectorPriorityUnitGrowth+Repreciation-Depreciation
      ARow depreciation = new ARow(ec);
      ARow rdepreciation = new ARow(ec);
      ARow sdepreciation = new ARow(ec);
      ARow repreciation = new ARow(ec);// gen Catastrophic
      ARow rrepreciation = new ARow(ec);// gen Catastrophic
      ARow srepreciation = new ARow(ec);// gen Catastrophic
      ARow preciation = new ARow(ec);// gen Catastrophic
      ARow cumSectorBonus = new ARow(ec);
      ARow bonusYearlyUnitGrowth = new ARow(ec);
      //ARow limitedBonusYearlyUnitGrowth = new ARow(ec);
      // ARow yearlyBonusGrowthFrac = new ARow(ec);
      ARow tradedGrowth = new ARow(ec);
      //   ARow fGrowth; // based on fertility function
      ARow growth = new ARow(ec);  // based on balance /
      ARow swappedGrowth = growth;
      int lgrades2 = E.lgrades;
      double[][] grades = new double[E.lsecs][E.LGRADES];
      ARow work;
      // Costs to staff are in terms of work, not balance
      //  do value * balance / work to convert or
      //  do value * workToBalance  =  balance / work
      //  ARow workToBalance; // convert work based remnant to balance remnant
      ARow facultyEquiv = new ARow(ec);
      ARow researcherEquiv = new ARow(ec);
      ARow manualsToKnowledgeEquiv = new ARow(ec);
      ARow colonists = new ARow(ec);
      ARow engineers = new ARow(ec);
      ARow faculty = new ARow(ec);
      ARow researchers = new ARow(ec);
      //   ARow maintCost = new ARow(ec);
      //   ARow travelCost = new ARow(ec);
      //  ARow requiredForMaint = new ARow(ec);
      //  ARow requiredForGrowth = new ARow(ec);
      //Assets.CashFlow.SubAsset  sectors
      ARow maintEfficiency = new ARow(ec);
      ARow groEfficiency = new ARow(ec);
      ARow invMaintEfficiency = new ARow(ec);
      ARow invGroEfficiency = new ARow(ec);
      // ARow cumulativeCost = new ARow(ec);
      ARow worth = new ARow(ec);
      //   ARow unitWorth = new ARow(ec);
      //    ARow nominalGrowth;
      //     ARow emergencyGrowth;
      //     ARow growFull;
      ARow avail = new ARow(ec);   // only for working SubAsset
      //    ARow availWithPartner = new ARow(ec);
      ARow reserved = new ARow(ec);

      /**
       * now calculate the SubAsset possible Fertility and Growth The
       * calculation in Asset includes balance - R and C growthRequirement costs
       * / R + C growth Requirements the S Fertility is calculated similarly / R
       * Growth Requirement Variables are declared not defined, because most
       * uses of the class use only a few of the variables, only the CashFlow
       * Cur uses all variable variable are defined with the "make" and
       * "makeZero" methods of Assets Already defined variables are simply used
       * without a new. Assets "copy" is used to new variables than copy their
       * values
       */
      ARow health;
      //(balance-tReqGrowthCosts)/tReqGrowthCosts
      //   ARow reqRawFertility;
      // E.minFertility <= reqRawFertility <= E.maxFertility
      //   ARow reqFertility;
      // ARow fertility;
      // ARow gFertility;  // fertility with the goal
      //  ARow wFertility;  // fertility with 1 whole goal
      // ARow eFertility;  // fertility with emergency goal
      // double goalFertility; // E.goalFertility[pors][clan]
      //  ARow tRawFertility;
      // ARow health1;
      //  ARow fertility1;
      //  ARow fertility2;
      //     ARow jGrowth;
      // ARow tRawGrowth;
      //   ARow hptcosts;// total costs with health penalty hpt...
      // Prefix hp = healthPenalty, np=no penalty
      //  ARow nptgrowthCosts;// total grow costs without health penalty npt
      //   ARow nptgrowthCosts1;
      //    ARow nptgrowthCosts2;
      //   ARow nptgrowthCosts3;
      //  ARow tReqGrowthCosts;
      //  ARow tReqGrowthCosts1;
      // ARow gReqGrowthCosts;  //Growth Costs with the Coal Applied
      // ARow wReqGrowthCosts;  //Growth Costs with the Coal Applied
      //  ARow eReqGrowthCosts;  //Growth Costs with emergency Applied
      //  ARow tReqMaintCosts;
      //  ARow tReqRawMaintCosts;
      //  ARow gReqMaintCosts;
      // ARow gReqRawMaintCosts;
      // ARow wReqMaintCosts;
      //  ARow wReqRawMaintCosts;
      // ARow eReqMaintCosts;
      //    ARow tReqMaintCosts2;
      //  ARow tReqMaintCosts3;
      //    ARow tReqMaintCosts4;
      //  ARow nptT1yrCosts;
      //  ARow nptT1yrCosts1;
      //   ARow nptT1yrCosts2;
      //  ARow nptT1yrCosts3;

      //  ARow nptRawGrowthCosts;
      //  ARow nptRawGrowthCosts1;
      // ARow nptMTRawGrowthCosts;
      // Prefix hp = healthPenalty, np=no penalty
      //  ARow hptRawGrowthCosts;
      //  ARow hpgRawGrowthCosts;
      //  ARow hpwRawGrowthCosts;
      // ARow hpeRawGrowthCosts;
      //   ARow hptRawGrowthCosts1;
      //    ARow nptRawGrowthCosts2;
      //    ARow nptRawGrowthCosts3;
      /**
       * goal and emergency costs are used in swapping and trading in swapping,
       * transmute/repurposed can be done below emergency or goal values
       * swapping may try to reach whole 100% health/growth values if resources
       * and time allow. in trading, attempt to trade goals, but if some
       * resource/staff is in emergency, allow major resources (not staff) to
       * trade down to emergency level.
       */
      //  ARow hptGrowthCosts;  // Prefix hp = healthPenalty, np=no penalty
      //  ARow hpgGrowthCosts;  // Prefix hp = healthPenalty, g=Goal cost
      //   ARow hpwGrowthCosts;  // Prefix hp = healthPenalty, w=whole(1) cost
      //  ARow hpeGrowthCosts;  // Prefix hp = healthPenalty, emergency cost
      //  ARow hptMaintCosts;
      //  ARow hpgMaintCosts;
      //  ARow hpwMaintCosts;
      //   ARow hpeMaintCosts;
      //   ARow hptTravCosts;
      //  ARow hptMTRawGrowthCosts;
      // ARow hpgMTRawGrowthCosts;
      // ARow hpwMTRawGrowthCosts;
      ///  ARow hpeMTRawGrowthCosts;
      //  ARow hptMTReqGrowthCosts;
      //ARow hpgMTReqGrowthCosts;
      // ARow hpwMTReqGrowthCosts;
      //  ARow hpeMTReqGrowthCosts;
      // ARow hptMTCosts;
      //  ARow hpgMTCosts;
      //  ARow hpwMTCosts;
      //  ARow hptGCosts;
      // ARow hpgGCosts;
      // ARow hpwGCosts;
      // ARow hpeGCosts;
      //  ARow hptMTGCosts;
      //  ARow hpgMTGCosts;  // costs with Growth Goal
      // ARow hpgMTGCostsLG;// Goal Costs less Growth
      //ARow hpwMTGCosts;  // costs with Growth Goal
      // ARow hpwMTGCostsLG;// Goal Costs less Growth
      // ARow hpeMTGCosts;  // costs with Growth Goal
      //  ARow hpeMTGCostsLG;// Goal Costs less Growth
      // ARow hptMTGCosts2;
      //  ARow tFuthpMTGCosts;
      //   ARow nptReqGrowthRemnant;
      //  ARow tReqGrowthFertility;
      //  ARow tReqGrowthRemnant;
      // ARow gReqGrowthRemnant;
      // ARow wReqGrowthRemnant;
      // ARow eReqGrowthRemnant;
      // ARow hptMTReqGrowthFertility;
      //  ARow hptRawGrowthRemnant;
      //    ARow hpwRawGrowthRemnant;
      //    ARow hpgRawGrowthRemnant;
      //    ARow hpeRawGrowthRemnant;
      //  ARow tReqMaintRemnant;
      //   ARow eReqMaintRemnant;
      //   ARow wReqMaintRemnant;
      //  ARow gReqMaintRemnant;
      //   ARow tReqMaintHealth;
      //  ARow wReqMaintHealth;
      //  ARow gReqMaintHealth;
      //  ARow eReqMaintHealth;
      // ARow nptRawMaintCosts;
      //  ARow nptMaintCosts;
      //  ARow npgMaintCosts;
      //  ARow npwMaintCosts;
      // ARow npeMaintCosts;
      //  ARow nptMaintRemnant;
      // ARow hptMaintRemnant;
      //  ARow nptT1yrRemnant;
      // ARow hptT1yrCosts;
      // ARow hptT1yrRemnant;
      //  ARow nptTravCosts;
      // ARow nptTrav1yrRemnant;
      //  ARow nptRawGrowthRemnant;
      //     ARow nptMTRawGrowthRemnant;
      //  ARow hptMTRawGrowthRemnant;
      //      ARow hptMTRawGrowthRawFertility;
      // ARow hptMTRawGrowthFertility;
      // ARow hptRawGrowthFertility;
      //    ARow hptTravRemnant;
      // ARow nptMTCosts;
      //  ARow nptMTRemnant;
      //    ARow tMTHealth;
      //    ARow hptMTRemnant;
      //  ARow tMTFertility = new ARow(ec);
      // ARow hptMTGRemnant;
      // ARow hptMTGRemnant2;
      //ARow tMTGFertility = new ARow(ec);
      // ARow hptMTGGRemnant;
      //  ARow posRemnantWithPartner = new ARow(ec);
      //  ARow remnantWithPartner = new ARow(ec);
      //  ARow lowResWithPartner = new ARow(ec);
      // ARow highResWithPartner = new ARow(ec);
      // ARow lowReservedWithPartner = new ARow(ec);  // subtract costs
      // highReservedWithPartner = new ARow(ec);
      //  ARow lowReserved;
      //ARow highReserved;
      //   ARow tHealthRemnant = new ARow(ec);
      //   ARow tWellnessRemnant = new ARow(ec);
      //   ARow tFertilityRemnant = new ARow(ec);
      //  ARow tRemnant = new ARow(ec);
      //  ARow AvailRemnant;
      //  ARow tFutRemnant = new ARow(ec);
      //  ARow tFutWithPartnerRemnant;
      ///  ARow tFutLowReservedWithPartner;
      //   ARow tFutHighReservedWithPartner;
      // ARow yrScost = new ARow(ec);
      // ARow cumXcost = new ARow(ec);
      // ARow cumScost = new ARow(ec);
      //  ARow cumtcosts = new ARow(ec);
      //    ARow hmtECosts;
      //   ARow nmtECosts;
      // Assets.CashFlow.SubAsset  just staff, guests have grades
      /**
       * Temp Values, don't copy for year
       *
       */
      /**
       * availTMRemnant available - poorHealthEffect adjustments on T & M
       */
      ARow rAvailTMRemnant; // r available - T M costs adjusted by poorHealthEffect
      ARow sAvailTMRemnant; // s
      ARow maxLeft;
      ARow need;

      ARow yYrStrtWorth;
      ARow prevGrowth;   // last years actual growth
      ARow prevUnitGrowth;
      ARow prevWorth;

      ARow prevHealth;
      ARow prevFertility;
      ARow prevNeed;
      ARow prevMaxLeft;
      ARow bonusYears = new ARow(ec);

      /**
       * data format using the method in Econ
       *
       * @param dd value to be formatted
       * @return string value
       */
      String df(double dd) {
        return ec.df(dd);
      }

      /**
       * constructor Assets.CashFlow.SubAsset
       *
       * @param asIx SubAsset index for SubAssets R C S G
       * @param areserve boolean true of a reserve not working SubAsset
       * @param apartner partner of this SubAsset
       *
       * requires a paired call by method setPartner
       */
      public SubAsset(int asIx, boolean areserve, SubAsset apartner) {
        // Assets.CashFlow.SubAsset
        reserve = areserve;
        sIx = sIx = asIx;
        partner = apartner;
        partner.partner = this;
        as1 = as3;
      }

      /**
       * another constructor for SubAsset
       *
       * @param asIx SubAsset index for SubAssets R C S G
       */
      public SubAsset(int asIx) {   // Assets.CashFlow.SubAsset
        reserve = false;
        sIx = asIx;
        as1 = as3;
      }

      void initResource(int sIx, boolean reserve, SubAsset partner, double resources) {
        sstaff = false;
        aName = "resource";
        other = staff;
        oPartner = guests;
        worth = bals.getRow(ABalRows.CURWORTHSIX + 0);

        initSubAsset(sIx, reserve, partner);
        // at start of econ, allocate resources according to ySectorPriorityYr
        if (!assetsInitialized) {
          for (int m = 0; m < E.lsecs; m++) {
            balance.set(m, resources * ySectorPriorityYr.get(m) / ySectorPriorityYr.sum(), "initial balance for each sector");
          }
        }
        worth.setAmultV(balance, eM.nominalWealthPerResource[pors]);
        hist.add(new History("&&", 5, "Init Res Bal", resource.balance));
      }

      void initCargo(int sIx, boolean reserve, SubAsset partner, double resources) {
        aName = "cargo";
        worth = bals.getRow(ABalRows.CURWORTHSIX + 1);;
        initSubAsset(sIx, reserve, partner);
        sstaff = false;
        other = guests;
        oPartner = staff;
        if (!assetsInitialized) {
          for (int m = 0; m < E.lsecs; m++) {
            balance.set(m, resources * ySectorPriorityYr.get(m) / ySectorPriorityYr.sum(), "initial balance for each sector");
          }
        }
        worth.setAmultV(balance, eM.nominalWealthPerResource[pors] * eM.cargoWorthBias[0]);

        hist.add(new History("&&", 6, "Init Cargo Bal", cargo.balance));
      }

      void initStaff(int asIx, boolean reserve, SubAsset partner, double initCol) {
        aName = "staff";
        worth = bals.getRow(ABalRows.CURWORTHSIX + 2);
        // sets balance and growth for the asIx SubAsset
        initSubAsset(asIx, reserve, partner);
        sstaff = true;
        other = resource;
        oPartner = cargo;
        // ARow resourceStaff = new ARow(ec);
        // for the grades sum to math the staff for that sector

        grades = bals.getStaffGrades(); // reset ref grades from bals
        // only init grades at the start of econ, but with tests below
        // boolean initedGrades = assetsInited || grades[0][0] > 0. || grades[0][1] > 0 || grades[0][5] > 0.;
        if (! !assetsInitialized) {
          double sumAssignments = 0.;
          // first get sum total of grade assignments sum of all sectors
          for (int j = 0; j < E.lgrades; j++) { // cycle grades
            sumAssignments += E.initStaffAssignmentPerEcon[pors][j];
          }
          for (int i = 0; i < E.lsecs; i++) { // cycle sectors
            // set s.balance and balances.A[4] as well as bals.A[4]
            bals.set(4, i, (initCol * ySectorPriorityYr.get(i) / ySectorPriorityYr.sum()), "total value for staff per sector");
            for (int j = 0; j < E.lgrades; j++) { // cycle grades
              staff.grades[i][j] = balance.get(i) * E.initStaffAssignmentPerEcon[pors][j] / sumAssignments;
            }// j
          }// i

          hist.add(new History("&&", 6, "staff PriYr", ySectorPriorityYr));

        } // inited grades
        hist.add(new History("&a", History.valuesMajor6, "staff", balance));
        staff.checkSumGrades();// grades are checked ok
        hist.add(new History("&&", 6, "initstaff=" + EM.mf(initCol), balance));

      }//initStaff

      // Assets.CashFlow.SubAsset
      void initGuests(int sIx, boolean reserve, SubAsset partner, double acolonists) {
        aschar = "guests";
        worth = bals.getRow(ABalRows.CURWORTHSIX + 3);
        initSubAsset(sIx, reserve, partner);
        sstaff = true;
        other = cargo;
        oPartner = resource;
        // ARow resourceStaff = new ARow(ec);
        grades = bals.getGuestGrades(); // reset ref grades into bals
        // initial grades at start of Econ and Assets
        if (!assetsInitialized) {
          boolean initedGrades = grades[0][0] > 0. || grades[0][1] > 0 || grades[0][5] > 0.;
          if (!initedGrades) {
            double sumAssignments = 0.;
            for (int j = 0; j < E.LGRADES; j++) {
              sumAssignments += E.initStaffAssignmentPerEcon[pors][j];
            }
            for (int i = 0; i < E.lsecs; i++) {
              bals.set(5, i, (acolonists * ySectorPriorityYr.get(i) / ySectorPriorityYr.sum()), "total value for staff per sector");

              for (int j = 0; j < E.LGRADES; j++) {
                guests.grades[i][j] = balance.get(i) * E.initStaffAssignmentPerEcon[pors][j] / sumAssignments;
              }
            }

          }
        }
        guests.checkSumGrades();
        hist.add(new History("&&", 6, " GBalance", guests.balance));
      }// initGuests

      /**
       * common init code for each init subAsset
       * Assets.CashFlow.SubAsset.initSubAsset called by each initResource,
       * initCargo,initStaff,initGuests
       *
       * @param asIx the index number for this subAsset
       * @param areserve true if this is a reserve not a working subAsset
       * @param apartner The partner working for reserve, reserve for working
       */
      // Assets.CashFlow.SubAsset.initSubAsset
      // called by each subassit init process
      void initSubAsset(int asIx, boolean areserve, SubAsset apartner) {
        reserve = areserve;
        sIx = asIx;
        aschar = aChar[asIx];
        this.partner = apartner;

        // make sure balance
        // move ARow references from bals, they will remain when SubAsset instance is freed
        balances.A[asIx + ABalRows.BALANCESIX] = sys[asIx].balance = bals.getRow(ABalRows.BALANCESIX + asIx);
        balances.A[asIx + ABalRows.BALANCESIX].setCnt++;
        sys[asIx].growth = growth = growths.A[BALANCESIX + asIx] = bals.getRow(ABalRows.GROWTHSEFFIX + asIx);
        growths.A[BALANCESIX + asIx].setCnt++;
        sys[asIx].depreciation = bals.getRow(ABalRows.DEPRECIATIONIX + asIx);
        sys[asIx].repreciation = bals.getRow(ABalRows.REPRECIATIONIX + asIx);
        sys[asIx].cumSectorBonus = bals.getRow(ABalRows.CUMULATIVESECTORBONUSIX + asIx);
        sys[asIx].bonusUnitGrowth = bals.getRow(ABalRows.BONUSUNITSIX + asIx);
        sys[asIx].bonusYears = bals.getRow(ABalRows.BONUSYEARSIX + asIx);
        sys[asIx].maxRawYearlyUnitGrowth = bals.getRow(ABalRows.MAXRAWYEARLYUNITGROWTHSIX + asIx);
        sys[asIx].rawYearlyUnitGrowth = bals.getRow(ABalRows.RAWYEARLYUNITGROWTHSIX + asIx);
        // sys[asIx].yearlyBonusGrowthFrac = bals.getRow(ABalRows.YEARLYBONUSSUMGROWTHVALIX + asIx);
        sys[asIx].rawGrowth = bals.getRow(ABalRows.RAWGROWTHSIX + asIx);
        tradedGrowth = bals.getRow(ABalRows.TRADEDGROWTHSIX + asIx);
        swappedGrowth = bals.getRow(ABalRows.SWAPPEDGROWTHSIX + asIx);
        prevUnitGrowth = makeZero(prevUnitGrowth);
        prevWorth = makeZero(prevWorth);
        prevHealth = makeZero(prevHealth);
        prevFertility = makeZero(prevFertility);
        prevNeed = makeZero(prevNeed);
        prevMaxLeft = makeZero(prevMaxLeft);
        grades = bals.getGrades()[sIx]; // of mpt staff
        yearlyUnitGrowth = new ARow(ec);
        rawUnitGrowth = new ARow(ec);
        //initSubAsset
        // rawUnitGrowthAfterDepreciation = makeZero(rawUnitGrowthAfterDepreciation);
        //rawGrowth = new ARow(ec);
        //      nominalGrowth = new ARow(ec);
        //     growFull = new ARow(ec);
        //      A6Row invMEfficiency = new A6Row(ec, lev, "invMEfficiency");
        //A6Row invGEfficiency = new A6Row(ec, lev, "invMEfficiency");
        // set to any prev instance reference
        invMaintEfficiency = invMEfficiency.A[ABalRows.BALANCESIX + asIx] = bals.getRow(ABalRows.INVMEFFICIENCYIX + asIx);
        invGroEfficiency = invGEfficiency.A[ABalRows.BALANCESIX + asIx] = bals.getRow(ABalRows.INVGEFFICIENCYIX + asIx);
        groEfficiency = new ARow(ec);
        maintEfficiency = new ARow(ec);
      }

      /**
       * copy AssetYr.SubAsset instance to a new copy of the sa copy variables
       * need for a year instance SubAsset variables that are not copied are
       * left with initial values
       *
       * @param sa AssetYr.SubAsset instance being copied
       * @return
       */
      SubAsset copyy(SubAsset sa) {// Assets.CashFlow.SubAsset
        setny(sa);
        rawUnitGrowth = copy(sa.rawUnitGrowth);
        worth = copy(sa.worth);
        health = copy(sa.health);
        //     fertility = copy(sa.fertility);
        need = copy(sa.need);
        growth = copy(sa.growth);
        rawGrowth = copy(sa.rawGrowth);
        maxLeft = copy(sa.maxLeft);
        depreciation = copy(sa.depreciation);
        repreciation = copy(sa.repreciation);
        bonusUnitGrowth = copy(sa.bonusUnitGrowth);
        bonusYears = copy(sa.bonusYears);
        return this;
      }

      /**
       * set AssetYr.SubAsset instance to a new copy of the sa copy variables
       * need during the n interations uncopied variables are left as
       * initialized
       *
       * @param sa
       * @return
       */
      SubAsset copyn(SubAsset sa) {// Assets.CashFlow.SubAsset
        setny(sa);
        if (!reserve) {
          //  tReqGrowthFertility = copy(sa.tReqGrowthFertility);
          //remnantWithPartner = copy(sa.remnantWithPartner);
          //highReservedWithPartner = copy(sa.highReservedWithPartner);
          //lowReservedWithPartner = copy(sa.lowReservedWithPartner);

          if (sstaff) {
            work = copy(sa.work);
          }
          if (sstaff) {
            grades = sa.copyGrades(sa.grades);
          }
        }
        return this;
      }

      /**
       * common method to copy HCashFlow.SubAsset members from CashFlow.SubAsset
       *
       * @param sa variables out of cur.Subassets
       * @return a HCashFlow.SubAsset with common copied members
       */
      SubAsset setny(SubAsset sa) { // Assets.CashFlow.SubAsset
        as1 = sa.as1;
        sIx = sa.sIx;
        sstaff = sa.sstaff;
        reserve = sa.reserve;
        aschar = sa.aschar;
        aschar = sa.aschar;
        partner = sys[sa.partner.sIx];
        balance = copy(sa.balance);
        // tRemnant = copy(sa.tRemnant);
        //      balanceWithPartner = copy(sa.balanceWithPartner);
        // availWithPartner = copy(sa.availWithPartner);
        if (!reserve) {
          // hptMTGCosts = copy(sa.hptMTGCosts);
          //fertility = copy(sa.fertility);
          // fertility1 = copy(sa.fertility1);
          health = copy(sa.health);
          //remnantWithPartner = copy(sa.remnantWithPartner);
          //highReservedWithPartner = copy(sa.highReservedWithPartner);
          //lowReservedWithPartner = copy(sa.lowReservedWithPartner);
        }
        if (sstaff) {  // both staff and guests
          work = copy(sa.work);
          grades = copyGrades(sa.grades);
          sumGrades();
        }

        return this;
      }

      /**
       * calculate efficiency only for SubAsset resource and SubAsset Staff
       * input percentDifficulty is EM.difficultyPercent[0] passed through Econ
       * and Assets and CashFlow
       *
       */
      protected void calcEfficiency() {  // Assets.CashFlow.SubAsset
        // never less than .5 E.effBias[pors] or more tnan 1.5 E.effBias
        // larger workEffBias make for more efficiency
        // larger percentDifficulty reduces workEffBias
        double workEffBias = eM.effBias[pors] * .5 + eM.effBias[pors] * (148 - percentDifficulty) * .1;
// define temporary internal variables
        ARow GroReqSum = new ARow(ec);
        ARow GroReqMultiplier = new ARow(ec);
        ARow MaintReqSum = new ARow(ec);
        ARow MaintReqMultiplier = new ARow(ec);
        ARow KnowledgeGroMultiplier = new ARow(ec);
        ARow KnowledgeMaintMultiplier = new ARow(ec);
        ARow meff = new ARow(ec);
        ARow geff = new ARow(ec);
        ARow rsefficiencyGMin = new ARow(ec);
        ARow rsefficiencyMMin = new ARow(ec);
        GroReqSum = makeZero(GroReqSum);
        MaintReqSum = makeZero(MaintReqSum);
        GroReqMultiplier = makeZero(GroReqMultiplier);
        MaintReqMultiplier = makeZero(MaintReqMultiplier);
        KnowledgeGroMultiplier = makeZero(KnowledgeGroMultiplier);
        KnowledgeMaintMultiplier = makeZero(KnowledgeMaintMultiplier);
        maintEfficiency = makeZero(maintEfficiency);
        groEfficiency = makeZero(groEfficiency);
        double dski = 0.;
        double dskj = 0.;
        double dmans = manuals.sum();
        aschar = aChar[sIx];
        splus = spluss[sIx];
        // one factor in efficiency, is a sectors importance to other sectors
        // use the grow Requirements table and Maint Requirements table to determin
        // sector importance.  Sum them in the following loops
        //  for (int i = 0; i < E.lsecs; i++) {
        //   for (int j = 0; j < E.lsecs; j++) {
        for (int i : E.ASECS) {
          for (int j : E.ASECS) {
            dskj = Math.sqrt(knowledge.get(j));
            GroReqSum.add(i, gReqs[pors][i][j + splus] * E.gReqEffMult * E.gReqMult[pors][0] * dskj);
            MaintReqSum.add(i, maintRequired[pors][i][j + splus] * E.mReqEffMult * E.mReqMult[pors][0] * dskj);
          } // end go through j to get the sums
          dski = Math.sqrt(knowledge.get(i));
          GroReqMultiplier.add(i, GroReqSum.get(i) * multiplierForEfficiencyFromRequirements);
          MaintReqMultiplier.add(i, MaintReqSum.get(i) * multiplierForEfficiencyFromRequirements);
          KnowledgeGroMultiplier.add(i, Math.sqrt((GroReqMultiplier.get(i) + knowledge.sum() * additionToKnowledgeBiasForSumKnowledge + (pors == E.S ? dmans : dmans) * eM.manualEfficiencyMult[pors][0] + dski) / eM.nominalKnowledgeForBonus[0]) + eM.additionToKnowledgeBiasSqrt[0]);
          KnowledgeMaintMultiplier.add(i, Math.sqrt((MaintReqMultiplier.get(i) + knowledge.sum() * additionToKnowledgeBiasForSumKnowledge + (pors == E.S ? dmans : dmans) * eM.manualEfficiencyMult[pors][0] + dski) / eM.nominalKnowledgeForBonus[0]) + eM.additionToKnowledgeBiasSqrt[0]);
          // the higher difficulty the lower the efficiency
          // workEffBias lower if difficulty is higher
          // ydifficulty set in calcPriority called in aStartCashFlow before this
          // maintEfficiency.add(i, Math.sqrt(workEffBias + (1. - workEffBias) * (ydifficulty.get(i) < PZERO ? KnowledgeMaintMultiplier.get(i))* .05: KnowledgeMaintMultiplier.get(i)) / ydifficulty.get(i)));

          EM.wasHere6 = " i=" + subStrs[sIx] + i + " Y" + EM.year + " ydifficulty=" + eM.mf2(ydifficulty.values[i]) + " wokEffBias=" + eM.mf(workEffBias) + "KnowledgeMaintMultiplier.values=" + eM.mf(KnowledgeMaintMultiplier.values[i]);

          double sqrtVal = workEffBias + (1. - workEffBias) * (ydifficulty.get(i) < PZERO ? KnowledgeMaintMultiplier.get(i) / 80. : KnowledgeMaintMultiplier.get(i));
          EM.wasHere6 += " sqrtVal=" + EM.mf(sqrtVal);
          Double sqrtv = Math.sqrt(sqrtVal);
          EM.wasHere6 += " sqrtv=" + EM.mf(sqrtv);
          if (E.debugEfficiencyOut && sqrtv.isNaN()) {
            eM.printHere("----CEF----", ec, EM.wasHere6);
            ec.doubleTrouble(KnowledgeMaintMultiplier.values[i], "kMMultiplier");
            ec.doubleTrouble(sqrtVal, "sqrtVal");
          }
          maintEfficiency.add(i, sqrtVal);
          groEfficiency.add(i, Math.sqrt(eM.effBias[pors] + (1. - eM.effBias[pors]) * (ydifficulty.get(i) < PZERO ? KnowledgeGroMultiplier.get(i) * .05 : KnowledgeGroMultiplier.get(i)) / ydifficulty.get(i)));
          //(z-99)*y = ,15  (x-25)*y=.3  x=148, y=0.002439
          // decrease the efficiency min as difficulty increases
          rsefficiencyGMin.set(i, eM.rsefficiencyGMin[pors][0] * (148 - percentDifficulty) * .001739);
          rsefficiencyMMin.set(i, eM.rsefficiencyMMin[pors][0] * (148 - percentDifficulty) * .001739);
        }// end loop on i
        meff = copy(maintEfficiency);
        geff = copy(groEfficiency);

        if (History.dl > History.debuggingMajor10) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          StackTraceElement aa = Thread.currentThread().getStackTrace()[2];
          StackTraceElement ab = Thread.currentThread().getStackTrace()[3];
          // hist.add(new History(History.debuggingMajor10, ">>>", a0.getMethodName(), "at", a0.getFileName(), wh(a0.getLineNumber()), "from=", aa.getFileName(), wh(aa.getLineNumber()), "ffrom", ab.getFileName(), wh(ab.getLineNumber())));

        }

        invMaintEfficiency = make(invMaintEfficiency);
        // the lower efficiency inverse is higher cost
        invMaintEfficiency.invertA(maintEfficiency.setLimVal(rsefficiencyMMin, eM.rsefficiencyMMax[pors][0]));
        poorKnowledgeAveEffect = invMaintEfficiency.ave();//larger is worse
        ARow tt1 = invMEfficiency.getRow(sIx + 2);
        invMEfficiency.getRow(sIx + 2).set(invMaintEfficiency);
        invGroEfficiency = make(invGroEfficiency);
        // low limit  at diff 100 = .25, at diff 50 .5 .25= .25 * (1 - 1.)*m .5 =(1 - .5)*m
        //
        // the lower efficiency inverse is higher cost
        invGroEfficiency.invertA(groEfficiency.setLimVal(rsefficiencyGMin, eM.rsefficiencyGMax[pors][0]));
        //  invGroEfficiency = invGroEfficiency.set(invMaintEfficiency);
        invGEfficiency.getRow(sIx + 2).set(invGroEfficiency);

        partner.invGroEfficiency.set(invGroEfficiency);
        partner.invMaintEfficiency.set(invMaintEfficiency);
        partner.groEfficiency.set(groEfficiency);
        partner.maintEfficiency.set(maintEfficiency);
        if (E.debugEfficiencyOut) {
          eM.printHere("----SCEa---", ec, " age=" + ec.age + " invGroEfficiency[0]= " + EM.mf(invGroEfficiency.get(0)) + " rsefficencyGMin=" + EM.mf(rsefficiencyGMin.get(0)) + " groEfficiency=" + EM.mf(groEfficiency.get(0)) + " invMEfficiency=" + EM.mf(invMEfficiency.get(0)));//groEfficiency
        }
        assert invGroEfficiency.get(0) > 0.0 : "invGroEfficiency.get(0) <= 0.0 =" + EM.mf(invGroEfficiency.get(0));
        assert invMEfficiency.get(0) > 0.0 : "invMEfficiency.get(0) <= 0.0 =" + EM.mf(invMEfficiency.get(0));

        if (History.dl > 2) {
          hist.add(new History(History.headers20, aschar + " efficiency " + ec.name, "0LifeSup", "1Struct", "2Energy", "3Propel", "4Defense", "5Gov", "6Col", "Min", "Sum", "Ave"));
          hist.add(new History("&&", 5, aschar + " GroReqSum", GroReqSum));
          hist.add(new History("&&", 5, aschar + " GroReqMultiplier", GroReqMultiplier));
          hist.add(new History("&&", 5, aschar + " MaintReqSum", MaintReqSum));
          hist.add(new History("&&", 5, aschar + " MaintReqMultiplier", MaintReqMultiplier));
          hist.add(new History("&&", 5, aschar + " KnowledgeMaintMultiplier", KnowledgeMaintMultiplier));
          hist.add(new History("&&", 5, aschar + " KnGroMultiplier", KnowledgeGroMultiplier));

          hist.add(new History("&&", 5, "knowledge", knowledge));
          hist.add(new History("&&", 5, aschar + " o meff", meff));
          hist.add(new History("&&", 5, aschar + " o geff", geff));
          hist.add(new History("&&", 5, aschar + " difficulty", ydifficulty));
          hist.add(new History("&&", 5, aschar + " mEfficiency", maintEfficiency));
          hist.add(new History("&&", 5, aschar + " gEfficiency", groEfficiency));
          hist.add(new History("&&", 5, aschar + " rsefficMMin", rsefficiencyMMin));
          hist.add(new History("&&", 5, aschar + " rsefficGMin", rsefficiencyGMin));
          hist.add(new History("&&", 5, aschar + " invMEfficiency", invMaintEfficiency));
          hist.add(new History("&&", 5, aschar + " invGEfficiency", invGroEfficiency));

        }
      } // end calcEfficiency  in SubAsset

      // double deteriorationIncSum = 0., cumDepreciationSum = 0., bonusUnitGrowthSum;
      /**
       * initYr init Start of Year process for SubAsset s
       *
       */
      void initYr() {// Assets.CashFlow.SubAsset.initYr
        calcGrowth();
      }

      /**
       * calculate rawSectorGrowth of each SubAsset.sectors less
       * cumulativeDeterioration plus bonusUnits staff are limited by maxStaff
       * rawSectorGrowth is always 0.0 or greater never negative rawSectorGrowth
       * is always less that rawUnitGrowth*maxFracUnitGrowth
       * cumulativeDeterioration is the sum of newUnitDeterioration a fraction
       * of prevYearGrowth cumulativeDeterioration is also reduced by a
       * Catastrophy and may become negative
       *
       * priority resources are limited by cumulative unit deterioration,
       * efficiency, priority and random values, cumulative unit deterioration
       * rawUnitGrowth becomes growth in Assets.CashFlow.calcRawCosts
       * thenAssets.CashFlow.getNeeds() growth is applied to SubAssets in
       * Assets.CashFlow.yearEnd
       */
      void calcGrowth() { // Assets.CashFlow.SubAsset.calcGrowth
        splus = spluss[sIx];
        if (sstaff) {
          sumGrades();//do recompute includes work
        }
        //  aschar = aChar[sIx];  alllready set
        //   deteriorationIncSum = cumDepreciationSum = bonusUnitGrowthSum = 0.;
        //ARow rawBiasedUnitGrowth = new ARow(ec);
        // ARow rawUnitGrowth1 = new ARow(ec);
        //ARow rawUnitGrowth = new ARow(ec);
        ARow newDepreciation = bals.getRow(ABalRows.NEWDEPRECIATIONIX + sIx);
        double dUnitGrowth7 = EM.assetsUnitGrowth[sIx][pors] * 7.0;
        double invUnitGrowth7 = 1. / dUnitGrowth7;// /by 0
        double dUnitGrowth = EM.assetsUnitGrowth[sIx][pors];
        double invUnitGrowth = 1. / dUnitGrowth;
        double dSecDepreciation = 0.;
        double dPrevGrowth = 0.;
        double dLimDepreciation = 0.;
        double dDifDepreciation = 0.;
        ARow rg1 = new ARow(ec);
        //ARow rg2 = new ARow(ec);
        ARow rg3 = new ARow(ec);
        double rawValue = 0., rawUValue = 0., dGrowthFrac = 0.;

        prevMaxLeft = make(maxLeft);
        bonusYears = bals.getRow(ABalRows.BONUSYEARSIX + sIx);
        bonusUnitGrowth = bals.getRow(ABalRows.BONUSUNITSIX + sIx);

        //changes in  limitedBonusYearlyUnitGrowth show up in the ABalRows reference automatically
        //limitedBonusYearlyUnitGrowth = bals.getRow(ABalRows.LIMTEDBONUSYEARLYUNITGROWTHIX + sIx);
        rawYearlyUnitGrowth = bals.getRow(ABalRows.RAWYEARLYUNITGROWTHSIX + sIx); // before decr
        maxRawYearlyUnitGrowth = bals.getRow(ABalRows.MAXRAWYEARLYUNITGROWTHSIX + sIx); // before decr
        rawUnitGrowth = bals.getRow(ABalRows.RAWUNITGROWTHSIX + sIx);
        ARow rawSectorPriorityUnitGrowth = bals.getRow(ABalRows.RAWPRIORITYUNITGROWTHSIX + sIx);
        rawGrowth = bals.getRow(ABalRows.RAWGROWTHSIX + sIx);
        prevGrowth = bals.getRow(ABalRows.PREVGROWTHSIX + sIx);
        //   prevRawUnitGrowth = rawUnitGrowth.copy();
        prevWorth.set(worth = make(worth));
        prevHealth.set(health = make(health));
        // growthsix includes the catstrophy benefits from last year
        //3 references to the same growth instance
        growths.A[2 + sIx] = growth = bals.getRow(ABalRows.GROWTHSEFFIX + sIx);
        // bals.copy4BtoC(ABalRows.GROWTHSEFFIX, ABalRows.GROWTHS8IX);
        //bals.set1(ABalRows.GROWTHS2IX, sIx, growth);
        if (sIx == 0 && ec.getAge() > 1) { // after one yeaEnd with growth
          assert growth.get(0) > 0.0 : " growth.get(0) <= 0.0=" + EM.mf(growth.get(0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
        }
        //  prevGrowth.set(growth);

        // bals.set1(ABalRows.GROWTHS1IX, sIx, prevGrowth);
        // bals.set1(ABalRows.PREVGROWTHSIX, sIx, prevGrowth);
        ARow zeroVals = new ARow(ec);
        depreciation = bals.getRow(ABalRows.DEPRECIATIONIX + sIx);
        //SubAssets C,G have no Repreciations
        repreciation = bals.getRow(ABalRows.NEWREPRECIATIONIX + sIx);
        // growthsix includes the catstrophy benefits from last year
        if (sIx == 2) {
          aChar[sIx] = "s";  // sometimes got lost
        }
        if (!didDepreciation) { // no more cumulative deterioration if already done this year
          // doing rows not sectors
          // prevGrowth was growthEff includes the catstrophy benefits, and the depreciation from last year
          // newDepreciation is prevGrowth mult 
          newDepreciation.set(prevGrowth); // for this SubAsset
          newDepreciation.mult(eM.growthDepreciation[sIx][pors]);
          bals.set1(ABalRows.NEWDEPRECIATIONIX, sIx, newDepreciation); // ALL SECTORS
          EM.wasHere6 = "testing UNITDEPRECIATION ";
          double ds0newDepreciation = newDepreciation.get(0);//sector 0 of NewUnitDe[recoatopm
          EM.wasHere6 += " ds0NewUnitDepreciation" + EM.mf(ds0newDepreciation);

          // use SubAsset.add and ABalRows
          depreciation.add(newDepreciation); // add subasset and ABalRows

          double ds0depreciation = depreciation.get(0);//after add
          EM.wasHere6 += "\n ds0CumulativeUnitDepreciation sum value" + EM.mf(ds0depreciation);
          double ds0SumDepreciation = ds0newDepreciation + ds0depreciation;
          EM.wasHere6 += " sum new cums0SumUnitDepreciation=" + EM.mf(ds0SumDepreciation);
          //was add reflected in ABalRows by common references
          double dif1 = ds0SumDepreciation - ds0depreciation; // direct sums match
          boolean bb1 = dif1 > E.NNZERO && dif1 < E.PPZERO;
          //  boolean bb1 = s0SumUnitDepreciation == a0CumulativeUnitDepreciation; // direct sums match
          EM.wasHere6 += " dif1=" + EM.mf(dif1) + (bb1 ? " the A6Row.sum matcheas0SumUnitDepreciation new+cum" : " the A6Row.sum match failed s0SumUnitDepreciation new+cum");
          // later    handle bonuses  didDepreciation = true;
        }// !didDepreciation

        double bonusLeft = 0;
        // calculate each sector raw growth for this year.  A function of game growth value,
        // economy priorities, and groEfficiency
        // find growth fraction, less as the balance sum increases until large maxStaffGrowth
        // calculate each sector raw unit growth
        for (int secIx : E.ASECS) { // each 7 sectors
          secCnt = (secIx == (E.LSECS - 1)) && sIx == 3 ? 1 : 0; // cnt for setStat
          dSecDepreciation = bals.get(ABalRows.DEPRECIATIONIX + sIx, secIx);//newDepreciation already added
          dPrevGrowth = bals.getRow(ABalRows.PREVGROWTHSIX + sIx).get(secIx);
          dLimDepreciation = dUnitGrowth > dPrevGrowth ? dUnitGrowth : dPrevGrowth;
          dDifDepreciation = dSecDepreciation - dLimDepreciation;//?? too big
          dSecDepreciation -= dDifDepreciation > 0.0 ? dDifDepreciation : 0.0;
          dSecDepreciation = dSecDepreciation - bals.get(ABalRows.NEWREPRECIATIONIX + sIx, secIx);
          // ?? too small
          dSecDepreciation = dSecDepreciation < 0.0 ? 0.0 : dSecDepreciation;
          bals.set(ABalRows.DEPRECIATIONIX + sIx, secIx, dSecDepreciation);
          //limit staff growth by eM.maxStaffGrowth[pors] less growth as larger
          // apply size limit to staff
          dGrowthFrac = fracGrowths.set(secIx, sstaff ? (EM.maxStaffGrowth[pors] - balance.get(secIx)) / EM.maxStaffGrowth[pors] : 1.0);

          // calc bonus growth to add if bonusYears of secIx > PZERO
          double dBonusYearUnitGrowth = bals.set(ABalRows.BONUSUNITSIX + sIx, secIx, (bonusLeft = (bonusYears.get(secIx) > PZERO ? bonusUnitGrowth.get(secIx) : 0.)));
          setStat(EM.BONUSGROWTH, dBonusYearUnitGrowth, secCnt);
// calc potential sector growth, before dePreciation rePreciation
          double dRawYearlyUnitGrowth = bals.set(ABalRows.RAWYEARLYUNITGROWTHSIX + sIx, secIx, EM.assetsUnitGrowth[sIx][pors] * EM.fracBiasInGrowth[pors]);
          double dMaxRawYearlyUnitGrowth = bals.set(ABalRows.MAXRAWYEARLYUNITGROWTHSIX + sIx, secIx, 1.5 * (dRawYearlyUnitGrowth * dGrowthFrac));
          double dYrPotentialUnitGrowth = dRawYearlyUnitGrowth * dGrowthFrac;
          setStat(EM.RAWYEARLYUNITGROWTH, dYrPotentialUnitGrowth, secCnt);
          double dRawSumGrowth = dYrPotentialUnitGrowth - dSecDepreciation + dBonusYearUnitGrowth;
          //   setStat(EM.GROWTHPRECIATED, dRawSumGrowth, secCnt);
          //surplus bonus, if bonus pushes growth over max
          double dSurplusBonusGrowth = dRawSumGrowth > dMaxRawYearlyUnitGrowth ? dRawSumGrowth - dMaxRawYearlyUnitGrowth : 0.0;
          double dRawUnitGrowth = dRawSumGrowth - dSurplusBonusGrowth;
          setStat(EM.NEWGROWTHS, dRawUnitGrowth, secCnt);
          dBonusYearUnitGrowth -= dSurplusBonusGrowth;//
          bals.add(ABalRows.CUMULATIVESECTORBONUSIX + sIx, secIx, dBonusYearUnitGrowth);
          ec.rem.add(ABalRows.CUMULATIVESECTORBONUSIX + sIx, secIx, dBonusYearUnitGrowth);
          setStat(EM.GROWTHPRECIATED, dBonusYearUnitGrowth, secCnt);

          bals.set(ABalRows.RAWUNITGROWTHSIX + sIx, secIx, dRawUnitGrowth);//RAWUNITGROWTHSIX + sIx
          //  now calc priority and growthEfficiency(from knowledge)
          rawSectorPriorityUnitGrowth.set(secIx, (rawUnitGrowth.get(secIx) * eM.fracPriorityInGrowth[pors] * ySectorPriorityYr.get(secIx)) * groEfficiency.get(secIx) * cRand(3 * sIx + secIx + 30));
          //RAWPRIORITYUNITGROWTHSIX
          /*
           * raw unit growth in ships, is dependent on lightYearsTraveled raw growth
           * for planets dependent on staff work
           */
          double dRawUFrac = bals.set(ABalRows.RAWUNITGROWTHSIX + sIx, secIx, rawUnitGrowth.set(secIx, (rg1.set(secIx, (sstaff ? rg3.set(secIx, lightYearsTraveled * eM.travelGrowth[E.S]) : 1.) * rawSectorPriorityUnitGrowth.get(secIx)))));
          //raw growth is calculated  in Assets.CashFlow.calcRawCosts
          double dRawGrowthFrac = rawGrowth.set(secIx, s.work.get(secIx) * dRawUFrac * 1.5);

          double dGrowthVal = bals.set(ABalRows.SECTORRAWGROWTHSIX + sIx, secIx, bals.getRow(ABalRows.BALANCESIX + sIx).get(secIx) * dRawGrowthFrac);

// now reduce the value of bonusUnitGrowth
          if (!didDepreciation) {
            // now count down the bonus units & years
            bonusYears.add(secIx, - 1);
            if (bonusYears.get(secIx) > 0
                && bonusUnitGrowth.get(secIx) > 0.) {
              // reduce bonusUnitGrowth for next year
              double dBonusUnitGrowth = bonusUnitGrowth.add(secIx, -(dRawGrowthFrac + dUnitGrowth) * .04); //subtract the sum/25
              if (dBonusUnitGrowth < 0.) {  // zero growth and years
                bonusYears.set(secIx, 0.);
                bonusUnitGrowth.set(secIx, 0.); //updates
              }
            }
            //        didDepreciation = true; done only after all 4 sIx in aStartCashFlow
          }

          if (dGrowthVal < -0.0) {
            if (E.debugNegGrowth) {
              // throw new MyErr(String.format(">>>>ERROR dGrowthVal %14.10f too small,eM.Growth=%14.10f,yearlyUnitGrowth=%14.10f,rawUG1=%14.10f, rawUnitGrowth %14.10f, misecIx %14.10f,n=%d,%s,lightYearsTraveled=%10.7f", dGrowthVal, eM.assetsUnitGrowth[sIx][pors], dYrUnitGrowth, rawUnitGrowthd, rawUValue, eM.mRCSGGrowth[sIx][pors][0], secIx, name, lightYearsTraveled));
              throw new MyErr(String.format(">>>>ERROR rawGrowth " + EM.mf(rawUValue)));
            }
            else {
              // only if was < 0.0 set to min growth
              rawGrowth.set(secIx, 0.0);
            }

          }

        }//end for on secIx

        // bals.set2(ABalRows.RAWUNITGROWTHSIX + sIx,rawUnitGrowth);
        String[] potentialGrowthStats = {"potentialResGrowthPercent", "potentialCargoGrowthPercent", "potentialStaffGrowthPercent", "potentialGuestGrowthPercent"};
        String[] bonusYearlyUnitGrowthStats = {"potentialResGrowthPercent", "potentialCargoGrowthPercent", "potentialStaffGrowthPercent", "potentialGuestGrowthPercent"};
        //     int[] depreciations = {EM.RDEPRECIATIONP, EM.CDEPRECIATIONP, EM.SDEPRECIATIONP, EM.GDEPRECIATIONP};
        double tt = calcPercent(eM.assetsUnitGrowth[sIx][pors], rawUnitGrowth.sum());
        double ttt = calcPercent(eM.assetsUnitGrowth[sIx][pors], sys[sIx].depreciation.sum());
        if (tt > 0.0 && sIx % 2 == 0) {

        }
        tt = calcPercent(eM.assetsUnitGrowth[sIx][pors], bonusYearlyUnitGrowth.sum());
        if (tt > 0.0) {
          setStat(bonusYearlyUnitGrowthStats[sIx], calcPercent(eM.assetsUnitGrowth[sIx][pors], rawUnitGrowth.sum()), 1);
        }

        if (rawUnitGrowth.getNegCount() == 1 && rawUnitGrowth.getNegSum() > 0.) {
          String[] negRawUnitGrowths = {"rNeg1RawUnitGrowth", "cNeg1RawUnitGrowth", "sNeg1RawUnitGrowth", "gNeg1RawUnitGrowth"};
          setStat(negRawUnitGrowths[sIx], rawUnitGrowth.getNegSum(), 1);
        }
        if (rawUnitGrowth.getNegCount() >= 2 && rawUnitGrowth.getNegSum() > 0.) {
          String[] neg2RawUnitGrowths = {"rNeg2RawUnitGrowth", "cNeg2RawUnitGrowth", "sNeg2RawUnitGrowth", "gNeg2RawUnitGrowth"};
          setStat(neg2RawUnitGrowths[sIx], rawUnitGrowth.getNegSum(), 1);
        }
        ec.aPre = aPre = "#G";
        if (History.dl > 5 && Econ.saveHist) {
          hist.add(new History(aPre, 5, aschar + " growth[sIx][pors]", EM.mf(eM.assetsUnitGrowth[sIx][pors]), "sIx", wh(sIx), "pors", wh(pors), "fracBiax..", EM.mf(eM.fracBiasInGrowth[pors]), "fracPriority", EM.mf(eM.fracPriorityInGrowth[pors])));
          hist.add(new History(aPre, 5, aschar + " balance", balance));
          hist.add(new History(aPre, 5, aschar + " prevGrowth", prevGrowth));
          if (!sstaff) {
            hist.add(new History(aPre, 5, aschar + " depreciation", depreciation));
          }
          hist.add(new History(aPre, 5, " knowledge", knowledge));
          hist.add(new History(aPre, 5, aschar + " bonusUnitGrowth", bonusUnitGrowth));
          hist.add(new History(aPre, 5, " bonusYears", bonusYears));
          hist.add(new History(aPre, 5, aschar + " groEfficiency", groEfficiency));
          hist.add(new History(aPre, 5, aschar + " yearlyUnitGrowth", yearlyUnitGrowth));
          hist.add(new History(aPre, 5, aschar + " rawUG1", rawUValue));
          hist.add(new History(aPre, 5, aschar + " rawBiasedUnitGrowth", rawBiasedUnitGrowth));
          hist.add(new History(aPre, 5, aschar + " rawPriorityUnitGrowth", rawSectorPriorityUnitGrowth));
          hist.add(new History(aPre, 5, aschar + " rawUnitGrowth", rawUnitGrowth));
          hist.add(new History(aPre, 5, aschar + " rg1", rg1));
          //    hist.add(new History(aPre, 5, aschar + " rg2", rg2));
          hist.add(new History(aPre, 5, aschar + " rg3", rg3));
          //    hist.add(new History(aPre, 5, aschar + " rg4", rg4));
          hist.add(new History(aPre, 5, aschar + " rawGrowth", rawGrowth));
          hist.add(new History(aPre, 5, aschar + "sIx=" + sIx + " Trav=", EM.mf(lightYearsTraveled), "pors+" + pors));
          hist.add(new History(aPre, 5, " ySectorPriorityYr=", ySectorPriorityYr));
          hist.add(new History(aPre, 5, "staffWork", s.work));

          //        hist.add(new History("&&", 5, aschar + " balWPartner", balanceWithPartner));
        }
      }// end Assets.CashFlow.SubAsset.calcGrowth

      protected double setGrade(int sector, int grade, double value) {
        // Assets.CashFlow.SubAsset
        grades[sector][grade] = value;
        return grades[sector][grade];

      }

      /**
       * make a copy of the grades array
       *
       * @param gr old grades array
       * @return new copy of gr;
       */
      double[][] copyGrades(double[][] gr) {// Assets.CashFlow.SubAsset
        double[][] ret = new double[E.lsecs][];
        for (int m : ASECS) {
          ret[m] = new double[E.LGRADES];
          for (int n = 0; n < E.LGRADES; n++) {
            ret[m][n] = gr[m][n];
          }
        }
        return ret;
      }

      protected double getGrade(int sector, int grade) {// Assets.CashFlow.SubAsset
        return grades[sector][grade];
      }

      /**
       * debug test an array of increases for multiple ARow s from the grades of
       * a SubAsset
       *
       * the ARow ending in Equiv are used in SubAsset.calcGrowth() knowledge is
       * reAdded from commonKnowledge, newKnowledge
       *
       * @return the balance.sum() by grades not ARow units sum which should
       * match
       * @param sourceIx 0 to 6 sum only sourceIx sectors,
       * <br>&lt; 0 && &gt; -5 ?sum all sectors
       * <br>&lt; -10 no debuging of any kind
       * @param moves an array ot the changes for each sector
       * @return sum of units values from Assets.CashFlow.SubAsset.sumGrades()
       */
      void checkGrades() { //Assets.CashFlow.SubAsset.checkGrades
        double preValues[] = {0., 0., 0., 0., 0., 0., 0., 0., 0.};
        double sGSums[] = {0., 0., 0., 0., 0., 0., 0., 0., 0.};
        double preGSums[] = {0., 0., 0., 0., 0., 0., 0., 0., 0.};
        double resGSums[] = {0., 0., 0., 0., 0., 0., 0., 0., 0.};
        debugSumGrades2 = E.debugSumGrades;
        int ma = sIx, mm = 1;
        double sectUnits = 0.;
        double sectUnits1 = 0.;
        double sectUnits2 = 0.;
        double sectUnits3 = 0.;
        double sectUnits4 = 0.;
        double psectUnits1 = 0.;
        double psectUnits = 0.;
        double ppsectUnits1 = 0.;
        double ppsectUnits = 0.;

        // int sourceMax = sourceIx > -1 ? sourceIx : LSECS; // debug 7 financialSectors
        if (debugSumGrades2) { // check grades against units
          for (int secIx = 0; secIx < E.LSECS; secIx++) {
            for (int gradesIx = 0; gradesIx < E.LGRADES; gradesIx++) {
              sGSums[secIx] += doubleTrouble(grades[secIx][gradesIx], "secIx=" + secIx + "gradesIx=" + gradesIx + ",");
            }//gradesIx
            sGSums[8] += sGSums[secIx];
          }//secIx
          double sumDif = 0., dif = 0, sumg = 0., sumu = 0., difFracSum = .00001, difFrac = .001;
          //Prevalidate the existing grades and balance if debugSumGrades2
          for (int sourceIx2 = 0; sourceIx2 < E.LSECS; sourceIx2++) {
            preValues[sourceIx2] = balance.get(sourceIx2);
            sumg = 0.;
            for (int gradesIx = 0; gradesIx < E.LGRADES; gradesIx++) {
              if (grades[sourceIx2][gradesIx] < -0.0) {
                throw (new MyErr(String.format(didTradeInitCF + " checkGradesIllegal negative grade %7.3g at   sourceIx%d, gradesIx%d, term%d, i%d, j%d, m%d, n%d", grades[sourceIx2][gradesIx], sourceIx2, gradesIx, as3.term, as3.i, as3.j, as3.m, as3.n)));
              }
              preGSums[sourceIx2] += grades[sourceIx2][gradesIx];
            } //for gradesIx
            preGSums[8] += preGSums[sourceIx2];

            ppsectUnits = psectUnits;
            psectUnits = sectUnits;
            ppsectUnits1 = psectUnits1;
            psectUnits1 = sectUnits1;
            sectUnits = balance.get(sourceIx2);
            sectUnits2 = balance.get(sourceIx2);
            sectUnits3 = balance.get(sourceIx2);
            sectUnits4 = balance.get(sourceIx2);
            if (sectUnits != balance.get(sourceIx2)) {
              throw (new MyErr("----CGA5----- " + ec.name + " " + didTradeInitCF + "sectorUnits balance[sourceIx2] missMatch=" + EM.mf(sectUnits) + "!=, balance.get(" + sourceIx2 + ")=" + EM.mf(balance.get(sourceIx2)) + "\n, more units=" + EM.mf(ppsectUnits) + ", " + EM.mf(psectUnits)
                               + ", 1=" + EM.mf(ppsectUnits1) + ", " + EM.mf(psectUnits1) + ", " + EM.mf(sectUnits1) + "\n, 2=" + EM.mf(sectUnits2)
                               + ", 3=" + EM.mf(sectUnits3) + " for " + aschar + sourceIx2 + "\n grades sum=" + EM.mf(sGSums[sourceIx2]) + " pregradesSum]=" + EM.mf(preGSums[sourceIx2]) + "\n grades sGSums[8]=" + EM.mf(sGSums[8]) + ", grades preGSums[8]=" + EM.mf(preGSums[8]) + ", sourceIx2=" + sourceIx2 + ", term" + as3.term + ", i" + as3.i + ", j" + as3.j + ", m" + as3.m + ", n" + as3.n));
            }
            if (sectUnits2 != balance.get(sourceIx2)) {
              throw (new MyErr("----CGA6----- " + ec.name + " " + didTradeInitCF + "sectorUnits balance[sourceIx2] missMatch=" + EM.mf(sectUnits) + "!=, balance.get(" + sourceIx2 + ")=" + EM.mf(balance.get(sourceIx2)) + "\n, more units=" + EM.mf(ppsectUnits) + ", " + EM.mf(psectUnits)
                               + ", 1=" + EM.mf(ppsectUnits1) + ", " + EM.mf(psectUnits1) + ", " + EM.mf(sectUnits1) + "\n, 2=" + EM.mf(sectUnits2)
                               + ", 3=" + EM.mf(sectUnits3) + " for " + aschar + sourceIx2 + "\n grades sum=" + EM.mf(sGSums[sourceIx2]) + " pregradesSum]=" + EM.mf(preGSums[sourceIx2]) + "\n grades sGSums[8]=" + EM.mf(sGSums[8]) + ", grades preGSums[8]=" + EM.mf(preGSums[8]) + ", sourceIx2=" + sourceIx2 + ", term" + as3.term + ", i" + as3.i + ", j" + as3.j + ", m" + as3.m + ", n" + as3.n));
            }
            if (sectUnits3 != balance.get(sourceIx2)) {
              throw (new MyErr("----CGA7----- " + ec.name + " " + didTradeInitCF + "sectorUnits balance[sourceIx2] missMatch=" + EM.mf(sectUnits) + "!=, balance.get(" + sourceIx2 + ")=" + EM.mf(balance.get(sourceIx2)) + "\n, more units=" + EM.mf(ppsectUnits) + ", " + EM.mf(psectUnits)
                               + ", 1=" + EM.mf(ppsectUnits1) + ", " + EM.mf(psectUnits1) + ", " + EM.mf(sectUnits1) + "\n, 2=" + EM.mf(sectUnits2)
                               + ", 3=" + EM.mf(sectUnits3) + " for " + aschar + sourceIx2 + "\n grades sum=" + EM.mf(sGSums[sourceIx2]) + " pregradesSum]=" + EM.mf(preGSums[sourceIx2]) + "\n grades sGSums[8]=" + EM.mf(sGSums[8]) + ", grades preGSums[8]=" + EM.mf(preGSums[8]) + ", sourceIx2=" + sourceIx2 + ", term" + as3.term + ", i" + as3.i + ", j" + as3.j + ", m" + as3.m + ", n" + as3.n));
            }
            if (sectUnits4 != balance.get(sourceIx2)) {
              throw (new MyErr("----CGA8----- " + ec.name + " " + didTradeInitCF + "sectorUnits balance[sourceIx2] missMatch=" + EM.mf(sectUnits) + "!=, balance.get(" + sourceIx2 + ")=" + EM.mf(balance.get(sourceIx2)) + "\n, more units=" + EM.mf(ppsectUnits) + ", " + EM.mf(psectUnits)
                               + ", 1=" + EM.mf(ppsectUnits1) + ", " + EM.mf(psectUnits1) + ", " + EM.mf(sectUnits1) + "\n, 2=" + EM.mf(sectUnits2)
                               + ", 3=" + EM.mf(sectUnits3) + " for " + aschar + sourceIx2 + "\n grades sum=" + EM.mf(sGSums[sourceIx2]) + " pregradesSum]=" + EM.mf(preGSums[sourceIx2]) + "\n grades sGSums[8]=" + EM.mf(sGSums[8]) + ", grades preGSums[8]=" + EM.mf(preGSums[8]) + ", sourceIx2=" + sourceIx2 + ", term" + as3.term + ", i" + as3.i + ", j" + as3.j + ", m" + as3.m + ", n" + as3.n));
            }
            double difMax = sectUnits * 0.001 + .0003;
            dif = sectUnits - preGSums[sourceIx2];
            dif = dif < 0.0 ? -dif : dif; // get abs value
            boolean difOk = dif < difMax;
            double sumBal = balance.sum();
            //check for more than a very small dif between sum of sector grades and  sector balance
            if (((dif = sectUnits - preGSums[sourceIx2]) < -difMax || dif > difMax) || preGSums[sourceIx2] != sGSums[sourceIx2] || sectUnits != balance.get(sourceIx2)) {
              throw (new MyErr("----CGA1----- " + ec.name + " " + didTradeInitCF + "sector grade sum difference too large=" + EM.mf(dif) + " difMax=" + EM.mf(difMax) + ", balance.get(" + sourceIx2 + ")=" + EM.mf(balance.get(sourceIx2)) + " for " + aschar + sourceIx2 + " grades sum=" + EM.mf(sGSums[sourceIx2]) + " pregrades[sourceIx2]=" + EM.mf(preGSums[sourceIx2]) + "\n less units" + EM.mf(sectUnits) + "\n, more units=" + EM.mf(ppsectUnits) + ", " + EM.mf(psectUnits)
                               + ", 1=" + EM.mf(ppsectUnits1) + ", " + EM.mf(psectUnits1) + ", " + EM.mf(sectUnits1) + "\n, 2=" + EM.mf(sectUnits2)
                               + ", 3=" + EM.mf(sectUnits3) + "\n units sumBal=" + EM.mf(sumBal) + " grades sGSums[8]=" + EM.mf(sGSums[8]) + ", grades preGSums[8]=" + EM.mf(preGSums[8]) + ", sourceIx2=" + sourceIx2 + ", term" + as3.term + ", i" + as3.i + ", j" + as3.j + ", m" + as3.m + ", n" + as3.n));

              //   throw(new MyErr(String.format("difference[%d] %7.3g is greater than difMax %7.3g for pre balance %7.3g  less pre grade units %7.3g   sourceIx%d, term%d, i%d, j%d, m%d,n%d",sourceIx2, dif,difMax,sectU,preGSums[sourceIx2],sourceIx,as.term,as.i,as.j,as.m,as.n)));
            }//dif
          }// for sourceIx2
        }// if debugSumGrades2
      }// checkGrades

      /**
       * sum multiple ARow s from the grades of a SubAsset
       *
       * Uses SubAsset variables grade, debugSumGrades, balance
       *
       * output SubAsset ARow are: worth, work, facultyEquiv, researcherEquiv,
       * manualsToKnowledgeEquiv, colonists, engineers, faculty, researchers,
       * the ARow ending in Equiv are used in SubAsset.calcGrowth() knowledge is
       * reAdded from commonKnowledge, newKnowledge
       *
       * @return sum of both balance and grades
       */
      protected double sumGrades() {// Assets.CashFlow.SubAsset.sumGrades
        if (debugSumGrades2 && !(sIx == 2 || sIx == 3)) {
          throw new MyErr(String.format("Can't sumGrades for " + aName));
        }

        balance = makeZero(balance);
        worth = makeZero(worth);
        work = makeZero(work);
        facultyEquiv = makeZero(facultyEquiv);
        researcherEquiv = makeZero(researcherEquiv);
        manualsToKnowledgeEquiv = makeZero(manualsToKnowledgeEquiv);
        colonists = makeZero(colonists);
        engineers = makeZero(engineers);
        faculty = makeZero(faculty);
        researchers = makeZero(researchers);
        knowledge.set(commonKnowledge, newKnowledge);
        // double[] sgWork = {0, 0, 1., 0.};
        double sumG = 0;
        for (int i = 0; i < E.lsecs; i++) {  // accross sectors
          for (int j = 0; j < E.LGRADES; j++) {
            if (debugSumGrades2 && grades[i][j] < E.NZERO) {
              throw new MyErr(String.format("Neg grade=grades[" + i + "][" + j + "]=" + EM.mf(grades[i][j])));
            }
            balance.add(i, doubleTrouble(grades[i][j]));
            sumG += grades[i][j];
            worth.add(i, grades[i][j] * (.5 + .5 * E.sumWorkerMults[j]) * eM.nominalWealthPerStaff[pors] * E.staffWorthBias[j] * eM.worthBias[sIx][0]);
            //   worth.add(i, grades[i][j] * (.5 + .5 * E.sumWorkerMults[j]) * eM.nominalWealthPerStaff[pors] * E.staffWorthBias[j]);
          }
          // count titles for Staff & Guests
          for (int j = 0; j < 4; j++) {
            colonists.add(i, grades[i][j]);
            engineers.add(i, grades[i][j + 4]);
            faculty.add(i, grades[i][j + 8]);
            researchers.add(i, grades[i][j + 12]);
          }
        }
        if (sIx == SIX) { // staff only sums
          for (int i = 0; i < E.lsecs; i++) {
            for (int j = 0; j < E.LGRADES; j++) {
              work.add(i, grades[i][j] * E.sumWorkerMults[j]);
              facultyEquiv.add(i, grades[i][j] * E.sumFacultyMults[j]);
              researcherEquiv.add(i, grades[i][j] * E.sumResearchMults[j]);
              manualsToKnowledgeEquiv.add(i, grades[i][j] * E.sumManualToKnowledgeByStaff[j]);
            }

            // now sum the subgrades of staff / guests
          }
        }
        return sumG;
      } // Assets.CashFlow.SubAsset.sumGrades

      /**
       * check that the balance matches the sum of the grades for each sector do
       * not check if Assets have not be initialized
       *
       * @return sum of both balance and grades
       */
      double checkSumGrades() {
        debugSumGrades2 = E.debugSumGrades;
        if (debugSumGrades2) {
          checkGrades();
        }
        double ret = sumGrades();
        /*
        worth = makeZero(worth);
        work = makeZero(work);
        facultyEquiv = makeZero(facultyEquiv);
        researcherEquiv = makeZero(researcherEquiv);
        manualsToKnowledgeEquiv = makeZero(manualsToKnowledgeEquiv);
        colonists = makeZero(colonists);
        engineers = makeZero(engineers);
        faculty = makeZero(faculty);
        researchers = makeZero(researchers);
        knowledge.set(commonKnowledge, newKnowledge);
         */
        if (didCashFlowStart && sIx == SIX && work.sum() < E.NZERO) {
          EM.doMyErr(didTradeInitCF + " work has no value");
        }
        if (didCashFlowStart && sIx == SIX && worth.sum() < E.NZERO) {
          EM.doMyErr(didTradeInitCF + " worth has no value");
        }
        if (debugSumGrades2) {
          checkGrades();
        }
        return ret;
      }
      //    int[] growthNames = {EM.RGROWTH, EM.CGROWTH, EM.SGROWTH, EM.GGROWTH};

      /**
       * doGrow do end of year growth for each SubAsset growth is done for the
       * SubAsset which calls the method both resource/cargo and staff/guests
       * are handled staff/guests move colonists up grades, as determined the
       * sumFacultyMults and sumResearchMults than knowledge upgrade is done for
       * the new grades of staff new knowledge growth is done only for staff
       *
       */
      void doGrow(String aPre) { // Assets.CashFlow.SubAsset.doGrow(...)
        ARow agro = bals.getRow(ABalRows.GROWTHSEFFIX + sIx);

        if (agro != growth) {
          EM.doMyErr("Err subasset growth not match bals growth");
        }
        ARow abal = bals.getRow(ABalRows.BALANCESIX + sIx);
        if (abal != balance) {
          EM.doMyErr("Err subasset balance not match bals balance");
        }
        double tmp3 = calcPercent(balance.sum(), growth.sum());
        double tmp4 = tmp3 < E.PZERO ? growth.sum() : tmp3;
        // nzStat(growthNames[sIx], tmp4);
        hist.add(new History(aPre, History.valuesMajor6, aschar + " balance", balance));
        hist.add(new History(aPre, History.valuesMajor6, subStrs[sIx] + "worth", worth));
        hist.add(new History(aPre, 3, "grow " + aschar, growth));
        if (!sstaff) {  // resource and cargo
          balance.add(growth);  // ARow adds all sectors
          for (int balIx : E.ASECS) {
            balance.set(balIx, balance.get(balIx) > EM.maxResources ? EM.maxResources : balance.get(balIx));
          }
          if (sIx == E.R) {
            r.worth.setAmultV(balance, eM.nominalWealthPerResource[pors]);
          }
          else {
            c.worth.setAmultV(balance, eM.nominalWealthPerResource[pors] * eM.cargoWorthBias[0]);
          }
        }
        else {  //S G
          /* facultyEquiv = makeZero(facultyEquiv);
           researcherEquiv = makeZero(researcherEquiv);
           manualToKnowledgeEquiv = makeZero(manualToKnowledgeEquiv);
           */

          moreK = makeZero(moreK);
          lessM = makeZero(lessM);

          double orig1s, effctiveUpgradePower, yesSkipGrades, sUp, kIncr, kMore, mDecr;
          int gradesUp;
          sumGradesUp = sIx == 0 ? 0 : sumGradesUp; // add all subassets
          for (int ix = 0; ix < E.lsecs; ix++) {;
            for (int k = 14; k > 0; k--) { //gradesix][15] keeps growing to max
              assert grades[ix][k] >= 0.0 : "in doGrow negative grade[" + ix + "][" + k + "]=" + EM.mf(grades[ix][k]);
              if (E.debugNegGrowth && E.noAsserts) {
                E.myTest(grades[ix][k] < NZERO, "doGrow a neg grade %7.3f=grades[%1.0f][%2.0f]  ", grades[ix][k], ix + 0., k + 0.);
              }
              orig1s = grades[ix][k];
              // lower limit effective faculty equiv depending on random*sector knowledge
              effctiveUpgradePower = Math.min(s.facultyEquiv.get(ix), cRand(4 + ix + k) * knowledge.get(ix) / E.knowledgeRequiredPerFacultyForJumping[k]);
              // only allow skiping grades as a function of
              // fractionStaffUpgrade is what needed to advancde a grade
              // facultyEquiv
              double skipGradesValue = effctiveUpgradePower * E.staffPromotePerFaculty[k];
              // limit number of skippers
             // yesSkipGrades = Math.min(skipGradesValue, grades[ix][k]);
              // yesSkipGrades = Math.max(0., yesSkipGrades); // number to skip a grade
              yesSkipGrades = skipGradesValue > grades[ix][k] ? grades[ix][k] = grades[ix][k] > 0. ? grades[ix][k] : 0. : skipGradesValue;
              /* calculate if moving up 1,2 or 3 grades */

              gradesUp = (int) Math.min(3., E.fractionStaffUpgrade[k] * effctiveUpgradePower * .5 * cRand(5 + 3 * k));
              gradesUp = (k + gradesUp) < E.LGRADES ? gradesUp : E.LGRADES - 1 - k;//only LGRADES in array
              sumGradesUp += gradesUp; // sum number of grades skipped some mult of 48??
              /* increasse higher grade, reduce min1s */
              if (gradesUp > 0) { // not no change
                // add jumped staff to higher grades
                double AMval = grades[ix][k + gradesUp] += yesSkipGrades;
                //limit to maxGrade
                grades[ix][k + gradesUp] = grades[ix][k + gradesUp] < EM.maxGrade ? grades[ix][k + gradesUp] : EM.maxGrade;

                // remove jumped staff if any from grades[ix][k]
                grades[ix][k] -= yesSkipGrades;
              }
              assert grades[ix][k] >= NZERO : "in doGrow negative grade[" + ix + "][" + k + "]=" + EM.mf(grades[ix][k]) + " gradesUp" + gradesUp + " yesSkipGrades" + EM.mf3(yesSkipGrades) + " grades[" + ix + "][" + k + gradesUp + "]=" + EM.mf(grades[ix][k + gradesUp]) + "\n";
              //     E.myTest(grades[ix][k] < NZERO, "doGrow1 neg grade %7.3f=grades[%1.0f][%2.0f] %1.0f=gradesUp, %7.3f=yesSkipGrades, %7.3f=grades[%1.0f][%2.0f]", grades[ix][k], ix + 0., k + 0., gradesUp + 0., yesSkipGrades, grades[ix][k + gradesUp], ix + 0., k + gradesUp + 0.);
              /**
               * not kept constrain fraction of upgrades for Full Staff by the
               * fraction * in E.fractionStaffUpgrade[k] or if less than 1 allow
               * all of min1s the members of that grade to move up 1 grade.
               *
               * sUp = E.fractionStaffUpgrade[k] % 1. > PZERO ?
               * (E.fractionStaffUpgrade[k] % 1. * grades[ix][k]) :
               * grades[ix][k]; sUp = Math.min(sUp, grades[ix][k]); sUp =
               * Math.max(sUp, 0.); grades[ix][k] -= sUp; grades[ix][k + 1] +=
               * sUp;
               */
              if (grades[ix][k] < NZERO) {
                E.myTest(true, "doGrow2 neg grade %7.3f=grades[%1.0f][%2.0f], %1.0f=gradesUp, %7.3f=yesSkipGrades, %7.3f=grades[%1.0f][%2.0f]", grades[ix][k], ix + 0., k + 0., gradesUp + 0., yesSkipGrades, grades[ix][k + 1], ix, k + 1);
              }

              if (grades[ix][k + 1] < NZERO) {
                E.myTest(true, "doGrow3 neg grade %7.3f=grades[%1.0f][%2.0f], %1.0f=gradesUp, %7.3f=yesSkipGrades, %7.3f=grades[%1.0f][%2.0f]", grades[ix][k + 1], ix + 0., k + 1., gradesUp + 0., yesSkipGrades, grades[ix][k], ix, k + 0.);
              }
            } // end loop on k
            // now add in growth at the lowest grade
            grades[ix][0] += growth.get(ix);
            if (grades[ix][0] < NZERO) {
              E.myTest(true, "doGrow4 neg grade %7.3f=grades[%1.0f][0] %7.3f=growth.get[%1.0f]", grades[ix][0], ix + 0., growth.get(ix), ix + 0.);
            }
            sumGrades();  // sum researcher, manualsToKnowledge equiv ,worth
            if (!reserve) { // skip knowledge for guests
              // now upgrade the knowledge for sector ix.
              kIncr = moreK.set(ix, knowledge.get(ix) + cRand(6 + ix) * s.researcherEquiv.get(ix) * eM.knowledgeGrowthPerResearcher[0] + knowledge.sum() * additionToKnowledgeBiasForSumKnowledge + (knowledge.get(ix) > eM.nominalKnowledgeForBonus[0] ? knowledge.get(ix) * eM.additionalKnowledgeGrowthForBonus[0] : 0.) * s.groEfficiency.get(ix));
              kMore = newKnowledge.get(ix) + kIncr;
              kMore = kMore < EM.maxKnowledge ? kMore : EM.maxKnowledge;
              newKnowledge.set(ix, kMore);
              // now move manuals to commonKnowledge
              mDecr = lessM.set(ix, kIncr * eM.kLearnManuals[pors][0] * s.manualsToKnowledgeEquiv.get(ix));
              // limit mDecr to manuals balance
              mDecr = mDecr > manuals.get(ix) ? manuals.get(ix) : mDecr;
              // mDecr = lessM.set(ix, Math.min(manuals.get(ix), mDecr));
              commonKnowledge.add(ix, mDecr);
              manuals.add(ix, -mDecr);  // reduce manuals, move manuals to knowledge
            }
          } // end loop on ix
          checkSumGrades(); // now  check sum all grades and related values
// now sum all grades and related values
          if (grades[0][2] < E.NZERO) {
            E.myTest(true, "doGrow grades neg1 staff.grades[0][2]=" + EM.mf(staff.grades[0][2]));
          }
        }
        bals.set1(ABalRows.PREVGROWTHSIX, sIx, agro);// prevgrowths starts here
        hist.add(new History(aPre, History.valuesMajor6, aschar + " endBalance", balance));
        hist.add(new History(aPre, History.valuesMajor6, aschar + " endWorth", worth));
      }// end doGrow

      /**
       * move staff/resources value between sectors move from balance of the
       * owning SubSector, to the destination. The owning SubAsset is the
       * source, its partner may also be used to satisfy the move. Only the
       * availFrac of the working SubAsset may be used. move staff to staff,
       * resource to resource, do a fatal error E.myTest if cannot do move
       *
       * @param move amount of resource or staff to move instance(source) to
       * @param sourceIx int sector of source to be moved
       * @param destIx int destination sector for move
       * @param myDest SubAsset destination for move, may be another econ
       * @param downgrade int Staff may be downgraded in a move
       * @param availFrac 1. value &lt; PZERO no avail test done 2. value &lt;
       * 1.-PZERO WR frac available 3. value &lt; 1.+PZERO no avail test 4.
       * value %ge; 1.+PZERO src units available
       * @return amount of move that couldn't be done must be 0
       */
      double putValue(double move, int sourceIx, int destIx, SubAsset myDest, int downgrade, double availFrac) {
        return putValue(balances, move, sourceIx, destIx, myDest, downgrade, availFrac);
      }

      /**
       * move staff/resources value between sectors move from balance of the
       * owning SubSector, to the destination. The owning SubAsset is the
       * source, its partner may also be used to satisfy the move. Only the
       * availFrac of the working SubAsset may be used. move staff to staff,
       * resource to resource, do a fatal error E.myTest if cannot do move
       *
       * @param avails6 the mtgAvails6 from CashFlows.getNeeds...
       * @param move amount of resource or staff to move instance(source) to
       * @param sourceIx int sector of source to be moved
       * @param destIx int destination sector for move
       * @param myDest SubAsset destination for move, may be another econ
       * @param downgrade int Staff may be downgraded in a move
       * @param availFrac <ul><li>value &lt; PZERO: no avail kept required after
       * move
       * <li> value &lt;1.-PZERO: WR frac to be kept available on source after
       * move
       * <li> value &lt; 1.+PZERO no avail kept required after the move
       * <li>value %ge; 1.+PZERO :src units to be kept availables after the move
       * </ol>
       * @return amount of move that couldn't be done must be 0
       */
      double putValue(A6Row avails6, double move, int sourceIx, int destIx, SubAsset myDest, int downgrade, double availFrac) { // Assets.CashFlow.SubAsset

        double remMov = move, spMov = 0., opMov = 0.;
        SubAsset sp = this;  // source partner
        SubAsset op = this.partner; // the other partner
        SubAsset dp = myDest; // destination partner
        SubAsset wp = (sp.reserve) ? op : sp;  // working partner
        SubAsset rp = (sp.reserve) ? sp : op; // reserve partner

        int sixsp = sp.sIx;
        int sixdp = dp.sIx;
        int sixop = op.sIx;
        int sixwp = wp.sIx;

        double availSp = avails6.get(sixsp + BALANCESIX, sourceIx);
        double availOp = avails6.get(sixop + BALANCESIX, sourceIx);
        double balSp = balances.get(sixsp + BALANCESIX, sourceIx);
        // double balOp = balances.get(sixop + BALANCESIX, sourceIx);
        double balOp = op.balance.get(sourceIx);
        // both balance and avail must have enough units for a move
        double balSp1 = balSp < availSp ? balSp : availSp;  // least available source
        double balOp1 = balOp < availOp ? balOp : availOp; // least available partner
        double bal1SO = balSp + balOp;  // sum of source and partner balances
        double bavailSO = balSp1 + balOp1; // least sum avail,bal
        double balWp = sp.reserve ? balOp1 : balSp1;  // available working
        double balRp = op.reserve ? balOp1 : balSp1; // available reserve
        double balDp = balances.get(sixdp + BALANCESIX, destIx);

        double resv = 5.;
        if (balSp1 > sp.balance.get(sourceIx)) {
          E.myTest(true, "Error bal%s%d %10.5g > balance%s%d %10.5g dif %10.5g n=%d", sp.aschar, sourceIx, balSp, sp.aschar, sourceIx, balSp - sp.balance.get(sourceIx));
        }
        if (E.debugPutValue && balOp1 > op.balance.get(sourceIx)) {
          E.myTest(true, "Error bal%s%d %10.5g > balance%s%d %10.5g dif %10.5g n=%d", sp.aschar, sourceIx, balSp, sp.aschar, sourceIx, balOp - op.balance.get(sourceIx), n);
        }

        if (availFrac < PZERO) { // find the reserved value, for optional inputs
          resv = 0.;
        }
        else if (availFrac < 1. - PZERO) {
          resv = bal1SO * availFrac;  // reserved values is frac of sum not just source
        }
        else if (availFrac < 1. + PZERO) {
          resv = 0.;
        }
        else {
          resv = availFrac;
        }
        // resv is the working  reserved that must remain with wp
        // resv = 0 if availtype==1 or availType == 0, else see below
        double canMovSp = balSp1 - (sp.reserve ? 0.0 : resv); // remainder available to move
        double canMovOp = balOp1 - (op.reserve ? 0.0 : resv);
        double canMovSO = canMovSp + canMovOp;
        // only availSp if incr or decr, for xfer avail == both with W reserved
        // this allows trade to also use availSP + availOp
        double canMov = (dp == sp.partner && sourceIx == destIx) ? canMovSp : canMovSO;

        assert move >= E.NNZERO : "ERROR negative move=" + EM.mf(move) + " n=" + n + ",term=" + term + ", i=" + i + ", j=" + j;
        if (!E.ifassert && E.debugPutValue && move < -0.0) {
          E.myTest(true, "ERROR negative move=" + EM.mf(move) + " n=" + n + ",term=" + term + ", i=" + i + ", j=" + j, move);
        }
        assert balSp1 >= E.NZERO : "ERROR negative " + sp.aschar + sourceIx + "=" + EM.mf(balSp) + ", n=" + n + ", term=" + term + ", i=" + i + ", j=" + j;
        if (!E.ifassert && E.debugPutValue && balSp1 < NZERO) {
          E.myTest(true, "ERROR negative %s%d = %7.4G, n=" + n + ",term=" + term + ", i=" + i + ", j=" + j, sp.aschar, sourceIx, balSp, n, term, i, j);
        }
        //This covers incr, decr, xfer and trade because of avail processing
        if (E.debugPutValue3) {
          if (canMov - move < -0.0) {
            throw new MyErr(String.format("ERROR move=%10.5g is more than canMov %20.5g, canMov%s%d = %10.5g,canMovOp%s%d=%20.5g rem=%10.5g, n=" + n + ",term=" + term + ", i=" + i + ", j=" + j, move, canMov, sp.aschar, sourceIx, canMovSp, op.aschar, sourceIx, canMovOp, canMov - move, n, term, i, j));
          }
        }
        else if (canMov - move < -0.0) {
          move = canMov;
        }

        // if incr or decr, only use avail sp
        // if xfer or trade(as1's differ
        if (sourceIx != destIx || sp.as1 != dp.as1) {
          opMov = Math.min(move, canMovOp);  // use only availOp
          if (E.debugPutValue3) {
            if (canMovOp - opMov < -0.0) {
              throw new MyErr(String.format("ERROR canMovOp" + EM.mf(canMovOp) + " too small, opMov=" + EM.mf(opMov) + " =" + EM.mf(canMovOp - opMov) + ", canMov " + EM.mf(canMov) + " - move" + EM.mf(move) + " =" + EM.mf(canMov - move) + ", n=" + n + ",term=" + term + ", i=" + i + ", j=" + j));
            }
          }
          else if (opMov > canMovOp) {
            opMov = canMovOp;
          }
          op.move2(opMov, sourceIx, destIx, myDest, downgrade);
          spMov = move - opMov;
        }
        else {
          //source must do the whole move
          spMov = move;
        }
        // either xfer or trade, use availsp and availop
        if (spMov > 0.0) {
          if (E.debugPutValue3) {
            if (canMovSp - spMov < -0.0) {
              throw new MyErr(String.format("ERROR %s%d->%s%d canMovSp%s%d %10.5g - spMov %10.5g=%10.5g, balSp %10.5g, balSp1 %10.5g, availSp %10.5g canMov %10.5g - move %10.5g=%10.5g, canMovOp%s%d %10.5g - opMov %10.5g = %10.5g, n=%d,  i=%d, j=%d, term=%d", sp.aschar, sourceIx, dp.aschar, destIx, sp.aschar, sourceIx, canMovSp, spMov, canMovSp - spMov, balSp, balSp1, availSp, canMov, move, canMov - move, op.aschar, sourceIx, canMovOp, opMov, canMovOp - opMov, n, i, j, term));
              //   E.myTest(true, "ERROR canMovSp" + sp.aschar + sourceIx + " " + EM.mf(canMovSp) + " - spMov  " + EM.mf(spMov) + "=" + EM.mf(canMovSp - spMov) + ", n=" + n + ",term=" + term + ", i=" + i + ", j=" + j);
            }
          }
          else if (canMovSp - spMov < -0.0) {
            spMov = canMovSp; //limit sourceMove to available
          }
          remMov = sp.move2(spMov, sourceIx, destIx, myDest, downgrade);
          if (sp.balance.get(sourceIx) < NZERO) {
            throw new MyErr(String.format("ERROR negative sp%s%d balance %10.5g,canMovSp%7.3g, n=%d, term=%d, i=%d, j=%d", sp.aschar, sourceIx, sp.balance.get(sourceIx), canMovSp, n, term, i, j));
          }

        }
        SubAsset op1 = op;
        ARow oprr = op.balance;
        double jj = op.balance.get(0);
        double jk = op.balance.get(sourceIx);
        hist.add(new History("@p", 8, name + " from " + aschar + sourceIx,
                             "mov=",
                             EM.mf(move),
                             sp.aschar + sourceIx + "=" + EM.mf(balSp),
                             "=>" + EM.mf(sp.balance.get(sourceIx)),
                             op.aschar + sourceIx + "=" + EM.mf(balOp),
                             "=>" + EM.mf(op.balance.get(sourceIx)),
                             "D" + myDest.asname + "=" + dp.aschar + destIx + "=",
                             EM.mf(balDp),
                             "=>" + EM.mf(myDest.balance.get(sourceIx))));
        return remMov;  // possible error if a leftover
      } // Assets.CashFlow.SubAsset.putValue

      /**
       * move sub-operation for putValue
       *
       * @param move move from this instance
       * @param sourceIx sector to move of the calling source
       * @param destIx sector to receive move
       * @param myDest destination sector (may be in a different Asset)
       * @param downgrade grades down for a staff moved
       *
       * @return any unfinished move
       */
      double move2(double move, int sourceIx, int destIx, SubAsset myDest, int downgrade) {//Assets.CashFlow.SubAsset
        double remMov = move;
        double prevSBal = balance.get(sourceIx);
        double prevDBal = myDest.balance.get(destIx);
        double[] spPreVals = new double[10];
        double[] dpPreVals = new double[10];
        double newSBal = 0., newDBal = 0.;
        for (m = 0; m < E.LSECS; m++) {
          spPreVals[m] = balance.get(m);
          dpPreVals[m] = myDest.balance.get(m);
        }
        if (this.balance.get(sourceIx) < E.NZERO) {
          if (E.debugPutValue) {
            assert false : "Error" + aschar + sourceIx + " negative=" + EM.mf(this.balance.get(sourceIx));
            if (!E.ifassert) {
              throw new MyErr(String.format("Error " + aschar + sourceIx + " negative=" + EM.mf(this.balance.get(sourceIx))));
            }
          }
          else {
            spPreVals[sourceIx] = balance.set(sourceIx, (0.0));
          }
        }
        if (move < -0.0 || spPreVals[sourceIx] - move < -0.0 || bals.get(this.sIx + 2, sourceIx) < -0.0 || this.balance.get(sourceIx) < -0.0) {
          if (E.debugPutValue) {
            assert false : String.format("ERROR negative source%s%d %7.3g - mov %7.3g rem=%7.3g,%7.3g", this.aschar, sourceIx, spPreVals[sourceIx], move, this.balance.get(sourceIx), bals.get(this.sIx + 2, sourceIx));
            if (!E.ifassert) {
              throw new MyErr(String.format("ERROR negative source%s%d %7.3g - mov %7.3g rem=%7.3g,%7.3g", this.aschar, sourceIx, spPreVals[sourceIx], move, this.balance.get(sourceIx), bals.get(this.sIx + 2, sourceIx)));
            }
          }
          else {
            if (move < -0.0) {
              move = 0.0;
            }
          }
        }
        // change balances for resources and staff
        newSBal = this.balance.add(sourceIx, -move); //decrement for all SubAssets
        newDBal = myDest.balance.add(destIx, move);
        if (!sstaff) {
          // see if source is enough for the move
          remMov -= move;
        }
        else { // balances done now do grades
          double mov1 = 0., mov2 = 0;
          // get frac of mov/bal increase frac to only 11 grades
          double gradeCost = (move / prevSBal) * (E.LGRADES / (E.LGRADES - 5)); // 3 - 14fraction of  move per staff
          // frac of mov/bal for only 8 grades
          double avmov = gradeCost * 1.3; // raise the cost slightly
          Double gradeCost2 = 0., oldSGrade = 0., oldDGrade = 0., gradeCost7 = 0.;

          int k = 0, kt = 0, kmax = 64, gradeIx = 0;
          int kmin = downgrade + 2;
          double srcSum = 0., destSum = 0.;
          //start with intern3 try up to 4 cycles
          for (gradeIx = 3; gradeIx < kmax && (remMov > E.PPZERO); gradeIx++) {
            k = gradeIx % E.LGRADES; // limit k value to E.LGRADES
            kmin = (downgrade + 2) - (int) (gradeIx / 8);
            kmin = kmin >= 0 ? kmin : 0;
            if (k < kmin) {
              int kup = kmin - k;
              k += kup;
              gradeIx += kup;
            }
            oldSGrade = grades[sourceIx][k];
            if (E.debugPutValue && grades[sourceIx][k] < -0.0) {
              throw new MyErr(String.format(" neg grades[" + sourceIx + "][" + k + "]=" + EM.mf(grades[sourceIx][k]) + " loop index gradeIx=" + gradeIx + " move=" + EM.mf(move) + " remMov=" + EM.mf(remMov) + " prevSBal=" + EM.mf(prevSBal)));
            }
            gradeCost2 = gradeCost * oldSGrade; // initial cost to this grade
            double gradeCost3 = gradeIx > (int) (E.LGRADES * 1.5) ? avmov * oldSGrade : gradeCost2;
            double gradeCost4 = Math.min(gradeCost3, grades[sourceIx][k]); // prevent neg
            double gradeCost5 = Math.min(gradeCost4, remMov); // only rest of move
            Double gradeCost6 = Math.max(gradeCost5, 0.); // force not negative
            gradeCost7 = gradeCost6.isInfinite() || gradeCost6.isNaN() ? 0. : gradeCost6;
            if (gradeCost7 > remMov) {  // again limit by remMov
              gradeCost7 = remMov;
            }
            if (E.debugPutValue && grades[sourceIx][k] - gradeCost7 < -0.0) {
              throw new MyErr(String.format(" moveValue grades neg2 grades[" + sourceIx + "][" + k + "]=" + EM.mf(grades[sourceIx][k]) + " - gradeCost7=" + EM.mf(gradeCost7) + " =" + EM.mf(grades[sourceIx][k] - gradeCost7) + " gradeIx=" + gradeIx + " move=" + EM.mf(move) + " avmov=" + EM.mf(avmov) + "gradeCost3=" + EM.mf(gradeCost3) + "gradeCost=" + EM.mf(gradeCost) + " remMov=" + EM.mf(remMov) + " prevSBal=" + EM.mf(prevSBal)));
            }
            grades[sourceIx][k] -= gradeCost7;
            kt = k - downgrade >= 0 ? k - downgrade : k; // kt >= 0
            oldDGrade = myDest.grades[destIx][kt];
            myDest.grades[destIx][kt] += gradeCost7;
            remMov -= gradeCost7;
            if (E.debugPutValue && myDest.grades[destIx][kt] < NZERO) {
              throw new MyErr(" moveValue grades neg myDest gradeIx=" + gradeIx + " myDest.sIx" + myDest.sIx + " myDest.grades[" + destIx + "][" + kt + "]=" + EM.mf(oldDGrade) + " source.six=" + sIx + " sourceIx.grades[" + sourceIx + "][" + k + "]=" + EM.mf(oldSGrade) + " - " + "gradeCost7=" + EM.mf(gradeCost7) + " source=" + EM.mf(srcSum) + " myDest=" + EM.mf(destSum) + " move=" + EM.mf(move) + " avmov=" + EM.mf(avmov) + "initial gradeCost=" + EM.mf(gradeCost) + " remMov=" + EM.mf(remMov) + " prevSBal=" + EM.mf(prevSBal));
            }

            if (E.debugPutValue && grades[sourceIx][k] < -0.0) {
              throw new MyErr(" moveValue grades neg2 gradesgradeIx=" + gradeIx + " myDest.sIx" + myDest.sIx + " myDest.grades[" + destIx + "][" + kt + "]=" + EM.mf(oldDGrade) + " source.six=" + sIx + " sourceIx.grades[" + sourceIx + "][" + k + "]=" + EM.mf(oldSGrade) + " - " + "gradeCost7=" + EM.mf(gradeCost7) + " source=" + EM.mf(srcSum) + " myDest=" + EM.mf(destSum) + " move=" + EM.mf(move) + " avmov=" + EM.mf(avmov) + "initial gradeCost=" + EM.mf(gradeCost) + " remMov=" + EM.mf(remMov) + " prevSBal=" + EM.mf(prevSBal));
            }

          }// end of for on k
          destSum = srcSum = 0.0;
          for (int gIx = 0; gIx < E.LGRADES; gIx++) {
            srcSum += grades[sourceIx][gIx];
            destSum += myDest.grades[destIx][gIx];
          }
          eM.printHere("---MVc---", ec.ec, " gradeIx=" + gradeIx + " myDest.sIx" + myDest.sIx + " myDest.grades[" + destIx + "][" + kt + "]=" + EM.mf(oldDGrade) + " source.six=" + sIx + "\n---MVc2--- sourceIx.grades[" + sourceIx + "][" + k + "]=" + EM.mf(oldSGrade) + " - " + "gradeCost7=" + EM.mf(gradeCost7) + " source=" + EM.mf(srcSum) + " myDest=" + EM.mf(destSum) + " move=" + EM.mf(move) + " avmov=" + EM.mf(avmov) + " ninitial gradeCost=" + EM.mf(gradeCost) + " remMov=" + EM.mf(remMov) + " prevSBal=" + EM.mf(prevSBal) + ", prevDBal=" + EM.mf(prevDBal));

          double difMax = balances.get(sourceIx) * 0.001 + .0001;
          double dif = 0.0;
          if (E.debugPutValue && remMov > E.PZERO) {
            throw new MyErr("---MVE1---- move " + ec.name + " remMov" + EM.mf(remMov) + " Error, difMax=" + EM.mf(difMax) + "\n gradeIx=" + gradeIx + " myDest.sIx" + myDest.sIx + "\n myDest.grades[" + destIx + "][" + kt + "]=" + EM.mf(oldDGrade) + " source.six=" + sIx + " sourceIx.grades[" + sourceIx + "][" + k + "]=" + EM.mf(oldSGrade) + " - " + "gradeCost7=" + EM.mf(gradeCost7) + "\n source=" + EM.mf(srcSum) + " myDest=" + EM.mf(destSum) + " move=" + EM.mf(move) + " avmov=" + EM.mf(avmov) + ", initial gradeCost=" + EM.mf(gradeCost) + " remMov=" + EM.mf(remMov) + " prevSBal=" + EM.mf(prevSBal) + ", prevDBal=" + EM.mf(prevDBal));
          }
          if (E.debugPutValue && (dif = Math.abs(newSBal - srcSum)) > difMax) {
            throw new MyErr("---MVE2---- move " + ec.name + " Source Dif to large, difMax=" + EM.mf(difMax) + ", dif=" + EM.mf(dif) + " remMov" + EM.mf(remMov) + " gradeIx=" + gradeIx + "\n, newSBal=" + EM.mf(newSBal) + " -srcSum=" + EM.mf(srcSum) + "=" + EM.mf(newSBal - srcSum) + "\n, exceeds difMax=" + EM.mf(difMax) + " myDest.sIx" + myDest.sIx + "\n source.six=" + sIx + " move=" + EM.mf(move) + " remMov=" + EM.mf(remMov) + " prevSBal=" + EM.mf(prevSBal) + ", prevDBal=" + EM.mf(prevDBal));
          }
          if (E.debugPutValue && (dif = Math.abs(newDBal - destSum)) > difMax) {
            throw new MyErr("---MVE3---- move " + ec.name + " Error, difMax=" + EM.mf(difMax) + ", dif=" + EM.mf(dif) + " remMov" + EM.mf(remMov) + "gradeIx=" + gradeIx + "\n,   newDBal=" + EM.mf(newDBal) + " -destSum=" + EM.mf(destSum) + "=" + EM.mf(newDBal - destSum) + "\n, exceeds difMax=" + EM.mf(difMax) + " myDest.sIx" + myDest.sIx + "\n source.six=" + sIx + " move=" + EM.mf(move) + " remMov=" + EM.mf(remMov) + " prevSBal=" + EM.mf(prevSBal) + ", prevDBal=" + EM.mf(prevDBal));
          }
          //  E.myTest(remMov > difMax,"move Error, difMax=%10.3g, remMov left=%10.3g",difMax,remMov);

          myDest.checkSumGrades();  // myDest with added mov
          checkSumGrades();// with subtracted mov
          if (E.debugPutValue && this.balance.get(sourceIx) < NZERO) {
            E.myTest(true, "Error " + aschar + sourceIx + " = negative " + EM.mf(this.balance.get(sourceIx)));
          }
          if (E.debugPutValue && myDest.balance.get(destIx) < NZERO) {
            E.myTest(true, "Error " + myDest.aschar + destIx + " = negative " + EM.mf(myDest.balance.get(destIx)));
          }
        }
        return remMov;
      }// Assets.CashFlow.SubAsset.move2

      /**
       * apply the cost to sp then to op if needed
       *
       * @param cost assume caller has tested for value in sp or sp+op
       * @param sourceIx
       * @param availFrac<ul><li>value &lt; PZERO: no avail kept required after
       * cost
       * <li> value &lt;1.-PZERO: Source+Other frac to be kept available on
       * source after move
       * <li> value &lt; 1.+PZERO no avail kept required after the move
       * <li>value %ge; 1.+PZERO :src units to be kept availables after the move
       * </ol>
       *
       * @return
       */
      synchronized double cost3(double cost, int sourceIx, double availFrac) {
        EM.wasHere8 = "---ELa6--- Assets cost3 has lock";
        // Assets.CashFlow.SubAsset.cost3
        if (sstaff) {
          checkGrades();
        }
        double costRem = cost, myRem = 0.;
        SubAsset sp = this;  // source partner
        SubAsset op = this.partner; // the partner partner
        SubAsset wp = (reserve) ? op : sp; // select working partner

        int sixsp = sp.sIx; // index of the source partner
        int sixop = op.sIx;
        int sixwp = wp.sIx;

        double balSp = bals.get(sixsp + E.LSUMS, sourceIx);
        double balOp = bals.get(sixop + E.LSUMS, sourceIx);
        double balWp = bals.get(sixwp + E.LSUMS, sourceIx);
        double balSO = balSp + balOp;  // sum of source and partner

        int availType = 0;
        double resv = 0.0, resvs = 0., resvo = 0.;
        if (availFrac < PZERO) {
          availType = 1;
          resvs = resvo = resv = 0.0;
        }
        else if (availFrac < 1. - PZERO) {
          availType = 2;
          resvs = (balSp) * availFrac;  // the reserve is source bal* availFrac
          resvo = balOp * availFrac;
        }
        else if (availFrac < 1. + PZERO) {
          availType = 1; // around 1
          resvs = resvo = 0.0;
        }
        else {
          availType = 3; // over 1.+PZERO
          resvs = availFrac;
          resvo = 0.0;
        }
        resv = resvs + resvo;
        // resv is the reserved units cannot be spent
        double availSp, availOp = 0., availSO, avail;
        if (E.debugDouble) {
          //avail source = source balance - resv if source is working partner
          availSO = (availSp = doubleTrouble(doubleTrouble(balSp) - doubleTrouble(resvs)) + (availOp = doubleTrouble(balOp) - resvo)); // remainder available to move
          //availOp = doubleTrouble(doubleTrouble(balOp) - (sp.reserve ? resv : 0.));
          //availSO = doubleTrouble(availSp + availOp);
          // only availSp if incr or decr, for xfer avail == both with W reserved
          // this allows trade to also use availSP + availOp
          avail = availSO;
        }
        else {
          // availSp = balSp - (op.reserve ? resv : 0.); // remainder available to move
          //availOp = balOp - (sp.reserve ? resv : 0.);
          // availSO = availSp + availOp;
          // only availSp if incr or decr, for xfer avail == both with W reserved
          // this allows trade to also use availSP + availOp
          avail = availSO = (availSp = balSp - resvs) + (availOp = balOp - resvo); // remainder available to move
        }
        boolean isShip = pors == E.S;
        double costSpa = Math.min(cost, availSp);
        double mASp = mtgAvails6.get(E.LSUMS + sixsp, sourceIx);
        double costSp = Math.min(costSpa, mASp * .8);
        double costOp = Math.min((costRem = cost - costSp), availOp);
        if (E.debugFFOut) {
          System.out.println(EM.wasHere3 = "-----GA----" + name + "Y" + EM.year + " cost3 sixsp" + sixsp + EM.mf("availSp", availSp) + EM.mf("balSp", balSp) + EM.mf("mASp", mASp) + EM.mf("costSpa", costSpa) + EM.mf("costSp", costSp) + EM.mf("remSp", availSp - costSp) + EM.mf("availFrac", availFrac) + EM.mf("resvs", resvs) + " sixop" + sixop + EM.mf("availOp", availOp) + " balOp" + EM.mf(balOp) + " resvo" + EM.mf(resvo) + " costRem" + EM.mf(costRem) + " costOp" + EM.mf(costOp));
        }

        aPre = "$c";
        // ensure there is enough balance to cover the cost

        //   hist.add(new History(aPre, History.valuesMinor7, n + "cost3 A " + aschar + sourceIx, "costExcd Avail", "kF=" + EM.mf(availFrac), "aW=" + EM.mf(availW), "aR=" + EM.mf(availR), "-Cst=" + EM.mf(cost), "=>" + EM.mf(availWR - cost)));
        assert cost >= 0 : "Error cost negative = " + EM.mf(cost);
        if (E.debugCosts) {
          if (E.noAsserts && cost < +0.0) {
            eM.doMyErr("Error cost negative = " + EM.mf(cost));
          }
        }
        // assert avail >= cost: "cost=" + EM.mf(cost) + " exceeds available=" + EM.mf(avail) + ", " + sp.aschar + sourceIx + "=" + EM.mf(avail) + ", O" + op.aschar + sourceIx + "=" + EM.mf(availOp) + ", n=" + n + ", reDo" + reDo + ", i=" + i + ", j=" + j;
        assert availSp >= costSp : "costSp=" + EM.mf(costSp) + " exceeds availSp=" + EM.mf(availSp) + ", " + sp.aschar + sourceIx + " balSp=" + EM.mf(balSp) + " remSp" + EM.mf(costSp - availSp) + ", O" + op.aschar + sourceIx + "=" + EM.mf(availOp) + ", n=" + n + ", reDo" + reDo + ", i=" + i + ", j=" + j;
        if (E.debugCosts) {
          if (E.noAsserts && availSp < costSp) {
            EM.doMyErr("costsp=" + EM.mf(costSp) + " exceeds availsp=" + EM.mf(availSp) + ", " + sp.aschar + sourceIx + "=" + EM.mf(availSp) + ", O" + op.aschar + sourceIx + "=" + EM.mf(availOp) + ", n=" + n + ", reDo" + reDo + ", i=" + i + ", j=" + j);
          }
        }

        if (availSp < costSp) {
          if (E.debugFFOut) {
            System.out.println(EM.wasHere2 = "-----GC----" + name + "Y" + EM.year + " cost3 sixsp" + sixsp + EM.mf("availSp", availSp) + EM.mf("balSp", balSp) + EM.mf("mASp", mASp) + EM.mf("costSpa", costSpa) + EM.mf("costSp", costSp) + EM.mf("remSp", availSp - costSp) + EM.mf("availFrac", availFrac) + EM.mf("resvs", resvs) + " sixop" + sixop + EM.mf("availOp", availOp) + " balOp" + EM.mf(balOp) + " resvo" + EM.mf(resvo) + " costRem" + EM.mf(costRem) + " costOp" + EM.mf(costOp));
          }
          sp.cost1(costSp, sourceIx);
          if (E.debugCosts) {
            if (sp.balance.get(sourceIx) < NZERO) {
              EM.doMyErr("source" + sp.aschar + sourceIx + " is negative " + EM.mf(sp.balance.get(sourceIx)) + ", n=" + n + ", swapType=" + swapType);
            }
          }

        }
        // now look at second cost
        if (costOp > 0.0) {
          if (availOp < costOp) {
            if (E.debugCosts) {
              EM.doMyErr(String.format("costRem=%10.5 exceeds availOp%s%d=%10.5g" + ", n=" + n + ", reDo" + reDo + ", i=" + i + ", j=" + j, costRem, op.aschar, sourceIx, availOp));
            }
            else {
              costRem = availOp;
            }

          }
          myRem = op.cost1(costOp, sourceIx);
        }

        if (op.balance.get(sourceIx) < NZERO) {
          if (E.debugCosts) {
            EM.doMyErr("Error " + op.aschar + sourceIx + " is less than 0.0 = " + EM.mf(op.balance.get(sourceIx)));
          }
          else {
            op.balance.set(sourceIx, 0.0);
          }
        }

        if (E.debugFFOut) {
          System.out.println(EM.wasHere2 = "-----H----" + name + "Y" + EM.year + " cost3 sixsp" + sixsp + " availSp" + EM.mf(availSp) + " balSp" + EM.mf(balSp) + " costSp" + EM.mf(costSp) + " remSp" + EM.mf(availSp - costSp) + " availFrac" + EM.mf(availFrac) + " resvs" + EM.mf(resvs) + " sixop" + sixop + " availOp" + EM.mf(availOp) + " balOp" + EM.mf(balOp) + " resvo" + EM.mf(resvo) + " costRem" + EM.mf(costRem) + " costOp" + EM.mf(costOp) + " myRem" + EM.mf(myRem));
        }
        // raise W cost as needed, the test above shows there is enough balance
        // double costsW = costsW1;
        //double costsR = costsR1;
        hist.add(new History(aPre, History.informationMinor9, n + "cost3 " + aschar + sourceIx, "sP" + EM.mf(balSp), "sAF" + EM.mf(availFrac), "sA=" + EM.mf(availSp), "sC" + EM.mf(costSp), "sB" + EM.mf(bals.get(sixsp + BALANCESIX, sourceIx)), "oP" + EM.mf(balOp), "oA" + EM.mf(availOp), "oC" + EM.mf(costRem), "=>" + EM.mf(availOp - costRem), "oB" + EM.mf(bals.get(sixop + BALANCESIX, sourceIx)), "<<<<<<<<<"));

        return myRem;
      }  // Assets.CashFlow.SubAsset.cost3

      /**
       * docost charge the total costs for a year of a SubAsset docost is called
       * as a method of the SubAsset resource and staff
       */
      void doCost(String aPre, ARow pays) {// Assets.CashFlow.SubAsset.doCost
        if (History.dl > 4) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          hist.add(new History(aPre, 5, ">>> n" + n + "doCost " + name, a0.getMethodName(), "at", a0.getFileName(), wh(a0.getLineNumber()), "pays=", EM.mf(pays.sum())));
        }
        for (m = 0; m < E.lsecs; m++) {
          cost3(pays.get(m), m, eM.availFrac[pors][clan]);
        }

      }

      /**
       * docost charge the total costs for a year of a SubAsset docost is called
       * as a method of the SubAsset resource and staff
       */
      void doCost(String aPre, String costName, ARow costRow) {
        // Assets.CashFlow.SubAsset
        if (History.dl > 4) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          hist.add(new History(aPre, 5, ">>> n" + n + "doCost " + name, a0.getMethodName(), "at", a0.getFileName(), wh(a0.getLineNumber()), costName + "=", EM.mf(costRow.sum())));
        }
        for (m = 0; m < E.lsecs; m++) {
          cost3(costRow.get(m), m, .00001);
        }

      }

      /**
       * a lazy conversion a moveStaff to dieStaff where move is now die
       *
       * @param cost number of staff to die
       * @param sourceIx
       * @return
       */
      double cost1(double cost, int sourceIx) {// Assets.CashFlow.SubAsset
        double[] spPreVals = new double[10];
        double spPreSum = 0;
        for (m = 0; m < LSECS; m++) {
          spPreSum += spPreVals[m] = balance.get(m);
        }
        spPreVals[8] = spPreSum;
        if (sstaff) {
          checkGrades();
        }
        double prevbal = balance.get(sourceIx);
        double remMov = doubleTrouble(cost);
        double cost2 = cost;
        if (cost < E.NNZERO) {
          if (E.debugCosts) {
            throw new MyErr(String.format("Negative cost%7.3g, term%d, i%d, j%d, m%d, n%d", cost, as.term, as.i, as.j, as.m, as.n));
          }
          else {
            cost = 0.0;
          }
        }
        //9/9/15 skip almost 0  cost, avoid infinite or NaN results
        //     hist.add(new History("cst1a", 7, n + " preCost", balance));
        if (cost > E.PPZERO) {
          int gradeIx = 0;
          //  double mvd = 0;
          if (bals.get(2 + sIx, sourceIx) - cost < NZERO && E.debugCosts) {
            throw new MyErr(String.format(" " + aschar + sourceIx + " cost=" + EM.mf(cost) + " exceeds balance=" + EM.mf(balance.get(sourceIx)) + " remainder=" + EM.mf(cost - balance.get(sourceIx)) + ", i" + i + ", j" + j + ", m" + m + ", n" + n));
          }

          if (!sstaff) { // resources
            if (E.debugDouble) {
              double v = doubleTrouble(
                      doubleTrouble(balance.get(sourceIx))
                      - doubleTrouble(cost));
              bals.set(2 + sIx, sourceIx, v);
            }
            else {
              bals.add(2 + sIx, sourceIx, -cost);// for all SubAssets
            }
            remMov -= cost;
            hist.add(new History("$P", 5, n + "cost1 ", "cost" + aschar + sourceIx + "=", EM.mf(cost2), "prevbal=", EM.mf(prevbal), "->" + EM.mf(bals.get(2 + sIx, sourceIx)), "rem=", EM.mf(remMov)));
          }
          else { // staff process
            // cost/(balance *(E.LGRADES-2) = costFrac per grade
            // lpgrades2 versus lpgrades2-5 increases the frac
            // normal cost This will probably prevent costing the 2 top grades
            checkGrades(); // should still add up right
            double sourceBal = balance.get(sourceIx);
            // avoid infinite gradeCost if balance.get(sourceIx) == 0.
            boolean goodSBal = sourceBal > +0.0;
            double gradeCost = goodSBal ? cost * E.lgrades / (balance.get(sourceIx) * (E.lgrades - 2)) : .01;
            // to be safe put in a somewhat larger limit frac
            double avmov = goodSBal ? cost / (balance.get(sourceIx) * (E.LGRADES - 5)) : 0.0;
            double gradeCost2 = 0., gradeCost3 = 0., gradeCost4 = 0., dmov = 0., emov = 0., fgrad = 0., grem = 0.;
            int k = 0, kt = 0;
            int gradeIxMax = E.LGRADES * 6;
            double lMult = 0.0;
            for (gradeIx = 0; gradeIx < gradeIxMax && (remMov > +0.0); gradeIx++) {
              k = gradeIx % E.LGRADES; // 0-> 15
              k = E.LGRADES - 1 - k; // 15 -> 0
              if (grades[sourceIx][k] < NZERO) {
                if (History.dl > 4) {
                  StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

                  hist.add(new History(aPre, 7, "n" + n + ">>>> zero error", a0.getMethodName(), "at", a0.getFileName(), wh(a0.getLineNumber()), "cost=" + EM.mf(cost), "prebal " + aschar + sourceIx, "[" + k + "]=" + EM.mf(grades[sourceIx][k])));
                }
                throw new MyErr(" grade lt zero " + aschar + sourceIx + "  grades[" + sourceIx + "][" + k + "]=" + EM.mf(grades[sourceIx][k]) + " gradeIx=" + gradeIx + " avmov=" + EM.mf(avmov) + "gradeCost=" + EM.mf(gradeCost) + " remMov=" + EM.mf(remMov) + " prevBal=" + EM.mf(prevbal) + ", term" + as.term + ", i" + as.i + ", j" + as.j + ", m" + as.m + ", n" + as.n);
              }
              // otherwise subtract cost
              // increase gradeCost2 at the count moves up
              //gradeCost2 = gradeCost * grades[sourceIx][k]* (gradeIx+ gradeIxMax -10)/gradeIxMax;
              if (E.debugDouble) {
                gradeCost2 = doubleTrouble(
                        doubleTrouble(gradeCost)
                        * doubleTrouble(grades[sourceIx][k])
                        * doubleTrouble(0. + (gradeIx + gradeIxMax - 10) / gradeIxMax));
                gradeCost3 = doubleTrouble(Math.max(
                        doubleTrouble(avmov),
                        doubleTrouble(gradeCost2))); // increase a small tail
                gradeCost4 = doubleTrouble(Math.min(
                        doubleTrouble(gradeCost3),
                        doubleTrouble(grades[sourceIx][k]))); // prevent neg grade result
                dmov = doubleTrouble(Math.min(
                        doubleTrouble(gradeCost4),
                        doubleTrouble(remMov)));  //don't take more than needed
                emov = doubleTrouble(Math.max(dmov, 0.)); // keep gradeCost2 positive

              }
              else {
                gradeCost2 = gradeCost * grades[sourceIx][k] * (gradeIx + gradeIxMax - 10) / gradeIxMax;
                gradeCost3 = Math.max(avmov, gradeCost2); // increase a small tail
                gradeCost4 = Math.min(gradeCost3, grades[sourceIx][k]); // prevent neg grade result
                dmov = Math.min(gradeCost4, remMov);  //don't take more than needed
                emov = Math.max(dmov, 0.); // keep gradeCost2 positive
              }
              if (grades[sourceIx][k] - emov < NZERO) {
                if (History.dl > 4) {
                  StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
                  hist.add(new History("cst1E", 7, "n" + n + ">>>> error", "neg grade", a0.getMethodName(), "at", a0.getFileName(), EM.mf(a0.getLineNumber()), "cost=" + EM.mf(cost), "bal " + aschar + sourceIx, "[" + k + "] - emov=" + EM.mf(grades[sourceIx][k] - emov)));
                }
                throw new MyErr(" cost1 grades neg2 grades[" + sourceIx + "][" + k + "]=" + EM.mf(grades[sourceIx][k]) + " - " + "emov=" + EM.mf(emov) + " =" + EM.mf(grades[sourceIx][k] - emov) + " gradeIx=" + gradeIx + " cost=" + EM.mf(cost) + " avmov=" + EM.mf(avmov) + "gradeCost=" + EM.mf(gradeCost) + " remMov=" + EM.mf(remMov) + " prevBal=" + EM.mf(prevbal) + ", term" + as.term + ", i" + as.i + ", j" + as.j + ", m" + as.m + ", n" + as.n);
              }
              if (E.debugDouble) {
                fgrad = grades[sourceIx][k]
                        = doubleTrouble(
                                doubleTrouble(grades[sourceIx][k])
                                - doubleTrouble(emov));
              }
              else {
                fgrad = grades[sourceIx][k] -= emov;
              }
              //    mvd += emov;
              grem = remMov -= emov;
              if (grades[sourceIx][k] < NZERO) {
                throw new MyErr(" cost1 grades neg3 grades[" + sourceIx + "][" + k + "]=" + EM.mf(grades[sourceIx][k]) + "emov=" + EM.mf(emov) + " gradeIx=" + gradeIx + " cost=" + EM.mf(cost) + " avmov=" + EM.mf(avmov) + "gradeCost=" + EM.mf(gradeCost) + " remMov=" + EM.mf(remMov) + " prevBal=" + EM.mf(prevbal) + ", term" + as.term + ", i" + as.i + ", j" + as.j + ", m" + as.m + ", n" + as.n);
              }

              //    destination.sumGrades();
            }//
            if (E.debugCosts && remMov > .0001 * prevbal) {
              throw new MyErr("cost1 " + aschar + sourceIx + " gradeIx=" + gradeIx + " excessive cost=" + EM.mf(cost) + " remainder=" + EM.mf(remMov) + " balance=" + EM.mf(balance.get(sourceIx)) + " prev balance=" + EM.mf(prevbal) + ", gradeCost" + EM.mf(gradeCost) + ", gradeCost2" + EM.mf(gradeCost2) + ", gradeCost3" + EM.mf(gradeCost3) + ", gradeCost4" + EM.mf(gradeCost4) + ", dmov" + EM.mf(dmov) + ", emov" + EM.mf(emov) + ", fgrad" + EM.mf(fgrad) + ", grem" + EM.mf(grem) + ", term" + as.term + ", i" + as.i + ", j" + as.j + ", m" + as.m + ", n" + as.n);
            }
            if (E.debugDouble) {
              double v = doubleTrouble(
                      doubleTrouble(balance.get(sourceIx))
                      - doubleTrouble(cost));
              bals.set(2 + sIx, sourceIx, v);
            }
            else {
              bals.add(2 + sIx, sourceIx, -cost);// for all SubAssets
            }
            checkSumGrades();
          }//sstaff
        }//cost
        //     hist.add(new History("cst1", 7, "postBal", balance));
        return remMov;  // return only if no error
      }// Assets.CashFlow.SubAsset.cost1

      /**
       * Apply ARow of costs to the calling SubAsset
       *
       * @param theCosts
       * @return
       */
      double costs(ARow theCosts) {// Assets.CashFlow.SubAsset
        double reMove = 0.;
        double mov = 0.;
        double bal = 0.;
        for (m = 0; m < E.lsecs; m++) {
          reMove = theCosts.get(m);
          bal = balance.get(m);
          mov = cost1(reMove, m);
          if (mov > PZERO) {
            E.myTest(true, "cost exceeds prev balance =" + EM.mf(bal) + " cost=" + EM.mf(reMove) + " Ix=" + m + " remainder=" + EM.mf(mov) + " balance=" + EM.mf(balance.get(m)));
          }
        }
        return mov;
      }// Assets.CashFlow.SubAsset.costs
    } // end Assets.AssetYr.SubAsset

    // Assets.CashFlow
    /**
     * Trades offers are part of the trading process between ships and planets.
     * Trade is a subclass of AssetYr enables its methods to access needed
     * objects in CashFlow, yet only 2 trade classes exist at the same time,
     * there is one thread with a binary trade session. Trade is instantiated
     * and closed in the CashFlow.barter
     */
    class Trades { // Assets.CashFlow.Trades

      //  int term; raised to Assets
      int changes, bartersNoChange = 0;

      String aPre = "*T", aPre0 = "*M", aPre1 = "*N", aPre2 = "*O", aPre3 = "*P";
      String tradeHist = "HIST ";
      int lRes = History.loopIncrements3; // listing flag for summary
      int mRes = History.valuesMajor6; // listing for other
      boolean doHistOther = eM.trade2HistOutputs; // dup all hist to other
      boolean newBarter = true;
      boolean inited = false;
      int oBlev = eM.trade2HistOutputs ? History.dl : -1; //blev of other lines
      int ifSearch = yphase == yrphase.SEARCH ? 0 : 1; // search or barter
      int hcntr = (int) eM.criticalNumbers[ifSearch][pors][clan];
      double sumCriticalNeeds;
      int histStart = -100;
      int prevTerm = 20;
      double rGoalT, rGoalFrac;
      double requestsAddedValue;
      int chgCnt = 0;
      //  double cashBeforeTrade;
      //    ARow sCargo = new ARow(ec);
      //    ARow sGuests = new ARow(ec);
      double[][] gGrades = new double[E.lsecs][E.lgrades];
      double sf1 = 0., sv1 = 0., ts1 = 0., tr1 = 0., isf1 = 1., isv1 = 1., sv0 = 0., xof = 0., xof1 = 0., xof2 = 0.;
      Offer myOffer;
      int primaryStaffNeed, secondStaffNeed;
      A2Row myFirstBid = new A2Row(ec, History.valuesMinor7, "myFirstBid");
      // value of the other at exit, entrance before flip
      A2Row oFirstBid = new A2Row(ec, History.valuesMinor7, "oFirstBid");
      A2Row oprevGoods;
      //  A2Row oprev1Goods, oprev2Goods, oprev3Goods, oprev4Goods, initGoods;
      A2Row myxitGoods, myxit1Goods, myxit2Goods, myxit3Goods;
      // A2Row stratV,stratCriticalRequests,stratF,nominalV,nominalCV;
      // use curPlusSum
      A2Row stratV = new A2Row(ec, 13, "stratV");
      A2Row stratCriticalRequests = new A2Row(ec, 13, "stratCriticalRequests"); // critical strat values
      A2Row stratF = new A2Row(ec, 13, "stratF"); // stratFraction*stratWeight
      A2Row nominalV = new A2Row(ec, 13, "nominalV");
      A2Row nominalCV = new A2Row(ec, 13, "nominalCV");
      A2Row nominalCF = new A2Row(ec, 13, "nominalCF");
      A2Row goodC = new A2Row(ec, 13, "goodC");
      A2Row stratCF = new A2Row(ec, 13, "StratCF");// sF * critW
      A2Row nominalF = new A2Row(ec, 13, "nominalF"); // nF * normW
      A2Row multF = new A2Row(ec, 13, "multF"); //sum of stratF,normF, ?stratCF
      A2Row multV = new A2Row(ec, 13, "multV"); //sum of stratV,normV ?stratCriticalRequests
      double nbOffers;
      double nbRequests, nbStrategicValue, nbExcessOffers;
      A2Row nbStratF, nbStratV, nbStratCV;

      //  ARow cLimTrade = new ARow(ec);
//      ARow gLimTrade = new ARow(ec);
      double realChanges = 0.; // per turn
      int[] valueChangesTried = new int[E.L2SECS];
      int[] oraisedBid = new int[E.L2SECS];
      int[] myRaisedBids = new int[E.L2SECS];
      int[] myTakeReq = new int[E.L2SECS]; // change offer(oReq)->myReq

      //   A2Row limTrade = new A2Row(cLimTrade, gLimTrade);
      //    A2Row myLim = limTrade; // a second reference to limTrade;
      A2Row availOfrs = new A2Row(ec, History.informationMinor9, "availOfrs");
      A2Row maxReqs = new A2Row(ec, History.loopMinorConditionals5, "maxReqs");
      A2Row needReq = new A2Row(ec, History.informationMinor9, "needReq");
      A2Row fneedReq = new A2Row(ec, History.informationMinor9, "fneedReq");
//      ARow cpyCMaxTrade = new ARow(ec);
      //    ARow cpyGMaxTrade = new ARow(ec);
      //     A2Row cpyMaxTrade = new A2Row(cpyCMaxTrade, cpyGMaxTrade);

      A2Row emergOfrs = new A2Row(ec, History.informationMinor9, "emergOfrs");
      ARow cMovedTrade = new ARow(ec);
      ARow gMovedTrade = new ARow(ec);

      A2Row movedTrades = new A2Row(cMovedTrade, gMovedTrade);
      //   A2Row futRemnants = new A2Row(r.tFutRemnant, s.tFutRemnant);
      A2Row didGood = new A2Row(ec); // seet flags done
      int[] did = new int[E.l2secs];
      ARow pr = copy(r.balance);
      ARow pc = copy(c.balance);
      ARow ps = copy(s.balance);
      ARow pg = copy(g.balance);
      A6Row pbal = new A6Row(ec, History.valuesMajor6, "pbal").set(balances);

      Trades() {// Assets.CashFlow.Trades
      }

      void initTrade(Offer inOffer, CashFlow ar) { // Assets.CashFlow.Trades
        histTitles("initTrade");
        try {
          ohist = inOffer.getOtherHist();
          oEcon = inOffer.getOEcon();
          stratV.zero();
          stratCriticalRequests.zero(); // critical strat values
          stratF.zero(); // stratFraction*stratWeight
          nominalV.zero();
          nominalCV.zero();
          nominalCF.zero();
          goodC.zero();
          stratCF.zero();// sF * critW
          nominalF.zero(); // nF * normW
          availOfrs.zero();
          maxReqs.zero();
          needReq.zero();
          fneedReq.zero();
          emergOfrs.zero();
          cMovedTrade.zero();
          gMovedTrade.zero();
          movedTrades.zero();
          //     futRemnants = new A2Row(r.tFutRemnant, s.tFutRemnant);
          didGood.zero(); // seet flags doneA2Row movedTrades = new A2Row(cMovedTrade, gMovedTrade);
          multF.zero(); //sum of stratF,normF, ?stratCF
          multV.zero(); //sum of stratV,normV ?stratC
          if (ec.dead) {
            return;
          }
          inOffer.setC(ar.c.balance);  // check c == c
          if (oTradedEconsNext < lTradedEcons - 1) {
            oTradedEcons[oTradedEconsNext++] = oEcon;
          }
          ec.blev1 = oBlev = eM.trade2HistOutputs ? -1 : History.dl; //blev of other lines
          lightYearsTraveled = ec.distanceMoved; // set in sStartTrade
          lightYearsTraveled = ((lightYearsTraveled < .2)) ? eM.initTravelYears[pors][0] : lightYearsTraveled;
          //initialize for the growth and efficiency
          eM.printHere("----TINa----", ec, "initTrade... before calcEfficiency loop");
          if (!didCashFlowStart && !ec.dead) {
            if (E.doCalcCatastrophy) {
              // do after AI start  calcCatastrophy(0);
            }

          }
          if (ec.dead) {
            return;
          }
          rs = eM.makeClanRS(eM.rs4, eM.mult5Ctbl, ec);//may change yearly
          if (false && !didDepreciation) { //only in aStartCashFlow
            for (k = 0; k < 4 && !ec.dead; k++) {
              sys[k].calcEfficiency();
              sys[k].calcGrowth();
            }
            didDepreciation = true;
          }

          for (int i = 0; i < E.L2SECS; i++) {
            valueChangesTried[i] = 0;
            oraisedBid[i] = 0;
          }

          // always recalculate costs
          String ttype = yphase.toString() + " ";
          lTitle
                  = "trd " + inOffer.cnName[1] + " " + inOffer.cnName[0];
          histTitles(lTitle);
          hist.add(new History("**", History.loopIncrements3, lTitle, " >>>>>>> initiate trade" + inOffer.cnName[1] + " " + inOffer.cnName[0] + " <<<<<<"));
          swapped = true;
          n = 0;
          prevn = n;
          inOffer.setCash(cash);
          myOffer = inOffer; // give reference an additional name
          fracN = n / eM.maxn[pors];
          nextN = 2.;
          blev = History.dl;
          aPre = "Ta";
          bals.unzero("bals", BALANCESIX, 4);
          ifSearch = yphase == yrphase.SEARCH ? 0 : 1; // search or barter
          hcntr = (int) eM.criticalNumbers[ifSearch][pors][clan];
          sumCriticalNeeds = 0.0;
          for (int ixStrat = 0; ixStrat < hcntr; ixStrat++) {
            int ix2 = stratVars.curMaxIx(ixStrat);
            sumCriticalNeeds = bals.get(ix2) * stratVars.curMax(ixStrat);
          }
          bals.sendHist(blev, aPre);
          //    hist.add(new History(aPre, 7, name + "r balance", r.balance));
          //   hist.add(new History(aPre, 7, name + " s balance", s.balance));

          // ------- set reserves --------------
          // set reserve to the enter trade value, before recalc costs
          // set large values higher and small least reserve
          if (true) {
            histTitles("setReserves " + name);
            balances.checkBalances(ar);
            ARow rcRow = bals.getRow(BALANCESIX + RCIX);
            ARow sgRow = bals.getRow(BALANCESIX + SGIX);
            ARow rcOld = rcRow;
            ARow sgOld = sgRow;
            double rcAve = rcRow.ave();
            double sgAve = sgRow.ave();
            ARow cOld = bals.getRow(BALANCESIX + CIX);
            ARow gOld = bals.getRow(BALANCESIX + GIX);
            ARow rOld = bals.getRow(BALANCESIX + RIX);
            ARow sOld = bals.getRow(BALANCESIX + SIX);
            double cPre, gPre, rPre, sPre, sgPre, rcPre;

            // the frac values in EM represent the fracs to be reserved
            // after costs are calculated, they we be evaluated again
            double cFrac, cFrac1, gFrac, gFrac1, tifrac = eM.tradeReserveIncFrac[pors][clan];
            double rFrac = 1. - (cFrac = eM.startTradeCFrac[searchTrade][pors][clan]);
            double sFrac = 1. - (gFrac = eM.startTradeGFrac[searchTrade][pors][clan]);

            //     double cGoal = rcRow.sum() * eM.startTradeCFrac[0][pors][clan];
            //     double gGoal = sgRow.sum() * eM.startTradeGFrac[0][pors][clan];
            int maxN = E.LSECS;
            double nFrac = 1. / (6 + maxN); // 1/13 =.076923
            double dif;
            inOffer.setC(c.balance); // check cargo again
            for (int ix = 0; ix < E.LSECS; ix++) {
              int nn = rcRow.maxIx(ix);//rc MaxIx(0,1,2,3,4,5,6)
              rcPre = rcOld.get(nn);
              sgPre = sgOld.get(nn);
              cPre = cOld.get(nn);
              rPre = rOld.get(nn);
              gPre = gOld.get(nn);
              sPre = sOld.get(nn);
              tifrac = eM.tradeReserveIncFrac[pors][clan];

              // calculate the desired units in r less for larger rc
              //                              .6    + .14  * (7-0 - 1=6)=.86
              //   E.sysmsg("initTrade start loop1 %s%d=%7.2f, %s%d=%7.2f,%s%d=%7.2f,%s%d=%7.2f,rcsg=%b,%b,%b,%b",r.aschar,ix,r.balance.get(ix),c.aschar,ix,c.balance.get(ix),s.aschar,ix,s.balance.get(ix),g.aschar,ix,g.balance.get(ix),bals.A[2] == r.balance,bals.A[3] == c.balance,bals.A[4] == s.balance, bals.A[5] == g.balance);
              // (1-.076923 *ix) (ix=0,1,2,3...),
              // cFrac1 = cFra*1,.92,.84.... cVal decrement by ix
              double cVal = rcPre * (cFrac1 = cFrac * (1. - nFrac * ix));
              double gVal = sgPre * (gFrac1 = gFrac * (1. - nFrac * ix));
              // cval = desired value of c
              // calculate move for the desired units in r
              mov = cPre - cVal;
              double wReservFrac = 1.0 - cFrac;
              double cReservFrac = cFrac;
              double rdif = 0., sdif = 0.;
              if (E.debugResumP) {
                if ((rdif = (dif = rcPre - rPre - cPre) / rcPre) > E.PZERO || rdif < E.NZERO) {
                  EM.doMyErr(String.format("in initTrade resum Failure dif=%E,rcPre=%7.3f != rPre = %7.3f - cPre=%7.3f = %7.3f ix=%2d, nn=%2d", dif, rcPre, rPre, cPre, rPre + cPre, ix, nn));
                }
                if ((sdif = (dif = sgPre - sPre - gPre) / sgPre) > E.PZERO || sdif < E.NZERO) {
                  EM.doMyErr(String.format("in initTrade resum Failure dif=%E  sgPre=%7.3f - sPre= %7.3f - gPre=%7.3f = %7.3f, ix=%2d,  nn=%2d", dif, sgPre, sPre, gPre, sPre + gPre, ix, nn));
                }
              }
              if (mov < E.NNZERO) { // c too small,  move some r to c
                mov = Math.min(-mov, Math.min(rcPre * tifrac, Math.min(rcPre * wReservFrac, rPre)));
                hist.add(new History(aPre, 7, " r->c" + nn + "=" + EM.mf(mov), "preC=" + EM.mf(cPre), "=>c " + EM.mf(cPre + mov), "preR=" + EM.mf(rPre), "=>R " + EM.mf(rPre - mov), "cFrac=" + EM.mf(cFrac), "cFr1=" + EM.mf(cFrac1)));
                EM.addlErr = "r->c" + nn + "=" + EM.mf(mov) + ", preC=" + EM.mf(cPre) + "->c " + EM.mf(cPre + mov) + ", preR=" + EM.mf(rPre) + ", ->R " + EM.mf(rPre - mov);
                r.putValue(balances, mov, nn, nn, c, 0, .0001);
                inOffer.setC(c.balance);
              }
              else if (mov > E.PZERO) {  // c too large put some to r
                mov = Math.min(mov, cPre);
                hist.add(new History(aPre, 7, " c=>r" + nn + "=" + EM.mf(mov), "preC=" + EM.mf(cPre), "=>C " + EM.mf(cPre + mov), "preR" + EM.mf(rPre), "=>R " + EM.mf(rPre - mov), "cVal=" + EM.mf(cVal), "cFrac=" + EM.mf(cFrac), "rFr1=" + EM.mf(cFrac1)));
                EM.addlErr = "c->r" + nn + "=" + EM.mf(mov) + ", preC=" + EM.mf(cPre) + "->c " + EM.mf(cPre + mov) + ", preR=" + EM.mf(rPre) + ", ->R " + EM.mf(rPre - mov);
                c.putValue(balances, mov, nn, nn, r, 0, .0001);
                inOffer.setC(c.balance);
              }

              // initTrade now process the g moves
              mov = gPre - gVal;
              wReservFrac = 1. - gFrac;
              double gReservFrac = gFrac;
              if (mov < E.NNZERO) {  // g too small SKIP move  s=>g
                mov = Math.min(-mov, Math.min(sgPre * tifrac, Math.min(sgPre * wReservFrac, sPre)));
                hist.add(new History(aPre, 7, "s=>g" + nn + "=" + EM.mf(mov), "preG=" + EM.mf(gPre), "=>C " + EM.mf(gPre + mov), "preS=" + EM.mf(sPre), "=>S " + EM.mf(sPre - mov)));
                EM.addlErr = "s->g" + nn + "=" + EM.mf(mov) + ", m2=" + EM.mf(mov2) + ", m1=" + EM.mf(mov1) + ", preG=" + EM.mf(gPre) + "->g " + EM.mf(gPre + mov) + ", preS=" + EM.mf(sPre) + ", ->S " + EM.mf(sPre - mov);
                s.putValue(balances, mov, nn, nn, g, 0, wReservFrac);
                inOffer.setG(g.balance, as.getGuestGrades());

              }
              else if (mov > E.PZERO) {  //g too large move g=>s
                mov = Math.min(mov, gPre);
                hist.add(new History(aPre, 7, "g=>s" + nn + "=" + EM.mf(mov), "preG=" + EM.mf(gPre), "=>G " + EM.mf(gPre - mov), "preS=" + EM.mf(sPre), "=>S " + EM.mf(sPre + mov)));
                EM.addlErr = "g->s" + nn + "=" + EM.mf(mov) + ", preG=" + EM.mf(gPre) + "->g " + EM.mf(gPre - mov) + ", preS=" + EM.mf(sPre) + ", ->S " + EM.mf(sPre + mov);
                g.putValue(balances, mov, nn, nn, s, 0, .0001);
                inOffer.setG(g.balance, as.getGuestGrades());
              }
              //       E.sysmsg("initTrade  end first loop %s%d=%7.2f, %s%d=%7.2f,%s%d=%7.2f,%s%d=%7.2f,rcsg=%b,%b,%b,%b",r.aschar,ix,r.balance.get(ix),c.aschar,ix,c.balance.get(ix),s.aschar,ix,s.balance.get(ix),g.aschar,ix,g.balance.get(ix),bals.A[2] == r.balance,bals.A[3] == c.balance,bals.A[4] == s.balance, bals.A[5] == g.balance);
            }
          } // end first set reserves before get travel cost

          EM.addlErr = ""; // wipe error info
          EM.wasHere = "in trade init & set Reserves, before calcCosts travel";
          aPre = "#b";
          bals.sendHist(hist, aPre);
          inOffer.setC(c.balance);
          //       hist.add(new History(aPre, 7, name + "r balance", r.balance));
//        hist.add(new History(aPre, 7, name + " s balance", s.balance));
          // prepare for trade
          //   CashFlow.AssetsFlow asf = new CashFlow.AssetsFlow(as);
          //      asf.init(as, as.cur);
          aPre = "#c";
          //     balances.sendHist4(hist, History.aux2Info, aPre, 7, "iniT r", "iniT c", "iniT S", "iniT G");
          // recalc costs with the new r s values
          n = 0;
          // only use the m + t costs here to represent the travel
          histTitles("calcTravel " + ec.name);
          yCalcCosts(aPre, lightYearsTraveled, eM.tradeHealth[pors][clan], eM.tradeGrowth[pors][clan]);
          if (!didInitRawProspects) {
            initRawProspects2 = rawProspects2.copy();
            didInitRawProspects = true;
          }
          // save for CashFlows.barter
          preTradeMtgAvails6 = mtgAvails6.copy(History.valuesMajor6, "preTMAvails6");
          preTradeBalances = balances.copy(History.valuesMajor6, "preTBalances");
          //save how bad the entry Prospects were: level of SOS
          if (!didCashFlowStart) {
            tradedFirstNegProspectsSum = rawProspects2.negSum();
          }
          didCashFlowStart = true;
          // record the start of next year
          if (as.endYearEnd) {
            syW = new DoTotalWorths();
          }

          // in Assets.CashFlow.Trades.initTrade
          // Save the maint & travel for when lightYearsTraveled was used in yCalcCosts
          if (pors == E.S && newTradeYear1) {
            tradeTravelCosts10 = travelCosts10.copy10();
            tradeTravelMaintCosts10 = maintCosts10.copy10();
            sumTradeTravelCosts = tradeTravelCosts10.curSum();
            sumTradeTravelMaintCosts = tradeTravelMaintCosts10.curSum();
            as.sumTrade1YearTravelMaintCosts = (sumTradeTravelCosts + sumTradeTravelMaintCosts) / lightYearsTraveled;
            newTradeYear1 = false;
            newTradeYear2 = true; // use Maint&travel in yrawCalcCosts
          }
          tradeStrategicVars = stratVars;
          preTradeAvail = -mtgNeeds6.curSum();
          preTradeSum4 = bals.sum4();
          hEmerg = rawProspects2.curMin() < .1;
          //   histStart = hist.size();  // for rehist to start here
          btW = new DoTotalWorths();
          btWTotWorth = btW.getTotWorth(); // Assets.CashFlow.Trades.initTrade
          aPre = "#d";
          //pbal.setTitle("preInitBal");
          pbal.sendHist(hist, aPre);
          bals.sendHist2(History.loopMinorConditionals5, aPre);
          // emergOfrs.titl = "emergOfrs";
          // availOfrs.titl = "availOfrs";
          // needReq.titl = "needReq";
          // fneedReq.titl = "fneedReq";

          inOffer.setC(c.balance);
          calcTrades();
          inOffer.setC(c.balance);

          int gix = stratVars.maxIx();
          ARow gradeCost4 = new ARow(ec).zero();
          ARow gmov = new ARow(ec).zero();
          aPre = "$b";
          if (History.dl > History.valuesMajor6) {
            balances.sendHist4(hist, History.aux2Info, aPre, 7, " r bal3", " c bal3", " s bal3", " g bal3");
          }
          if (History.dl > History.valuesMinor7 && E.debugLogsOut) {
            emergOfrs.sendHist(hist, aPre);
            availOfrs.sendHist(hist, aPre);
            needReq.sendHist(hist, aPre);
            fneedReq.sendHist(hist, aPre);
            maxReqs.sendHist(hist, aPre);
            stratVars.sendHist(hist, aPre);
          }
          // now set C and G to the emergOfrs amounts
          // the idea is to have a significant set of units to trade
          // loop financial sectors 0 - 6
          inOffer.setMyIx(ec);
          for (int ix = 0; ix < E.LSECS; ix++) {
            double cbal = balances.get(BALANCESIX + CIX, ix);
            double gbal = balances.get(BALANCESIX + GIX, ix);
            double rbal = balances.get(BALANCESIX + RIX, ix);
            double sbal = balances.get(BALANCESIX + SIX, ix);
            double tifrac = eM.tradeReserveIncFrac[pors][clan];
            double rcbal = balances.get(BALANCESIX + RCIX, ix);
            double sgbal = balances.get(BALANCESIX + SGIX, ix);
            // prevent subtracting more than cbal or gbal have
            double cet = emergOfrs.get(0, ix);
            double get = emergOfrs.get(1, ix);
            double cFrac = eM.startTradeCFrac[doingSearchOrTrade][pors][clan];//.5
            double gFrac = eM.startTradeGFrac[doingSearchOrTrade][pors][clan];
            // calc amount to decrease C, neg means increase
            double cdif = cet > PZERO ? cet - cbal : 0.;// add to c from r
            // limit by amount of r available after reserve is kept
            // 1.-reserve is the amount available
            cdif = Math.min(cdif, rcbal * (1. - eM.tradeReservFrac[pors]));// 1.-.15 = .85
            // the avail frac here is only .3 of the previous reserve
            double rReservFrac = (1. - cFrac) * .3;  //even smailer (.5)*.3
            double rAvail = rbal - rcbal * rReservFrac; // rb-rc*.15
            cdif = Math.min(cdif, rAvail);// muist leave rc*..15
            double gdif = get > PZERO ? get - gbal : 0; // g to get from s
            gdif = Math.min(gdif, sbal * (1. - eM.tradeReservFrac[pors]));
            double sReservFrac = (1. - gFrac) * .3;
            double sAvail = sbal - sgbal * sReservFrac;
            gdif = Math.min(gdif, sAvail);  // may be negative

            //  limit moves by Trade Reserve Increase Frac
            double maxRmov = rbal > rcbal * tifrac ? rcbal * tifrac : rbal;
            double maxSmov = sbal > sgbal * tifrac ? sgbal * tifrac : sbal;
            // move only remaining balance from resource to cargo
            cdif = Math.min(cdif, maxRmov);
            if (cdif > E.PZERO) {  // cargo is short, needs some from resource
              // limit by reserve against rc
              E.myTest(rbal < cdif, "emergOfrs err ix=%d rbal %5.2f < cdif %5.2f, emergOfrs %5.2f, cbal %5.2f, rcbal=%5.2f", ix, rbal, cdif, cet, cbal, rcbal);
              //       cMovedTrade.set(m, mov)
              r.putValue(balances, cdif, ix, ix, cargo, 0, 0.);
              E.myTest((rbal = bals.get(BALANCESIX + RIX, ix)) < NZERO, "ERROR: r=%4.2f less than zero", rbal);
              emergOfrs.set(0, ix, bals.get(BALANCESIX + CIX, ix)); // set to cargo bal
              hist.add(new History(aPre, 7, "r=>c" + ix + "=" + EM.mf(cdif), "r=" + EM.mf(rbal), "=>" + EM.mf(bals.get(2, ix)), "c=" + EM.mf(cbal), "=>" + EM.mf(bals.get(3, ix)), "cet=" + EM.mf(cet), "cdif=" + EM.mf(cdif)));
              inOffer.setC(c.balance);
              // else cargo is -cdif more than is available to trade
            }
            else if (cdif < NZERO && false) { // don't put r back
              cdif = Math.min(-cdif, cbal);
              c.putValue(balances, cdif, ix, ix, r, 0, .0000);
              E.myTest((rbal) < NZERO, "ERROR r=%4.2f less than zero", rbal);
              emergOfrs.set(0, ix, bals.get(BALANCESIX + CIX, ix)); // set to cargo bal
              hist.add(new History(aPre, 7, "c=>r" + ix + "=" + EM.mf(cdif), "c=" + EM.mf(cbal), "=>" + EM.mf(bals.get(3, ix)), "r=" + EM.mf(rbal), "=>" + EM.mf(bals.get(2, ix)), "cet=" + EM.mf(cet), "cdif=" + EM.mf(cdif)));
              E.sysmsg("initTrade loop2 end %s%d=%7.2f, %s%d=%7.2f,%s%d=%7.2f,%s%d=%7.2f,rcsg=%b,%b,%b,%b", r.aschar, ix, r.balance.get(ix), c.aschar, ix, c.balance.get(ix), s.aschar, ix, s.balance.get(ix), g.aschar, ix, g.balance.get(ix), bals.A[2] == r.balance, bals.A[3] == c.balance, bals.A[4] == s.balance, bals.A[5] == g.balance);
            }
            if (gdif > PZERO) {  // guests is short, needs some from resource
              gdif = Math.min(gdif, Math.min(sgbal * tifrac, sbal));
              emergOfrs.set(1, ix, bals.get(5, ix)); // set to guests bal
              E.myTest(sbal < gdif, "emerg Ofrs %5.2f err ix=%d, sbal %5.2f < gdif %5.2f gbal %5.2f, sgbal %5.2f", get, ix, sbal, gdif, gbal, sgbal);
              //       cMovedTrade.set(m, mov);
              // move balance from resource to cargo
              s.putValue(balances, gdif, ix, ix, g, 0, 0.);
              E.myTest((sbal = bals.get(BALANCESIX + SIX, ix)) < NZERO, "ERROR: sbal=%4.2f less than zero", sbal);
              hist.add(new History(aPre, 7, "s=>g" + ix + "=" + EM.mf(gdif), "s=" + EM.mf(sbal), "=>" + EM.mf(bals.get(4, ix)), "g=" + EM.mf(gbal), "=>" + EM.mf(bals.get(5, ix)), "get=" + EM.mf(get), "gdif=" + EM.mf(gdif), "<<<<<<<<<<<<<<<<"));
              // else cargo is -cdif more than is available to trade
            }
            else if (gdif < NZERO && false) { // nothing back to s
              gdif = Math.min(-gdif, gbal);;
              g.putValue(balances, gdif, ix, ix, s, 0, .000);
              E.myTest((sbal = bals.get(4, ix)) < NZERO, "sbal=%7.2f less than zero", sbal);
              hist.add(new History(aPre, 7, "s!=>g" + ix, "s=" + EM.mf(sbal), "g=" + EM.mf(gbal), "=>" + EM.mf(g.balance.get(ix)), "get=" + EM.mf(get), "gdif=" + EM.mf(gdif), "****"));
            }

            // now revise the trades to abide by the cbal and gbal
            emergOfrs.set(0, ix, Math.min(emergOfrs.get(0, ix), cargo.balance.get(ix)));
            availOfrs.set(0, ix, Math.min(availOfrs.get(0, ix), cargo.balance.get(ix)));
            emergOfrs.set(1, ix, Math.min(emergOfrs.get(1, ix), guests.balance.get(ix)));
            availOfrs.set(1, ix, Math.min(availOfrs.get(1, ix), guests.balance.get(n)));
          }

          aPre = "T#";
          if (History.dl > 5) {
            hist.add(h1 = new History(aPre, 5, name + " initTrade R", resource.balance));
            hist.add(h2 = new History(aPre, 5, name + " initTrade S", staff.balance));
            hist.add(h3 = new History(aPre, 5, name + " initTrade C", c.balance));
            hist.add(h4 = new History(aPre, 5, name + " initTrade G", g.balance));
            emergOfrs.sendHist(hist, aPre);
            availOfrs.sendHist(hist, aPre);
          }
          myIx = inOffer.setMyIx(ec); // set myIx in Offer
          oIx = (myIx + 1) % 2;

          g.checkSumGrades();
          A2Row cg = new A2Row(c.balance, g.balance);

          inOffer.setC(c.balance);
          inOffer.setG(g.balance, g.grades);
          double pz = PZERO;
          double rNeed = PZERO;
          double aOffer = PZERO;
          double fNeed = PZERO;
          double need = fNeed > rNeed ? fNeed : rNeed;
          double eOffer = PZERO;
          double offer = aOffer > PZERO ? aOffer : 0.;
          int sv = 0;
          int ifSearch = yphase == yrphase.SEARCH ? 0 : 1; // search or barter
          double criticalNumber = (int) eM.criticalNumbers[ifSearch][pors][clan];
          bids = makeZero(bids, "bids");
          // make a copy of bids which is not used
          for (m = 0; m < E.L2SECS; m++) {
            // go from the most needed to least needed
            int ix = stratVars.maxIx(m);//highest sv need, lowest bal
            rNeed = needReq.get(ix);
            aOffer = availOfrs.get(ix);
            fNeed = fneedReq.get(ix);
            need = fNeed > rNeed ? fNeed : rNeed;
            eOffer = emergOfrs.get(ix);  // used only in barter
            offer = aOffer > E.PZERO ? aOffer : 0.;
            // sv <  6 least strategic value, so most to give away, but fneed>0, so need not offer
            // if hot need and aOffer (avail) > 0, than save as an offer but not tEmerg offer
            bids.set(ix, m < criticalNumber ? need > E.PZERO ? -need : offer : offer > E.PZERO ? offer : need > E.PZERO ? -need : 0.);
          }
          tradeGoodsNeeds = bids.copy();
          //  initGoods = E.copy(bids);
          myxitGoods = bids.copy();
          myxit1Goods = myxit2Goods = myxit3Goods = myxitGoods;
          hist.add(new History("ti", History.valuesMinor7, name + "initTrade" + ">>>>>>>>>>", "myIx" + inOffer.myIx, "c " + (inOffer.cargos[inOffer.myIx] == c.balance ? "c == cargos" : " c not cargos"), "<<<<<<<<<<<<<"));
          //       inOffer.resetIx();
          listBids(aPre, 3);
        }
        catch (Exception | Error ex) {
          eM.firstStack = eM.secondStack + "";
          ex.printStackTrace(eM.pw);
          ex.printStackTrace(System.err);
          eM.secondStack = eM.sw.toString();
          System.out.flush();
          System.err.flush();
          EM.newError = true;
          EM.tError = ("----ATI3----ERROR initTrade Caught " + ex.toString() + ", cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName());
          System.err.println(EM.tError + EM.andMore());
          //     ex.printStackTrace(System.err);
          st.setFatalError();
          // throw new WasStopped(eM.tError);
        }
      }  // Assets.CashFlow.Trades.initTrade

      /**
       * exit Trade and move cargo and guests back to resource and staff however
       * if guests were redistributed for trade, put them back where they were
       * originally needed. term= 0 mytrade,-1 my reject,-2 other traded,-3o
       * reject
       */
      void xitTrade() {// Assets.CashFlow.Trades

        A2Row cg = new A2Row(c.balance, g.balance);
        A2Row cg2 = new A2Row(ec).set(cg);
        // following stats

        i = 0;

        gGrades = g.copyGrades(g.grades);

        //    ARow ic = initGoods.getARow(0);
        //     ARow ig = initGoods.getARow(E.lsecs);
        //   hist.add(h1 = new History(aPre, 5, name + " xit0Good C", ic));
        //    hist.add(h2 = new History(aPre, 5, name + " xit0Good g", ig));
        //    hist.add(h3 = new History(aPre, 5, name + " xit0T C", pc));
        //  hist.add(h4 = new History(aPre, 5, name + " xit0T G", pg));
        //  hist.add(h5 = new History(aPre, 5, name + " xit1 R", resource.balance));
        //  hist.add(h6 = new History(aPre, 5, name + " xit1 S", staff.balance));
        //  hist.add(h7 = new History(aPre, 5, name + " xit1 C", c.balance));
        //    hist.add(h8 = new History(aPre, 5, name + " xit1 G", g.balance));
        if (myOffer.getTerm() == -1) {
          cash = pCash;  // no Trade done
        }
        yCalcCosts(aPre, lightYearsTraveled, eM.tradeHealth[pors][clan], eM.tradeGrowth[pors][clan]);
        postTradeAvail = -mtgNeeds6.curSum();
        postTradeSum4 = bals.sum4();
        hist.add(h1 = new History(aPre, 5, name + " xitf R", resource.balance));
        hist.add(h2 = new History(aPre, 5, name + " xitf S", staff.balance));
        hist.add(h3 = new History(aPre, 5, name + " xitf C", c.balance));
        hist.add(h4 = new History(aPre, 5, name + " xitf G", g.balance));
        if (pors == E.s) {
          //   myOffer.clean();
          //    ec.buildPlanetOffers(myOffer.cn[0].myPlanetOffers, myOffer);
        }

      }// Assets.CashFlow.Trades.xitTrade

      /**
       * exit Barter, do clean up, in particular if ohist is set copy my history
       * to other history
       */
      void xitBarter() { // Assets.CashFlow.Trades
        // test whether to copy the last section of hist to ohist
        if (ohist != null && histStart > 100 && false) {
          int endHist = hist.size();
          History hh;
          for (int m = histStart; m < endHist; m++) {
            hh = hist.get(m);
            if (hh != null) {
              ec.addOHist(ohist, hh);
            }
          }
        }
        histStart = -100;
      } // Assets.CashFlow.Trades.xitBarter

      /**
       * Consider the new offer, and make a return offer
       *
       * for my unfavored clans, the strategicGoal increases for their unfavor
       * myclan the strategicGoal increases
       *
       * First convert of their offer to my offer. Their requests become my
       * offers (positive values of cargo or guests) Ensure that my offers do
       * not exceed values of maxTrades, or at most emergOfrs if reasserting a
       * high priority request, reduce my offers to 0 or the needed ..Trades.
       *
       * Their offers become my requests, these may become less than maxTrades
       *
       * then process requests/(offers + manualsValue) = strategicValue if
       * strategicValue .le strategicGoal
       *
       * strategicGoal = tradeFrac[pors][clan] *
       * ((tm1=cRand(15,clanMult[pors][clan])< 1./randMax?1./randMax:tm1>randMax:randMax:tm1)
       * *(1. - ((3. - fav[myclan][pors][oClan])/3.)
       * *((3.-oclanFavMult[myclan]*fav[oClan][pors][myclan])/3.)
       * *((fav[myclan][pors][pclan]>3. && fav[oClan][pors][myclan] .gt
       * 3.?sosFrac:1.)) *(1 * - barterTimes*barterMult)
       *
       * Each offer is processed by its strategic value and several rules
       *
       * 18) Initial offer: by ship, request up to 5 highest strategic requests
       * (lowest tradeMax values), requests are set to tradeMax.
       *
       * Offer everything that has a positive tradeMax value regardless of
       * stategicValue ratio
       *
       *
       * 17) Planet make request and offers equivalent to the ship method,
       * ignoring the offer from the ship.
       *
       * 16) Ship: Start processing using the strategicValue of the
       * request/offer. Attempt the reassert the top 4 of the initial ship
       * requests removed by planet. request only the 4 top request with values
       * no higher than previous values, only request 5 if it is offered. Repeat
       * ship offers up to ratio. Offers are created starting from lowest
       * strategic value, offering the positive tradeMax amount. Offer only
       * enough to equal the strategic value of the requests including requested
       * cash, divided by this turns strategicRatio minus the value of manuals
       * earned in the trade. If health is &lt 40% an include available cash in
       * the offer.<br>
       * 15) check for accept, keep track of raises rejected, either reduce
       * later requests, increase offers but not above rejection, or offer cash
       * if urgent and cash available. Planet request only the 4 top requests,
       * 5'th only if offered, repeat planet offers<br>
       * 14) Ship request only 2 top requests no higher than previous requests 3
       * - 5 only if offered, raise only those a previous raise was not refused,
       * and only the amount offered, repeat ship offers to offer fraction.<br>
       * 13) planet request only 2 top requests no higher than previous
       * requests. 3 - 5 only if offered and amount of offer. repeat planet
       * offer to offer faction<br>
       * 12) ship request 1 top request no higher than previous, 2 -5 only to
       * amount of planet offer, repeat ship offers to offer fraction<br>
       * 11) planet request top 1 request no higher than previous, 2 - 5 only to
       * amount of ship offer, repeat planet offers to offer fraction<br>
       * 10,9,8,7,6,5,4,3,2 ship planet alternate, only trim what is offered to
       * offer fraction<br>
       * 1) final offer, no change allowed, either ship or planet may jump to
       * this value. May jumped to at any offer after 15<br>
       * 0) offer accepted as offered. may be issued at any offer after 15.
       * <br>
       * -1) offer rejected as offered, may be issued at any offer after 15.
       *
       * @param otherOffer
       *
       * @return
       */
      Offer barter(Offer prevOffer
      ) { // Assets.CashFlow.Trades.barter
        if (histStart < 100) {
          // initialize for xitBarter copy of hist to ohist
          histStart = hist.size();
        }
        hist.add(new History("**", 5, name + " ntr tBartr t=" + prevOffer.getTerm(), "before", "any test"));
        changes = 0; // restart count each barter
        newBarter = true;
        myOffer = prevOffer; // add myOffer as xit reference of prevOffer
        prevTerm = term = prevOffer.getTerm();
        ohist = prevOffer.getOHist();
        oname = prevOffer.getOName();
        oClan = prevOffer.getOClan();
        myIx = prevOffer.setMyIx(ec);;
        myIx = prevOffer.getMyIx();
        aPre0 = "#A";
        aPre = "#B";
        aPre2 = "#C";
        aPre3 = "#D";
        ec.blev = History.dl;
        ec.lev = mRes;
        fav = eM.fav[clan][pors][oClan];
        oFav = eM.fav[oClan][pors][clan];
        xof1 = excessOffers;
        if (term == EM.barterStart) {
          maxReqs.zero();
        }
        //    E.sysmsg(name + "Enter Trades.barter term=" + term);
        hist.add(new History(aPre0, mRes, name + "ntr barter" + ">>>>>>>>>>", "myIx" + prevOffer.myIx, "c" + (prevOffer.cargos[prevOffer.myIx] == c.balance ? "c == cargos" : " c != cargos"), "<<<<<<<<<<<<<"));
        if (E.debugAssetsStats) {
          if (((EM.barterStart - term) % 6) == 0) {
            st.paintCurDisplay(eM.curEcon);
          }
        }
        // ===================== t18 =======================================
        if (term >= EM.barterStart) {  // go to the next section
          //  tradedFirstStrategicReceipts = totalStrategicRequests;
          // tradedFirstReceipts = totalRequests;
          //tradedFirstSends = totalOffers;
          // listneedReq(mRes, aPre0);
          //  listavailOfrs(mRes, aPre0);
          // listemergOfrs(mRes, aPre0);
          // listGoods(mRes, aPre0);

          if (History.dl > History.valuesMinor7) {
            emergOfrs.sendHist(hist, aPre);
            availOfrs.sendHist(hist, aPre);
            needReq.sendHist(hist, aPre);
            fneedReq.sendHist(hist, aPre);
            stratVars.sendHist(hist, aPre);
          }

          //   listDifBid(History.valuesMajor6, "xit", oprevGoods);
          enforceStrategicGoal();
          if (EM.dfe()) {
            return prevOffer;
          }
          assert sumCriticalStrategicRequestsFirst > 0.0 : "error zero term=" + term + ",  sumCriticalStrategicRequestsFirst=" + EM.mf(sumCriticalStrategicRequestsFirst) + ", sumCriticalStrategicRequests=" + EM.mf(sumCriticalStrategicRequests) + ", sumBidRequests=" + EM.mf(sumBidRequests) + ", goodC.plusSum=" + EM.mf(goodC.plusSum()) + ", goodC.negSum=" + EM.mf(goodC.negSum());
          assert sumCriticalBidRequestsFirst > 0.0 : " error zero: sumCriticalBidRequestsFirst=" + EM.mf(sumCriticalBidRequestsFirst);
          myOffer.set2Values(bids, offers, requests, totalSend, totalReceipts, strategicGoal, strategicValue); // save for selectPlanet
          //   myOffer.set2InitialPlanetGoods(bids); // save for selectplanet
          hist.add(new History(aPre, lRes, "T" + term + " " + name + " vals" + changes, "sv=" + EM.mf(sv1), "->" + EM.mf(sv), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), "ofr=" + EM.mf(offers), EM.mf(bids.curPlusSum()), "rqst=" + EM.mf(requests), EM.mf(bids.curNegSum()), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " vals" + changes, "sv=" + EM.mf(sv1), "->" + EM.mf(sv), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), "ofr=" + EM.mf(offers), EM.mf(bids.curPlusSum()), "rqst=" + EM.mf(requests), EM.mf(bids.curNegSum()), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));
          String aHist = "Trade T" + term + " sv" + EM.mf(sv) + " sf" + EM.mf(sf) + " rGoal0=" + EM.mf(rGoal0) + tradeHist;
          if (E.debugDisplayTrade) {
            System.out.println(aHist);
          }
          tradeHist = " tHist ";
          term--;
          myOffer.setTerm(term);
          if (E.debugDisplayTrade) {
            System.out.println("Trade.barter " + name + " t=" + prevTerm + "=>" + term
                               + " changes" + changes + " trdVals=" + EM.mf(sv1) + "->" + EM.mf(sv) + " goals=" + EM.mf(sf1) + "=>" + EM.mf(sf) + " offrs=" + EM.mf(offers) + ">" + EM.mf(bids.curPlusSum()) + " rqst=" + EM.mf(requests) + "=>" + EM.mf(bids.curNegSum()) + "xof" + EM.mf(xof) + "=>" + EM.mf(excessOffers) + " xcess/of=" + EM.mf(excessOffers / offers)
            );
          }
          xitBarter();
          myxit3Goods = myxit2Goods = myxit1Goods = myxitGoods = bids.copy();
          //        E.sysmsg(" xit Trades.barter term=" + myOffer.getTerm());
          // listCG(balances, 4, "bsx", myOffer);
          return myOffer;
// Assets.CashFlow.Trades.barter
        } // ====================P18,S17,P16,S15,P14 =================================
        else if (term > EM.barterStart - 5) { // p18 s17 p16 s15 p14 ship > 13
          int myIx1 = prevOffer.myIx;
          ARow cc1 = prevOffer.cargos[0];

          bids = prevOffer.getGoods();  // S17 get P18 bids

          bids.sendHistt("ntr bid c", "ntr bid g");
          hist.add(new History(aPre, mRes, name + "ntr barter" + ">>>>>>>>>>", "myIx" + prevOffer.myIx, "c" + (prevOffer.cargos[prevOffer.myIx] == c.balance ? "c == cargos" : " c != cargos"), "<<<<<<<<<<<<<"));
          myOffer = prevOffer.flipOffer(ec); // set up for our process
          if (term > EM.barterStart - 3 && term < EM.barterStart) { // s17 P16
            oFirstBid = bids.copy(); // save xit p18 s17
          }
          int myIx = myOffer.myIx;
          ARow ccc = myOffer.cargos[myIx];
          hist.add(new History(aPre, mRes, name + "ntr barter" + term + ">>>>>>>>>>", "myIx=" + myOffer.myIx, "c " + (myOffer.cargos[myOffer.myIx] == c.balance ? "==cargos" : "!=cargos"), (ccc == c.balance ? "ccc == c.balance" : "ccc != c.balance"), "<<<<<<<<<<<<<"));
          // E.myTest(myOffer.cargos[myIx] != cargo.balance, "ccc != cargo.balance myIx=" + myIx);
          //        bids = myOffer.getGoods();
          if (History.dl > History.valuesMinor7 && E.debugLogsOut) {
            //bids.sendHistt("flpd bid c", "flpd bid g");
            bids.sendHistcg();
            emergOfrs.sendHistcg();
            availOfrs.sendHistcg();
            needReq.sendHistcg();
            fneedReq.sendHistcg();
            stratVars.sendHistcg();
          }
          xof2 = excessOffers;
          // calculate the next barter
          //if(term < EM.barterStart)enforceStrategicGoal(); // sf1, sv1, sf,sv,excessOffers
          enforceStrategicGoal(); // sf1, sv1, sf,sv,excessOffers
          if (EM.dfe()) {
            return myOffer;
          }
          myOffer.set2Values(bids, offers, requests, totalSend, totalReceipts, strategicGoal, strategicValue); // save for selectPlanet
          hist.add(new History(aPre, lRes, "T" + term + " " + name + " CONT" + changes, "sv=" + EM.mf(sv1), "->" + EM.mf(sv), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), "ofr=" + EM.mf(offers), EM.mf(bids.curPlusSum()), "rqst=" + EM.mf(requests), EM.mf(bids.curNegSum()), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, 3, "T" + term + " " + name + " CONT" + changes, "sv=" + EM.mf(sv1), "->" + EM.mf(sv), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), "ofr=" + EM.mf(offers), EM.mf(bids.curPlusSum()), "rqst=" + EM.mf(requests), EM.mf(bids.curNegSum()), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));

          myxit3Goods = myxit2Goods = myxit1Goods = myxitGoods = bids.copy();
          //     listDifBid(History.valuesMajor6, "xit17", oprevGoods);

          String aHist = "---TRD3---Trade T" + term + " sv" + EM.mf(sv) + " sf" + EM.mf(sf) + " rGoal0=" + EM.mf(rGoal0) + tradeHist;
          if (E.tradeDebugTerms) {
            System.out.println(aHist);
          }
          tradeHist = " tHist ";
          term--;
          myOffer.setTerm(term);
          if (E.debugDisplayTrade) {
            System.out.println("---TRD4---Trd.btr " + name + " t=" + prevTerm + "=>" + term
                               + " changes" + changes + " trdVals=" + EM.mf(sv1) + "=>" + EM.mf(sv) + " goals=" + EM.mf(sf1) + "=>" + EM.mf(sf) + " offrs=" + EM.mf(offers) + "=>" + EM.mf(bids.curPlusSum()) + " rqst=" + EM.mf(requests) + "=>" + EM.mf(bids.curNegSum()) + "excessOfrs" + EM.mf(xof2) + "=>" + EM.mf(excessOffers) + " xcess/of=" + EM.mf(excessOffers / offers)
            );
          }
          //       E.sysmsg(" xit Trades.barter term=" + myOffer.getTerm());
          xitBarter(); // list messages to other
          //      listCG(balances, 4, "bsx", myOffer);
          return myOffer;

          // Assets.CashFlow.Trades.barter
        } //============================== 1 ==================================
        else if (term == 1) { // just go to the next ones
          bids = prevOffer.getGoods();
          listGoods(mRes, "ba");
          hist.add(new History(aPre, mRes, name + "ntr barter" + ">>>>>>>>>>", "myIx" + prevOffer.myIx, "c" + (prevOffer.cargos[prevOffer.myIx] == c.balance ? "c == cargos" : " c not cargos"), "<<<<<<<<<<<<<"));
          myOffer = prevOffer.flipOffer(ec); // set up for our process

          int myIx = myOffer.myIx;
          ARow ccc = myOffer.cargos[myIx];
          hist.add(new History(aPre, mRes, name + "ntr barter" + term + ">>>>>>>>>>", "myIx=" + myOffer.myIx, "c" + (myOffer.cargos[myOffer.myIx] == c.balance ? "c == cargos" : " c != cargos"), (ccc == c.balance ? "ccc == c.balance" : "ccc != c.balance"), "<<<<<<<<<<<<<"));
          //     E.myTest(ccc != cargo.balance, "ccc != cargo.balance myIx=" + myIx);
          bids = myOffer.getGoods();  // get the reference
          oprevGoods = bids.copy();
          // possibly reduce offers to fit requests
          enforceStrategicGoal(); // sf1,sv1,sf,sv,excessOffers
          myOffer.set2Values(bids, offers, requests, totalSend, totalReceipts, strategicGoal, strategicValue); // save for selectPlanet
          aPre = "#y";
          listGoods(mRes, aPre);
          hist.add(new History(aPre, lRes, "done=" + term + " " + name, "barter", "ended", "barter", "ended", "<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, lRes, "done=" + term + " " + name, "barter", "ended", "barter", "ended", "<<<<<<<"));
          // if I had to make changes, I do not accept final offer
          if (testNoTrade(myOffer) || myOffer.getTerm() < 1) {  // Assets.CashFlow.Trades.barter
            tradeRejected = true;
            listDifBid(History.valuesMajor6, "rej" + term, oprevGoods);
            term = -1; //rejected
            myOffer.setTerm(term);
            if (E.debugDisplayTrade) {
              System.out.println("----TRDR1----Trade.barter rejected " + name + " t=" + prevTerm + "=>" + term
                                 + " changes" + changes + " trdVals=" + EM.mf(sv1) + "->" + EM.mf(sv) + " goals=" + EM.mf(sf1) + "->" + EM.mf(sf) + " offrs=" + EM.mf(offers) + EM.mf(bids.curPlusSum()) + " rqst=" + EM.mf(requests) + " negSum" + EM.mf(bids.curNegSum()) + "excessOfrs" + EM.mf(excessOffers) + " xcess/of=" + EM.mf(excessOffers / offers)
              );
            }
            listGoods(5, aPre);
            // strategicValue = calcStrategicSums();
            // strategicGoal = calcStrategicGoal();
            lRes = History.loopIncrements3; // leave a loop result

            hist.add(new History(aPre, lRes, "T" + term + " " + name + " REJ1" + changes, "sv=" + EM.mf(sv1), "->" + EM.mf(sv), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), "ofr=" + EM.mf(offers), EM.mf(bids.curPlusSum()), "rqst=" + EM.mf(requests), EM.mf(bids.curNegSum()), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));
            ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " REJO" + changes, "sv=" + EM.mf(sv1), "->" + EM.mf(sv), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), "ofr=" + EM.mf(offers), EM.mf(bids.curPlusSum()), "rqst=" + EM.mf(requests), EM.mf(bids.curNegSum()), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));

            xitBarter();
            //         listCG(balances, 4, "bsx", myOffer);
            return myOffer;
          }
          // if trade was not rejected than it was accepted
          myOffer.setTerm(term = 0);  //flag accept trade
          //    E.sysmsg(" Trades.barter accepted");
          lRes = History.loopIncrements3; // leave a loop result
          listGoods(lRes, "G+");
          //    listCG(balances, 4, "byes", myOffer);
          // stats are already saved in cur = Assets.CashFlow by calcStrategicSums
          //         sendStats(tSend, tReceipts, tStratValue, tBid, (int) E.fav[clan][myOffer.getOClan()]);
          //     strategicValue = calcStrategicSums();
          //   strategicGoal = calcStrategicGoal();
          hist.add(new History(aPre, lRes, "T" + term + " " + name + " ACC1" + changes, "sv=" + EM.mf(sv1), "->" + EM.mf(sv), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), "ofr=" + EM.mf(offers), EM.mf(bids.curPlusSum()), "rqst=" + EM.mf(requests), EM.mf(bids.curNegSum()), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));

          ec.addOHist(ohist, new History(aPre, 3, "T" + term + " " + name + " ACC3" + changes, "sv=" + EM.mf(sv1), "->" + EM.mf(sv), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), "ofr=" + EM.mf(offers), EM.mf(bids.curPlusSum()), "rqst=" + EM.mf(requests), EM.mf(bids.curNegSum()), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));
          if (E.debugDisplayTrade) {
            eM.printHere("---TRDA---", ec, "Trade.barter accepted " + name + " t=" + prevTerm + "=>" + term
                                           + " changes" + changes + " trdVals=" + EM.mf(sv1) + "->" + EM.mf(sv) + " goals=" + EM.mf(sf1) + "->" + EM.mf(sf) + " offrs=" + EM.mf(offers) + EM.mf(bids.curPlusSum()) + " rqst=" + EM.mf(requests) + " negSum" + EM.mf(bids.curNegSum()) + "excessOfrs" + EM.mf(excessOffers) + " xcess/of=" + EM.mf(excessOffers / offers)
            );
          }
          myOffer.accepted(ec); // the bid becomes a move
          didTrade = true;
          rejectTrade = false;
          listDifBid(History.valuesMajor6, "#X" + term, oprevGoods);
          xitBarter();
          //    E.sysmsg(" xit Trades.barter accepted");
          //      listCG(balances, 4, "bsx", myOffer);
          return myOffer;

          // Assets.CashFlow.Trades.barter
        }
        else if (term < 1) {
          E.myTest(true, "Error: barter error term < 1");

        } //========================= S13 P12 -> P2 ===============================
        else if (term < EM.barterStart - 3) {  // Now the rest S15 P14 S13...0
          bids = prevOffer.getGoods();
          listGoods(mRes, "@a");
          hist.add(new History(aPre, mRes, name + "ntr barter" + ">>>>>>>>>>", "myIx" + prevOffer.myIx, "c" + (prevOffer.cargos[prevOffer.myIx] == c.balance ? "c == cargos" : " c not cargos"), "<<<<<<<<<<<<<"));
          myOffer = prevOffer.flipOffer(ec); // set up for our process

          //    oprev1Goods = oprevGoods;
          hist.add(new History(aPre, mRes, name + "flipped barter" + ">>>>>>>>>>", "myIx" + myOffer.myIx, "c" + (myOffer.cargos[myOffer.myIx] == c.balance ? "c == cargos" : " c not cargos"), "<<<<<<<<<<<<<"));
          int myIx = myOffer.myIx;
          ARow ccc = myOffer.cargos[myIx];
          //     E.myTest(ccc != cargo.balance, "ccc != cargo.balance myIx=" + myIx);
          //      bids = myOffer.getGoods();  // get the reference
          oprevGoods = bids.copy();
          //  sv1 = strategicValue = calcStrategicSums();
          //  sf1 = strategicGoal = calcStrategicGoal();
          if (History.dl > History.valuesMinor7 && E.debugLogsOut) {
            emergOfrs.sendHist(hist, aPre);
            availOfrs.sendHist(hist, aPre);
            needReq.sendHist(hist, aPre);
            fneedReq.sendHist(hist, aPre);
            stratVars.sendHist(hist, aPre);
          }

          enforceStrategicGoal(); // sf1,sv1,sf,sv,excessOffers
          myOffer.set2Values(bids, offers, requests, totalSend, totalReceipts, strategicGoal, strategicValue); // save for selectPlanet
          if (testNoTrade(myOffer)) { // Assets.CashFlow.Trades.barter
            //         tBid = bids.copy();
            //        tStratValue = sv1;
            //        tStratFrac = sf1;
            //        offeredTrade = eM.year;
            //tSend = totalSend;
            //        tReceipts = totalReceipts;
            //       tMoreManuals = myOffer.getMoreE.LSECSs();
            myOffer.setTerm(-1);  //rejected
            term = -1;
            listDifBid(History.valuesMajor6, "rej", oprevGoods);
            // myOffer.setTerm(-1);  //rejected
            // term = -1;
            didTrade = false;
            rejectTrade = true;
            listGoods(mRes, "B-");
            //     strategicValue = calcStrategicSums();
            //     strategicGoal = calcStrategicGoal();
            hist.add(new History(aPre, lRes, "T" + term + " " + name + " REJ chgs" + changes, "sv=" + EM.mf(sv1), "->" + EM.mf(sv), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), "ofr=" + EM.mf(offers), EM.mf(bids.curPlusSum()), "rqst=" + EM.mf(requests), EM.mf(bids.curNegSum()), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));
            ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " REJchgs" + changes, "sv=" + EM.mf(sv1), "->" + EM.mf(sv), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), "ofr=" + EM.mf(offers), EM.mf(bids.curPlusSum()), "rqst=" + EM.mf(requests), EM.mf(bids.curNegSum()), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));
            if (E.debugDisplayTrade) {
              eM.printHere("---TRDR---", ec, "Trade.barter rejected2 " + name + " t=" + prevTerm + "=>" + term
                                             + " changes" + changes + " trdVals=" + EM.mf(sv1) + "->" + EM.mf(sv) + " goals=" + EM.mf(sf1) + "->" + EM.mf(sf) + " offrs=" + EM.mf(offers) + EM.mf(bids.curPlusSum()) + " rqst=" + EM.mf(requests) + " negSum" + EM.mf(bids.curNegSum()) + "excessOfrs" + EM.mf(excessOffers) + " xcess/of=" + EM.mf(excessOffers / offers)
              );
            }
            xitBarter();
            //     listCG(balances, 4, "bsx", myOffer);
            return myOffer;
          }
          if (testTrade(myOffer)) {
            // already fixed  strategicValue = calcStrategicSums();
            //   strategicGoal = calcStrategicGoal();
            if (term == 0) {
              hist.add(new History(aPre, lRes, "T" + term + " " + name + " ACCc" + changes, "sv=" + EM.mf(sv1), "->" + EM.mf(sv), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), "ofr=" + EM.mf(offers), EM.mf(bids.curPlusSum()), "rqst=" + EM.mf(requests), EM.mf(bids.curNegSum()), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));
              ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " ACCc" + changes, "sv=" + EM.mf(sv1), "->" + EM.mf(sv), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), "ofr=" + EM.mf(offers), EM.mf(bids.curPlusSum()), "rqst=" + EM.mf(requests), EM.mf(bids.curNegSum()), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));
            }
            if (E.debugDisplayTrade) {
              eM.printHere("---TRDa2---", ec, "Trade.barter accepted2 " + name + " t=" + prevTerm + "=>" + term + " changes" + changes + " trdVals=" + EM.mf(sv1) + "->" + EM.mf(sv) + " goals=" + EM.mf(sf1) + "->" + EM.mf(sf) + " offrs=" + EM.mf(offers) + EM.mf(bids.curPlusSum()) + " rqst=" + EM.mf(requests) + " negSum" + EM.mf(bids.curNegSum()) + "excessOfrs" + EM.mf(excessOffers) + " xcess/of=" + EM.mf(excessOffers / offers)
              );
            }
            myOffer.accepted(ec); // the bid becomes a move
            didTrade = true;
            rejectTrade = false;
            listDifBid(History.valuesMajor6, "#X" + term, oprevGoods);
            xitBarter();
            //    E.sysmsg(" xit Trades.barter accepted");
            //      listCG(balances, 4, "bsx", myOffer);
            return myOffer;
          }
          // not rejected or accepted, term==1 case stops thing

          //     E.myTest(true,"Error: entered dead end of barter");
          // give a little extra offer initially
          //     strategicGoal = calcStrategicGoal(term) - .2;  // use next barter term
          //      calcStrategicSums();
          //      renewStrategicRequests(3);
          //     limitOffers();
          //        enforceStrategicGoal(); //sf1,sv1,sf,sv,excessOffers
          //    previously set  strategicValue = calcStrategicSums();
          //     strategicGoal = calcStrategicGoal();
          double ts2 = totalSend;
          double tr2 = totalReceipts;
          listGoods(mRes, "brtrx");
          hist.add(new History(aPre, lRes, "T" + term + " " + name + " CONTc" + changes, "sv=" + EM.mf(sv1), "->" + EM.mf(sv), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), "ofr=" + EM.mf(offers), EM.mf(bids.curPlusSum()), "rqst=" + EM.mf(requests), EM.mf(bids.curNegSum()), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " CONTc" + changes, "sv=" + EM.mf(sv1), "->" + EM.mf(sv), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), "ofr=" + EM.mf(offers), EM.mf(bids.curPlusSum()), "rqst=" + EM.mf(requests), EM.mf(bids.curNegSum()), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));
          // remember bids is a pointer to bids in the offer
          myOffer.setTerm(term - 1);
          term--;
          if (E.debugDisplayTrade) {
            eM.printHere("---TRDc---", ec, "Trade.barter continue " + name + " t=" + prevTerm + "=>" + term
                                           + " changes" + changes + " trdVals=" + EM.mf(sv1) + "->" + EM.mf(sv) + " goals=" + EM.mf(sf1) + "->" + EM.mf(sf) + " offrs=" + EM.mf(offers) + EM.mf(bids.curPlusSum()) + " rqst=" + EM.mf(requests) + " negSum" + EM.mf(bids.curNegSum()) + "excessOfrs" + EM.mf(excessOffers) + " xcess/of=" + EM.mf(excessOffers / offers)
            );
          }
          //         myxit3Goods = myxit2Goods;
          //         myxit2Goods = myxit1Goods;
          myxit1Goods = myxitGoods;
          myxitGoods = bids.copy();
          listDifBid(History.valuesMajor6, "xit" + term, oprevGoods);
          //    listCG(balances, 4, "bsx", myOffer);
          xitBarter();
          E.sysmsg("xit Trades.barter term=" + myOffer.getTerm());
          return myOffer;
        }

        /*---------------------------------------------not reach ========*/
// should not be reached
        E.myTest(true, "Error: entered dead end of barter");
        bids = prevOffer.getGoods();
        listGoods(mRes, "bstrtaa");
        hist.add(new History(aPre, mRes, name + "ntr barter" + ">>>>>>>>>>", "myIx" + prevOffer.myIx, "c" + (prevOffer.cargos[prevOffer.myIx] == c.balance ? "c == cargos" : " c not cargos"), "<<<<<<<<<<<<<"));
        myOffer = prevOffer.flipOffer(ec); // set up for our process

        hist.add(new History(aPre2, mRes, name + "ntr barter" + ">>>>>>>>>>", "myIx" + myOffer.myIx, "c" + (myOffer.cargos[myOffer.myIx] == c.balance ? "c == cargos" : " c not cargos"), "<<<<<<<<<<<<<"));
        int myIx = myOffer.myIx;
        ARow ccc = myOffer.cargos[myIx];
        E.myTest(ccc != cargo.balance, "ccc != cargo.balance myIx=" + myIx);
        bids = myOffer.getGoods();  // get the reference
        oprevGoods = bids.copy();
        sv1 = strategicValue = calcStrategicSums();
        sf1 = strategicGoal = calcStrategicGoal();
        ts1 = totalSend;
        tr1 = totalReceipts;
        listGoods(5, aPre2);

        // either do no, or set Term=1 or accept and Term=0
        if (testTrade(myOffer)) {
          //myOffer.setTerm(0);
          // term = 0;
          if (term == 1) {
            strategicValue = calcStrategicSums();
            strategicGoal = calcStrategicGoal();
            double ts2 = totalSend;
            double tr2 = totalReceipts;
            //   listGoods(3, "TT");

            hist.add(new History(aPre, lRes, "T" + term + " " + name + " CONTINUE", "sv=" + EM.mf(sv1), "->" + EM.mf(sv), EM.mf(strategicValue / isv1), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), EM.mf(sf / isf1), "ofr=" + EM.mf(offers), "rqst=" + EM.mf(requests), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));
            ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " CONTINUE", "sv=" + EM.mf(sv1), "->" + EM.mf(sv), EM.mf(strategicValue / isv1), "sf=" + EM.mf(sf1), "->" + EM.mf(sf), EM.mf(sf / isf1), "ofr=" + EM.mf(offers), "rqst=" + EM.mf(requests), "exOf" + EM.mf(excessOffers), "x/of" + EM.mf(excessOffers / offers), "<<<<<<<"));
            //   sendStats(tSend, tReceipts, tStratValue, tBid, (int) E.fav[clan][myOffer.getOClan()]);
            // myOffer.accepted(ec);
            // didTrade = true;
            // rejectTrade = false;
          }
          xitBarter();
          return myOffer;
        }
        if (testNoTrade(myOffer)) {
          myOffer.setTerm(-1);
          term = -1;
          strategicValue = calcStrategicSums();
          strategicGoal = calcStrategicGoal();
          double ts2 = totalSend;
          double tr2 = totalReceipts;
          listGoods(3, "xx");
          didTrade = false;
          rejectTrade = true;
          hist.add(new History(aPre, lRes, "T" + term + " " + name + " vals", "sv=" + EM.mf(sv1), "->" + EM.mf(strategicValue), EM.mf(strategicValue / isv1), "sf=" + EM.mf(sf1), "->" + EM.mf(strategicGoal), EM.mf(strategicGoal / isf1), "ofr=" + EM.mf(offers), "rqst=" + EM.mf(requests), "<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " vals", "sv=" + EM.mf(sv1), "->" + EM.mf(strategicValue), EM.mf(strategicValue / isv1), "sf=" + EM.mf(sf1), "->" + EM.mf(strategicGoal), EM.mf(strategicGoal / isf1), "ofr=" + EM.mf(offers), "rqst=" + EM.mf(requests), "<<<<<<<"));
          xitBarter();
          return myOffer;
        }

        // give a little extra offer initially
        //     strategicGoal = calcStrategicGoal(term) - .2;  // use next barter term
        enforceStrategicGoal();
        myOffer.setTerm(term - 1);
        double ts2 = totalSend;
        double tr2 = totalReceipts;
        listBids("x", History.loopIncrements3);
        hist.add(new History(aPre, lRes, "T" + term + " " + name + " vals", "sv=" + EM.mf(sv1), "->" + EM.mf(strategicValue), EM.mf(strategicValue / isv1), "sf=" + EM.mf(sf1), "->" + EM.mf(strategicGoal), EM.mf(strategicGoal / isf1), "ofr=" + EM.mf(offers), "rqst=" + EM.mf(requests), "<<<<<<<"));
        ec.addOHist(ohist, new History(aPre, lRes, "T" + term + " " + name + " vals", "sv=" + EM.mf(sv1), "->" + EM.mf(strategicValue), EM.mf(strategicValue / isv1), "sf=" + EM.mf(sf1), "->" + EM.mf(strategicGoal), EM.mf(strategicGoal / isf1), "ofr=" + EM.mf(offers), "rqst=" + EM.mf(requests), "<<<<<<<"));
        // remember bids is a pointer to bids in the offer
        myxit3Goods = myxit2Goods;
        myxit2Goods = myxit1Goods;
        myxit1Goods = myxitGoods;
        myxitGoods = bids.copy();
        //    E.sysmsg(" xit Trades.barter term=" + myOffer.getTerm());
        xitBarter();
        return myOffer;
      } // Assets.CashFlow.Trades.Barter

      // Assets.CashFlow.Trades.listG
      void listBids(String pre, int lev
      ) {
        hist.add(h1 = new History(pre, lev, term + " " + name + " bidC" + myOffer.getMyIx(), bids.getARow(0)));
        hist.add(h2 = new History(pre, lev, term + " " + name + " bidG" + myOffer.getPrevMyIx(), bids.getARow(E.lsecs)));
      }

      /**
       * list the values of c and g
       *
       * @param bals balances etc
       * @param pre hist prefix of listing
       * @param lev level at which to list
       * @param myOffer
       */
      void listCG(ABalRows bals, String pre, int lev,
                  Offer myOffer
      ) {
        hist.add(h1 = new History(pre, lev, term + " " + name + " balC.3 " + myOffer.getMyIx(), bals.getRow(3)));
        hist.add(h2 = new History(pre, lev, term + " " + name + " balG.5 " + myOffer.getPrevMyIx(), bals.getRow(5)));
        hist.add(h1 = new History(pre, lev, term + " " + name + " c.bal", c.balance));
        hist.add(h2 = new History(pre, lev, term + " " + name + " g.bal", g.balance));
        E.myTest(myOffer.getC() != c.balance, "myOffer cargo not c.balance");
        E.myTest(myOffer.getG() != guests.balance, "myOffer guest not g.balance");
        E.myTest(c.balance != EM.curEcon.as.cur.c.balance, "c.balance is bad");
      }

      void NolistStrategicValues(String pre, int lev) {
        //lNoistStrategicValues(lev, pre);
      }

      // Assets.CashFlow.Trades
      /**
       * list strategicValues
       *
       * @param lev level of listing
       * @param desc descriptor of listing
       */
      void NolistStrategicValues(int lev, String desc
      ) {
        //  A2Row bids = strategicValues;
//        hist.add(h1 = new History(desc, lev, term + " " + name + " straC", strategicValues.getARow(0)));
//        hist.add(h2 = new History(desc, lev, term + " " + name + " straG", strategicValues.getARow(E.lsecs)));
      }

      // Assets.CashFlow.Trades
      /**
       * list stratVars
       *
       * @param desc descriptor of listing
       * @param lev level of listing
       */
      void listStratVars(String desc, int lev
      ) {
        //  A2Row bids = stratVars;
        hist.add(h1 = new History(desc, lev, term + " " + name + " straC", stratVars.getARow(0)));
        hist.add(h2 = new History(desc, lev, term + " " + name + " straG", stratVars.getARow(E.lsecs)));
      }

      void listNeedreq(String pre, int lev) {
        listneedReq(lev, pre);
      }

      // Assets.CashFlow.Trades
      /**
       * list the need requirement
       *
       * @param lev level to list
       * @param desc descriptor
       */
      void listneedReq(int lev, String desc
      ) {
        //  A2Row bids = maxTrades;
        hist.add(h1 = new History(desc, lev, term + " " + name + " needC", needReq.getARow(0)));
        hist.add(h2 = new History(desc, lev, term + " " + name + " needG", needReq.getARow(E.lsecs)));
      }

      void listFneedReq(String pre, int lev) {
        listFneedReq(lev, pre);
      }

      // Assets.CashFlow.Trades
      /**
       * list the future need requirement
       *
       * @param lev level of listing
       * @param desc descriptor
       */
      void listFneedReq(int lev, String desc
      ) {
        //  A2Row bids = maxTrades;
        hist.add(h1 = new History(desc, lev, term + " " + name + " fneedC", fneedReq.getARow(0)));
        hist.add(h2 = new History(desc, lev, term + " " + name + " fneedG", fneedReq.getARow(E.lsecs)));
      }

      void listEmergOfrs(String pre, int lev) {
        listemergOfrs(lev, pre);
      }

      // Assets.CashFlow.Trades
      /**
       * list the emergency offor
       *
       * @param lev level of listing
       * @param desc descriptor
       */
      void listemergOfrs(int lev, String desc
      ) {

        hist.add(h1 = new History(desc, lev, term + " " + name + " emrgC", emergOfrs.getARow(0)));
        hist.add(h2 = new History(desc, lev, term + " " + name + " emrgG", emergOfrs.getARow(E.lsecs)));
      }

      void listAvailOfrs(String pre, int lev) {
        listavailOfrs(lev, pre);
      }

      // Assets.CashFlow.Trades
      /**
       * list available offers
       *
       * @param lev level of listing
       * @param desc descriptor
       */
      void listavailOfrs(int lev, String desc
      ) {
        hist.add(h1 = new History(desc, lev, term + " " + name + " limC", availOfrs.getARow(0)));
        hist.add(h2 = new History(desc, lev, term + " " + name + " limG", availOfrs.getARow(E.lsecs)));
      }

      /**
       * list the changes to the current bids from the previous bids
       *
       * @param lev level of hist output
       * @param desc description of changer
       * @param prevBid previous good (often entry)
       */
      void listDifBid(int lev, String desc, A2Row prevBid) {
        hist.add(h1 = new Difhist(ec, lev, desc + term + " " + name + " difC", bids.getARow(0), prevBid.getARow(0)));
        hist.add(h2 = new Difhist(ec, lev, desc + term + " " + name + " difG", bids.getARow(E.lsecs), prevBid.getARow(E.lsecs)));
      }

      // Assets.CashFlow.Trades
      void histOther(History... hargs
      ) {
        if (doHistOther && false) {
          int len = hargs.length;
          for (int k = 0; k < len; k++) {
            if (hargs[k] != null) {
              ohist.add(hargs[k]);
            }
          }
        }
      }

      /**
       * caculate various trading sums.<br>
       *
       * excessOffers&gt;0 increase requests, or reduce offers.<br>
       * excessOffers&lt;0 increase offers or reduce requests.<br>
       * sv = requests/offers or Iget/Ipaid.<br>
       * sf = goal requests/offers or I get/I paid.<br>
       * Critical high requests mult a little more than just stratMult.<br>
       * Critical low offers mult a little less than just stratMult.<br>
       * SubAsset.unitWorth value of worth per unit use the staff.unitWorth
       * CashFlow.stratMult is the multiplier of bid. units for strategic values
       * of bid offers, set totalReceipts and totalSend. <br>Calculate value
       * sums:
       *
       * @return strategicValue requests/offers or what frac I get for what I
       * give
       * @note A2Row stratV complex strategic values of bids traded negative //*
       * values are requests/, positive offers, sends
       * @note A2Row stratF complex strategic fraction to be applied to bids
       * value it is bids values which are negative or positive
       * @note offers are the positive bids we offer to the other
       * @note requests are the negative bids we want to get
       * @note bids positive offers, negatives requests
       */
      double calcStrategicSums() {// Assets.CashFlow.Trades
        sf = calcStrategicGoal();  // reduced after each barteer
        if (EM.dfe()) {
          return 0.;
        }
        totalStrategicRequests = totalStrategicOffers = totalStrategicFrac = sumCriticalStrategicRequests = criticalStrategicOffers = criticalStrategicFrac = lowStrategicOffers = strategicRequests = sumCriticalNominalRequests = strategicOffers = 0.;
        nominalRequests = nominalOffers = nominalFrac = 0.;
        criticalNominalRequests = criticalNominalOffers = criticalNominalFrac = 0.;
        sumCriticalBidRequests = sumBidRequests = 0.;
        stratVars.sendHist(hist, History.informationMinor9, "%$", 5, "C stratVals", "G stratVals");
        bids.sendHist(hist, History.informationMinor9, "%&", 5, "C bids", "G bids");
        requests = offers = 0.;
        multF = makeZero(multF); // all A2Row
        stratV = makeZero(stratV);
        stratCriticalRequests = makeZero(stratCriticalRequests);
        stratF = makeZero(stratF);
        stratCF = makeZero(stratCF);
        nominalF = makeZero(nominalF);
        nominalV = makeZero(nominalV);
        nominalCV = makeZero(nominalCV);
        nominalCF = makeZero(nominalCF);
        goodC = makeZero(goodC);
        int ifSearch = yphase == yrphase.SEARCH ? 0 : 1; // search or barter
        int ix, p, iXrors;
        // high sector indexes > hcntr most important high values
        int hcntr = (int) eM.criticalNumbers[ifSearch][pors][clan];
        // low sector indexes < lcntr most important low values
        int lcntr = (int) eM.criticalNumbers[ifSearch][pors][clan];
        int criticalHighSectors = stratVars.minIx(hcntr);
        int criticalLowSectors = stratVars.maxIx(lcntr);
        // loop max to min StrategicValues
        for (int m = 0; m < E.L2SECS; m++) {
          ix = bids.minIx(m);// bids min-max  not max to min iX stratV
          double bidV = bids.get(ix);
          // double stv = stratVars.get(ix);
          double stratMultV = stratMult.get(ix); //reallly stratVars
          iXrors = (int) ix / E.lsecs;// 0 or 1
          //double nv = eM.nominalRSWealth[iXrors][pors];
          double nv = ix < E.LSECS ? eM.nominalWealthPerResource[pors] : s.worth.get(ix - E.LSECS) * eM.nominalWealthPerStaff[pors];

          // requests  negative bids
          if (bidV < E.NZERO) {
            if (m < hcntr) {
              sumCriticalBidRequests -= bidV;
              sumBidRequests -= bidV;
              sumCriticalStrategicRequests -= bidV * stratCF.add(ix, stratMultV);
              criticalNominalRequests -= bidV * nv;
              sumCriticalNominalRequests -= criticalNominalRequests;
              nominalCV.set(ix, bidV * nominalCF.set(ix, nv));
              stratCriticalRequests.set(ix, -bidV * stratCF.set(ix, stratMultV));  // critical strategic values
              nominalV.set(ix, bidV * nominalF.set(ix, nv));
              stratV.set(ix, bidV * stratF.set(ix, nv * stratMultV * (1. + eM.tradeCriticalFrac[pors][clan])));
              goodC.set(ix, bidV);
              multV.set(ix, bidV * multF.set(ix, nv * stratMultV * (1. + eM.tradeCriticalFrac[pors][clan])));
            }
            else {
              //    totalStrategicRequests -= bidV * nv * stratMultV;
              //    nominalRequests -= bidV * nv;
              sumBidRequests -= bidV;
              nominalV.set(ix, bidV * nominalF.set(ix, nv));
              //nominalV.set(ix, bidV * nominalF.set(ix, nv ));
              goodC.set(ix, bidV);
              stratV.set(ix, bidV * stratF.set(ix, nv * stratMultV));
              // multV.set(ix, bidV * multF.set(ix, stratMultV * eM.tradeStrategicFrac2[pors][clan] + nv * eM.tradeNominalFrac2[pors][clan]));
              multV.set(ix, bidV * multF.set(ix, nv * stratMultV));
              // only count entries
            }
          } //   totalStrategicRequests -= (bidV < NZERO ? bidV*stv : 0.);
          //    sumCriticalStrategicRequests -= (bidV < NZERO && ix > criticalHighSectors) ? bidV * stv : 0.;
          //    requests -= bidV < NZERO ? bidV:0.;
          //   nominalRequests -= (bidV < NZERO) ? bidV * nv : 0.;
          //  criticalNominalRequests -= (bidV < NZERO && ix > criticalHighSectors) ? bidV * nv : 0.;
          // offers
          else { //offers
            if (m > (E.L2SECS - lcntr)) {
              //  criticalStrategicOffers += bidV * nv * stratMultV;
              //   criticalNominalOffers += bidV * nv;
              //  nominalCV.set(ix, bidV * nv );
              //   stratCriticalRequests.set(ix, stratV.set(m, bidV * nv * stratMultV));
              // only count entries with a higher strategic value than LowSectors
              goodC.set(ix, bidV);
              nominalV.set(ix, bidV * nv);
              multV.set(ix, bidV * multF.set(ix, nv * stratMultV));
            }
            else {  // reduce value of lowest sectors least critical
              nominalV.set(ix, bidV * nv);
              goodC.set(ix, bidV);
              stratV.set(ix, bidV * stratF.set(ix, nv * stratMultV * (1. - eM.eM.tradeCriticalFrac[pors][clan])));
              multV.set(ix, bidV * multF.set(ix, nv * stratMultV * (1. - eM.eM.tradeCriticalFrac[pors][clan])));
            }
          }
        } // end m , ix
//        tradingOfferWorth = criticalNominalOffers;
        //excessOffers = multV.curSum();
        sumNominalOffers = nominalOffers = nominalV.curPlusSum();
        sumNominalRequests = nominalRequests = nominalV.curNegSum();
        //sumCriticalStrategicRequests =
        nominalFrac = nominalRequests / nominalOffers;
        sumStrategicOffers = strategicOffers = stratV.curPlusSum();
        sumStrategicRequests = strategicRequests = stratV.curNegSum();
        double myKnowledge = myOffer.commonKnowledge[myIx].sum();
        double oKnowledge = myOffer.commonKnowledge[oIx].sum();
        Double bCash, plusCash, negCash;
        tradedCash = bCash = myOffer.getCash();
        plusCash = bCash > PZERO ? bCash : 0.;
        negCash = bCash < NZERO ? bCash : 0.;
        offeredManuals = myOffer.getValueMoreManuals(myIx).sum();
        requestedManuals = myOffer.getValueMoreManuals(oIx).sum();
        // calculate the mult against both request and some based on requests
        // if 0 bids.negSum() set 1;
        goodFrac = bids.negSum() > NZERO || -goodC.negSum() * 2 > -bids.negSum() ? 1. : -goodC.negSum() * 2 / -bids.negSum();
        offeredManualsValue = Math.min(multV.plusSum() * EM.manualsMaxPercent[pors][clan], offeredManuals * eM.tradeManualsFrac2[oPors][oClan]);
        requestedManualsValue = Math.min(multV.plusSum() * EM.manualsMaxPercent[pors][clan], offeredManuals * eM.tradeManualsFrac2[pors][clan]);
        tradedOffers = offers = multV.plusSum() + offeredManualsValue + plusCash;
        tradedTotalStrategicOffers = totalStrategicOffers = sumStrategicOffers + offeredManualsValue + plusCash;
        nominalOffers = nominalV.plusSum();
        tradedTotalNominalOffers = totalNominalOffers = nominalOffers + offeredManualsValue + plusCash;
        sendSum = bids.plusSum() + offeredManuals + plusCash;
        totalSend = unitOffers = multV.plusSum() + plusCash;
        tradedRequests = requests = totalStrategicRequests = -multV.negSum() + requestedManualsValue + negCash;
        unitRequests = -multV.negSum() + negCash;
        nominalRequests = -nominalV.negSum();
        nominalRequestsSum = nominalRequests + negCash + requestedManualsValue;
        // requests = totalStrategicReceipts = -multV.negSum() * goodFrac + requestedManuals + negCash;
        E.myTestDouble(goodC.negSum(), "goodC.negSum()");
        E.myTestDouble(goodC.plusSum(), "goodC.plusSum()");
        E.myTestDouble(multV.negSum(), "multV.negSum");
        E.myTestDouble(multV.plusSum(), "multV.plusSum");
        E.myTestDouble(requestedManuals, "requestedManuals");
        E.myTestDouble(bCash, "bCash");
        E.myTestDouble(negCash, "negCash");

        //sv = requests / offers; // fraction strategicValue get/give
        // do not include any traded manuals
        // see what I get for what I paid = get/paid
        sv = strategicValue = offers < PZERO ? 0. : requests / offers;
        // goal strategicFraction sf desired profit
        // desiredOffer = requests/sf,
        excessOffers = offers - requests / sf; // these are strategic worth values incl manuals, cash
        listBids(aPre, History.valuesMinor7);
        listStratVars(aPre, History.valuesMinor7);
        hist.add(new History(aPre, History.valuesMinor7, name + "bids calcSum ",
                             "mo=" + EM.mf(bids.min(0)),
                             "m1=" + EM.mf(bids.min(1)),
                             "m2=" + EM.mf(bids.min(2)),
                             "s0=" + EM.mf(bids.get(bids.minIx(0))),
                             "s1=" + EM.mf(bids.get(bids.minIx(1))),
                             "s2=" + EM.mf(bids.get(bids.minIx(2))),
                             "rC=" + EM.mf(sumCriticalBidRequests),
                             "rb=" + EM.mf(sumBidRequests), "<<<<<<<"));
        hist.add(new History(aPre, History.valuesMinor7, name + "fracs calcSum ",
                             "S=" + EM.mf(eM.strategicFracs[ifSearch][pors][clan]),
                             "C=" + EM.mf(eM.tradeCriticalFrac[pors][clan]),
                             "N=" + EM.mf(eM.nominalFracs[ifSearch][pors][clan]),
                             "rS=" + EM.mf(requests), "rC=" + EM.mf(sumCriticalStrategicRequests),
                             "rN=" + EM.mf(nominalRequests),
                             "oS=" + EM.mf(offers),
                             "oC=" + EM.mf(criticalStrategicOffers),
                             "oN=" + EM.mf(nominalOffers), "<<<<<<<"));
        hist.add(new History(aPre, History.valuesMinor7,
                             " multSums",
                             "rS=" + EM.mf(totalStrategicRequests * eM.strategicFracs[ifSearch][pors][clan]),
                             "rC=" + EM.mf(sumCriticalStrategicRequests * eM.tradeCriticalFrac[pors][clan]),
                             "rN=" + EM.mf(nominalRequests * eM.nominalFracs[ifSearch][pors][clan]),
                             "oS=" + EM.mf(totalStrategicOffers * eM.strategicFracs[ifSearch][pors][clan]),
                             "oC=" + EM.mf(criticalStrategicOffers * eM.tradeCriticalFrac[pors][clan]),
                             "oN=" + EM.mf(nominalOffers * eM.nominalFracs[ifSearch][pors][clan]),
                             "sS=" + EM.mf((totalStrategicOffers - totalStrategicRequests) * eM.strategicFracs[ifSearch][pors][clan]),
                             "sC=" + EM.mf((criticalStrategicOffers - sumCriticalStrategicRequests) * eM.tradeCriticalFrac[pors][clan]),
                             "sN=" + EM.mf((nominalOffers - nominalRequests) * eM.nominalFracs[ifSearch][pors][clan]), "<<<<<<<"));
        hist.add(new History(aPre, History.valuesMinor7, " Sums ", "rq" + EM.mf(requests), "Of" + EM.mf(offers), "csh=" + EM.mf(cash), "bC" + EM.mf(bCash), "sv" + EM.mf(strategicValue), "xf" + EM.mf(excessOffers), "<<<<<"));
        //   hist.add(new History(aPre, History.valuesMinor7, " from Offers", "cK" + EM.mf(myOffer.commonKnowledge[myIx].sum()), EM.mf(myOffer.commonKnowledge[oIx].sum()), "mls" + EM.mf(offeredManuals), EM.mf(requestedManuals), "total o=", EM.mf(totalStrategicOffers), "r=", EM.mf(totalStrategicRequests), "<<<<<<<<<<"));

        E.myTestDouble(offers, "offers");
        E.myTestDouble(requests, "requests");
        E.myTestDouble(sv, "sv");
        if (newBarter) {
          newBarter = false;
          nbOffers = offers;
          nbRequests = requests;
          sv0 = nbStrategicValue = strategicValue;
          nbExcessOffers = excessOffers;

          nbStratF = stratF.copy();
          nbStratV = stratV.copy();
          nbStratCV = stratCriticalRequests.copy();
        }
        if (term > EM.barterStart - 2) {
          totalStrategicRequestsFirst = totalStrategicRequests;
          totalStrategicOffersFirst = totalStrategicOffers;
          totalStrategicFracFirst = totalStrategicFrac;
          sumCriticalStrategicRequestsFirst = sumCriticalStrategicRequests;
          sumCriticalBidRequestsFirst = sumCriticalBidRequests;
          sumBidRequestsFirst = sumBidRequests;
          criticalStrategicOffersFirst = criticalStrategicOffers;
          criticalStrategicFracFirst = criticalStrategicFrac;
          nominalRequestsSumFirst = nominalRequestsSum;
          nominalRequestsFirst = nominalRequests;
          nominalOffersFirst = nominalOffers;
          nominalFracFirst = 0.;
          criticalNominalRequestsFirst = criticalNominalRequests;
          criticalNominalOffersFirst = criticalNominalOffers;
          criticalNominalFracFirst = criticalNominalFrac;
          requestsFirst = requests;
          sumNominalRequestsFirst = sumNominalRequests;
          offersFirst = offers;
          sendSumFirst = sendSum;
          bidsFirst = bids.copy();
          stratVarsFirst = stratVars.copy();
          firstStrategicValue = strategicValue;
          firstStrategicGoal = strategicGoal;
        }
        int bLev = ec.blev = History.dl;

        didGoods = true;
        return strategicValue;
      } // Assets.CashFlow.Trades.calStrategicSums

      /**
       * test whether to accept a trade
       *
       * @param myOffer has the info for the terst
       * @return true if we accept a trade
       */
      boolean testTrade(Offer myOffer
      ) { // Assets.CashFlow.Trades
        if (changes > 0) {
          hist.add(new History("@i", 5, term + " CHANGES", "changes=" + changes, "sv=" + EM.mf(strategicValue), "sf=" + EM.mf(strategicGoal), "ts=" + EM.mf(offers), "tr=" + EM.mf(requests)));
          String aHist = " TTrade changes=" + EM.mf(changes) + " T" + term + " sv" + EM.mf(sv) + " sf" + EM.mf(sf) + " rGoal0=" + EM.mf(rGoal0) + tradeHist;
          if (E.tradeDebugTerms) {
            System.out.println(aHist);
          }
          tradeHist = " tHist ";
          return false; // no trade if changes
        }
        // trade, changes &eq; 0, sv &gt; rgoal0 term &lt; barterStart*.75
        if (false && sv > sf || (sv > rGoal0 && term < EM.barterStart * .75)) {
          hist.add(new History("@g", 3, "T" + term + " " + name + " doTrm 1", "sv" + EM.mf(strategicValue), "sf" + EM.mf(strategicGoal), "ofrs" + EM.mf(offers), "rqst" + EM.mf(requests)));
          term = 1;
          myOffer.setTerm(1);
          //   myOffer.accepted(ec);
          return true;
        }
        // there are no changes so can accept
        if (sv > sf || (sv > rGoal0)) {
          hist.add(new History("@g", 3, "T" + term + " " + name + " Acpt", "sv" + EM.mf(strategicValue), "sf" + EM.mf(strategicGoal), "ofrs" + EM.mf(offers), "rqst" + EM.mf(requests)));
          String aHist = " Accept T" + term + " sv" + EM.mf(sv) + " sf" + EM.mf(sf) + " rGoal0=" + EM.mf(rGoal0) + tradeHist;
          if (E.tradeDebugTerms) {
            System.out.println(aHist);
          }
          tradeHist = " tHist ";
          term = 0;
          myOffer.setTerm(0);
          String aSss = " ";
          E.sysmsg(aSss = ec.name + " Trades.barter accepted term=" + myOffer.getTerm());
          listGoods(mRes, "Y+" + myOffer.getTerm());
          //        sendStats(tSend, tReceipts, tStratValue, tBid, (int) E.fav[clan][myOffer.getOClan()]);
          myOffer.accepted(ec);
          didTrade = true;
          rejectTrade = false;
          E.sysmsg(aSss = ec.name + "Trades.barter after accepted term=" + term + ":" + myOffer.getTerm());
          myOffer.setTerm(term = 0);
          listDifBid(History.valuesMajor6, "Z+" + term, oprevGoods);
          hist.add(new History("B+", History.informationMinor9, "aftr accpt term=" + term, "abcde fgh ijk"));
          return true;
        }
        else {
          hist.add(new History("@h", History.loopMinorConditionals5, "T" + term + " " + name + " no trade ", " no trade ", "sv" + EM.mf(strategicValue), "< sf" + EM.mf(strategicGoal), "ofrs" + EM.mf(offers), "rqst" + EM.mf(requests)));
          return false;
        }
      } // Assets.CashFlow.Trades

      void sendStats(double tSend, double tReceipts, double tStratValue, A2Row tBid,
                     int favr
      ) {
        //percent tReceipts/strtYearTotWorth per favr, per ship#,tot, sumtot list1?
        setStat("TRADEDRCD", pors, clan, tReceipts / startYrSumWorth, 1);
        switch (favr) {
          case 5:
            // gameRes.TRADEDRCDF5.wet(pors, clan, tReceipts / strtYearTotWorth, 1);
            setStat("TRADEDRCDF5", pors, clan, tReceipts / startYrSumWorth, 1);
            break;
          case 4:
            // gameRes.TRADEDRCDF4.wet(pors, clan, tReceipts / strtYearTotWorth, 1);
            setStat("TRADEDRCDF4", pors, clan, tReceipts / startYrSumWorth, 1);
            break;
          case 3:
            // gameRes.TRADEDRCDF3.wet(pors, clan, tReceipts / strtYearTotWorth, 1);
            setStat("TRADEDRCDF3", pors, clan, tReceipts / startYrSumWorth, 1);
            break;
          case 2:
            // gameRes.TRADEDRCDF2.wet(pors, clan, tReceipts / strtYearTotWorth, 1);
            setStat("TRADEDRCDF2", pors, clan, tReceipts / startYrSumWorth, 1);
            break;
          case 1:
            // gameRes.TRADEDRCDF1.wet(pors, clan, tReceipts / strtYearTotWorth, 1);
            setStat("TRADEDRCDF1", pors, clan, tReceipts / startYrSumWorth, 1);
            break;
          case 0:
            // gameRes.TRADEDRCDF0.wet(pors, clan, tReceipts / strtYearTotWorth, 1);
            setStat("TRADEDRCDF0", pors, clan, tReceipts / startYrSumWorth, 1);
            break;
        }
      }

      /**
       * test whether the barter should reject not continue rejectNegRequests
       * statTinyRequests rejectGoal0 rejectAtOne
       *
       * @param myOffer
       * @return true if barter is to terminate rejected
       */
      boolean testNoTrade(Offer myOffer
      ) { // Assets.CashFlow.Trades
        aPre = "t";
        double strategicFrac = calcPercent(strategicGoal, strategicValue);
        if (changes > 0 && myOffer.getTerm() < 2) {// no trade if changes were required end
          hist.add(new History(aPre, History.loopMinorConditionals5, "T" + term + " " + name + " rej&change", "changes=" + changes, "sv=" + EM.mf(strategicValue), "sf=" + EM.mf(strategicGoal), "ofrs=" + EM.mf(offers), "rqst=" + EM.mf(requests)));
          setStat(EM.rejectAtOne, sf, 1);
          setStat(EM.rejectAtOneTerm, prevTerm, 1);
          return true;
        }
        // current sv too small, future sv below 0 goal, choose reject
        if (((sv < sf && sv < rGoal0)) && (term < EM.barterStart * .4)) {
          hist.add(new History(aPre, History.loopMinorConditionals5, "T" + term + " " + name + " REJGoal0 ", "sv" + EM.mf(strategicValue), "<sf" + EM.mf(strategicGoal), "ofrs=" + EM.mf(offers), "rqst=" + EM.mf(requests)));
          setStat(EM.rejectGoal0, strategicFrac, 1);
          setStat(EM.rejectGoal0Term, term, 1);
          String aHist = "T!Trade T" + term + " sv" + EM.mf(sv) + " sf" + EM.mf(sf) + " rGoal0=" + EM.mf(rGoal0) + tradeHist;
          if (E.tradeDebugTerms) {
            System.out.println(aHist);
          }
          tradeHist = " tHist ";
          return true;
        }
        //reject tiny (too small) in terms of the sum of FirstCriticalStrategicRequests ships only
        if (((sumCriticalStrategicRequests < sumCriticalStrategicRequestsFirst * EM.criticalStrategicRequestsRejectFrac[pors][clan]) && pors == E.S)) {
          hist.add(new History(aPre, History.loopMinorConditionals5, "T" + term + " " + name + " CNT", "tiny", "sv" + EM.mf(strategicValue), "<sf" + EM.mf(strategicGoal), "ofrs=" + EM.mf(offers), "rqst=" + EM.mf(requests)));
          setStat(EM.rejectTinyRequests, strategicGoal, 1);
          setStat(EM.rejectTinyRequestsSv, strategicValue, 1);
          setStat(EM.rejectTinyRequestsFirst, sumCriticalStrategicRequestsFirst, 1);
          setStat(EM.rejectTinyRequestsPercentFirst, calcPercent(sumCriticalStrategicRequestsFirst, sumCriticalStrategicRequests), 1);
          setStat(EM.rejectTinyRequestsTerm, term, 1);
          String aHist = "tiny T" + term + " sv" + EM.mf(sv) + " sf" + EM.mf(sf) + " rGoal0=" + EM.mf(rGoal0) + tradeHist;
          if (E.tradeDebugTerms) {
            System.out.println(aHist);
          }
          tradeHist = " tHist ";
          return false;
        }
        if (requests < E.PZERO) {
          hist.add(new History(aPre, History.loopMinorConditionals5, "T" + term + " " + name + " REJECT 0.0", "sv" + EM.mf(strategicValue), "<sf" + EM.mf(strategicGoal), "ofrs=" + EM.mf(offers), "rqst=" + EM.mf(requests)));
          setStat(EM.rejectNegRequests, strategicFrac, 1);
          String aHist = " requests<0 T" + term + " sv" + EM.mf(sv) + " sf" + EM.mf(sf) + " rGoal0=" + EM.mf(rGoal0) + tradeHist;
          if (E.tradeDebugTerms) {
            System.out.println(aHist);
          }
          tradeHist = " tHist ";
          return true;
        }
        else {
          hist.add(new History(aPre, History.loopMinorConditionals5, "T" + term + " " + name + " yet more", "sv" + EM.mf(strategicValue), "sf" + EM.mf(strategicGoal), "ofrs=" + EM.mf(offers), "rqst=" + EM.mf(requests)));
          String aHist = " T!TradeContinue T" + term + " sv" + EM.mf(sv) + " sf" + EM.mf(sf) + " rGoal0=" + EM.mf(rGoal0) + tradeHist;
          if (E.tradeDebugTerms) {
            System.out.println(aHist);
          }
          tradeHist = " tHist ";
          return false;
        }
      }  // Assets.CashFlow.Trade.testNoTrade

      /*
      rejectNegRequests
      .StatTinyRequests
      rejectGoal0
      rejectAtOne
       */
      /**
       * override the first num strategic bids (planet offers) to ship requests
       * do not override planet requests save sum as strat1Sum, norm1Sum Now add
       * any remaining planet offers sums from this operation strat2Sum,
       * norm2Sum, total requests strat3Sum, norm3Sum use availOfrs as limit of
       * ship offers unless emergency
       *
       * @param num number of bids ti override
       */
      void shipOverridePlanetsRequests(int num
      ) {// Assets.CashFlow.Trades
        double good = 0., fneed, need, avail, emerg, ig;
        for (int p : I2ASECS) {  // force did clear
          did[p] = 0;
        }
        listFneedReq(5, "ovr" + num + " ");
        listneedReq(5, "ovr" + num + " ");
        listavailOfrs(5, "ovr" + num + " ");
        listemergOfrs(5, "ovr" + num + " ");
        listGoods(5, "ovr" + num + " ");

        A2Row prevGood = bids.copy();
        double sv = 0., nv = 0.;
        int ix;
        double strat1Sum = 0., strat2Sum = 0., strat3Sum = 0.;
        double nom1Sum = 0., nom2Sum = 0., nom3Sum = 0.;
        // limit all values, but set ours if possible
        for (int gradeIx = 0; gradeIx < num; gradeIx++) {
          ix = stratVars.maxIx(gradeIx); // start with highest strategic values
          good = bids.get(ix);
          fneed = fneedReq.get(ix);
          need = needReq.get(ix);
          avail = availOfrs.get(ix);
          emerg = emergOfrs.get(ix);
          sv = stratVars.get(ix);
          nv = eM.nominalRSWealth[(int) (ix / E.lsecs)][pors];
          double needsum = 0;
          //other request=> my request  dont do
          if (good > PZERO && need > PZERO && false) {
            hist.add(new History(aPre, 5, term + " " + name + "oOfr=>myOfr", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "gradeIx=" + gradeIx, EM.mf(good), "to", EM.mf(-need)));
            bids.set(ix, -need);  //  o offer force to my request
            strat1Sum += -need * sv;
            nom1Sum += -need * nv;
            // valueChangesTried[ix]++;
            changes++;
          } // O Req => my ofr(-need) new different O Req
          else if (good > PZERO && -need < NZERO) {
            hist.add(new History(aPre, 5, term + " " + name + " oOfr => myReqest", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "gradeIx=" + gradeIx, EM.mf(good), "to", EM.mf(-need), "<<<<"));
            bids.set(ix, -need);  // force a new offer (o request)
            did[ix] = 1;
            strat1Sum += -need * sv;
            nom1Sum += -need * nv;
            //   valueChangesTried[ix]++; // doesn't count
            changes++;
          } // o Ofr(my Req) => larger o Ofr(my Req)
          else if (good < NZERO && need > PZERO && -need < good) {
            hist.add(new History(aPre, 5, term + " " + name + " ovr decrment", "good", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "gradeIx=" + gradeIx, EM.mf(good), "to", EM.mf(-need), "<<<<"));
            bids.set(ix, -need);  // force a larger offer
            did[ix] = 1;
            strat1Sum += -need * sv;
            nom1Sum += -need * nv;
            //   valueChangesTried[ix]++; // doesn't count
            changes++;
          }
          else // o Ofr(my Req) => same oOfr(myReq)
          if (good < NZERO && need > PZERO) {
            strat1Sum += -need * sv;
            nom1Sum += -need * nv;
            did[ix] = 1;
            hist.add(new History(aPre, 13, term + " " + name + " ovr keep", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "gradeIx=" + gradeIx, EM.mf(good), "<<<", "<<<"));
          }
          else {
            hist.add(new History(aPre, 13, term + " " + name + " ovr keep", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "gradeIx=" + gradeIx, EM.mf(good), "<<", "<<"));
          }
        }
        // now do the rest of the requests
        // override offers starting from least strategic
        // skip for now
        for (int gradeIx = 0; false && gradeIx < num; gradeIx++) {
          ix = stratVars.minIx(gradeIx); // start with lowestt strategic offer
          good = bids.get(ix);
          fneed = fneedReq.get(ix);
          need = needReq.get(ix);
          avail = availOfrs.get(ix);
          emerg = emergOfrs.get(ix);
          if (good < NZERO && avail > PZERO) {
            hist.add(new History(aPre, 5, term + " " + name + " override", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "gradeIx=" + gradeIx, EM.mf(good), "to", EM.mf(avail)));
            bids.set(ix, ig);  // force an offer
            //       valueChangesTried[ix]++;
            changes++;
          }
          else if (good > NZERO && avail > good) {
            hist.add(new History(aPre, 5, term + " " + name + " ovr incr", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "gradeIx=" + gradeIx, EM.mf(good), "to", EM.mf(avail)));
            bids.set(ix, ig);  // force an offer
            //       valueChangesTried[ix]++;
            changes++;

          }
          else {
            hist.add(new History(aPre, 13, term + " " + name + " ovr keep", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "gradeIx=" + gradeIx, EM.mf(good), "not", EM.mf(ig), "", ""));
          }

        }
        listDifBid(5, "ovr", prevGood);
      }// Assets.CashFlow.Trades.overrideOthersRequests

      /**
       * reset some of the bids to reflect the planet requests only reset the
       * first num requests
       *
       * @param num number of bids ti override
       */
      void overrideOthersRequests(int num
      ) {// Assets.CashFlow.Trades
        double good = 0., fneed, need, avail, emerg, ig;
        listFneedReq(5, "ovr" + num + " ");
        listneedReq(5, "ovr" + num + " ");
        listavailOfrs(5, "ovr" + num + " ");
        listemergOfrs(5, "ovr" + num + " ");
        listGoods(5, "ovr" + num + " ");
        A2Row prevGood = bids.copy();
        int ix;
        // limit all values, but set ours if possible
        for (int gradeIx = 0; gradeIx < num; gradeIx++) {
          ix = stratVars.maxIx(gradeIx); // start with highest strategic value first
          good = bids.get(ix);
          fneed = fneedReq.get(ix);
          need = needReq.get(ix);
          avail = availOfrs.get(ix);
          emerg = emergOfrs.get(ix);
          double needsum = 0;
          //my requests can override make ours requests
          if (good > PZERO && need > PZERO) {
            hist.add(new History(aPre, 5, term + " " + name + " override", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "gradeIx=" + gradeIx, EM.mf(good), "to", EM.mf(-need)));
            bids.set(ix, -need);  // force a request
            //   valueChangesTried[ix]++; // doesn't count
            changes++;
          }
          else if (good > PZERO && need < NZERO) {
            hist.add(new History(aPre, 5, term + " " + name + " override", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "gradeIx=" + gradeIx, EM.mf(good), "to", EM.mf(-need)));
            bids.set(ix, -need);  // force a request
            //   valueChangesTried[ix]++; // doesn't count
            changes++;
          }
          else if (good < NZERO && need > PZERO && -need < good) {
            hist.add(new History(aPre, 5, term + " " + name + " ovr decrment", "good", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "gradeIx=" + gradeIx, EM.mf(good), "to", EM.mf(-need)));
            bids.set(ix, -need);  // force a offer
            //   valueChangesTried[ix]++; // doesn't count
            changes++;

          }
          else {
            hist.add(new History(aPre, 13, term + " " + name + " ovr keep", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "gradeIx=" + gradeIx, EM.mf(good), "", ""));
          }
        }
        // override offers starting from least strategic
        for (int gradeIx = 0; gradeIx < num; gradeIx++) {
          ix = stratVars.minIx(gradeIx); // start with lowestt strategic offer
          good = bids.get(ix);
          fneed = fneedReq.get(ix);
          need = needReq.get(ix);
          avail = availOfrs.get(ix);
          emerg = emergOfrs.get(ix);
          if (good < NZERO && avail > PZERO) {
            hist.add(new History(aPre, 5, term + " " + name + " override", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "gradeIx=" + gradeIx, EM.mf(good), "to", EM.mf(avail)));
            //          bids.set(ix, ig);  // force an offer
            //       valueChangesTried[ix]++;
            changes++;
          }
          else if (good > NZERO && avail > good) {
            hist.add(new History(aPre, 5, term + " " + name + " ovr incr", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "gradeIx=" + gradeIx, EM.mf(good), "to", EM.mf(avail)));
            //      bids.set(ix, ig);  // force an offer
            //       valueChangesTried[ix]++;
            changes++;

          }
          else {
            //     hist.add(new History(aPre, 13, term + " " + name + " ovr keep", (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "ix=" + ix, "gradeIx=" + gradeIx, EM.mf(good), "not", EM.mf(ig), "", ""));
          }

        }
        listDifBid(5, "ovr", prevGood);
      }// Assets.CashFlow.Trades.overrideOthersRequests

      /**
       * get the bid SubAasset name and sector number
       *
       * @param ix
       * @return
       */
      String gbsnn(int ix
      ) {
        String ret = (ix < E.lsecs ? "c." : "g.") + ix % E.lsecs;
        return ret;
      }

      /**
       * get the bid SubAasset name and sector number
       *
       * @param ix
       * @return
       */
      String getBidSubAssetNameNumber(int ix
      ) {
        return gbsnn(ix);
      }

      /**
       * return tag string without ! if true, with ! if false
       *
       * @param tag boolean variable to be checked
       * @param sss visual symbol for variable, may be shorter
       * @return blank + ? ! + sss
       */
      String ifTag(boolean tag, String sss) {
        return " " + (tag ? sss : "!" + sss);
      }

      /**
       * Enforce the strategic goal for your clan and planet or ship each term
       * use calcStrategicGoal then calcStrategicValue the strategicValue is the
       * trading profit, requests /offers sv = strategic requests/offers ,sv1 is
       * sv after calcSums sv0 is sv at beginning of barter strategicGoal,sg,sg1
       * = desired strategic requests/offers excessOffers = currentOffers -
       * requests/sg currentRequests come from calcStrategicGoal Assume traders
       * make their interest in specific sectors known by again raising the
       * request for a specific sector after the other trader decreased the
       * request. <br>
       * After re-raising a request E.maxTries = {@value E#maxTries} assume the
       * specified sector is not available.<br>
       * if excessOffers is positive then increase requests and later decrease
       * offers. if excessRequests is negative then increase offers and later
       * decrease requests A planets offers become ship requests in the next
       * term and a planets requests become ship offers in the next term. Each
       * economy limits offers to the max available except in emergencies limit
       * offers to the max emergency available. A trade only takes place when
       * both ship and planet agree on the same amounts. that is one term must
       * take place with no changes and be an acceptable strategic Value, When a
       * change limit is reached, requests and offers cannot be increased they
       * can only be decreased or left unchanged. Enter with current value of
       * fliped bids and cash set for developing this Econ's bid
       */
      void enforceStrategicGoal() {// Assets.CashFlow.Trades.enforceStrategicGoal
        myIx = myOffer.setMyIx(ec);
        double bCash = myOffer.getCash();
        ec.blev2 = History.dl;
        hist.add(new History(aPre, 5, "T" + term + " " + " enfStVal", "rq" + EM.mf(requests), "of" + EM.mf(offers), "sf=" + EM.mf(sf), "xcsOfrs=", EM.mf(excessOffers), "bCash=", wh(bCash), (hEmerg ? "hEmerg" : "!!!hEmerg"), "<<<<<<<<<<<"));
        A2Row entrGoods = bids.copy();
        // calculate strategicGoal the desired value of strategicValue
        // allow a little extra
        // do the first calculation
        calcStrategicSums(); //  sv,excessOffers
        if (EM.dfe()) {
          return;
        }
        sv1 = sv;  // sf,requests,offers,excessOffers
        sf1 = sf;  // save the first sf as sf1, sv as sv1

        // define variables for use in m forloops, save garbage collection
        // sv = reqs/offrs; sf *ofrs = reqs; ofrs = reqs/sf
        double maxDif, dif;

        // if excessOffers &lt; 0, need to increase offers or reduce requests
        int ix = E.l2secs - 1;
        aPre = "^E";
        double svg = 0, bid = 0, emerg = 0, avail, fneed, need, needo, fuzz, more, mf, dif2;
        double bid2 = 0., maxReq = 0.;
        double first = 0.;
        double xou = 0;  //excessOfferUnits for these bids
        xof = 0; // excessOffers
        int ifSearch = yphase == yrphase.SEARCH ? 0 : 1; // search or barter
        int hcntr = (int) eM.criticalNumbers[ifSearch][pors][clan];
        int lcntr = (int) eM.criticalNumbers[ifSearch][pors][clan];
        boolean highCritical = false;
        boolean isExcessOffers = false, isExcessRequests = false, isAvail, isEmerg, isNeed, isFneed;
        boolean triesOK, isRequest, isOffer, isChanged, doLimit, isMoreOffers;
        boolean hiCrit, didChange, doChg;
        boolean rnd1, rnd2, rnd3, rnd23, notHiCrit;
        //    double xov = 0;  //excessOffer (value in strategic values)
        A2Row changed = new A2Row(ec, 13, "changed"); // zero's elements

        fuzz = (offers + requests) * .01;
        double subFuzz = fuzz * -1.01;
        double limit = PZERO;
        //   double incTerm = eM.incTriesPTerm[pors];
        //force alternation of sectors to override
        double incTerm = Math.max(eM.maxTries[pors], (.8 + EM.barterStart - term) * eM.incTriesPTerm[pors]);
        double incYr = 1. / eM.incTriesPTerm[pors]; //count of max tries
        int maxCnt = (int) (eM.cntMult[pors] * (eM.maxTries[pors] + (EM.barterStart - 2 - term) * incTerm));
        double incTerm1 = eM.maxTries[pors] + (EM.barterStart - 2 - term) * incTerm;
        double incTerm2 = eM.maxTries[pors] - .3 + (EM.barterStart - 2 - term) * incTerm;
        double incTerm3 = eM.maxTries[pors] - .6 + (EM.barterStart - 2 - term) * incTerm;
        double ofrac = (200. + term) / (198.); // strategic frac multiplyer decrease f as term decreases
        double realChangesMax = 10.;
        hist.add(new History(aPre, 5, term + " " + name + " enfStVal", "rq" + EM.mf(requests), "of" + EM.mf(offers), "sf=" + EM.mf(sf), "xof=" + EM.mf(excessOffers), "bCash=" + wh(bCash), (hEmerg ? "hEmerg" : "!!!hEmerg"), "fuzz" + EM.mf(fuzz), "ofrac" + EM.mf(ofrac), "maxCnt=" + maxCnt, "<<<<<<<<<<<"));
        ec.addOHist(ohist, new History(aPre, 5, term + " " + name + " enfStVal", "rq" + EM.mf(requests), "of" + EM.mf(offers), "sf=" + EM.mf(sf), "xof=" + EM.mf(excessOffers), "bCash=" + wh(bCash), (hEmerg ? "hEmerg" : "!!!hEmerg"), "fuzz" + EM.mf(fuzz), "ofrac" + EM.mf(ofrac), "maxCnt=" + maxCnt, "<<<<<<<<<<<"));

        if (!ec.myClearHist && E.debugDisplayTrade) {
          rlev = ec.lev = History.loopMinorConditionals5;
          blev = ec.blev = ec.lev + 2;
          oFirstBid.sendHist(hist, blev, aPre, rlev, "c oFirstBid", "g oFirstBid");
          maxReqs.sendHist(hist, blev, aPre, rlev, "c maxReq", "g maxReq");
          bids.sendHist(hist, blev, aPre, rlev, "c bids", "g bids");
          multV.sendHist(hist, blev, aPre, rlev, "c multV", "g multV");
          multF.sendHist(hist, blev, aPre, rlev, "c multF", "g multF");
          emergOfrs.sendHist(hist, blev, aPre, rlev, "c emergOfrs", "g emergOfrs");
          availOfrs.sendHist(hist, blev, aPre, rlev, "c availOfrs", "g availOfrs");
          needReq.sendHist(hist, blev, aPre, rlev, "c needReq", "g needReq");
          fneedReq.sendHist(hist, blev, aPre, rlev, "cfneedReq", "g fneedReq");
          stratVars.sendHist(hist, blev, aPre, rlev, "c StratVals", "g StratVals");
        }
        boolean isG = false; // if ix is a g index
        int iXg = -5; // g index ix -E.LSECS
        // starting means set all avail > 0 to emerg, do not count as
        // value changes tried
        boolean starting = term > EM.barterStart - 2; // 18,17. set offers for the next trader
        // allow inc values
        boolean incValueOK = term > EM.barterStart - 9;//18-10 raise req again
        int mmMax = starting ? 2 : 4; // after T16
        //1 at start and limits then 2 and 3 rounds
        for (int mm = 1; mm < mmMax; mm++) {
          rnd1 = mm == 1;
          rnd2 = mm == 2;
          rnd3 = mm == 3;
          rnd23 = mm > 1;
          realChangesMax = EM.maxTries[pors] * (EM.barterStart - term) * EM.incTriesPTerm[pors] * 3 + mm;
          //      excessOffers += subFuzz;
          for (int m = 0; m < E.L2SECS; m++) {
            // requests go from the most stratVars (m = index), we have least of them
            // increase requests of possible
            ix = stratVars.maxIx(m); // requests <0, start least amount
            iXg = ix - E.LSECS;
            isG = ix >= E.LSECS;
            String ty = "rA";
            String tt = "T"; //"OT"
            String sT = tt + term + ty + "." + mm;
            String gcNN = sT + " " + ix + "->" + (ix / LSECS > 0. ? "G" : "C") + (int) (ix % E.LSECS);
            String gcn = ix > E.LSECS ? "g" + (ix - E.LSECS) : "c" + ix;
            didChange = false;
            bid = bids.get(ix);
            isRequest = bid < E.NZERO;
            maxReq = maxReqs.get(ix); // the most request accepted by other starts at 0
            //first bid is good only starting at term = 16 !starting
            first = starting ? 0. : oFirstBid.get(ix); // requests<0
            first = maxReq < first ? maxReq : first; // <0 maxReq beats first
            maxReq = maxReqs.set(ix, rnd1 && (bid < first) ? bid : first); // <0 least
            double need1 = needReq.get(ix); // > 0
            double fneed1 = fneedReq.get(ix); // > 0
            double fneed2 = hEmerg ? fneed1 : need1; // fneed2 > 0.depends on emergency
            need = -fneed2 < first && !starting ? first : -fneed2; // first limits need, after starting
            emerg = emergOfrs.get(ix); // eofr > 0
            avail = availOfrs.get(ix);

            // String sreq = gcN + " " + (bid < E.NZERO ? "bReq" : bid > E.PZERO ? "bOfr" : "bfuzz");
            //  String sreq = gcN + " " + (starting ? bid < E.NZERO ? "sReq" : bid > E.PZERO ? "sOfr" : "sfuzz" : bid < E.NZERO ? "bReq" : bid > E.PZERO ? "bOfr" : "bfuzz");
            isOffer = bid > E.PZERO;
            // approach emerg if the first mm round did not gain enough offer
            //only use emerg in emergency
            // on rounds &gt; 1 limit = emerg or (avail + 2*emerg)/3
            limit = hEmerg ? emerg : (rnd23 ? (avail + emerg + emerg) * .3333 : avail);
            //' String sLimit = (limit < bid?"limit":"!limit");
            mf = multF.get(ix); //composit strategic Fraction
            //  starting = mm < 1 ? starting : false; // starting only round 0
            // limit need and fneed to all the other was offering

            // ensure that need/req does not exceed a first offer by other;
            //  need = fneed = first > E.NZERO && term < EM.barterStart - 1 ? fneed2 > first ? first : fneed2 : first < E.PZERO ? 0. : fneed2; // need, fneed > 0
            //  if(term < EM.barterStart && bid < first) first = maxReqs.set(ix,bid); // update maxReqs and first
            // bFirst if first is less than -fneed2
            Boolean bFirst = !starting && first < E.NZERO && first < -fneed2;
            // maxReq and first limit need

            // String sNeed = (bid - -need > fuzz? " limit": " bidOK";
            //        excessOffers += subFuzz;
            // isExcessRequests = excessOffers < -fuzz;
            // isExcessOffers = excessOffers > fuzz;
            isExcessRequests = excessOffers < E.NZERO;
            isExcessOffers = !isExcessRequests;
            String sXcReq = (isExcessRequests ? " xcReq" : isExcessOffers ? " xcOfr" : " fuzz");
            //     isMoreOffers = excessOffers + offers*eM.moreOfferBias[pors] > fuzz;
            isNeed = need < E.NZERO; // need exists
            //        highCritical = m < hcntr;
            isAvail = avail > E.PZERO; //avail exists
            isEmerg = emerg > E.PZERO;  // emergency value exists
            Boolean needy = need < bid;  //
            doLimit = bid > limit; //bid must be reduced bid > emerg
            Boolean greedy = avail < bid;  // sff ,ptr
            Boolean pbid = bid > E.PZERO;
            Boolean nbid = bid < E.NZERO;
            notHiCrit = m >= hcntr;
            hiCrit = m < hcntr;
            doChg = chgCnt < maxCnt;
            isChanged = changed.get(ix) > 0.; // changed this term
            triesOK = valueChangesTried[ix] < incTerm && incValueOK && !isChanged && doChg;
            String sLimit = (bFirst ? "first" : !incValueOK ? "!inc" : !triesOK ? "!tries" : needy ? "needy" : nbid ? "bReq" : doLimit ? "limit" : greedy ? "greedy" : pbid ? "bOfr" : "bfuzz");
            String stries = "ct" + chgCnt + ":" + maxCnt + "tr" + EM.mf(valueChangesTried[ix]) + ":" + EM.mf(incTerm);
            ;
            svg = multV.get(ix);
            xou = mf > PZERO ? excessOffers / mf : 0.; //convert to bid units
            xof = excessOffers;
            double gtst = myOffer.getG().get(ix % LSECS);
            if (E.debugTradeBarter) {
              if (isG && (myOffer.getG().get(iXg) - emerg) < NZERO) {
                eM.doMyErr("Emerg=" + EM.mf(emerg) + " is more than Guest[" + iXg + "]="
                           + EM.mf(myOffer.getG().get(iXg)) + ":" + EM.mf(g.balance.get(iXg))
                           + " m=" + m + ", myIx=" + myIx + "," + myOffer.myIx + ", term=" + term
                );
              }
              if (!isG && (myOffer.getC().get(ix % LSECS) - emerg) < NZERO) {
                EM.doMyErr("Emerg=" + EM.mf(emerg) + " is more than Cargo[" + ix + "]="
                           + EM.mf(myOffer.getC().get(ix)) + ":" + EM.mf(c.balance.get(ix))
                           + " m=" + m + ", myIx=" + myIx + "," + myOffer.myIx + ", term=" + term
                );
              }
            }
            // E.myTest(ix >= LSECS
            //        && (myOffer.getG().get(ix % LSECS) - emerg) < NZERO,
            //        "Emerg %8e is greater than Guest[%2d] %8e,%8e, m=%2d,ix=%2d,myIx=%1d,%1d,term=%3d",
            //        emerg,
            //       ix % LSECS,
            //        myOffer.getG().get(ix % LSECS),
            //       g.balance.get(ix % LSECS), m, ix,
            //      myIx, myOffer.myIx, term);
            //   if (ix < LSECS && (myOffer.getC().get(ix % LSECS) - emerg) < NZERO) {
            //     E.myTest(ix < LSECS && (myOffer.getC().get(ix % LSECS) - emerg) < NZERO, "Emerg %2.4g is greater than cargo[%2d] %2.4g,%4.4g, m=%2d,ix=%2d,myIx=%1d,%1d,term=%3d", emerg, ix % LSECS, myOffer.getC().get(ix % LSECS), c.balance.get(ix % LSECS), m, ix, myIx, myOffer.myIx, term);
            //     }

            // when starting 17 showing offerings takes priority, request only if hiCrit need>0.
            if (starting && isNeed && bid > need && rnd1 && (((!isAvail) || hiCrit))) {
              dif = bid - need;  // dif > 0, amouint to decr bid, excessOffers
              double bid1 = bids.add(ix, -dif); // decrease bid increase this request<0 add dif < 0
              excessOffers += dif2 = -dif * mf * ofrac;  // decrease excessOffers
              if (E.tradeInitOut) {
                tradeHist += " T" + term + "rA" + m + "," + ix + "," + gcn + " B" + EM.mf(bid) + "=>" + EM.mf(bids.get(ix)) + " need" + EM.mf(need) + "fneed2" + EM.mf(fneed2) + " first" + EM.mf(first) + " avail" + EM.mf(avail) + " sums" + EM.mf(bids.plusSum()) + " : " + EM.mf(bids.negSum()) + " sf" + EM.mf(sf1) + "=>" + EM.mf(sf) + " sv" + EM.mf(sv1) + "=>" + EM.mf(sv) + "xof" + EM.mf(xof1) + "=>" + EM.mf(xof) + "=>" + EM.mf(excessOffers) + " : ";
              }
              if (!ec.myClearHist && E.debugLogsOut) {
                hist.add(new History(aPre, History.informationMinor9, "T" + term + "rA", stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "=>" + EM.mf(bid1), "ned" + EM.mf(need), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                hist.add(new History(aPre, History.loopMinorConditionals5, "T" + term + "rA" + mm + ", " + gbsnn(ix), "Nd" + EM.mf(need), "B" + EM.mf(bid), "dif" + EM.mf(dif), "->" + EM.mf(bids.get(ix)), "ofrs" + EM.mf(xof), "dif2" + EM.mf(dif2), "->" + EM.mf(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
                ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, "OT" + term + "rA" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                ec.addOHist(ohist, new History(aPre, History.loopMinorConditionals5, "OT" + term + "rA" + mm + ", " + gbsnn(ix), "Nd" + EM.mf(need), "B" + EM.mf(bid), "sub" + EM.mf(dif), "->" + EM.mf(bids.get(ix)), "ofrs" + EM.mf(xof), "sub" + EM.mf(dif2), "->" + EM.mf(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
              }
              if (term <= EM.barterStart - 2) {
                changed.set(ix, 1.);
                chgCnt += 1;
                valueChangesTried[ix]++;
              }
            }
            else // list hiCrit that weren't set && isNeed && bid > need
            if (starting && rnd1 && hiCrit) {
              if (E.tradeDebugTerms) {
                tradeHist += " T" + term + "rAA" + m + "," + ix + "," + gcn + " B" + EM.mf(bid) + "=>" + EM.mf(bids.get(ix)) + " need" + EM.mf(need) + "fneed2" + EM.mf(fneed2) + " first" + EM.mf(first) + " avail" + EM.mf(avail) + " sums" + EM.mf(bids.plusSum()) + " : " + EM.mf(bids.negSum()) + " sf" + EM.mf(sf1) + "=>" + EM.mf(sf) + " sv" + EM.mf(sv1) + "=>" + EM.mf(sv) + "xof" + EM.mf(xof1) + "=>" + EM.mf(xof) + "=>" + EM.mf(excessOffers) + " : ";
              }
            }
            else // now other needs, allow offers first
            if (!starting && rnd23 && isNeed && bid > need && isExcessOffers
                && triesOK && doChg && !isChanged) {
              dif = bid - need;  // dif > 0, decr bid, excessOffers
              bids.add(ix, -dif); // decrease bid increase this request<0 add dif < 0
              excessOffers += dif2 = -dif * mf * ofrac;  // decrease excessOffers
              if (E.tradeInitOut) {
                tradeHist += " T" + term + "rB" + m + "," + ix + "," + gcn + " B" + EM.mf(bid) + "=>" + EM.mf(bids.get(ix)) + " need" + EM.mf(need) + "fneed2" + EM.mf(fneed2) + " first" + EM.mf(first) + " avail" + EM.mf(avail) + " sums" + EM.mf(bids.plusSum()) + " : " + EM.mf(bids.negSum()) + " sf" + EM.mf(sf1) + "=>" + EM.mf(sf) + " sv" + EM.mf(sv1) + "=>" + EM.mf(sv) + "xof" + EM.mf(xof1) + "=>" + EM.mf(xof) + "=>" + EM.mf(excessOffers) + " : ";
              }
              if (!ec.myClearHist && E.debugLogsOut) {
                hist.add(new History(aPre, History.informationMinor9, "T" + term + "rB" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                hist.add(new History(aPre, 5, "T" + term + "rB" + mm + ", " + gbsnn(ix), "Nd" + EM.mf(need), "B" + EM.mf(bid), "sub" + EM.mf(dif), "->" + EM.mf(bids.get(ix)), "ofrs" + EM.mf(xof), "sub" + EM.mf(dif2), "->" + EM.mf(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
                ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, "OT" + term + "rB" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                ec.addOHist(ohist, new History(aPre, 5, "OT" + term + "rB" + mm + ", " + gbsnn(ix), "Nd" + EM.mf(need), "B" + EM.mf(bid), "sub" + EM.mf(dif), "->" + EM.mf(bids.get(ix)), "ofrs" + EM.mf(xof), "sub" + EM.mf(dif2), "->" + EM.mf(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
              }

              if (dif > 0.) { // only count if a change
                changed.add(ix, 1.);
                chgCnt += 1;
                valueChangesTried[ix]++;
              }
            }
            else // set request unless starting if bid > need, counts ok
            //&&  && isNeed  dup of rB
            if (false && !starting && rnd23 && need < bid
                && isExcessOffers
                && triesOK && doChg && !isChanged) {
              dif = -(need - bid);  // dif > 0, decr bid, excessOffers
              bids.add(ix, -dif); // decrease bid increase this request<0 add dif < 0
              excessOffers += dif2 = -dif * mf * ofrac;  // decrease excessOffers
              if (E.tradeInitOut) {
                tradeHist += " T" + term + "rD" + m + "," + ix + "," + gcn + " B" + EM.mf(bid) + "=>" + EM.mf(bids.get(ix)) + " need" + EM.mf(need) + "fneed2" + EM.mf(fneed2) + " first" + EM.mf(first) + " avail" + EM.mf(avail) + " sums" + EM.mf(bids.plusSum()) + " : " + EM.mf(bids.negSum()) + " sf" + EM.mf(sf1) + "=>" + EM.mf(sf) + " sv" + EM.mf(sv1) + "=>" + EM.mf(sv) + "xof" + EM.mf(xof1) + "=>" + EM.mf(xof) + "=>" + EM.mf(excessOffers) + " : ";
              }
              if (!ec.myClearHist && E.debugLogsOut) {
                hist.add(new History(aPre, History.debuggingMinor11, "T" + term + "rD" + mm + "," + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                hist.add(new History(aPre, 5, "T" + term + "rD" + mm + ", " + gbsnn(ix), "Nd" + EM.mf(need), "B" + EM.mf(bid), "sub" + EM.mf(dif), "->" + EM.mf(bids.get(ix)), "ofrs" + EM.mf(xof), "sub" + EM.mf(dif * mf), "->" + EM.mf(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
                ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, "OT" + term + "rD" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                ec.addOHist(ohist, new History(aPre, 5, "OT" + term + "rD" + mm + ", " + gbsnn(ix), "Nd" + EM.mf(need), "B" + EM.mf(bid), "sub" + EM.mf(dif), "->" + EM.mf(bids.get(ix)), "ofrs" + EM.mf(xof), "sub" + EM.mf(dif * mf), "->" + EM.mf(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
              }
              changed.add(ix, 1.);
              chgCnt += 1;
              valueChangesTried[ix]++;
            } //   dup rD decrease offers if excessOffers !!do offers later
            else if (false && rnd23 && isExcessOffers && isOffer) {
              maxDif = bid; // bid maxDif>0 max decrease of bid units
              dif = (xou < maxDif) ? xou : maxDif; // dif>0,xou>0, maxDif > 0
              bids.add(ix, -dif); //decr bid to 0 bid or  0 excessOffers
              excessOffers -= dif * mf * ofrac;  // decrease excessOffers
              if (E.tradeInitOut) {
                tradeHist += " T" + term + "rE" + m + "," + ix + "," + gcn + " B" + EM.mf(bid) + "=>" + EM.mf(bids.get(ix)) + " need" + EM.mf(need) + "fneed2" + EM.mf(fneed2) + " first" + EM.mf(first) + " avail" + EM.mf(avail) + " sums" + EM.mf(bids.plusSum()) + " : " + EM.mf(bids.negSum()) + " sf" + EM.mf(sf1) + "=>" + EM.mf(sf) + " sv" + EM.mf(sv1) + "=>" + EM.mf(sv) + "xof" + EM.mf(xof1) + "=>" + EM.mf(xof) + "=>" + EM.mf(excessOffers) + " : ";
              }
              if (!ec.myClearHist && E.debugLogsOut) {
                hist.add(new History(aPre, History.debuggingMinor11, "T" + term + "rE" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, "OT" + term + "rE" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                hist.add(new History(aPre, 5, "T" + term + "rE" + mm + ", " + gbsnn(ix), "Xou" + EM.mf(xou), "B" + EM.mf(bid), "-" + EM.mf(dif), "=" + EM.mf(bids.get(ix)), "ofrs" + EM.mf(xof), "sub" + EM.mf(dif * mf), "->" + EM.mf(excessOffers), "VCTrid" + wh(valueChangesTried[ix]), "<<<<<"));

                ec.addOHist(ohist, new History(aPre, 5, "OT" + term + "rE" + mm + ", " + gbsnn(ix), "Xou" + EM.mf(xou), "B" + EM.mf(bid), "+" + EM.mf(-dif), "=" + EM.mf(bids.get(ix)), "ofrs" + EM.mf(xof), "sub" + EM.mf(dif * mf), "->" + EM.mf(excessOffers), "VCTrid" + wh(valueChangesTried[ix]), "<<<<<"));
              }
              chgCnt += 1;
              changed.add(ix, 1.);
              valueChangesTried[ix]++;
            }
            else {
              if (false && !ec.myClearHist && E.debugLogsOut) { // false list if no change
                hist.add(new History(aPre, History.aux2Info, "T" + term + "rG" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
              }
            }

            //else // now enforce the limits
            bid2 = bids.get(ix);  // a previous test may have updated bid
            if (bid2 > emerg) {  // offer bid  too large,  reduce to emerg
              dif = bid2 - emerg; // dif > 0 decrease bid
              bids.add(ix, -dif); //move bid back to emerg limit
              excessOffers += dif2 = -dif * mf;  // get strategic value of bids
              if (E.tradeInitOut) {
                tradeHist += " T" + term + "rF" + ix + "," + gbsnn(ix) + " B" + EM.mf(bid) + "=>" + EM.mf(bids.get(ix)) + " sums" + EM.mf(bids.plusSum()) + " : " + EM.mf(bids.negSum()) + " sf" + EM.mf(sf1) + "=>" + EM.mf(sf) + " sv" + EM.mf(sv1) + "=>" + EM.mf(sv) + "xof" + EM.mf(xof) + "=>" + EM.mf(excessOffers) + " : ";
              }
              if (!ec.myClearHist && E.debugLogsOut) {
                hist.add(new History(aPre, History.debuggingMinor11, "T" + term + "rF" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "bid2" + EM.mf(bid2), "lim" + EM.mf(limit), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, "OT" + term + "rF" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                hist.add(new History(aPre, 5, "T" + term + "rF" + mm + ", " + gbsnn(ix), "E" + EM.mf(emerg), "B" + EM.mf(bid), "2B" + EM.mf(bid2), "sub" + EM.mf(-dif), "=" + EM.mf(bids.get(ix)), "xof" + EM.mf(xof), "sub" + EM.mf(dif2), "->" + EM.mf(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
                ec.addOHist(ohist, new History(aPre, 5, "OT" + term + "rF" + mm + ", " + gbsnn(ix), "L" + EM.mf(limit), "B" + EM.mf(bid), "2B" + EM.mf(bid2), "sub" + EM.mf(-dif), "=" + EM.mf(bids.get(ix)), "xof" + EM.mf(xof), "sub" + EM.mf(dif2), "->" + EM.mf(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
              }
              changed.add(ix, 1.);

            }
          }// for m
          //        changes += (int)changed.curSum();
          calcStrategicSums(); //  recomput sv,excessOffers
          bids.sendHist(hist, aPre);
          //   excessOffers += subFuzz;  //force sv to be a bit more than sf
          if (term < EM.barterStart - 5) {
            // if round 2 or 3, no changes and sum &gt; myGoal for trade.
            // if ( sv > sf && changed.sum() <= PZERO) {
            if (sv > rGoal0 && changed.sum() <= PZERO) {
              return;  // done if no change and strategic value is more than the 0 goal
            }
          }
          // loop increase offer from least strategic to most, amount most to least
          // rount 1: ? still offering too little, increase offers
          // or finally rnd2 decrease requests
          for (int m = 0; m < E.L2SECS; m++) {
// low to high
            ix = stratVars.minIx(m);
            String sT = "T." + term + "." + mm;
            String gcN = sT + " " + ix + "->" + (ix / LSECS > 0. ? "G" : "C") + (int) (ix % E.LSECS);
            didChange = false;
            bid = bids.get(ix);
            //    first = oFirstBid.get(ix);
            emerg = emergOfrs.get(ix);
            avail = availOfrs.get(ix);
            double need1 = needReq.get(ix); // > 0
            double fneed1 = fneedReq.get(ix); // > 0
            isRequest = bid < E.NZERO;
            String sreq = gcN + " " + (starting ? bid < E.NZERO ? "sReq" : bid > E.PZERO ? "sOfr" : "sfuzz" : bid < E.NZERO ? "bReq" : bid > E.PZERO ? "bOfr" : "bfuzz");
            isOffer = bid > PZERO;
            // approach emerg if the first mm round did not gain enough offer
            //only use emerg in emergency
            // on rnd23 limit = emerg or (avail + 2*emerg)/3
            limit = hEmerg ? emerg : rnd23 ? (avail + emerg + emerg) * .3333 : avail;
            //' String sLimit = (limit < bid?"limit":"!limit");
            mf = multF.get(ix); //composit strategic Fraction
            //  starting = mm < 1 ? starting : false; // starting only round 0
            // limit need and fneed to all the other was offering
            double fneed2 = hEmerg ? fneed1 : need1; // > 0.depends on emergency
            // ensure that need/req does not exceed a first offer by other;
            //  need = fneed = first > E.NZERO && term < EM.barterStart - 1 ? fneed2 > first ? first : fneed2 : first < E.PZERO ? 0. : fneed2; // need, fneed > 0
            first = (term < EM.barterStart) ? oFirstBid.get(ix) : fneed2; // update maxReqs and first
            // bFirst if first is less than -fneed2
            Boolean bFirst = !starting && first < E.NZERO && first < -fneed2;
            need = bFirst ? first : -fneed2; // need<0
            // String sNeed = (bid - -need > fuzz? " limit": " bidOK";
            isExcessRequests = excessOffers < E.NZERO;
            isExcessOffers = !isExcessRequests;
            String sXcReq = (isExcessRequests ? " xcReq" : isExcessOffers ? " xcOfr" : " fuzz");
            //String stries = "trys=" + EM.mf(valueChangesTried[ix]) + EM.mf(incTerm);
            //     isMoreOffers = excessOffers + offers*eM.moreOfferBias[pors] > fuzz;
            isNeed = need < E.NZERO; // need exists
            //        highCrit>ical = m < hcntr;
            isAvail = avail > PZERO; //avail exists
            isEmerg = emerg > PZERO;  // emergency value exists
            Boolean needy = -need < bid;  // subtract dif
            doLimit = bid > limit;
            Boolean greedy = bid > E.PZERO;  // sff ,ptr
            Boolean pbid = bid > E.PZERO;
            Boolean nbid = bid < E.NZERO;
            // String sLimit = (needy ? "needy" : bFirst ? "first" : doLimit ? "limit" : greedy ? "greedy" : pbid ? "pbid" : nbid ? "nbid" : "bfuzz");
            notHiCrit = m <= E.L2SECS - hcntr;
            hiCrit = m > E.L2SECS - hcntr;

            doChg = chgCnt < maxCnt;
            isChanged = changed.get(ix) > 0.; // changed this term
            triesOK = valueChangesTried[ix] < incTerm && !isChanged && doChg;
            String sLimit = (bFirst ? "first" : !incValueOK ? "!inc" : !triesOK ? "!tries" : needy ? "needy" : nbid ? "bReq" : doLimit ? "limit" : greedy ? "greedy" : pbid ? "bOfr" : "bfuzz");
            String stries = "ct" + chgCnt + ":" + maxCnt + "tr" + EM.mf(valueChangesTried[ix]) + ":" + EM.mf(incTerm);
            svg = multV.get(ix);
            xou = mf > E.PZERO ? excessOffers / mf : 0.; //convert to bid units
            xof = excessOffers;
            double goodStrategicValue = bid * mf * ofrac;

            // Starting terms process offers starting with least significant
            // if starting and excessRequests try to make offer
            // raise offers rnd23
            // can steal requests
            //   if ((starting || isExcessRequests) && isAvail && triesOK && bid < avail) {
            if (starting && notHiCrit && isAvail && bid < limit) {
              dif = limit - bid;  // avail > 0 make bid <= avail dif>0
              bids.add(ix, dif); // increase bid for offer
              excessOffers += dif * mf * ofrac;  // increase excessOffers
              if (E.tradeInitOut) {
                tradeHist += " T" + term + "oA" + ix + "," + gbsnn(ix) + " B" + EM.mf(bid) + "=>" + EM.mf(bids.get(ix)) + " sums" + EM.mf(bids.plusSum()) + " : " + EM.mf(bids.negSum()) + " sf" + EM.mf(sf1) + "=>" + EM.mf(sf) + " sv" + EM.mf(sv1) + "=>" + EM.mf(sv) + "xof" + EM.mf(xof) + "=>" + EM.mf(excessOffers) + " : ";
              }
              if (!ec.myClearHist && E.debugLogsOut) {
                hist.add(new History(aPre, History.debuggingMinor11, "T" + term + "oA" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, "OT" + term + "oA" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                hist.add(new History(aPre, 5, "T" + term + "oA" + mm + ", " + gbsnn(ix), "A" + EM.mf(avail), "B" + EM.mf(bid), "+" + EM.mf(-dif), "=" + EM.mf(bids.get(ix)), "ofrs" + EM.mf(xof), "add" + EM.mf(-dif * mf), "->" + EM.mf(excessOffers), "vChangesTried", wh(valueChangesTried[ix]), "<<<<<"));
                ec.addOHist(ohist, new History(aPre, 5, "OT" + term + "oA" + mm + ", " + gbsnn(ix), "A" + EM.mf(avail), "B" + EM.mf(bid), "+" + EM.mf(dif), "=" + EM.mf(bids.get(ix)), "ofrs" + EM.mf(xof), "add" + EM.mf(-dif * mf), "->" + EM.mf(excessOffers), "vChangesTried", wh(valueChangesTried[ix]), "<<<<<"));
              }
              changed.add(ix, 1.);
              if (!starting) {
                chgCnt += 1;
                valueChangesTried[ix]++;
              }

            }
            else // list needs that weren't set && isNeed
            if (starting && rnd1 && notHiCrit && isAvail && bid < limit) {
              if (E.tradeInitOut) {
                tradeHist += " T" + term + "oAA" + ix + "," + gbsnn(ix) + " B" + EM.mf(bid) + " need" + EM.mf(need) + " avail" + EM.mf(avail) + " sums" + EM.mf(bids.plusSum()) + " : " + EM.mf(bids.negSum()) + " sf" + EM.mf(sf) + " sv" + EM.mf(sv) + EM.mf(xof) + "=>" + "xof" + EM.mf(xof1) + "=>" + EM.mf(xof) + "=>" + EM.mf(excessOffers) + " : ";
              }
            }
            else // no may move request to zero
            //  raise offers if excessRequests, no steal requests, can get zero
            // ignore triesOk
            if (!starting && isExcessRequests && isAvail && triesOK && notHiCrit && bid < limit) {
              dif = limit - bid; // dif > 0.
              //   dif2 = xou > dif ? dif : xou + .5 * (dif - xou); // may make excessOffers > 0
              bids.add(ix, dif); // increase offer increase excessOffers
              excessOffers += dif2 = dif * mf * ofrac;  // decrease excessOffers
              if (E.tradeInitOut) {
                tradeHist += " T" + term + "oB" + ix + "," + gbsnn(ix) + " B" + EM.mf(bid) + "=>" + EM.mf(bids.get(ix)) + " sums" + EM.mf(bids.plusSum()) + " : " + EM.mf(bids.negSum()) + " sf" + EM.mf(sf1) + "=>" + EM.mf(sf) + " sv" + EM.mf(sv1) + "=>" + EM.mf(sv) + "xof" + EM.mf(xof) + "=>" + EM.mf(excessOffers) + " : ";
              }
              if (!ec.myClearHist && E.debugLogsOut) {
                hist.add(new History(aPre, History.debuggingMinor11, "T" + term + "oB" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, "OT" + term + "oB" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                hist.add(new History(aPre, 5, "T" + term + "oB" + mm + ", " + gbsnn(ix), "L" + EM.mf(limit), "B" + EM.mf(bid), "+" + EM.mf(dif), "=" + EM.mf(bids.get(ix)), "ofrs" + EM.mf(xof), "add" + EM.mf(dif2), "->" + EM.mf(excessOffers), "VCTrid" + wh(valueChangesTried[ix]), "<<<<<"));

                ec.addOHist(ohist, new History(aPre, 5, "OT" + term + "oB" + mm + ", " + gbsnn(ix), "L" + EM.mf(limit), "B" + EM.mf(bid), "+" + EM.mf(dif), "=" + EM.mf(bids.get(ix)), "ofrs" + EM.mf(xof), "add" + EM.mf(dif2), "->" + EM.mf(excessOffers), "VCTrid" + wh(valueChangesTried[ix]), "<<<<<"));
                changed.add(ix, 1.);
                chgCnt += 1;
                valueChangesTried[ix]++;
              }
            } //make less neg requests
            // if excessRequests and triesOK did above
            else if (false && rnd3 && isExcessRequests && !isChanged && doChg && triesOK && isRequest) {
              //  maxDif = bid; // bid<0, maxDif<0-max increase of bid units
              dif = (xou > -bid) ? -bid : xou; //dif >0, xou>0, bid< 0
              bids.add(ix, dif); //incr bid, decr req, 0 bid or excessOffers
              excessOffers += dif * mf * ofrac;  // increase excessOffers
              if (E.tradeInitOut) {
                tradeHist += " T" + term + "oC" + ix + "," + gbsnn(ix) + " B" + EM.mf(bid) + "=>" + EM.mf(bids.get(ix)) + " sums" + EM.mf(bids.plusSum()) + " : " + EM.mf(bids.negSum()) + " sf" + EM.mf(sf1) + "=>" + EM.mf(sf) + " sv" + EM.mf(sv1) + "=>" + EM.mf(sv) + "xof" + EM.mf(xof) + "=>" + EM.mf(excessOffers) + " : ";
              }
              if (!ec.myClearHist && E.debugLogsOut) {
                hist.add(new History(aPre, History.debuggingMinor11, "T" + term + "oC" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, "OT" + term + "oC" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                hist.add(new History(aPre, History.loopMinorConditionals5, "T" + term + "oC" + mm + ", " + gbsnn(ix), "B" + EM.mf(bid), "+" + EM.mf(-dif), "=" + EM.mf(bids.get(ix)), "ofrs" + EM.mf(xof), "add" + EM.mf(dif * mf), "->" + EM.mf(excessOffers), "VCTrid" + wh(valueChangesTried[ix]), "<<<<<"));
                ec.addOHist(ohist, new History(aPre, History.loopMinorConditionals5, "OT" + term + "oC" + mm + ", " + gbsnn(ix), "B" + EM.mf(bid), "+" + EM.mf(-dif), "=" + EM.mf(bids.get(ix)), "ofrs" + EM.mf(xof), "add" + EM.mf(dif * mf), "->" + EM.mf(excessOffers), "VCTrid" + wh(valueChangesTried[ix]++), "<<<<<"));
              }
              changed.add(ix, 1.);
              chgCnt += 1;
              valueChangesTried[ix]++;
              isExcessRequests = excessOffers < NZERO;
              isExcessOffers = excessOffers > PZERO;
            }
            else {
              if (!ec.myClearHist && E.debugLogsOut && false) {
                hist.add(new History(aPre, History.aux2Info, "T" + term + "oD" + mm + ", " + gbsnn(ix), "B" + EM.mf(bid), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
              }
            }

            bid2 = bids.get(ix);  // a previous test may have updated bid
            if (bid2 > emerg) {  // reduce offer too large, to emerg
              dif = bid2 - emerg; // dif > 0 decrease bid
              bids.add(ix, -dif); //move bid back to emerg as limit
              excessOffers += dif2 = -dif * mf;  // get strategic value of bids
              if (E.tradeDebugTerms) {
                tradeHist += " T" + term + "oF" + ix + "," + gbsnn(ix) + " B" + EM.mf(bid) + "=>" + EM.mf(bids.get(ix)) + " sums" + EM.mf(bids.plusSum()) + " : " + EM.mf(bids.negSum()) + " sf" + EM.mf(sf1) + "=>" + EM.mf(sf) + " sv" + EM.mf(sv1) + "=>" + EM.mf(sv) + "xof" + EM.mf(xof) + "=>" + EM.mf(excessOffers) + " : ";
              }
              if (!ec.myClearHist && E.debugLogsOut) {
                hist.add(new History(aPre, History.debuggingMinor11, "T" + term + "oF" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "bid2" + EM.mf(bid2), "lim" + EM.mf(limit), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                ec.addOHist(ohist, new History(aPre, History.debuggingMinor11, "OT" + term + "oF" + mm + ", " + gbsnn(ix), stries, sLimit + ifTag(triesOK, "tries"), ifTag(doChg, "chg") + ifTag(isChanged, "chgd"), "bid" + EM.mf(bid), "ned" + EM.mf(need), "avl" + EM.mf(avail), "xof=" + EM.mf(xof), "xou=" + EM.mf(xou), "mf=" + EM.mf(mf), "svg=" + EM.mf(svg), "<<<<<<<"));
                hist.add(new History(aPre, 5, "T" + term + "oF" + mm + ", " + gbsnn(ix), "L" + EM.mf(limit), "B" + EM.mf(bid), "2B" + EM.mf(bid2), "sub" + EM.mf(-dif), "=" + EM.mf(bids.get(ix)), "xof" + EM.mf(xof), "sub" + EM.mf(dif2), "->" + EM.mf(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
                ec.addOHist(ohist, new History(aPre, 5, "OT" + term + "oF" + mm + ", " + gbsnn(ix), "L" + EM.mf(limit), "B" + EM.mf(bid), "2B" + EM.mf(bid2), "sub" + EM.mf(-dif), "=" + EM.mf(bids.get(ix)), "xof" + EM.mf(xof), "sub" + EM.mf(dif2), "->" + EM.mf(excessOffers), "VCTried" + wh(valueChangesTried[ix]), "<<<<<"));
              }
              changed.add(ix, 1.);
            }
          }// m
          calcStrategicSums(); //reset excessOffers,sf,sv
          bids.sendHist(hist, aPre);
          if (term < EM.barterStart - 5) {
            // if round 2 or 3, no changes and sum &gt; myGoal for trade.
            // if ( sv > sf && changed.sum() <= PZERO) {
            if (sv > rGoal0 && changed.sum() <= PZERO) {
              return;  // done if no change and strategic value is more than the 0 goal
            }
          }
        } // mm

        changes += (int) changed.sum();
        // deal with availCash
        // offer:send less or request more
        // Assets.CashFlow.Trades.enforceStrategicGoal
        // keep min cash of 1000 unless rawhealth < .2 than spend more
        // its an emergency use cash
        //        double cash2 = cash - minCash; // cash we may trade
        xof = excessOffers; // excessOffers < 0 means excess req
        bCash = myOffer.getCash();

        // if bCash < 0, it increases aCash aCash the tradableCash
        double aCash = availCash - bCash; // remaining avail cash
        if (aCash > PZERO && xof < fuzz) {
          double bCash2 = bCash;  // result of change
          double aCash2 = aCash;
          if (aCash + xof < fuzz) { //aCash partial solution
            xof += aCash; // reduce excess requests
            // offer positive cash is cash I offer
            bCash2 = myOffer.setCash(bCash + aCash); // inc offered cash
            aCash2 = 0;   // reduce myCash
            hist.add(new History(aPre, 5, "T" + term + "cA" + ", " + gbsnn(ix) + " excessOfffers=", EM.mf(excessOffers), "=>" + EM.mf(xof), "$=" + wh(pCash - bCash), "=>" + EM.mf(pCash - bCash2), "myOffer.getCash=", EM.mf(bCash), "=>" + EM.mf(bCash2), "rawH=", EM.mf(rawProspects2.min())));
          }
          else { // aCash is enough or more
            aCash2 += excessOffers;  // reduce cash by  excessOffers<0
// add excessOffers to cash we are offering, excessOffers<0
            bCash2 = myOffer.setCash(-excessOffers + bCash);
            xof = 0.;
            hist.add(new History(aPre, 5, "T" + term + "cB" + ", " + gbsnn(ix) + " excessOfffers=", EM.mf(excessOffers), "=>" + EM.mf(xof), "$=" + wh(pCash - bCash), "=>" + EM.mf(cash - bCash2), "myOffer.getCash=", EM.mf(bCash), "=>" + EM.mf(bCash2), "rawH=", EM.mf(rawProspects2.min())));

          }
          changes++;
        } // end reduce cash

        // still need to reduce our offers?
        // reduce cash offered from the other (request here)
        // Assets.CashFlow.Trades.enforceStrategicGoal
        bCash = myOffer.getCash();
        double bCash2 = bCash;
        xof = excessOffers;
        if (bCash < NZERO) {  // can we reduce requested cash
          if (excessOffers > fuzz && excessOffers + bCash > fuzz) { //still need to reduce more than just cash
            //       cash -= myOffer.getCash(); //increase cash
            xof += bCash;
            bCash2 = myOffer.setCash(0.);
            hist.add(new History(aPre, 5, "T" + term + "cC" + ", " + gbsnn(ix) + " excessOffers", EM.mf(excessOffers), "=>", EM.mf(xof), "offerCash", wh(bCash), "=>", wh(bCash2), "cash", wh(pCash), "=>" + wh(pCash + bCash)));
            changes++;
          }
          else if (excessOffers > fuzz && (bCash + excessOffers) < fuzz) {
            // reduce requested cash
            bCash2 = myOffer.setCash(bCash + excessOffers);

            xof = 0.;
            if (History.dl > 4) {
              hist.add(new History(aPre, 5, "T" + term + "cD" + ", " + gbsnn(ix) + " excessOffers", EM.mf(excessOffers), "=>", EM.mf(xof), "offerCash", wh(bCash), "=>", wh(bCash2), "cash", wh(pCash), "=>" + wh(pCash + bCash)));
            }
            changes++;
          }

        } // end increase cash
        calcStrategicSums(); //reset excessOffers,sf,sv
        listDifBid(5, "sl2", entrGoods);
        changed.sendHist(History.loopMinorConditionals5, "#F");
      } // Assets.CashFlow.Trades.enforceStrategicGoal

      /**
       * decide whether to re-raise my request or not
       *
       * @param n a number of requests to be considered
       */
      void renewStrategicRequests(int n
      ) {// Assets.CashFlow.Trades.renewStrategicRequests
        double aTry, aDif, good, need, prevGood, fneed, avail, ig;
        double sv = 0;
        listGoods(9, "renw");
        //   listInitGoods(9, "renw");
        requestsAddedValue = 0; // start count here
        int ix, gCnt, n2 = n / 2;
        // try to renew the n top strategic values
        for (int m = 0; m < E.lsecs && n > 0; m++) {

          ix = stratVars.maxIx(m);
          good = bids.get(ix);

          ig = 0;
          //    ig = initGoods.get(ix);
          gCnt = valueChangesTried[ix];
          prevGood = myxitGoods.get(ix);
          sv = stratVars.get(ix);
          // renew a sector only maxTries times,
          // renew starting with high statistical value
          // hLim < 0  a request
          // good > hLim  good request could be higher
          // valueChangesTried  times for more request (decrease good)
          if (ig < NZERO && good > ig) {  //renew reduced request
            if (valueChangesTried[ix] < eM.maxTries[pors]) {
              bids.set(ix, ig); // reset to original value;
              valueChangesTried[ix]++;
              hist.add(new History(aPre, 5, "b" + term + " " + name, "renew" + m, (ix < E.lsecs ? "cargo" : "guests") + ix % E.lsecs, "good=", EM.mf(good), "to", EM.mf(bids.get(ix)), "changes=" + valueChangesTried[ix], ""));
              changes++;

            }
          }
        }// end loop increasing my high strategic value bids
      } // Assets.CashFlow.Trades.renewStrategicRequests

      /**
       * class variables for calcStrategicGoals defined only at the first 2
       * terms (steps) of the trade barter processs
       */
      int ttype;
      double frac;
      double tmpRand;
      double randFrac;
      double myFavV;
      double myFavFrac; // higher favor makes lower goal
      double oFavV;
      double oFavFrac;
      double sosFrac;
      double gtBias; // reduce goal at each barter (term)
      double termFrac2;
      double termFrac02;
      double termFrac3;
      double termFrac03;
      // amount of reduction of goal per term
      double termFrac;
      double termFrac0;
      double tfrac, thAccept, thReject, thLost, thSum, thFrac;
      int wasTerm = -200;

      /**
       * calculate the clan strategic Goal of received over sent include history
       * of trade success or failure, myFavor, otherFavor, number of offers
       * (term) and possible SOS Also note the number of accepted,rejected and
       * lost trades to modify goal
       *
       * @return strategicGoal = desired totalReceipts/totalSend
       */
      double calcStrategicGoal() {// Assets.CashFlow.Trades.calcStrategicGoal
        //       term = myOffer.getTerm();  // use class term
//        oClan = myOffer.getOClan();
        try {
          aPre = "S$";
          ttype = pors;
          // look at previous goals
          thAccept = eM.getCumulativeClanUnits(EM.TRADESTRATLASTGAVE, pors, clan);
          thReject = eM.getCumulativeClanUnits(EM.TradeRejectedStrategicGoal, pors, clan);
          thLost = eM.getCumulativeClanUnits(EM.TradeLostStrategicGoal, pors, clan);
          thSum = thAccept + thReject + thLost;  // sum of trades
          // history fraction change to goal fail lower goal, accept raise goal of profits
          // thFrac = 1.0 - (thReject+thLost)*.01 + thAccept *.002;
          thFrac = 1.0 + Math.max(-.96, (-thReject * EM.rejectBias[pors][clan] - thLost * EM.lostBias[pors][clan] + thAccept) * EM.historyBias[pors][clan]);
          assert thFrac > E.PPZERO : "Illegal thFrac=" + EM.mf(thFrac) + ", thAccept=" + thAccept + ", thReject=" + thReject + ", thLost=" + thLost;
          //Set special ttype for ship to ship trade
          ttype = oEcon.pors == E.S && pors == E.S ? 2 : pors;
          //       tmpRand = cRand(15 + term, EM.randMult);
          tmpRand = cRand(15, EM.randMult);
          // randFrac = 1/EM.randMax < tmpRand < EM.randMax
          randFrac = tmpRand < 1. / EM.randMax ? 1. / EM.randMax : tmpRand > EM.randMax ? EM.randMax : tmpRand;
          oClan = myOffer.getOClan();
          oPors = myOffer.getOPors();
          boolean oSOS = myOffer.getOtherSOS();
          //reduce FavFrac as favor goes higher
          //set V from my clan favor of other clan
          myFavV = (EM.favMult * EM.fav[clan][pors][oClan] - EM.favMult * 3.);
          myFavFrac = 1. / (1. + myFavV); // higher favor makes lower goal
          //
          // and the others favor of my clan above 3
          oFavV = (EM.favMult * (EM.fav[oClan][oPors][clan] - 3.)) * EM.oClanMult;
          oFavFrac = 1. / (1. + oFavV);
          // reduce goal if you accept an SOS from the other
          //double sosFrac = 1. / (1. + (sos ? (EM.fav[clan][oClan] > 3. && EM.fav[oClan][clan] > 3.) ? EM.sosfrac[pors] : 0. : 0.));
          double sosMore = (oSOS ? (EM.fav[clan][pors][oClan] > 3. && EM.fav[oClan][oPors][clan] > 3.) ? EM.sosfrac[pors] : 0. : 0.);
          // assert sosMore > E.PPZERO : "ILLEGAL sosMore=" + EM.mf(sosMore) + " ," + (oSOS?" oSOS": "!oSOS") + ", myFavV=" + EM.mf(myFavV) + " , myFavFrac=" + EM.mf(myFavFrac) + ", oFavV=" + EM.mf(oFavV) + ", oFavFrac=" + EM.mf(oFavFrac)       ;
          double sosFrac1 = 1. / (1. + sosMore);
          assert sosFrac1 > E.PPZERO : " ILLEGAL sosFrac1=" + EM.mf(sosFrac1) + ", sosMore=" + EM.mf(sosMore) + " ," + (oSOS ? " oSOS" : "!oSOS") + ", myFavV=" + EM.mf(myFavV) + " , myFavFrac=" + EM.mf(myFavFrac) + ", oFavV=" + EM.mf(oFavV) + ", oFavFrac=" + EM.mf(oFavFrac);
          assert myFavFrac > E.PPZERO : " ILLEGAL myFavFrac=" + EM.mf(myFavFrac) + ", sosFrac1=" + EM.mf(sosFrac1) + ", sosMore=" + EM.mf(sosMore) + " ," + (oSOS ? " oSOS" : "!oSOS") + ", myFavV=" + EM.mf(myFavV) + ", oFavV=" + EM.mf(oFavV) + ", oFavFrac=" + EM.mf(oFavFrac);
          assert oFavFrac > E.PPZERO : " ILLEGAL oFavFrac=" + EM.mf(oFavFrac) + ", myFavFrac=" + EM.mf(myFavFrac) + ", sosFrac1=" + EM.mf(sosFrac1) + ", sosMore=" + EM.mf(sosMore) + " ," + (oSOS ? " oSOS" : "!oSOS") + ", myFavV=" + EM.mf(myFavV) + ", oFavV=" + EM.mf(oFavV);
          double sosMin = .80;
          double sosFrac = Math.max(sosMin, sosFrac1);
          // predict facs at various terms
          gtBias = EM.goalTermBias[pors]; // reduce goal at each barter (term)
          termFrac02 = (EM.barterStart + gtBias) / ((gtBias + EM.barterStart - 0.) * (gtBias + EM.barterStart - 0.));
          termFrac03 = (EM.barterStart + gtBias) / ((gtBias + EM.barterStart - 0.));
          termFrac0 = (EM.barterStart) / ((gtBias + EM.barterStart - 0.));
          // amount of reduction of goal per term
          termFrac2 = (EM.barterStart + gtBias) / ((gtBias + EM.barterStart - term) * (gtBias + EM.barterStart - term));
          termFrac3 = (EM.barterStart + gtBias) / ((gtBias + EM.barterStart - term));
          termFrac = (EM.barterStart) / ((gtBias + EM.barterStart - term));//1.=>.5
          tfrac = tradeFracNudge[nudBoth];
          // rGoalFrac = EM.tradeFrac[pors][clan] * randFrac * myFavFrac * oFavFrac * sosFrac;
          rGoalFrac = Math.min(EM.goalMaxBias[pors][0] * tfrac, tfrac * myFavFrac * oFavFrac * sosFrac * thFrac);
          sf = strategicGoal = frac = rGoalFrac * termFrac;
          rGoal0 = rGoalFrac * termFrac0;  // the goal after doing all the barters
          EM.wasHere = "before History clan=" + clan + " oClan=" + oClan + " term=" + term;
          EM.wasHere2 = " (EM.fav[clan][pors][oClan])=" + EM.mf(EM.fav[clan][pors][oClan]);
          assert rGoalFrac > E.PPZERO : " ILLEGAL rGoalFrac =" + EM.mf(rGoalFrac) + "\n" + name + "Y" + EM.year + "CP" + (clan * 2 + pors) + " nudge" + EM.mf(tradeFracNudge[nudV]) + " tFrac=" + EM.mf(tfrac) + " , oFavFrac=" + EM.mf(oFavFrac) + " myFavFrac=" + EM.mf(myFavFrac) + " sosFrac1=" + EM.mf(sosFrac1) + "\n sosMore=" + EM.mf(sosMore) + " ," + (oSOS ? " oSOS" : "!oSOS") + ", myFavV=" + EM.mf(myFavV) + ", oFavV=" + EM.mf(oFavV) + ", thFrac=" + EM.mf(thFrac);
          hist.add(new History(aPre, 5, "T" + term + " goal=" + EM.mf(frac), "rnd" + EM.mf(tmpRand), "rF" + EM.mf(randFrac), "*myF" + EM.mf(EM.fav[clan][pors][oClan]), "=>" + EM.mf(myFavFrac), "*oFc" + EM.mf(EM.fav[oClan][oPors][clan]), "=>" + EM.mf(oFavFrac), "*sosF=" + EM.mf(sosFrac), "trdF =" + EM.mf(tradeFracNudge[nudBoth]), "*rand=" + EM.mf(tfrac), "*termF=" + EM.mf(termFrac), "goal=" + EM.mf(frac), "gtb" + EM.mf(gtBias), "<<<<<<<"));
          hist.add(new History(aPre, 5, "2T" + term, "*rnd=" + EM.mf(tfrac), "*tF=" + EM.mf(termFrac), "goal=", EM.mf(frac), "gtb", EM.mf(gtBias), "<<<<<<<"));
        }
        catch (Exception | Error ex) {
          eM.firstStack = eM.secondStack + "";
          ex.printStackTrace(eM.pw);
          ex.printStackTrace(System.err);
          eM.secondStack = eM.sw.toString();
          System.out.flush();
          System.err.flush();
          EM.newError = true;
          EM.tError = ("----CSGf----ERROR Barter Caught " + ex.toString() + ", cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName());
          System.err.println(EM.tError + EM.andMore());
          EM.newError = true;
          //     ex.printStackTrace(System.err);
          System.out.flush();
          System.err.flush();
          st.setFatalError();
          //throw new WasStopped(eM.tError);
        }
        return strategicGoal = frac;
      }// Assets.CashFlow.Trades.calcStrategicGoal

      /**
       * calculate requ double calcStrategicGoal(int term){
       *
       * }
       * /**
       * Calculate stategic values of resource/cargo and staff/guests. Only
       * cargo and guests are traded, but some resources are moved to cargo, and
       * some staff are moved to guests. Trading of cargo and guests is done in
       * order to promote long term growth in resources and staff for planets,
       * and for ships, trades gain necessary resources and staff, and
       * accumulates tradable guests and cargo that can be traded with other
       * planets. In every case, the strategy is to attempt to optimize long
       * term growth. The application of different random costs each year
       * complicates the issue of calculating strategy. The input to strategy,
       * are the calculated needs, generally needs are seen as resource/cargo
       * and staff/guests,
       *
       * using the mtggNeeds6 (5 year future needs) and using mtgNeeds6 (current
       * needs) calculate results: &\n; fneedReq future need request &\n;
       * needReq current need request availOfrs current offers,&\n; emergOfrs
       * emergency offer (reserve eliminated (c, g will be allocated for
       * this)&\n;
       *
       */
      void calcTrades() {// Assets.CashFlow.Trades.calcTrades

        // Assets.CashFlow.Trades.calcTrades
        // Calc Strategic value resource nominal value 2
        ec.aPre = aPre = "&j";
        histTitles("calcTrades1");
        stratVars = make2(stratVars, "stratValues");
        //stratVars = stratVars.copy();
        //stratVars.getRow(BALANCESIX + RCIX).setAmultV(stratVars.getRow(BALANCESIX + RCIX), EM.nominalWealthPerTrade[pors]);
        // planets should not request staff;
        // stratVars.getRow(BALANCESIX + SGIX).setAmultV(stratVars.getRow(BALANCESIX + SGIX), EM.tradeWealthPerStaff[pors]);
        tEmerg = rawProspects2.curMin() < EM.tradeEmergFrac[pors][clan]; //.1-.3
        //      stratVars = stratVars.mult(stratVars, E.nominalWealthPerResource);
        //   cStratVal = stratVars.getRow(0);
        //    gStratVal = stratVars.getRow(1);
        //  hist.add(new History(aPre, 9, " stratVars", " is stratVars rc mult", EM.mf(EM.nominalWealthPerTrade[pors]), "PG" + EM.mf(EM.tradeWealthPerStaff[pors]), "SG" + EM.mf(EM.tradeWealthPerStaff[pors])));
        bLev = 99;
        stratVars.sendHist(hist, bLev, aPre, "c stratVars", "g stratVars");
        stratVars.sendHist(hist, bLev, aPre, "c stratVars", "g stratVars");

        double aneed = 0., aavail = 0., avail2 = 0., eneed = 0., eeneed = 0.;
        // fneedReq likely need x yrs in future biggest needs
        // fneedTrads also include the current goal needs
        //  fneedReq sets the largest need
        // needReq are the current needs, from the remnants, not goals
        // availOfrs amount available within the trade goal
        //  availOfrs is the amount offered first for trade
        // emergOfrs least need, most amount available in an emergency
        //       maxTrades = new A2Row(cMaxTrade.zero(), gMaxTrade.zero());

        // Planets need to offer ships about .5 of their resource & staff
        // offer the top resources&staff
        // Ships need to make available much of their resources, and
        // swap/repurpose them to make availabe if needed by planet in emergency
        // This code tries to increase the offered values of least strategic values
        int startAvail = (int) EM.startAvail[pors][clan]; //.8
        int criticalNumber = (int) EM.tradeCriticalNumber[pors][clan];
        // double[] availMin = {EM.availMin[pors] * bals.getRow(BALANCESIX + RCIX).ave(), EM.availMin[pors] * bals.getRow(BALANCESIX + SGIX).ave()}; // min avail for rc&sg
        int endRequests = criticalNumber; // 3
        double reservFrac = EM.availFrac[pors][clan];  //.6
        double emergFrac = EM.emergFrac[pors][clan]; //.9
        double emergRequestFrac = 1.0 / emergFrac; //1./.9
        int kk = 3, ix = 0;
        // add -need(surplus) max-kk to bals max-kk
        // use the increment to make offers larger than surplus
        double availIncrement[] = {-EM.tradeReserveIncFrac[pors][clan] * (mtgNeeds6.getRow(0).min() - mtgNeeds6.getRow(0).max(kk)), -EM.tradeReserveIncFrac[pors][clan] * (mtgNeeds6.getRow(1).min() - mtgNeeds6.getRow(1).max(kk))};
        double emergToRegular = reservFrac / emergFrac;//.6/.9  2/3
        // double emergeOfferFrac = emergFrac;
        int alev = History.loopMinorConditionals5;
        String initHist = " Init ";
        A2Row ened = new A2Row(ec, alev, "ened");
        A2Row aval = new A2Row(ec, alev, "aval");
        A2Row aval1 = new A2Row(ec, alev, "aval1");
        A2Row aval2 = new A2Row(ec, alev, "aval2");
        A2Row stratn = new A2Row(ec, alev, "stratn");
        // double sumAvails = 0;
        // balAvail is zeroed
        //A2Row balAvail = new A2Row(History.loopMinorConditionals5, "balAvail");
        //  A2Row balEmerg = new A2Row(History.loopMinorConditionals5, "balEmerg");
        // A2Row stratAvails = new A2Row(History.loopMinorConditionals5, "stratAvail");
        // min(0) is the least strat value, min(13) most strat value
        // max(0) is the most strategic value, most wanted, most Need
        // the least stratValues are best traded
        // max startAvail=>L2SECS covers the least strat, most available
        int n = 0, nn = 0, nmax = stratVars.maxIx(startAvail);
        int rIx = 0, nIx = 0, brIx = 0, bnIx = 0;
        double balsSuperAve = bals.curSum() / 6.; // more than ave
        double gNeeds = -0.0;
        for (int j = 0; j < E.L2SECS; j++) {
          // go highest to lowest straVars show highest need
          ix = n = stratVars.curMaxIx(j); // go from most stategic value to the least
//if(ix < 7){ E.sysmsg("calcTrades %s%d=%7.2f, %s%d=%7.2f,%s%d=%7.2f,%s%d=%7.2f",r.aschar,ix,r.balance.get(ix),c.aschar,ix,c.balance.get(ix),s.aschar,ix,s.balance.get(ix),g.aschar,ix,g.balance.get(ix));}
          // now set up need and fneed for requests
          if (j < endRequests) { // process high stratVars, so make 3 requests
            // set init request - sector balance
            //double eneed1 = ened.set(ix, (gNeeds = mtgNeeds6.get(ix)));
            double eneed1 = mtgNeeds6.get(ix);
            eneed = Math.max(5.0, eneed1 > 0.0 ? eneed1 : .1 * bals.curGet(ix));//min 5.0
            eeneed = eneed * emergRequestFrac; // >0 slightly increase eneed
            aneed = eeneed * emergToRegular; // >0 normal request 2/3 fneed
            String gnc = "," + (ix > E.LSECS ? "g" + (ix - E.LSECS) : "c" + ix);
            needReq.set(ix, aneed);
            fneedReq.set(ix, eeneed);
            availOfrs.set(ix, 0.);
            emergOfrs.set(ix, 0.);
            if (E.tradeInitOut) {
              System.out.println("----CT----calcTrade " + name + " request" + j + gnc + "=" + ix + " mtg=" + EM.mf(eneed1) + ", ee" + EM.mf(eneed) + ", E" + EM.mf(eeneed) + ", R" + EM.mf(aneed) + ", B" + EM.mf(bals.curGet(ix)));
            }
            if (!ec.myClearHist && E.debugLogsOut) {
              hist.add(new History(aPre, History.valuesMinor7, j + gnc + "=" + ix, " fn=" + EM.mf(eeneed), "en=" + EM.mf(eneed), "an=" + EM.mf(aneed), "requestIni", EM.mf(balsSuperAve), "b=" + EM.mf(bals.curGet(ix)), "<<<<<"));
            }
            if (E.tradeInitOut) {
              initHist += "ne" + ix + gnc + j + gnc + "=" + ix + " mtg=" + EM.mf(eneed1) + ", ee" + EM.mf(eneed) + ", E" + EM.mf(eeneed) + ", R" + EM.mf(aneed) + ", B" + EM.mf(bals.curGet(ix));
            }
            // needReq.set(ix, 0.); // don't understand set 0
            // fneedReq.set(ix, 0.);
          }

          // calculate offers except for critical number of only requests
          // limit offers by actual balance, but much higher than mtgNeeds6 available
          if (j >= 0) { // now calculate offers for all
            nn = mtgNeeds6.curMaxIx(j); // try for a lesser balance
            bnIx = nn % E.LSECS;
            brIx = (nn / E.LSECS);
            rIx = n / E.LSECS;
            nIx = n % E.LSECS;
            aavail = Math.min(bals.curGet(ix) * reservFrac, aval.set(ix, (-mtgNeeds6.get(ix) + availIncrement[rIx])));
            avail2 = aavail * emergToRegular;
            // availOfrs: current surpluses we can barter, keeping a reserve
            availOfrs.set(ix, avail2 > PZERO ? avail2 : 0.);
            // emergOfrs are avail without the reserve, use of tEmerg is set
            emergOfrs.set(ix, aavail > PZERO ? aavail : 0.);
            if (!ec.myClearHist) {
              hist.add(new History(aPre, History.valuesMinor7, " j=" + j, "em" + EM.mf(aavail), "av" + EM.mf(avail2), "avInc" + EM.mf(availIncrement[rIx]), "nn=" + nn + ", ix=" + ix, "rIx=" + rIx + ",nIx=" + nIx, "brIx" + brIx + ",bnIx" + bnIx, "mtg=" + EM.mf(-mtgNeeds6.get(rIx, nIx)), "<<<<<<"));
            }
          }
          else {
            availOfrs.set(ix, 0.);
            emergOfrs.set(ix, 0.);
          }
        }

        bLev = History.informationMinor9;
        lev = History.valuesMinor7;
        histTitles("calcTrades2");
        if (!ec.myClearHist) {
          hist.add(new History(aPre, lev, name + " strtAvail" + startAvail, "rqInit=" + balsSuperAve, "aFrac=" + EM.mf(reservFrac), "eFrac=" + EM.mf(emergFrac), "avInc" + EM.mf(availIncrement[0]), EM.mf(availIncrement[1]), "<<<<<<<<<<<<"));
        }
        mtggNeeds6.sendHist2(hist, bLev, aPre, lev, "RCmtggNeeds6", "SGmtggNeeds6");
        mtGNeeds6.sendHist2(hist, bLev, aPre, lev, "RCmtGNeeds6", "SGmtGNeeds6");
        mtgNeeds6.sendHist2(hist, bLev, aPre, lev, "RCmtgNeeds6", "SGmtgNeeds6");
        bals.sendHist2(lev, aPre);
        ened.sendHistcg();
        aval.sendHistcg();
        aval1.sendHistcg();
        aval2.sendHistcg();
        emergOfrs.sendHist(hist, bLev, aPre, lev, "rc emergOfrs", "sg emergOfrs");
        availOfrs.sendHist(hist, bLev, aPre, lev, "rc availOfrs", "sg availOfrs");
        fneedReq.sendHist(hist, bLev, aPre, lev, "rc fneedReq", "sg fneedReq");
        needReq.sendHist(hist, bLev, aPre, lev, "rc needReq", "sg needReq");
      }  // Assets.CashFlow.Trades.calcTrades

      /**
       * innerclass to enable the search for a good planet to do the next trade.
       * trade.Assets.CashFlow.SearchRecord
       */
      class SearchRecord {

        Econ cn; //0=planet or ship,1=primaryShip
        String cnName = "aPlanetOther";
        A2Row goodsFracs = new A2Row(ec); //only one instance of goods, for both cn's
        int goodsCnt = 0; // count of goods found
        // int prevTerm = 60;
        int prevAge = 1;
        double firstTradeWorth = 0.;
        double lastTradeWorth = 0.;
        double curTradeWorth = 0.;
        double averageYearWorthIncrease = 0.0;
        int prevYear = -10;  // year of the offer **
        int firstYear = 0;
        int lastYear = 0;
        double travelCost = 0.; // travel, maintenence cost of travel to this planet
        double[] xyzs = {-40, -41, -42}; //planet location
        double startWorth = 0.;// worth before the trade.
        double endWorth = 0.; // worth after trade
        double strategicValue = 0.; // received/sent;
        int clan = 0;
        NumberFormat dFrac = NumberFormat.getNumberInstance();
        NumberFormat whole = NumberFormat.getNumberInstance();
        NumberFormat dfo = dFrac;
        // EM eM; see outer class

        /**
         * constructor with no variables
         *
         */
        SearchRecord() {
          prevYear = -10;
        } // end class SearchRecord constructor

        SearchRecord(Econ one) {
          cn = one;
          clan = one.clan;
          pors = one.pors;
          prevAge = -12;
          cnName = one.name;
        }

        void updatePlanet(TradeRecord tRec) {
          //   double goodsSum = tRec.goods.plusSum() - tRec.goods.negSum();
          if (prevYear < 0) { // first entry
            prevYear = EM.year;
            firstYear = EM.year;
            //   firstTradeWorth = tRec.endWorth;
          }
          // lastTradeWorth = tRec.endWorth;
          lastYear = EM.year;
          for (int n = 0; n < E.L2SECS; n++) {
            // initial values of 0.0
            //   goodsFracs.add(n, tRec.goods.get(n) / goodsSum);
            goodsCnt++;
          }

        }

        Econ searchForNextPlanet(Econ[] nearPlanetsList, ArrayList<TradeRecord> knownPlanets) {
          Econ ret = eM.planets.get(0);
          int nearPlanetsListLength = nearPlanetsList.length;
          int knownPlanetsLength = knownPlanets.size();
          SearchRecord[] searchNearPlanets = new SearchRecord[nearPlanetsListLength];
          for (int m = 0; m < nearPlanetsListLength; m++) {
            searchNearPlanets[m] = new SearchRecord(nearPlanetsList[m]);
          }

          for (int m = 0; m < knownPlanetsLength; m++) {
            TradeRecord nextKnownPlanet = knownPlanets.get(m);
            Econ knownEcon = knownPlanets.get(m).cn;
            for (int n = 0; n < nearPlanetsListLength; n++) {
              if (knownEcon == nearPlanetsList[n]) {
                searchNearPlanets[m].updatePlanet(knownPlanets.get(m));
              }
            }
          }

          return ret;
        } // end searchForNextPlanet

      } // end trade.Assets.CashFlows.Trades.SearchRecord
//

      Econ selectPlanet(Econ[] wilda) {
        String wildS = "in selectPlanet for:" + name + " names=";
        for (Econ ww : wilda) {
          wildS += " " + ww.name + " distance=" + ec.calcLY(ec, ww);
        }
        int r = (int) Math.floor(Math.random() * 5.3 % wilda.length);
        wildS += " selected:" + wilda[r].name;
        System.out.println(wildS);
        return wilda[r];
      }
    } // end Assets.CashFlow.Trades

    int eeea = 0, eeeb = 0, eeec = 0, eeed = 0, eeee = 0, eeef = 0, eeeg = 0, eeeh = 0, eeei = 0, eeej = 0;

    /**
     * initialize CashFlow the process to deal with cashFlow for the next year
     *
     * @param aas higher level Class Assets
     */
    void aStartCashFlow(Assets aas) { //Assets.CashFlow.initCashFlow
      histTitles("aStartCashFlow");
      if (!didCashFlowStart) {
        ec.age++; // increase age each first aStartCashFlow
      }
      bals.emptyFill();
      //ec.rem.emptyFill)();
      EM.wasHere = "aStartCashFlow... before HSwaps eeea=" + ++eeea;
      prevns = new HSwaps[lPrevns];
      // set balances sub ARows to reference in bals// they should get calculated
      balances = bals.makeA6();
      newKnowledge = bals.getRow(ABalRows.NEWKNOWLEDGEIX); // before SubAssets
      commonKnowledge = bals.getRow(ABalRows.COMMONKNOWLEDGEIX);
      manuals = bals.getRow(ABalRows.MANUALSIX);
      calcPriority(percentDifficulty);// get yprorityYr
      // now initialize knowledge subs from bals references
      EM.wasHere = "CashFlow.aStartCashFlow before for loop eeeb=" + ++eeeb;
      // only initialize if not didCashFlowStart first call
      for (int i = (didCashFlowStart ? E.LSECS : 0); i < E.LSECS; i++) { //first time
        commonKnowledge.set(i, E.knowledgeForPriority * aknowledge * ySectorPriorityYr.get(i) / ySectorPriorityYr.sum() + E.knowledgeByDefault * aknowledge, "set initial knowledge per econ sector");
      }
      //Assets.CashFlow.aStartCashFlow
      knowledge.set(commonKnowledge, newKnowledge);
      cash = wealth;
      term = -4;
      hist.add(new History("&&", 9, "knowledge", knowledge));
      //  System.out.println("5651 mid CashFlow.initCashFlow");
      /* NOW init subAssets */
      lTitle = "initResource";
      histTitles(lTitle);
      boolean worker = false;
      boolean reserve = true;
      resource.initResource(0, worker, cargo, res);
      r = resource;
      //lTitle = "init Cargo";
      //histTitles(lTitle);
      EM.wasHere = "CashFlow.init... before initCargo eeec=" + ++eeec;
      cargo.initCargo(1, reserve, resource, res * eM.initialReserve[pors]);
      c = cargo;
      //lTitle = "init Staff";
      // histTitles(lTitle);
      staff.initStaff(2, worker, guests, colonists);
      s = staff;
      if (!ec.myClearHist) {
        hist.add(new History("&&", 6, " IStaffBal", staff.balance));
        hist.add(new History("&&", 6, " StaffWork", staff.work));
        hist.add(new History("&&", 6, " StaffWorth", staff.worth));
      }
      //lTitle = "init Guests";
      //histTitles(lTitle);
      guests.initGuests(3, reserve, staff, colonists * eM.initialReserve[pors]);
      g = guests;
      dFrac.setMinimumFractionDigits(2);
      whole.setMaximumFractionDigits(0);
      EM.wasHere = "CashFlow.init... after initGuests eeed=" + ++eeed;
      E.myTest(!(r.balance == bals.getRow(BALANCESIX + RIX)), "r.balance.get(0)=%6.2f not equal bals.getRow(BALANCESIX+RIX).get(0)=%6.2f\n", r.balance.get(0), bals.getRow(BALANCESIX + RIX).get(0));
      // set the worth reference
      r.worth = bals.getRow(ABalRows.CURWORTHSIX);//set SubAssets worths instance
      c.worth = bals.getRow(ABalRows.CURWORTHSIX + 1);
      s.worth = bals.getRow(ABalRows.CURWORTHSIX + 2);
      g.worth = bals.getRow(ABalRows.CURWORTHSIX + 3);
      // now set the worth values based on exisiting balances
      r.worth.setAmultV(r.balance, eM.nominalWealthPerResource[pors]);
      c.worth.setAmultV(c.balance, eM.nominalWealthPerResource[pors] * eM.cargoWorthBias[0]);
      s.sumGrades(); // sets s worth
      g.sumGrades(); // sets g worth
      eM.printHere("----ACFg----", ec, " CashFlow.init after worths A" + ec.age);
      rawGrowths.setUseBalances(History.informationMinor9, "rawGrowth", r.rawGrowth, c.rawGrowth, s.rawGrowth, g.rawGrowth);// meaning unknown see calcGrowth
      invMEff.setUseBalances(History.valuesMinor7, "invMEff", r.invMaintEfficiency, c.invMaintEfficiency, s.invMaintEfficiency, g.invMaintEfficiency);
      invGEff.setUseBalances(History.valuesMinor7, "invGEff", r.invGroEfficiency, c.invGroEfficiency, s.invGroEfficiency, g.invGroEfficiency);
      //   calcPriority(percentDifficulty);
      clanRisk = EM.clanRisk[pors][clan];
      doFailed = false;
      // EM.wasHere = "CashFlow.init... before calc Priority eeef=" + ++eeef;
      //  calcPriority(percentDifficulty); // calc this years piority into priorityYr and as.difficulty
      //  EM.wasHere = "CashFlow.init... before calcCatastrophy eeeg=" + ++eeeg;
      if (!didCashFlowStart) { // do yearStart only, use rs the life of the Econ or not
        bals.copy4BtoC(ABalRows.BALANCESIX, ABalRows.STARTYEARBALANCESIX);
        r.worth.setAmultV(r.balance, EM.nominalWealthPerResource[pors]);
        c.worth.setAmultV(c.balance, EM.nominalWealthPerResource[pors] * EM.cargoWorthBias[0]);
        s.sumGrades(); // sets s worth
        g.sumGrades(); // sets g worth
        // in aStartCashFlow set values not references
        bals.set2(ABalRows.STARETYEARWORTHSIX, r.worth);
        bals.set2(ABalRows.STARETYEARWORTHSIX + 1, c.worth);
        bals.set2(ABalRows.STARETYEARWORTHSIX + 2, s.worth);
        bals.set2(ABalRows.STARETYEARWORTHSIX + 3, g.worth);
        if (!assetsInitialized) {
          bals.copy4BtoC(ABalRows.BALANCESIX, ABalRows.INITIALASSETSBALANCESIX);
          bals.set2(ABalRows.INITIALASSETSWORTHSIX, r.worth);
          bals.set2(ABalRows.INITIALASSETSWORTHSIX + 1, c.worth);
          bals.set2(ABalRows.INITIALASSETSWORTHSIX + 2, s.worth);
          bals.set2(ABalRows.INITIALASSETSWORTHSIX + 3, g.worth);
        }
        startAIYear(); // start AI set pre, set nudges
        if (!ec.dead && E.doCalcCatastrophy) {
          calcCatastrophy(0);
        }

        if (!ec.dead) {
          rs = eM.makeClanRS(eM.rs4, eM.mult5Ctbl, ec);//may change yearly
          EM.wasHere = "CashFlow.init... before calcEfficiency loop eeeh" + ++eeeh;
          for (k = 0; k < 4; k++) {
            sys[k].calcEfficiency();
            bals.useRef(ABalRows.INVMEFFICIENCYIX + k, sys[k].invMaintEfficiency);
            sys[k].invMaintEfficiency = invMEfficiency.A[ABalRows.BALANCESIX + k] = bals.getRow(ABalRows.INVMEFFICIENCYIX + k);
            sys[k].invGroEfficiency = invGEfficiency.A[ABalRows.BALANCESIX + k] = bals.getRow(ABalRows.INVGEFFICIENCYIX + k);
            sys[k].calcGrowth();
          }
          didDepreciation = true; // did both calcEfficiency and calcGrowth
          eM.printHere("---ASCF---", ec, "did calcEfficiency, calcGrowth growths.sum4=" + EM.mf(growths.sum4()));
        }// !dead

        rawFertilities2 = new A2Row(ec); //for DoTotalWorths
        EM.wasHere = "CashFlow.init... after calcGrowth loop eeei" + ++eeei;
        //  System.out.println("5631 near end CashFlow.initCashFlow");
        //didStart = (ec.age < 1 ? false : didStart);// probably age = -1 not y0

        if (!didStart) {
          lTitle = "strtCashFlow";
          histTitles(lTitle);
          if (!ec.myClearHist) {
            hist.add(new History(aPre, 5, ec.name + " CFinitYr" + EM.year, "wealth=", EM.mf(wealth), "colonists=", EM.mf(colonists), "res=", EM.mf(res), "Knowledge=", EM.mf(aknowledge), "difficulty=", EM.mf(percentDifficulty)));
          }
          start();  // set startTotalWorth
        }

      }//end !didCashFlowStart
      s.sumGrades();
      g.sumGrades();
      String pStarted = (didStart ? " doCFStart " : " wasStarted ");
      String pInited = (didCashFlowStart ? " initCF  " : " cfWasInited ");
      //    started = traded = growed = endyr = copyy(cur);
      // in Assets.CashFlow.aStartCashFlow
      didCashFlowStart = true;
      didCashFlowInit = true;
      EM.wasHere = " " + ec.name + " CF at end " + pStarted + pInited + " eeej" + ++eeej;
      eM.printHere("----CFSa---", ec, EM.wasHere);
    }  //Assets.CashFlow.aStartCashFlow

    void startAIYear() {
      try {
        // now makel the nudge pointers to install even if nudge=0,0
        // start system test
        int nudx1L = aiNudges.length;
        int nudx2L = aiNudges[0].length;
        //make a copy of preexisting aiNudges for this instance of Assets
        double myAINudges[][] = new double[nudx1L][];
        for (int nudx1 = 0; nudx1 < nudx1L; nudx1++) {
          myAINudges[nudx1] = new double[nudx2L];
          for (int nudx2 = 0; nudx2 < nudx2L; nudx2++) {
            myAINudges[nudx1][nudx2] = aiNudges[nudx1][nudx2];
          }
        }
        int vvat = eM.valAIN[0];// nudge to vv array traderFrac
        int vvbt = eM.valAIN[1];// nudge to vv array

        //use last years value, as in saveAI
        aKey = new String(EM.psClanChars[pors][clan]);
        //aVal[] = EM.myAIlearnings.get(aKey);

        //  eM.setCntAr(aKey, aVal,false,false, true);  //reset the listing in display with previous values
        int pValIxa = E.getAIMuch(EM.psClanChars[pors][clan][E.pNudge0]);// get key x value of setting TradeFrac
        int pValIxb = E.getAIMuch(EM.psClanChars[pors][clan][E.pNudge1]);// key x value of setting forwFTfrac
        // String valNudgea = EM.mf(aiNudges[0][nudV]);
        // String valNudgeb = EM.mf(aiNudges[1][nudV]);
        char ccc[] = {'a', EM.psClanChars[pors][clan][E.pNudge0]};
        int iii = (int) EM.psClanChars[pors][clan][E.pNudge0];
        String ssa = new String(ccc);
        int vva = eM.valAIN[0];// nudge to vv array traderFrac
        int vvb = eM.valAIN[1];// nudge to vv array ffTFracNudge
        //set the nuduge 3 values per current nudV
        double vala = eM.getAIVal(vva, clan, ec, 0), valb = eM.getAIVal(vva, clan, ec, 0);
        String preAKey = new String(EM.psClanChars[pors][clan]);
        Double vvva = E.AILimsC[pValIxa];
        String vvav = EM.mf("TFval", tradeFracNudge[nudBoth]);
        String vvvv = EM.mf("value of Xa", vvva);
        boolean[] doNudges = {y, y};
        //System.err.println("-----SAIs2----StartYearAI vvat=" +vvat +  " prevSliderVala=" + prevSliderVala +" =CC" + EM.psClanChars[pors][clan][E.pNudge0] + "X" + iii + "::"  + ssa +" ="  + pValIxa  + " E.pNudge0=" +  E.pNudge0 + vvvv + vvav +  " prevSliderValb=" + EM.mf(prevSliderValb)  +"  pValIxb="  +  EM.mf(pValIxb)  + " E.pNudge1=" +  E.pNudge1 + " preAKey=" + preAKey + " pre aiNudges=" + EM.mf(myAINudges[0][nudV] ) + ":" + EM.mf(myAINudges[0][nudSet] ) + ":" + EM.mf(myAINudges[0][nudBoth] ) );
        // to save any ai key from alst year via test then
        // one of 3 possible clans cnt>74 ok234, 75>cnt >50 34 ,
        // 50>cnt>25 4
        if ((EM.aicnt[0][0] > 24 && clan == 4 || (EM.aicnt[0][0] > 49 && clan == 3 || (EM.aicnt[0][0] > 74 || clan == 2)))) {
          // now use values from last year to save last the key for last year
          //     void saveAIKey boolean acct, double worth, double offer, double prosM, double prosA,  double score) {//Assets.CashFlow
          saveAIKey(acct, aiWorth, aiOffer, aiProsM, aiProsA, EM.myScore[clan]);//save last year values

          //set the nuduge 3 values per current nudV
          vala = eM.getAIVal(vva, clan, ec, 0);
          valb = eM.getAIVal(vva, clan, ec, 0);
          //   int gca = EM.valI[vva][EM.modeC][0][0];
          ///   int gcb = EM.valI[vvb][EM.modeC][0][0];

          //be sure these are set
          putValueChar(EM.psClanChars[pors][clan], E.ppors, pors, E.AILims123, "EconPors", y);
          putValueChar(EM.psClanChars[pors][clan], E.pLastScP, 4., E.AILims123, "lastaiPos", y);//force valid lim
          putValueChar(EM.psClanChars[pors][clan], E.pclanpors, clan * 2 + pors, E.AILims123, "clan*2+pors", y);
          // only help clan blue get smart start after at least 100 keys so
          // we want to separate planets and ships, but each Assets instance belongs to just one of them
          // charAt(1) E.ppors if set is 'f' or 'g'
          aKey = new String(EM.psClanChars[pors][clan]);

          // to set any ai nudge via test then
          //require size, set char1, and one of 3 possible clans cnt>74 ok234, 75>cnt >50 34 ,
          // 50>cnt>25 4
          if (EM.myAIlearnings.size() > 100 && aKey.charAt(1) != 'a' && (EM.aicnt[0][0] > 24 && clan == 4 || (EM.aicnt[0][0] > 49 && clan == 3 || (EM.aicnt[0][0] > 74 || clan == 2)))) {
            // use aKey and aVal left by saveAIKey
            // double val1 = valD[vv][gameAddrC][pors][klan] = sliderToVal(slider, valD[vv][gameLim][pors][vLowLim], valD[vv][gameLim][pors][vHighLim]);
            // assume the drs arrays are set, use those settings to derive new nudges
            //-----------------EM.tradeFrac[pors][clan]-------------------
            double prevNudv = tradeFracNudge[nudV];
            double prevVal = tradeFracNudge[nudBoth];
            double curVal = eM.getAIVal(vva, clan, ec, 0); // sum of setting and nudge
            double prevTF = tradeFracNudge[nudSet] = EM.tradeFrac[pors][clan]; // settings P .41, S.22
            double tooSmall = -tradeFracNudge[tradeFracNudge.length - 1] * 1.3;
            String prevTradeFracss[] = {"prevTradeFracp", "prevTradeFracs"};
            // get the best value, not a slider value
            double newTF1 = eM.tradeFracSetCntDr(aKey, aVal, prevTradeFracss, false, false, y);
            boolean notNew = newTF1 < tooSmall;
            double newTF = newTF1 < tooSmall ? prevTF : newTF1;
            tradeFracNudge[nudV] = newTF - prevTF;
            tradeFracNudge[nudBoth] = tradeFracNudge[nudV] + tradeFracNudge[nudSet];
            doNudges[0] = notNew;// prevent random reset of nudge 0  if not notNew
            String sss = name + "Y" + EM.year + "C" + clan + (notNew ? "no Change" + EM.mf2("prevNudv", prevNudv) : EM.mf2("prevNudv", prevNudv) + EM.mf2("newTF", newTF) + EM.mf2("newNudv", tradeFracNudge[nudV]) + EM.mf2("setTradeFrac", tradeFracNudge[nudSet]) + "=>" + EM.mf2("nudBoth", tradeFracNudge[nudBoth]));
            if (E.debugAIOut) {
              System.out.println("-----SAIy0----" + sss);
            }
            synchronized (A6Row.lock) {
              Econ.keyList[Econ.ixKeyList = ++Econ.ixKeyList < Econ.lKeyList ? Econ.ixKeyList : 0] = sss;
            }
            assert newTF > 0. : "Error newTF value negative=" + EM.mf2("newTF", newTF) + EM.mf2("prevNudv", prevNudv);
            //-----------------------   EM.futureFundTransferFrac[pors][clan]---------
            prevNudv = ffTFracNudge[nudV];
            prevVal = ffTFracNudge[nudBoth];
            double prevFFT = ffTFracNudge[nudSet] = EM.futureFundTransferFrac[pors][clan];
            tooSmall = -ffTFracNudge[ffTFracNudge.length - 1] * 1.3;
            curVal = eM.getAIVal(vva, clan, ec, 1); // sum of setting and nudge

            double newFFT1 = eM.fFTransferFracSetCntDr(aKey, aVal, "prevaFFTransferFrac", false, false, y);
            notNew = newFFT1 < tooSmall;
            double newFFT = newFFT1 < tooSmall ? prevFFT : newFFT1;
            ffTFracNudge[nudV] = newFFT - prevFFT;
            ffTFracNudge[nudBoth] = ffTFracNudge[nudV] + ffTFracNudge[nudSet];
            doNudges[1] = notNew; // // prevent random reset of nudge 1  if not notNew
            sss = name + "Y" + EM.year + "C" + clan + (notNew ? "no Change " + EM.mf2("prevNudv", prevNudv) : EM.mf2("prevNudv", prevNudv) + EM.mf2("set EM.futureFundTransferFrac", EM.futureFundTransferFrac[pors][clan]) + "=>" + EM.mf2("nudBoth", tradeFracNudge[nudBoth]));
            synchronized (A6Row.lock) {
              Econ.keyList[Econ.ixKeyList = ++Econ.ixKeyList < Econ.lKeyList ? Econ.ixKeyList : 0] = sss;
            }
            if (E.debugAIOut) {
              System.out.println("-----SAIy1----" + sss);
            }
            assert newFFT > -2.0 : "Error newFFT value negative=" + EM.mf2("newFFT", newFFT);
          }//end setting ai nudge

          // now possibly introduce random nudges
          Random rand = new Random();
          rIn = -7;
          ranInt = rand.nextInt(7);// 0-6
          int ran2 = ranInt % aiNudges.length; // 0-1
          int rInMax = 5; // number of nudge values
          rInMax = aiNudges[0].length - nudStrt; // currently 5
          rIn = rand.nextInt(rInMax * 2);// select sign and value of nudges 5
          double ranMult = .7 + .6 * Math.random();// .7 - 1.3
          double aiV = -7.7;
          // only nudge 2 out of 5 econs per year

          if (ranInt >= 0 && ranInt < aiNudges.length) {

            //ranInt==1 rIn==5: aiNudges[ranInt][pors] = -aiNudges[ranInt][2.5+1.5=4]
            //ranInt==1 rIn==4: aiNudges[rnInt][pors] = -aiNudges[ranInt][
            // rIn over 2 is negative
            // clan and pors defined in Assets
            // aiV = aiNudges[ranInt][(int) ((rIn % 3) + 2)];
            //randomize the applied values
            if (doNudges[ran2]) {
              aiNudges[ran2][nudV] = aiV = ranMult * (rIn < rInMax ? aiNudges[ran2][rIn + nudStrt] : -aiNudges[ran2][rIn - rInMax + nudStrt]);
              doNudges[ran2] = false;
            }
            //int sliderVal = eM.getAIVal(vv, pors, clan,ec,ranInt);
            //   res[ixa = ix + E.bValsStart] = E.getAISetChar(sliderVal);
          }
        }
        //zero all unset aiNudges
        for (int ranInta = 0; ranInta < aiNudges.length; ranInta++) {
          if (doNudges[ranInta]) {
            for (int rIna = 0; rIna < 2; rIna++) {
              aiNudges[ranInta][rIna] = 0.0;
            }
            doNudges[ranInta] = false;
          }
        }

        // now install the nudge pointers even if nudge=0,0
        // vva = eM.valAIN[0];// nudge to vv array traderFrac
        // vvb = eM.valAIN[1];// nudge to vv array
        eM.getAIVal(vva, clan, ec, 0);
        eM.getAIVal(vvb, clan, ec, 0);

        //use this years value, as in saveAI
        putValueChar(EM.psClanChars[pors][clan], E.pNudge0, tradeFracNudge[nudBoth], E.AILims1, "Nudged value0", y);
        putValueChar(EM.psClanChars[pors][clan], E.pNudge1, ffTFracNudge[nudBoth], E.AILims1, "Nudged value1", y);

        pValIxa = E.getAIMuch(EM.psClanChars[pors][clan][E.pNudge0]);// get key x value of setting TradeFrac
        pValIxb = E.getAIMuch(EM.psClanChars[pors][clan][E.pNudge1]);// key x value of setting forwFTfrac

        String pValIxaVal = EM.mf(E.AILims1[pValIxa]);
        String pValIxbVal = EM.mf(E.AILims1[pValIxb]);
        //use last years value, as in saveAI done before
        //   putValueChar(EM.psClanChars[pors][clan], E.pNudge0, prevSliderVala, E.AILimsC, "Nudged value0", y);
        //  putValueChar(EM.psClanChars[pors][clan], E.pNudge1, prevSliderValb, E.AILimsC, "Nudged value1", y);
        if (E.debugAIOut2) {
          System.err.println("-----SAIs3----StartYearAI after puts ranInt" + ranInt + " " + name + "Y" + EM.year + " rIn" + rIn + ":" + ":" + " A=" + EM.mf2(" aiNudges[0][nudV]", aiNudges[0][nudV]) + EM.mf2(" aiNudges[0][nudSet]", aiNudges[0][nudSet]) + EM.mf2(" aiNudges[0][nudBoth]", aiNudges[0][nudBoth]) + EM.mf2(" E.AILims1[pValIxa]]", E.AILims1[pValIxa]) + EM.mf2(" aiNudges[1][nudV]", aiNudges[1][nudV]) + EM.mf2(" aiNudges[1][nudSet]", aiNudges[1][nudSet]) + EM.mf2(" aiNudges[1][nudBoth]", aiNudges[1][nudBoth]) + EM.mf2(" E.AILims1[pValIxb])", E.AILims1[pValIxb]));
        }
        //EM.psClanChars[pors][clan][E.pNudge0 + nX] = E.getAISetChar(sliderVal);
      }
      catch (Exception | Error ex) {
        eM.firstStack = eM.secondStack + "";
        ex.printStackTrace(eM.pw);
        ex.printStackTrace(System.err);
        eM.secondStack = eM.sw.toString();
        System.out.flush();
        System.err.flush();
        EM.newError = true;
        EM.tError = ("----SYAf----ERROR startAIYear Caught " + ex.toString() + ", cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName());
        System.err.println(EM.tError + eM.andMore());
        //     ex.printStackTrace(System.err);
        st.setFatalError();
        // throw new WasStopped(eM.tError);
      }
    }//startAIYear

    /**
     * return the current value of loop n
     *
     * @return
     */
    int getN() {
      return n;
    }

    /**
     * return the last 5 values of Swap n from each time that getNSavd was
     * called return array of -5 if n was not set by swaps
     *
     * @return the nSavd array
     */
    int[] getNSavd() {
      for (int ix = nSavd.length - 2; ix > 0; ix--) {
        nSavd[ix + 1] = nSavd[ix];
      }
      nSavd[0] = n > -1 ? n : -5;
      return nSavd;
    }

    /**
     * return a pointer to the calling subasset
     *
     * @param sa a reference to a subasset
     * @return a reference to the same subasset I think
     */
    SubAsset copyIf(CashFlow.SubAsset sa) { // only
      if (sa == null) {
        return null;
      }
      return sys[sa.sIx];
    }

    /**
     * get the Assets equivalent of the score for each clan
     *
     * @param offer The last offer given of the previous year 0 if none
     * @param worth The final worth of the previous year
     * @return
     */
    double getScore(double offer, double worth) {
      double score = (offer) * EM.wGiven[0][0] + worth * EM.wLiveWorthScore[0][0];
      return score;
    }

    //  double addCash(double cash) {
    //     return this.cash += cash;
    //  }
    /**
     * Calculate composite sector priority with initial priority for an
     * economy+user priority which can be changed each year with that compute
     * the difficulty, this will be used to calculate efficiency
     *
     * @see calculate ySectorPriorityYr the composite if Econ and user
     * priorities
     */
    void calcPriority(double percentDifficulty) {  //CashFlow
      ARow uAdjPri = new ARow(ec);
      ARow yPritmp = new ARow(ec);
      for (int i = 0; i < E.lsecs; i++) {
        uAdjPri.set(i, eM.userPriorityAdjustment[i][pors][clan] * E.priorityAdjustmentMultiplierFrac[pors]);
        yPritmp.set(i, (aSectorPriority.get(i) + E.initPriorityBias[pors] + uAdjPri.get(i)));
      }
      String a11a = "----ACCp---- sectorPriority=";
      for (int i = 0; i < E.lsecs; i++) {
        //adjust each value by the sectoryPriory sum/ yPritmp sum
        // gets a sum of values add up to 100
        ySectorPriorityYr.set(i, (yPritmp.get(i)) * (yPritmp.sum() < PZERO ? 0. : aSectorPriority.sum() / yPritmp.sum()), "priority recalculated each year");
        a11a += EM.mf(aSectorPriority.get(i)) + ", " + EM.mf(ySectorPriorityYr.get(i)) + ": ";
      }
      if (E.debugPriorityOut) {
        System.out.println(a11a);
      }
      hist.add(new History("&&", 9, "uAdjPri", uAdjPri));
      hist.add(new History("&&", 9, "yPritmp", yPritmp));
      hist.add(new History("&&", 9, "asectorPriority", aSectorPriority));
      hist.add(new History("&&", 9, "apriorityYr", ySectorPriorityYr));
      //     ySectorPriorityYr.setReValuedA(10., 18., ySectorPriorityYr);//  values 10 - 18
//      hist.add(new History("&&", 9, "re priorityYr", ySectorPriorityYr));
      for (int i = 0; i < E.lsecs; i++) {
        // note that the following code is to increase ydifficulty as priority decreases
        // higher priority means more favored
        // higher difficultyByPriorityMult[ means higher costs  divided by priority
        // increase as difficulty increases
        // sector difficulty is a function of economy difficulty and priority
        ydifficulty.set(i, percentDifficulty * (eM.difficultyByPriorityMin[pors] + (ySectorPriorityYr.get(i) < PZERO ? eM.difficultyByPriorityMult[pors] * .05 : (eM.difficultyByPriorityMult[pors]) / ySectorPriorityYr.get(i))), "difficulty for each sector");
        // or just ignore the ySectorPriorityYr, just use percentDifficulty
        //  ydifficulty.set(i, percentDifficulty, "difficulty for each sector");
      }
      hist.add(new History("&&", 9, "userAdjPri", uAdjPri));
      hist.add(new History("&&", 9, "asectorPriority", aSectorPriority));
      hist.add(new History("&&", 9, "apriorityYr", ySectorPriorityYr));
      hist.add(new History("&&", 9, "difficulty=" + EM.mf(percentDifficulty), ydifficulty));
      if (History.dl > 10) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
        StackTraceElement aa = Thread.currentThread().getStackTrace()[2];
        StackTraceElement ab = Thread.currentThread().getStackTrace()[3];
        hist.add(
                new History(11, ">>>", a0.getMethodName(), "at", a0.getFileName(),
                            wh(a0.getLineNumber()), "from=", aa.getFileName(), wh(aa.getLineNumber()),
                            "ffrom", ab.getFileName(), wh(ab.getLineNumber())));
      }
      hist.add(new History(20, "difficulty", "0Life", "1Struct", "2Energy", "3Propel", "4Defense", "5Gov", "6Col", "Min", "Sum", "Ave"));
      //     hist.add(new History("&&", 9, "userPriority", userPriority));

    }

    /**
     * calculate whether to have a catastrophy, If a catastrophy is indicated,
     * than for planets there would be one for both economies a catastrophy does
     * the following  <ol>
     * <li>reduce a resource sector balance by a random percent statistic
     * rCatCosts</li>
     * <li>reduce a staff sector balance by a random percent statistic
     * sCatCosts</li>
     * <li>reduce a resource sector balance by a random percent statistic
     * rCatCosts  </li>
     * <li>set a years of bonus for a resource sector statistic catBonusY </li>
     * <li>set a value of bonus (reduces each year) for the resource sector
     * reduce by .04 * (value + initial growth value) each year in
     * didDepreciation statistic catBonusVal </li>
     * <li>set a years of bonus for a rstaff sector statistic catBonusY </li>
     * <li>set a value of bonus (reduces each year) for the staff sector
     * statistic sCatBonusVal </li>
     * <li>reduce the amount of deterioration for a resource , prevent a
     * negative deterioration statistic   </li>
     * <li>reduce the amount of deterioration of the above resource by a percent
     * statistic s </li>
     * <li>  </li>
     * <li>  </li>
     * <li>  </li>
     * </ol>
     */
    void calcCatastrophy(int n) {
      double t1 = 0., t2 = 0., cc = 1.;
      if (didCatastrophy) {
        return; // only 1 time per year
      }
      if (eM.randFrac[pors][0] > PZERO && ec.age > 1 && n < 2 && ((t1 = cRand(31)) < (t2 = eM.userCatastrophyFreq[pors][clan] * (cc = eM.gameUserCatastrophyMult[pors][0]) * cRand(34))) && t1 > 0.) {
        // cc will be both user and game catastrophy values
        // random 0.0 to 1.0,%5lt; asum of 0-.65 + 0-.65
        // if ((t2 = eM.gameUserCatastrophyMult[pors][0]) == 0.0) {
        // return; // skip if 0
        //}

        if ((t1 = Math.random()) < (cc = eM.userCatastrophyFreq[pors][clan] * (t2 = eM.gameUserCatastrophyMult[pors][0]))) { //.25 *2 = .5
          eM.printHere("----CATe1----", ec, EM.mf("enter Catastrophy t1", t1) + EM.mf(" t2", t2));

          didCatastrophy = true; // only if actually done
          int r1 = new Random().nextInt(7);
          int r2 = new Random().nextInt(7);
          int s1 = new Random().nextInt(7);
          int s2 = new Random().nextInt(7);
          int r3 = new Random().nextInt(7);
          int r4 = new Random().nextInt(7);
          int s3 = new Random().nextInt(7);
          int s4 = new Random().nextInt(7);
          int r5 = new Random().nextInt(7);
          int s5 = new Random().nextInt(7);
          //get fraction of reduction rand(0-1.) cc(0-1.3) cUR(0-1.) = (0-1.3) more small
          double reduce1 = Math.min(.65, (cRand(14) * cc * EM.catastrophyUnitReduction[pors][0] * 1.5)); //0-1.95  min.65
          double reduce2 = Math.min(.65, (cRand(15) * cc * EM.catastrophyUnitReduction[pors][0] * .6));
          double reduce3 = Math.min(.65, (cRand(16) * cc * EM.catastrophyUnitReduction[pors][0] * 1.5));
          double reduce4 = Math.min(.65, (cRand(17) * cc * EM.catastrophyUnitReduction[pors][0] * .6));
          int bonusYrs1 = (int) (cRand(16) * cc * EM.catastrophyBonusYears[pors][0] * 2);
          int bonusYrs2 = (int) (cRand(17) * cc * EM.catastrophyBonusYears[pors][0] * .8);
          int bonusYrs3 = (int) (cRand(18) * cc * EM.catastrophyBonusYears[pors][0] * 2);
          int bonusYrs4 = (int) (cRand(19) * cc * EM.catastrophyBonusYears[pors][0] * .8);
          double bonusVal1 = cRand(20) * cc * EM.catastrophyBonusGrowthValue[pors][0] * 1.9;
          double bonusVal2 = cRand(21) * cc * EM.catastrophyBonusGrowthValue[pors][0] * .5;
          double bonusVal3 = cRand(22) * cc * EM.catastrophyBonusGrowthValue[pors][0] * 1.9;
          double bonusVal4 = cRand(23) * cc * EM.catastrophyBonusGrowthValue[pors][0] * .5;
          double deteriorationReduce1 = cRand(26) * cc * EM.growthDepreciation[2][pors] * 2.3;//S
          double deteriorationReduce2 = cRand(24) * cc * EM.growthDepreciation[0][pors] * 1.1;//R
          double deteriorationReduce3 = cRand(25) * cc * EM.growthDepreciation[0][pors] * .7;//R
          double deteriorationReduce4 = cRand(27) * cc * EM.growthDepreciation[2][pors] * .3;//S
          int bonusX1 = new Random().nextInt(7);// choose  sector
          int bonusX2 = new Random().nextInt(7);
          int bonusX3 = new Random().nextInt(7);
          int bonusX4 = new Random().nextInt(7);
          double bonusManuals1 = cRand(32) * cc * EM.catastrophyManualsMultSumKnowledge[pors][0] * 2.6 * knowledge.sum();
          double bonusNewKnowledge1 = cRand(45) * cc * EM.catastrophyManualsMultSumKnowledge[pors][0] * 2.7 * knowledge.sum();
          double bonusManuals2 = cRand(35) * cc * EM.catastrophyManualsMultSumKnowledge[pors][0] * 2.6 * knowledge.sum();
          double bonusNewKnowledge2 = cRand(44) * cc * EM.catastrophyManualsMultSumKnowledge[pors][0] * 2.7 * knowledge.sum();

          /*
  static final int CRISISRESREDUCEPERCENT = ++e4; //
  static final int CRISISSTAFFREDUCEPERCENT = ++e4; //
  static final int CRISISSTAFFGROWTHPERCENTINCR = ++e4;
  static final int CRISISSTAFFGROWTHYEARSINCR = ++e4;
  static final int CRISISRESGROWTHPERCENTINCR = ++e4;
  static final int CRISISRESGROWTHYEARSINCR = ++e4;
  static final int CRISISMANUALSPERCENTINCR = ++e4;
        static final int CRISISRESREDUCESURPLUSPERCENT = ++e4;
        static final int CRISISSTAFFREDUCESURPLUSPERCENT = ++e4;
           */
          double rc1, sc2, rc3, rreduced, sreduced;
          yearCatastrophy = EM.year; // flag entered
          //ABalRows.CUMULATIVEUNITDEPRECIATIONIX
          // ARow newRUnitDepreciation = bals.getRow(ABalRows.NEWDEPRECIATIONIX + 0);
          //ARow newSUnitDepreciation = bals.getRow(ABalRows.NEWDEPRECIATIONIX + 1);

          r.cumSectorBonus.add(r1, deteriorationReduce3);  // do reducing deterioration
          r.repreciation.add(r1, deteriorationReduce3);
          r.cumSectorBonus.add(r2, deteriorationReduce2); // help those hit
          r.repreciation.add(r2, deteriorationReduce2);
          s.cumSectorBonus.add(s1, deteriorationReduce1);
          s.repreciation.add(s1, deteriorationReduce1);
          // do costs and report

          r.cost3((rc1 = balances.get(2, r1) * reduce1), r1, 0);  // apply costs to P and S
          setStat(EM.CATASTCOST, pors, clan, rc1, 0);
          s.cost3((sc2 = balances.get(4, s1) * reduce2), s1, 0);
          setStat(EM.CATASTCOST, pors, clan, sc2, 0);
          r.cost3((rc3 = balances.get(2, r2) * reduce3), r2, 0);
          setStat(EM.CATASTCOST, pors, clan, rc3, 1); //only 1 count

          r.bonusYears.add(bonusX1, bonusYrs1);             // both P & S
          r.bonusYears.add(bonusX2, bonusYrs2);
          setStat("catBonusY", pors, clan, bonusYrs1 + bonusYrs2, 0);
          r.bonusUnitGrowth.add(bonusX1, bonusVal1);
          r.bonusUnitGrowth.add(bonusX2, bonusVal2);
          eM.printHere("----CATr2---", ec, "Catastrophy  rsector" + bonusX2 + "=" + EM.mf("b unit1", bonusVal2) + " sec" + bonusX1 + "=" + EM.mf("b unit2", bonusVal2));
          setStat("catBonusVal", pors, clan, bonusVal1 + bonusVal2, 0);
          s.bonusYears.add(bonusX3, bonusYrs3);
          setStat("catBonusY", pors, clan, bonusYrs3, 1);
          s.bonusUnitGrowth.add(bonusX3, bonusVal3);
          setStat("catBonusVal", pors, clan, bonusVal3, 1);
          catEffRGBen += bonusVal1 + bonusVal2;
          catEffSGBen += bonusVal3;
          setStat(EM.NEWREPRECIATION, pors, clan, deteriorationReduce3 + deteriorationReduce2, 0);//
          setStat(EM.NEWREPRECIATION, pors, clan, deteriorationReduce1, 1);//
          bals.set(ABalRows.NEWREPRECIATIONIX, r3, deteriorationReduce1);
          bals.set(ABalRows.NEWREPRECIATIONIX + 2, s3, deteriorationReduce2);
          bals.set(ABalRows.NEWREPRECIATIONIX, r4, deteriorationReduce3);
          catEffSDepreciationBen += deteriorationReduce1;
          //setStat(eM.CRISISRESREDUCEPERCENT, pors, clan, rd1, 1);
          // setStat(eM.CRISISRESDEPRECIATIONBONUSPERCENT, pors, clan, nd1, 1);
          if (pors == E.P) {

          }
          else {  // ships
            manuals.add(bonusX2, bonusManuals1);  // Adds for sectorX bonusMan
            setStat("catBonusManuals", pors, clan, bonusManuals1, 0);
            newKnowledge.add(bonusX1, bonusNewKnowledge1);
            setStat("catBonusNewKnowledge", pors, clan, bonusNewKnowledge1, 0);
            manuals.add(bonusX1, bonusManuals2);  // Adds into value of trades
            setStat("catBonusManuals", pors, clan, bonusManuals2, 1);
            newKnowledge.add(bonusX3, bonusNewKnowledge2);
            setStat("catBonusNewKnowledge", pors, clan, bonusNewKnowledge2, 1);
            catEffManualsBen += bonusManuals1 + bonusManuals2;
            catEffKnowBen += bonusNewKnowledge1 + bonusNewKnowledge2;
            eM.printHere("----CATc3---", ec, "Catastrophy Ship sector" + bonusX2 + "=" + EM.mf("manuals", bonusManuals1) + " sec" + bonusX1 + "=" + EM.mf("manuals", bonusManuals2));
          }

        }
        sumCumulativeUnitBonus = r.cumSectorBonus.sum() + s.cumSectorBonus.sum();
        sumBonusUnitGrowth = r.bonusUnitGrowth.sum() + s.bonusUnitGrowth.sum();
        //  sumDepreciation = r.depreciation.sum() + s.depreciation.sum();
      }
    }// calcCatastrophy

    /**
     * calculate and process any need to move reserves into the
     * eM.clanFutureFunds
     *
     * @note n The number of the swap loop;
     * @return true = something set to future, false did nothing, continue swap
     */
    boolean calcFutureFund() {
      boolean doing = true, xcess = false, didXcessFF = false;
      int nReDo = 3;
      double mDif = .5;

      /*
      static final int[] forwardFundSwapNs = {0,1,2,3,4,5,11,12,15,16,20,25,30,31,35,36,37};
      to E.ffSwapNs
       */
      Double remainingFF = 0., excessForFF = 0., ff1 = 0., ff2 = 0., frac1 = 0., frac2 = 0.;
      Double val = 0., dif1 = 0., val1 = 0., val2 = 0., tmp1 = 0., max1 = 0., tmp3 = 0.;//REmergFF
      int sourceIx = 0, ixWSrc = 0, ixRSrc = 0;
      double totWorth = 0;

      boolean emergTrans = false;  // are we in emergency
      double rawProsp = 0.;
      double emergMult = emergTrans ? EM.emergFundFrac[pors][clan] : 1.0;
      double reservMult = emergTrans ? E.emergReserve[ixWRSrc][pors][clan] : E.regReserve[ixWRSrc][pors][clan];
      // calculate max transfer to futureFund larget sector .8*bal .9*avail, frac*totWorth
      double maxF0 = 0.;
      double maxFa = 0.;
      double maxF1 = 0.; // avails limited
      double maxF2 = 0.; //totWorth val
      double maxF3 = 0.; // min available units
      double maxF4 = 0.; //at end after test emergency reserve
      double maxFutureTrans = 0.; //
      double minFutureTrans = 0.;

      // finish processes before leaving this loop
      int mMax = 4, m = 0; // max loops internal vars
      // do we need more future fund or is it full
      double ffFull = EM.getInitialEconWorth(pors, clan) * 5.0;
      for (m = 0; m < mMax && (doing || xcess) && !eM.dfe() && ffFull > eM.clanFutureFunds[clan] + yearsFutureFund; m++) {
        xcess = doing = false; // false unless section does doing
        // only continue sizeFF the yearly dues process
        if (resTypeName.contains("izeFF")) {
          resTypeName = "anot";
        }
        if (resTypeName.contains("mergFF")) {
          resTypeName = "anot";
        }
        DoTotalWorths ffw = new DoTotalWorths(); // worths change because of calcYearCosts
        totWorth = ffw.getTotWorth();
        //minFutureTrans = totWorth*(.005 + .003*m);
        minFutureTrans = totWorth * .0002 * (1. + .2 * m + .03 * n);

        sourceIx = mtgAvails6.curMaxIx(0);  // pick largest sector to give
        ixWRSrc = sourceIx / E.LSECS; //0,1
        ixWSrc = ixWRSrc * 2 + 2;  // /working source
        ixRSrc = ixWRSrc * 2 + 3;  // reserve source
        srcIx = sourceIx % E.LSECS;// 0-6
        emergTrans = rawProspects2.min() < -.00001;  // are we in emergency
        rawProsp = 0.;
        emergMult = emergTrans ? EM.emergFundFrac[pors][clan] : 1.0;
        reservMult = emergTrans ? E.emergReserve[ixWRSrc][pors][clan] : E.regReserve[ixWRSrc][pors][clan];
        // calculate max transfer to futureFund larget sector .8*bal .9*avail, frac*totWorth
        maxF0 = bals.get(sourceIx);
        maxFa = mtgAvails6.get(sourceIx);
        maxF1 = maxF0 * (0.89 - reservMult); // avails limited
        maxF2 = totWorth * EM.futureFundFrac[pors][clan] * (0.89 - reservMult); //totWorth val
        maxF3 = Math.min(maxF1, maxF2); // min available units
        maxF4 = 0.; //at end after test emergency reserve
        maxFutureTrans = Math.min(bals.get(sourceIx) * .8, maxF3); //
        //  / (eM.worthBias[ixWRSrc * 2][0] * cur.sys[ixWRSrc * 2].unitWorth.get(srcIx))));  // units value
        // finish a previously started dues operation
        if (remainingFF > E.PZERO) {
          val = remainingFF;
          // select the largest for each move to forward fnd

          val = Math.min(remainingFF, maxFutureTrans);
          assert val > E.NZERO : "Assets.CashFlow.calFutureFund Negative val=" + EM.mf(bals.get(sourceIx));
          // E.myTest(val < NZERO, "Negative val=%7.4f, bals=%9.4f", val, bals.get(ixWRSrc, srcIx));

          if (val > E.PZERO) {
            xcess = true;
            remainingFF -= val;
            resTypeName = (emergTrans ? ixWRSrc > 0 ? "SizeFFEs" : "SizeFFEr" : ixWRSrc > 0 ? "SizeFFs" : "SizeFFr");

            if (remainingFF > 0.0) {
              mMax++; // increase allowed loops
            }
          }
          if (E.debugFFOut) {
            System.out.println("----A---" + name + " doing remainingFF m" + m + " mMax" + mMax + " n" + n + " sourceIx" + sourceIx + " sourceIx" + sourceIx
                               + " m" + m + " mMax" + mMax + " n" + n + EM.mf("remainingFF", remainingFF) + EM.mf("val", val) + " ixWSrc" + ixWSrc + EM.mf("rsval", rsval) + EM.mf("maxFutureTrans", maxFutureTrans) + EM.mf("minFutureTrans", minFutureTrans) + EM.mf("FFTransFrac", ffTFracNudge[nudBoth]) + EM.mf("reservMult", reservMult) + EM.mf("maxF0", maxF0) + " maxFa=" + EM.mf(maxFa) + EM.mf("maxF1", maxF1) + EM.mf("maxF2", maxF2) + EM.mf("maxF3", maxF3) + EM.mf("maxF4", maxF4));
          }
        }
        else // now initial test for dues,
        // set up emergency and normal future fund payments
        {// for non-emerg transfer totWorth-clanStartFFD decrease by prev ff1
          ff1 = totWorth - EM.clanStartFutureFundDues[pors][clan];
          excessForFF = emergTrans ? maxFutureTrans : ff1 * (1. + Math.sqrt(ff2)) * EM.futureFundFrac[pors][clan];
          ff2 = ff1;
        }
        double ff3 = EM.futureFundFrac[pors][clan] * totWorth;
        if (!didXcessFF && n >= 0 && (n % 3 == 0) && yearsFutureFund < (ff3) && reDo == 0 && excessForFF > E.PZERO) {

          // amount to  worth tranfer due to size
          val = Math.min(excessForFF, maxFutureTrans);
          remainingFF = excessForFF - val; // get any leftover worth

          if (val > E.PZERO) {
            didXcessFF = true;
            xcess = true;
            if (E.debugFutureFund) {
              if (val > bals.get(ixWRSrc, srcIx)) {
                EM.doMyErr("Error val=" + mf(val) + " >  bals=" + mf(bals.get(ixWRSrc, srcIx)));
              }
            }
            resTypeName = (emergTrans ? ixWRSrc > 0 ? "SizeFFEs" : "SizeFFEr" : ixWRSrc > 0 ? "SizeFFs" : "SizeFFr");
            if (remainingFF > 0.0) {
              mMax++; // increase allowed loops
            }
          }
          if (E.debugFFOut) {
            System.out.println("-----B---" + name + " doing excessForFF=" + EM.mf(excessForFF) + " sourceIx" + sourceIx
                               + " m" + m + " mMax" + mMax + " n" + n + EM.mf("remainingFF", remainingFF) + EM.mf("val", val) + " ixWSrc" + ixWSrc + EM.mf("rsval", rsval) + EM.mf("maxFutureTrans", maxFutureTrans) + EM.mf("minFutureTrans", minFutureTrans) + EM.mf("FFTransFrac", ffTFracNudge[nudBoth]) + EM.mf("reservMult", reservMult) + EM.mf("maxF0", maxF0) + " maxFa=" + EM.mf(maxFa) + EM.mf("maxF1", maxF1) + EM.mf("maxF2", maxF2) + EM.mf("maxF3", maxF3) + EM.mf("maxF4", maxF4));
          }
        } // now check for at upto 3 neg prospects, reduce max avail sector
        else if (E.ffSwapNs.contains(n) && (rawProsp = rawProspects2.curMin(2 - (n > 30 ? 0 : n > 20 ? 1 : 2))) < -0.0) {
          srcIx = mtgAvails6.curMaxIx(0);
          ixWRSrc = (int) srcIx / E.LSECS;
          srcIx = srcIx % E.LSECS;
          val = Math.min(mtgAvails6.getRow(ixWRSrc).get(srcIx) * .55, balances.getRow(ixWRSrc).get(srcIx) * (ffTFracNudge[nudBoth]));
          resTypeName = "EmergFF1";
          doing = val > 200 ? true : false;
          if (E.debugFFOut) {
            System.out.println("-----C---" + name + " doing rawProspects2 neg=" + EM.mf(rawProsp) + " sourceIx" + sourceIx + " m" + m + " mMax" + mMax + " n" + n + EM.mf("remainingFF", remainingFF) + EM.mf("val", val) + " ixWSrc" + ixWSrc + EM.mf("rsval", rsval) + EM.mf("maxFutureTrans", maxFutureTrans) + EM.mf("minFutureTrans", minFutureTrans) + EM.mf("FFTransFrac", ffTFracNudge[nudBoth]) + EM.mf("reservMult", reservMult) + EM.mf("maxF0", maxF0) + " maxFa=" + EM.mf(maxFa) + EM.mf("maxF1", maxF1) + EM.mf("maxF2", maxF2) + EM.mf("maxF3", maxF3) + EM.mf("maxF4", maxF4));
          }
        } // now check if resources balances too much bigger than staff,
        // swaps cannot solve  this problem
        // swaps cannot do staff transfers to make rawProspects2.curMin() > PZERO
        // decrease needed diference as r xfers increase
        // .1 * (rxfers * .1 +.7) > .1
        // max1 = rc sum, dif1 = rc sum - sg sum, frac1= (dif1/max1)
        // tmp1 = .7 + r xfers * .1, frac2 = frac1 * tmp1
        //test rc.sum vs sg.sum  (rc.sum - sg.sum)/rc.sumc
        //       else if (((frac2 = (frac1 = (dif1 = (max1 = bals.getRow(ixWRSrc = 0).sum()) - bals.getRow(ixArow = 1).sum()) / max1)) > eM.clanFutureFundEmerg1[pors][clan]) && reDo > nReDo && max1 > PZERO) {
        else if (((frac2 = (frac1 = (dif1 = (max1 = bals.getRow(ixWRSrc = 0).sum()) - bals.getRow(ixArow = 1).sum()) / max1)) > eM.clanFutureFundEmerg1[pors][clan]) && max1 > PZERO) {
          //   || (rawProspects2.getRow(ixArow).min(4) < NZERO && rawProspects2.getRow(ixSrc).min(3) > PZERO )
          resTypeName = "REmergFF1";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * mDif * (ffTFracNudge[nudBoth]));
          // limit the size of transferby largest balance* future fund trans limit
          val = Math.min(val1, bals.getRow(ixWRSrc).get(srcIx = bals.getRow(ixWRSrc).maxIx()) * (ffTFracNudge[nudBoth]));
          doing = true;
        } // now check available funds s too more r or r too more s
        // is s.sum() to greater r.sum()
        else if (((frac1 = (dif1 = (max1 = -mtgNeeds6.getRow(ixWRSrc = 1).sum()) - -mtgNeeds6.getRow(ixArow = 0).sum()) / max1) > eM.clanFutureFundEmerg1[pors][clan]) && reDo > nReDo && max1 > PZERO) {
          resTypeName = "SEmergFF1";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * mDif * (ffTFracNudge[nudBoth]));
          // limit the size of transfer
          val = Math.min(val1, bals.getRow(ixWRSrc).get(srcIx = bals.getRow(ixWRSrc).maxIx()) * (ffTFracNudge[nudBoth]));

          doing = true;
        }
        else if ((false && (frac1 = (dif1 = (max1 = -mtgNeeds6.getRow(ixWRSrc = 0).sum()) - -mtgNeeds6.getRow(ixArow = 1).sum()) / max1)
                           > eM.clanFutureFundEmerg2[pors][clan]) && reDo > nReDo && max1 > PZERO) {
          resTypeName = "REmergFF2";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * .5 * (eM.futureFundTransferFrac[pors][clan] + ffTFracNudge[nudBoth]));
          // limit the size of transfer
          val = Math.min(val1, bals.getRow(ixWRSrc).get(srcIx = bals.getRow(ixWRSrc).maxIx()) * (ffTFracNudge[nudBoth]));

          doing = true;
        }
        else if ((false && (frac1 = (dif1 = (max1 = -mtgNeeds6.getRow(ixWRSrc = 1).sum()) - -mtgNeeds6.getRow(ixArow = 0).sum()) / max1) > eM.clanFutureFundEmerg2[pors][clan]) && reDo > nReDo && max1 > PZERO) {
          resTypeName = "SEmergFF2";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * mDif * (ffTFracNudge[nudBoth]));
          // limit the size of transfer
          val = Math.min(val1, bals.getRow(ixWRSrc).get(srcIx = bals.getRow(ixWRSrc).maxIx()) * (ffTFracNudge[nudBoth]));

          doing = true;
        } // now compare balances
        else if (((frac1 = (dif1 = (max1 = bals.getRow(ixWRSrc = 0).sum()) - bals.getRow(ixArow = 1).sum()) / max1) > eM.clanFutureFundEmerg2[pors][clan]) && swapLoops > swapLoopMax - 1) {
          //   || (rawProspects2.getRow(ixArow).min(4) < NZERO && rawProspects2.getRow(ixWRSrc).min(3) > PZERO )
          resTypeName = "RcEmergFF2";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * mDif * (ffTFracNudge[nudBoth]));
          // limit the size of transfer
          val = Math.min(val1, bals.getRow(ixWRSrc).get(srcIx = bals.getRow(ixWRSrc).maxIx()) * (ffTFracNudge[nudBoth]));

          doing = true;
        } // max1 = sg.sum
        //  dif1 =  nax1 - rc,sum
        //  frac = dif/max1 *.7
        // if(frac > clanFFE1[pors][clan]
        else if (((frac1 = (dif1 = (max1 = bals.getRow(ixWRSrc = 1).sum()) - bals.getRow(ixArow = 0).sum()) / max1) > eM.clanFutureFundEmerg2[pors][clan]) && swapLoops > swapLoopMax - 1) {
          resTypeName = "SgEmergFF2";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * mDif * (ffTFracNudge[nudBoth]));
          // limit the size of transfer
          val = Math.min(val1, bals.getRow(ixWRSrc).get(srcIx = bals.getRow(ixWRSrc).maxIx()) * (ffTFracNudge[nudBoth]));

          doing = true;
        }
        else if ((false && (frac1 = (dif1 = (max1 = bals.getRow(ixWRSrc = 0).sum()) - bals.getRow(ixArow = 1).sum()) / max1) > eM.clanFutureFundEmerg2[pors][clan]) && reDo > nReDo && max1 > PZERO) {
          resTypeName = "RcEmergFF2";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * tmp1 * (ffTFracNudge[nudBoth]));
          // limit the size of transfer
          val = Math.min(val1, bals.getRow(ixWRSrc).get(srcIx = bals.getRow(ixWRSrc).maxIx()) * (ffTFracNudge[nudBoth]));

          doing = true;
        }// (sg -rc /sg) * .7 > cFFE2
        else if ((false && (frac1 = (dif1 = (max1 = bals.getRow(ixWRSrc = 1).sum()) - bals.getRow(ixArow = 0).sum()) / max1) > eM.clanFutureFundEmerg2[pors][clan]) && reDo > nReDo && max1 > PZERO) {
          resTypeName = "SgEmergFF2";
          // val1 is the surpluss of dif1 over staff
          // ..TransferFrac limits fraction of surpluss to transfer
          val1 = (dif1 * tmp1 * (ffTFracNudge[nudBoth]));
          srcIx = bals.getRow(ixWRSrc).maxIx();
          // limit the size of transfer
          val2 = Math.min(val1, bals.get(ixWRSrc, srcIx) * (ffTFracNudge[nudBoth]));
          //prevent taking more than balance - emergency reserve
          val = Math.min(val2, bals.get(ixWRSrc, srcIx) * (1.0 - E.emergReserve[ixWRSrc][pors][clan]));

          doing = true;
        }
        else {
          // end if no more FF needed
          //   E.sysmsg("in calcFutureFund endb m=%d",m);
          destIx = srcIx;
          if (E.debugFFOut) {
            System.out.println("-----D---FF unNeeded " + name + " m" + m + " mMax" + mMax + " n" + n + " sourceIx" + sourceIx + " sourceIx" + sourceIx
                               + " m" + m + " mMax" + mMax + " n" + n + EM.mf("remainingFF", remainingFF) + EM.mf("val", val) + " ixWSrc" + ixWSrc + EM.mf("rsval", rsval) + EM.mf("maxFutureTrans", maxFutureTrans) + EM.mf("minFutureTrans", minFutureTrans) + EM.mf("FFTransFrac", ffTFracNudge[nudBoth]) + EM.mf("reservMult", reservMult) + EM.mf("maxF0", maxF0) + " maxFa=" + EM.mf(maxFa) + EM.mf("maxF1", maxF1) + EM.mf("maxF2", maxF2) + EM.mf("maxF3", maxF3) + EM.mf("maxF4", maxF4));
          }
          return m > 0;
        } // did nothing do rest of swap

        // now test again whether the pevious code found something to process
        val = doing || xcess ? Math.min(val, Math.min(bals.get(ixWRSrc, srcIx), Math.min(maxFutureTrans, (maxF4 = mtgAvails6.get(sourceIx) * (0.89 - reservMult))))) : 0.0;
        if ((doing || xcess) && val > minFutureTrans) {
          // find cashValue/startYearWorth to transfer for size
          //srcIx = bals.getRow(ixWRSrc).maxIx();
          val = Math.min(val, Math.min(maxFutureTrans, (maxF4 = mtgAvails6.get(sourceIx) * (0.89 - reservMult))));
          rsval = val * eM.nominalRSWealth[ixWRSrc][pors];
          if (E.debugFFOut) {
            System.out.println("-----DFFa---FF unNeeded " + name + " m" + m + " mMax" + mMax + " n" + n + " sourceIx" + sourceIx + " sourceIx" + sourceIx
                               + " m" + m + " mMax" + mMax + " n" + n + EM.mf("remainingFF", remainingFF) + EM.mf("val", val) + " ixWSrc" + ixWSrc + EM.mf("rsval", rsval) + EM.mf("maxFutureTrans", maxFutureTrans) + EM.mf("minFutureTrans", minFutureTrans) + EM.mf("FFTransFrac", ffTFracNudge[nudBoth]) + EM.mf("reservMult", reservMult) + EM.mf("maxF0", maxF0) + " maxFa=" + EM.mf(maxFa) + EM.mf("maxF1", maxF1) + EM.mf("maxF2", maxF2) + EM.mf("maxF3", maxF3) + EM.mf("maxF4", maxF4));
          }
          //    srcIx = bals.getRow(ixWRSrc).maxIx();
          // limit the size of transfer
          //rsval2 = Math.min(rsval1, bals.get(sourceIx) (ffTFracNudge[nudBoth])* eM.nominalRSWealth[ixWRSrc][pors]);
          //prevent taking more than balance - emergency reserve
          //rsval = Math.min(rsval2,
          //        Math.min(bals.get(sourceIx), bals.get(sourceIx) * (1.0 - E.emergReserve[ixWRSrc][pors][clan])));
          double valInc = rsval / syW.getTotWorth();
          hist.add(new History("$b", History.loopIncrements3, "calcFF " + resTypeName,
                               "v=" + EM.mf(rsval),
                               rcNsq[ixWRSrc] + srcIx + "=" + EM.mf(bals.get(ixWRSrc, srcIx)),
                               "dif" + EM.mf(dif1), "f" + EM.mf(frac1),
                               "FF=" + EM.mf(eM.clanFutureFunds[clan]),
                               "Yf" + EM.mf(yearsFutureFund + rsval),
                               "rc" + EM.mf(bals.getRow(0).sum()),
                               "nd" + EM.mf(mtgNeeds6.getRow(0).sum()),
                               "sg" + EM.mf(bals.getRow(1).sum()),
                               EM.mf(mtgNeeds6.getRow(1).sum())));
          bals.sendHist(5, "$c");
          assert rsval >= 0. : "Error neg val" + EM.mf(val) + " type=" + resTypeName + " source" + sourceIx; //String.format("Error neg val=%9.4f, resTypeName=%s, ixWRSrc=%d, srcIx=%d", val, resTypeName, ixWRSrc, srcIx));
          double sourcSum = bals.get(2 + 2 * ixWRSrc, srcIx) + bals.get(3 + 2 * ixWRSrc, srcIx);
          //  assert E.PZERO > Math.abs(bals.get(sourceIx) - sourcSum) : "err rs sum" + EM.mf(bals.get(sourceIx)) + " != sourcSum" + EM.mf(sourcSum);
          assert bals.get(sourceIx) * eM.nominalRSWealth[ixWRSrc][pors] >= rsval / eM.nominalRSWealth[ixWRSrc][pors] : ("-----DFFE---rsval too big for working + reserve" + EM.mf(rawProsp) + " rsval" + EM.mf(rsval) + " source" + EM.mf(bals.get(sourceIx)) + " sourceIx" + sourceIx + " m" + m + " mMax" + mMax + " n" + n + EM.mf("remainingFF", remainingFF) + EM.mf("val", val) + " ixWSrc" + ixWSrc + EM.mf("rsval", rsval) + EM.mf("maxFutureTrans", maxFutureTrans) + EM.mf("minFutureTrans", minFutureTrans) + EM.mf("FFTransFrac", ffTFracNudge[nudBoth]) + EM.mf("reservMult", reservMult) + EM.mf("maxF0", maxF0) + " maxFa=" + EM.mf(maxFa) + EM.mf("maxF1", maxF1) + EM.mf("maxF2", maxF2) + EM.mf("maxF3", maxF3) + EM.mf("maxF4", maxF4));
          if (E.debugFutureFund) {
            if (rsval.isNaN() || rsval.isInfinite()) {
              E.myTestDouble(rsval, "val", "the value to move passed from previous tests, prevval bals %s%d =%7.2f", rcsg[2 * ixWRSrc], srcIx, bals.get(2 * ixWRSrc, srcIx));
            }

            if (E.noAsserts && rsval < NZERO) {
              EM.doMyErr(String.format("Error neg val=%9.4f, resTypeName=%s, ixWRSrc=%d, srcIx=%d", val, resTypeName, ixWRSrc, srcIx));
            }
            // if val exceeds the sum of the working and reserve values of resource or staff
            if (E.noAsserts && bals.get(2 + 2 * ixWRSrc, srcIx) + bals.get(3 + 2 * ixWRSrc, srcIx) - val < E.NZERO) {
              EM.doMyErr(String.format("calcFutureFund error name=%7s, %s%d = %7.2f, %s%d=%7.2f sum=%7.2f less than val=%7.2f * (ffTFracNudge[nudBoth])= %7.2f bals*eM=%7.2f", resTypeName, aChar[2 * ixWRSrc], srcIx, bals.get(2 + 2 * ixWRSrc, srcIx), aChar[1 + 2 * ixWRSrc], srcIx, bals.get(3 + 2 * ixWRSrc, srcIx), bals.get(ixWRSrc, srcIx), val, ffTFracNudge[nudBoth], bals.get(ixWRSrc, srcIx) * (ffTFracNudge[nudBoth])));
            }
          } // end of the 3 tests of value

          if (val > minFutureTrans * 1.9) {
            m++;
          }
          hist.add(new History("$c", History.loopMinorConditionals5, "n" + n + "calcFF" + " m" + m + rcNsq[ixWRSrc] + srcIx + " " + resTypeName,
                               "v" + EM.mf(rsval), "b" + EM.mf(bals.get(ixWRSrc, srcIx)),
                               "df" + EM.mf(dif1), "f" + EM.mf(frac1),
                               "FF=" + EM.mf(eM.clanFutureFunds[clan]),
                               "Yf" + EM.mf(yearsFutureFund + rsval),
                               "r" + EM.mf(bals.getRow(0).sum()), EM.mf(mtgNeeds6.getRow(0).sum()),
                               "s" + EM.mf(bals.getRow(1).sum()), EM.mf(mtgNeeds6.getRow(1).sum()), "<<<<<<<<"));
          if (E.debugFFOut) {
            System.out.println("-----FF22---" + name + "Y" + EM.year + " did rawProspects2 neg=" + EM.mf(rawProsp) + " sourceIx" + sourceIx
                               + " m" + m + " mMax" + mMax + " n" + n + EM.mf("remainingFF", remainingFF) + EM.mf("val", val) + " ixWSrc" + ixWSrc + EM.mf("rsval", rsval) + EM.mf("maxFutureTrans", maxFutureTrans) + EM.mf("minFutureTrans", minFutureTrans) + EM.mf("FFTransFrac", ffTFracNudge[nudBoth]) + EM.mf("reservMult", reservMult) + EM.mf("maxF0", maxF0) + " maxFa=" + EM.mf(maxFa) + EM.mf("maxF1", maxF1) + EM.mf("maxF2", maxF2) + EM.mf("maxF3", maxF3) + EM.mf("maxF4", maxF4));
          }
          // only count first FutureFund of each type of this year
          int thisYr = (resTypeName.contains("merg") ? emergeFutureFund : excessFutureFund) > 0.0 ? 0 : 1;
          int bothYr = yearsFutureFund > 0.0 ? 0 : 1;
          int excessYr = excessFutureFund > 0.0 ? 0 : 1;
          int emergYr = emergeFutureFund > 0.0 ? 0 : 1;
          setStat(resTypeName, pors, clan, rsval, 1);
          setStat(resTypeName.contains("merg") ? "EmergFF" : "SizeFF", pors, clan, rsval, thisYr);
          setStat("FutureFundSaved", pors, clan, rsval, bothYr);
          // transfer val to clanFutureFunds
          //.eM.clanFutureFunds[clan] += rsval;
          yearsFutureFund += rsval;  //transfer at yearEnd
          emergeFutureFund += resTypeName.contains("merg") ? rsval : 0.;
          excessFutureFund += resTypeName.contains("merg") ? 0.0 : rsval;
          yearsFutureFundTimes++;
          // cost is units not cashValue;
          sys[ixWRSrc * 2].cost3(val, srcIx, reservMult);
          //   E.sysmsg("did transfer val=%5.0f, name=%5s, m=%d",val,resTypeName,m);
          if (E.debugFFOut) {
            System.out.println("-----FF23---" + name + "Y" + EM.year + " doing rawProspects2 neg=" + EM.mf(rawProsp) + " sourceIx" + sourceIx
                               + " m" + m + " mMax" + mMax + " n" + n + ", remainingFF=" + EM.mf(remainingFF) + " val" + EM.mf(val) + " ixWSrc" + ixWSrc + " rsval" + EM.mf(rsval) + " maxFutureTrans" + EM.mf(maxFutureTrans) + " FFTransFrac" + EM.mf(ffTFracNudge[nudBoth]) + " reservMult" + EM.mf(reservMult) + " maxF0=" + EM.mf(maxF0) + " maxFa=" + EM.mf(maxFa) + " maxF1=" + EM.mf(maxF1) + " maxF2=" + EM.mf(maxF2) + " maxF3=" + EM.mf(maxF3) + " maxF4=" + EM.mf(maxF4));
          }

          yCalcCosts(aPre, lightYearsTraveled, eM.tradeHealth[pors][clan], eM.tradeGrowth[pors][clan]);
        }
        else {
          //      E.sysmsg("in calcFutureFund endc m=%d",m);
          // if m>0 we did something, so exit swaps
          destIx = srcIx;
          swapType = 3;
          if (E.debugFFOut) {
            System.out.println("-----FF24---" + name + "Y" + EM.year + " all done rawProspects2" + EM.mf(rawProsp) + " sourceIx" + sourceIx
                               + " m" + m + " mMax" + mMax + " n" + n + ", remainingFF=" + EM.mf(remainingFF) + " val" + EM.mf(val) + " ixWSrc" + ixWSrc + " rsval" + EM.mf(rsval) + " maxFutureTrans" + EM.mf(maxFutureTrans) + " FFTransFrac" + EM.mf(ffTFracNudge[nudBoth]) + " reservMult" + EM.mf(reservMult) + " maxF0=" + EM.mf(maxF0) + " maxFa=" + EM.mf(maxFa) + " maxF1=" + EM.mf(maxF1) + " maxF2=" + EM.mf(maxF2) + " maxF3=" + EM.mf(maxF3));
          }
          return m > 0;
        }
      } // end doing|xcess
      if (E.debugFFOut) {
        System.out.println("-----FF26---" + name + "Y" + EM.year + " all done rawProspects2" + EM.mf(rawProsp) + " sourceIx" + sourceIx
                           + " m" + m + " mMax" + mMax + " n" + n + ", remainingFF=" + EM.mf(remainingFF) + " val" + EM.mf(val) + " ixWSrc" + ixWSrc + " rsval" + EM.mf(rsval) + " maxFutureTrans" + EM.mf(maxFutureTrans) + " FFTransFrac" + EM.mf(ffTFracNudge[nudBoth]) + " reservMult" + EM.mf(reservMult) + " maxF0=" + EM.mf(maxF0) + " maxFa=" + EM.mf(maxFa) + " maxF1=" + EM.mf(maxF1) + " maxF2=" + EM.mf(maxF2) + " maxF3=" + EM.mf(maxF3));
      }
      //  E.sysmsg("in calcFutureFund end m=%d",m);
      destIx = srcIx;
      return m > 0;
    }

    /**
     * start body of CashFlow perform task for startYear, endYear initiated by
     * StarTrader->Envirn->Assets->CashFlow
     */
    void yinitCosts() {
      //  startYr.set(cur);
      histTitles("yhinitCosts");
      ycalcEfficiency();

      doFailed = false;
    } // end yinitCosts in CashFlow

    boolean notDoing() {  //Assets.CashFlow
      // add a line
      return failed = !swapped;  // remove doFailed
    }

    /**
     * Assets.AssetYr variables used by Assets.AssetYr.Trades These are the
     * values of the last trade in an AssetYr, and are used to set statistics
     * about the last trade
     */
    Trades myTrade;   // in Assets.CashFlow
    //   A2Row bids = new A2Row(History.informationMinor9, "bids");
    // A2Row strategicValues = new A2Row(ec, History.informationMinor9, "strategicValues");

    boolean didTrade = false, rejectTrade = false, lostTrade = false;
// now for the firsts
    A2Row bidsFirst = new A2Row(ec, History.informationMinor9, "bidsFirst");
    //A2Row strategicValuesFirst = new A2Row(ec, History.informationMinor9, "strategicValuesFirst");
    A2Row stratVarsFirst = new A2Row(ec, History.informationMinor9, "stratVarsFirst");

    int bb; // set bb so that (barterStart+bb)%2 always = 1
    int myIx, oIx;
    ArrayList<History> ohist;

    int searchTrade = yphase == yrphase.SEARCH ? 0 : 1;
    String oname;
    Econ oEcon;
//      int oClan = -5; don't override Assets.oClan, Assets.oPors
    //for calcStrategicSums
    double sumStrategicRequests = 0, sumStrategicOffers = 0;

    double lowStrategicOffers = 0.;
    // static double maxTradeFrac=10.,minTradeFrac=.03;
    // EM.multTotalStaticFrac[][],EM.gameMultTotalStrategicFrac
    double tradedMoreManuals = 0., tradedCash = 0.;
    int doingSearchOrTrade = yphase == yrphase.SEARCH ? EM.DOINGSEARCH : EM.DOINGTRADE;

    /**
     * process the offer to barter in CashFlow create the trade class, pass on
     * each barter and finally process the termination type of the barter:
     * didTrade, rejectedTrade,lostTrade
     *
     * @param inOffer The offer from the other economy<br>
     * if entryTerm and newTerm<ol>
     * <li>entryTerm >1 newTerm=barter entryTerm-1 </li>
     * <li>entryTerm >1 newTerm=barter => 1 force trade decision by the other
     * Econ</li>
     * <li>entryTerm >1 newTerm=barter => 0 traded xitTrade => 0</li>
     * <li>entryTerm >1 newTerm=barter => -1 rejected xitTrade => -1</li>
     * <li>entryTerm == 1 newTerm=barter => 0 traded xitTrade => 0</li>
     * <li>entryTerm == 1 newTerm=barter => -1 rejected xitTrade => -1</li>
     * <li>entryTerm == 0 traded other xitTrade => -2 ndLoop</li>
     * <li>entryTerm == -1 lost other xitTrade => -3 ndLoop</li>
     * <li>entryTerm == -2 traded ndLoop
     * </ol>
     *
     * @return a new offer for the other economy
     */
    Offer barter(Offer inOffer) { //Assets.CashFlow.barter
      boolean firstVisit = false; // for counting unique planets visited
      Offer retOffer = inOffer; // retOffer replaced if barter
      try {
        aPre = "b&";
        hist.add(new History(aPre, 5, name + " Y Barter R", resource.balance));
        hist.add(new History(aPre, 5, name + " B T=" + inOffer.getTerm() + " S", staff.balance));
        hist.add(new History(aPre, 5, name + " ntrB C", c.balance));
        hist.add(new History(aPre, 5, name + " ntrBa G", g.balance));
        //   inOffer.setMyIx(ec); // done later screws up flipOffer

        Trades eTrad = myTrade;  //Assets.myTrade
        int entryTerm = inOffer.getTerm();
        int newTerm = entryTerm; // until barter runs, then post barter value
        int ehist = 0;
        if (E.debugBarterOut) {
          eM.printHere("---CBAaa---", ec, "Assets.CashFlow.barter Term" + entryTerm + ":" + (tradeAccepted ? " tradeAccepted" : " !tradeAc") + (tradeRejected ? " tradeRejected" : " !tradeRe") + (tradeLost ? " tradeLost" : " !tradeLo") + (tradeMissed ? " tradeMissed" : " !tradeMissed"));
        }
        hist.add(new History(aPre, 5, "entr CashFlow barter", (eTrad == null ? " !eTrad" : " eTrad"), "entryTerm=", wh(entryTerm), "$=" + EM.mf(sumTotWorth), "l=" + hist.size() + "======================<<<<<<<<<<"));
        int lhist = hist.size();
        int lhista = lhist;
        retOffer = inOffer;
        // barter of a new trade, instantiate Trades, remember other hist for possible copy
        yphase = yrphase.TRADE;
        if (myTrade == null && entryTerm > 0) {
          //      E.sysmsg(name + " instantiate ASY Trades term=" + entryTerm);
          // start a new trade within this Econ->Assets->CashFlow->Trades
          aPre = "A&";
          hist.add(new History(aPre, 5, name + " ASYb R", resource.balance));
          hist.add(new History(aPre, 5, name + " initT S", staff.balance));
          hist.add(new History(aPre, 5, name + " initT C", c.balance));
          hist.add(new History(aPre, 5, name + " initT G", g.balance));
          //only count unique planets visited, count once for each planet
          // Count each ship first entry also
          if (++econVisited == 1) {
            EM.porsClanVisited[pors][clan]++;
            EM.porsVisited[pors]++;
            EM.clanVisited[clan]++;
            EM.visitedCnt++;
            firstVisit = true;
          }
          // new year barter in Assets.CashFlow.barter
          preTradeSum4 = bals.sum4();
          hist.add(new History(aPre, 5, " " + name + " now instantiate", ">>>>>>>", " a new", " trades", "<<<<<<<"));
          myTrade = new Trades();
          if (EM.dfe()) {
            return inOffer;
          }
          inOffer.setMyIx(ec);
          myTrade.initTrade(inOffer, this);
          if (EM.dfe()) {
            return inOffer;
          }
          hist.add(new History(aPre, 5, " " + name + " after init", ">>>>>>", " a new", " trades"));
        } // end myTrade == null
        // test for a new visitor
        if (!inOffer.getOName().equals(tradingShipName) && entryTerm > 0 && myTrade != null) {
          tradingShipName = inOffer.getOName();
          inOffer.setMyIx(ec);
          hist.add(new History(aPre, 5, " " + name + " after init2", ">>>>>>>", ">>>>>>>", " a new", " trades"));
          aPre = "c&";
          hist.add(new History(aPre, 5, name + " cur.Bar R", resource.balance));
          hist.add(new History(aPre, 5, name + " cur.Bar S", staff.balance));
          hist.add(new History(aPre, 5, name + " cur.Bar C", c.balance));
          hist.add(new History(aPre, 5, name + " cur.Bar G", g.balance));

          E.myTest(myTrade == null && entryTerm > 0, "xit ASY barter " + (eTrad == null ? " !eTrad" : " eTrad") + " entryTerm=" + entryTerm + (myTrade == null ? " !myTrade" : " myTrade"));
        }//end other name not equal
        // now set up for a barter by Trades.barter
        btW = new DoTotalWorths(); // Assets.CashFlow.barter before trade
        //   if (myTrade != null && entryTerm > 0) {
        if (myTrade != null) {
          hist.add(new History(aPre, 5, " " + name + "cashFlow barter", " term=" + inOffer.getTerm(), " trades"));
          inOffer.setMyIx(ec);  //Assets.CashFlow.barter
          if (EM.dfe()) {
            return inOffer;
          }
          // now barter ======entryTerm>0 ...=======================
          retOffer = entryTerm > 0 ? myTrade.barter(inOffer) : inOffer; // get entryTerm-1, 0, -1
          if (EM.dfe()) {
            return inOffer;
          }
          newTerm = retOffer.getTerm();
          oClan = retOffer.getOClan();
          Econ oEcon = retOffer.getOEcon();
          oPors = oEcon.pors;
          hist.add(new History(aPre, 5, name + " inCF" + newTerm, "newTerm=" + newTerm, "entryTerm=" + entryTerm, "copy to other", "history"));
          ehist = hist.size();
          ArrayList<History> ohist = retOffer.getOtherHist();
          String oname = retOffer.getOtherName();
          E.myTest(myTrade == null, "xit CF.barter " + (eTrad == null ? " !eTrad" : " eTrad") + " entryTerm=" + entryTerm + (myTrade == null ? " !myTrade" : " myTrade"));
          // copy all of the history to ohist, if eM.trade2HistOutputs for all newTerms
          if (eM.trade2HistOutputs && !ec.clearHist()) {  ///Assets.CashFlow.barter
            hist.add(new History(aPre, 5, " " + name + ">>>>>", " term=" + inOffer.getTerm(), "start copy hist=" + hist.size(), "frm " + name, "to " + oname, " ===================================<<<<<<<"));
            ec.addOHist(ohist, new History(aPre, 5, " " + name + ">>>>>", " term=" + inOffer.getTerm(), "start copy hist=" + hist.size(), "frm " + name, "to " + oname, " ===================================<<<<<<<"));

            for (; lhist < ehist; lhist++) {
              History ahist = hist.get(lhist);
              if (ahist != null) {
                ec.addHist(ohist, ahist);
              }
            }
            hist.add(new History(aPre, 5, " " + name + " => " + oname, " term=" + inOffer.getTerm(), " end copy ===================================<<<<<<<"));
            ec.addOHist(ohist, new History(aPre, 5, " " + name + " => " + oname, " term=" + inOffer.getTerm(), " end copy ===================================<<<<<<<"));
          } // end printing other
        } // end entryTerm > 0
        // check for ending this trade //Assets.CashFlow.barter
        fav = (eM.fav[oClan][oPors][clan]);
        eM.printHere("----CBt----", ec, " newTerm" + newTerm + " entryTerm" + entryTerm + "  other:" + retOffer.getOtherName() + " tradedShipOrdinal" + tradedShipOrdinal);
        // may enter barter terminating process
        if (newTerm < 1) {
          if (myTrade != null) {
            myTrade.xitTrade(); // term= 0 mytrade,-1 my reject,-2 other traded,-3 reject
            System.out.println("----CBt2----" + as.name + " xitTrade term=" + retOffer.getTerm() + "  other:" + retOffer.getOtherName());
          }
          double criticalStrategicRequestsPercentTWorth = 100. * sumCriticalStrategicRequests / startYrSumWorth;
          double criticalStrategicRequestsPercentFirst = 100. * criticalStrategicRequestsFirst / startYrSumWorth;
          double criticalStrategicRequestsPercentCSRFirst = 100. * sumCriticalStrategicRequests / criticalStrategicRequestsFirst;
          double nominalReceiptsPercentWorth = 100. * nominalRequestsSum / startYrSumWorth;
          double nominalRequestsPercentOffers = 100. * nominalRequestsSum / nominalOffers;
          double criticalNominalRequestsPercentFirst = 100. * criticalNominalRequests / criticalNominalRequestsFirst;
          double criticalNominalRequestsPercentCriticalStrategicRequests = 100. * criticalNominalRequests / sumCriticalStrategicRequests;
          //pretrade are initTrade values for this trade
          // frac of availiable units -mtgNeeds6.sum/bals.
          fracPreTrade = 100. * preTradeAvail / preTradeSum4;
          fracPostTrade = 100. * postTradeAvail / postTradeSum4;
          // see if/how much frac avail increases
          tradeAvailIncrPercent = preTradeAvail < E.PZERO ? 1. : 100. * (postTradeAvail - preTradeAvail) / preTradeAvail;

          tW = new DoTotalWorths();  // in Assets.CashFlow.Trade.initTrade after trade barter
          tWTotWorth = tW.getTotWorth();
          btWTotWorth = btW.getTotWorth();
          btWrcsgSum = btW.getSumRCSGBal();
          double tWrcsgSum = tW.getSumRCSGBal();
          worthIncr = tWTotWorth - btWTotWorth;
          worthIncrPercent = btWTotWorth < E.PZERO ? 1. : 100. * (worthIncr) / startYrSumWorth;
          double rcsgIncr = tWrcsgSum - btWrcsgSum;
          percentValuePerGoal = strategicGoal > E.PZERO ? 100. * strategicValue / strategicGoal : 1.;
          retOffer.set2Values(ec, btWTotWorth, btW.getSumRCSGBal(), tWTotWorth); // needed in TradeRecord SearchRecord

          //if (newTerm == 0 || newTerm == -2 || entryTerm == -2) {  //trade accepted
          if (newTerm == 0) {  //trade accepted
            // eM.printHere("---CBX---", ec, " newTerm" + newTerm + " entryTerm" + entryTerm + " tradedShipOrdinal" + tradedShipOrdinal);
            tradedShipOrdinal++; // set ordinal of the next ship if any
            tradedSuccessTrades++;
            tradeAccepted = true;
            tradeMissed = tradeRejected = tradeLost = false;
            lastAcceptedYear = yearTradeAccepted = EM.year;
            if (E.debugBarterOut) {
              eM.printHere("---CBA1---", ec, "Assets.CashFlow.barter set TradeAccepted true" + "  other:" + retOffer.getOtherName());
            }
            EM.tradedCnt++;
            if (firstVisit) {
              EM.porsTraded[pors]++;
            }
            EM.porsClanTraded[pors][clan]++;
            EM.clanTraded[clan]++;
            eM.porsVisited[pors]++;
            eM.porsClanVisited[pors][clan]++;

            // for both cases
            //  setStat(EM.TradeAcceptValuePerGoal, percentValuePerGoal, 1);
            //  setStat(EM.TradeBidRequestsFirst, sumBidRequestsFirst, 1);
            //  setStat(EM.TradeCriticalBidRequestsFirst, sumCriticalBidRequestsFirst, 1);
            if (newTerm == 0) {
              setStat(EM.TradeBidRequests, sumBidRequests, 1);// got
              setStat(EM.TradeCriticalBidRequests, sumCriticalBidRequests, 1); // crit got
            }
            else {
              setStat(EM.TradeAlsoBidRequests, sumBidRequests, 1);
              setStat(EM.TradeAlsoCriticalBidRequests, sumCriticalBidRequests, 1);
            }
            setStat(EM.TRADEFIRSTRECEIVE, pors, clan, requestsFirst, 1);
            setStat(EM.TRADELASTRECEIVE, pors, clan, requests, 1);
            setStat(EM.TRADERECEIVELASTPERCENTFIRST, pors, clan, requestsFirst > E.PZERO ? requests * 100. / requestsFirst : 0., 1);

            setStat(EM.TRADELASTDIVFGAVE, oPors, oClan, calcPercent(offersFirst, offers), 1);
            setStat(EM.TRADELASTDIVRCSG, oPors, oClan, calcPercent(btWTotWorth, offers), 1);
            setStat(EM.TRADEFIRSTGAVE, pors, clan, calcPercent(btWTotWorth, offersFirst), 1);
            setStat(EM.TRADELASTGAVE, pors, clan, offers, 1);
            setStat(EM.TRADENOMINALGAVE, pors, clan, nominalOffers, 1);
            setStat(EM.TRADESTRATFIRSTGAVE, oPors, oClan, calcPercent(btWrcsgSum, totalStrategicOffersFirst), 1);
            setStat(EM.TRADESTRATLASTGAVE, oPors, oClan, totalStrategicOffers, 1);
            //setStat(EM.TRADESTRATGOAL, oPors, oClan, strategicGoal, 1);
            //setStat(EM.TRADESTRATVALUE, oPors, oClan, strategicValue, 1);
            setStat(EM.TRADESTRATFIRSTRECEIVE, pors, clan, totalStrategicRequestsFirst, 1);
            setStat(EM.TRADESTRATLASTRECEIVE, pors, clan, totalStrategicRequests, 1);
            // setStat(EM.BEFORETRADEWORTH, pors, clan, btWTotWorth, 1);
            // setStat(EM.AFTERTRADEWORTH, pors, clan, tWTotWorth, 1);
            setStat(EM.TRADEWORTHINCR, pors, clan, worthIncr, 1);
            setStat(EM.TRADERCSGINCR, pors, clan, rcsgIncr, 1);
            setStat(EM.TradeFirstStrategicGoal, pors, clan, firstStrategicGoal, 1);
            setStat(EM.TradeLastStrategicGoal, pors, clan, strategicGoal, 1);
            setStat(EM.TradeFirstStrategicValue, pors, clan, firstStrategicValue, 1);
            setStat(EM.TradeLastStrategicValue, pors, clan, strategicValue, 1);
            setStat(EM.TradeStrategicValueLastPercentFirst, pors, clan, calcPercent(firstStrategicValue, strategicValue), 1);
            setStat(EM.TradeNominalReceivePercentNominalOffer, pors, clan, calcPercent(nominalOffers, nominalRequests), 1);
            setMax(EM.MaxNominalReceivePercentNominalOffer, calcPercent(nominalOffers, nominalRequests));
            setMin(EM.MinNominalReceivePercentNominalOffer, pors, clan, calcPercent(nominalOffers, nominalRequests), 1);
            setStat(EM.TradeStrategicReceivePercentStrategicOffer, pors, clan, calcPercent(strategicOffers, strategicRequests), 1);
            setMax(EM.MaxStrategicReceivePercentStrategicOffer, calcPercent(strategicOffers, strategicRequests));
            setMin(EM.MinStrategicReceivePercentStrategicOffer, pors, clan, calcPercent(strategicOffers, strategicRequests), 1);
            if (entryTerm == 0 || newTerm == 0) {
              retOffer.setTerm(-2); // other so no more return
            }            // else leave retOffer.term 0 for the other cn
            else if (newTerm == -2 || entryTerm == -2) {  // the other ship traded
              retOffer.setTerm(-7); // other so no more return
            }
          }
          else if (newTerm == -2 || entryTerm == -2) {  // skipped the other ship traded
            tradedShipOrdinal++; // set ordinal of the next ship if any
            tradedSuccessTrades++;
            tradeAccepted = true;
            tradeMissed = tradeRejected = tradeLost = false;
            if (E.debugBarterOut) {
              eM.printHere("---CBA0---", ec, "Assets.CashFlow.barter set Also TradeAccepted true" + "  other:" + retOffer.getOtherName());
            }
            lastAcceptedYear = yearTradeAccepted = EM.year;
            EM.tradedCnt++;
            EM.porsTraded[pors]++;
            EM.porsClanTraded[pors][clan]++;
            EM.clanTraded[clan]++;
            setStat(EM.AlsoTradeLastStrategicValue, pors, clan, strategicValue, 1);
            setStat(EM.AlsoTradeStrategicValueLastPercentFirst, pors, clan, calcPercent(firstStrategicValue, strategicValue), 1);
            setStat(EM.TradeAcceptValuePerGoal, percentValuePerGoal, 1);
            setStat(EM.TradeAlsoBidRequestsFirst, sumBidRequestsFirst, 1);
            setStat(EM.TradeAlsoCriticalBidRequestsFirst, sumCriticalBidRequestsFirst, 1);
            setStat(EM.TradeAlsoBidRequests, sumBidRequests, 1);
            setStat(EM.TradeAlsoCriticalBidRequests, sumCriticalBidRequests, 1);
            setStat(EM.TRADENOMINALGAVE, pors, clan, nominalOffers, 1);
            setStat(EM.TRADESTRATFIRSTGAVE, oPors, oClan, calcPercent(btWrcsgSum, totalStrategicOffersFirst), 1);
            setStat(EM.TRADESTRATLASTGAVE, oPors, oClan, totalStrategicOffers, 1);
            setStat(EM.TRADESTRATFIRSTRECEIVE, pors, clan, totalStrategicRequestsFirst, 1);
            setStat(EM.TRADESTRATLASTRECEIVE, pors, clan, totalStrategicRequests, 1);
            // setStat(EM.BEFORETRADEWORTH, pors, clan, btWTotWorth, 1);
            // setStat(EM.AFTERTRADEWORTH, pors, clan, tWTotWorth, 1);
            setStat(EM.TRADEWORTHINCR, pors, clan, worthIncr, 1);
            setStat(EM.TRADERCSGINCR, pors, clan, rcsgIncr, 1);
            setStat(EM.TradeFirstStrategicGoal, pors, clan, firstStrategicGoal, 1);
            setStat(EM.TradeFirstStrategicValue, pors, clan, firstStrategicValue, 1);
            setStat(EM.TradeLastStrategicValue, pors, clan, strategicValue, 1);
            setStat(EM.TradeStrategicValueLastPercentFirst, pors, clan, calcPercent(firstStrategicValue, strategicValue), 1);
            setStat(EM.TradeNominalReceivePercentNominalOffer, pors, clan, calcPercent(nominalOffers, nominalRequests), 1);
            setMax(EM.MaxNominalReceivePercentNominalOffer, calcPercent(nominalOffers, nominalRequests));
            setMin(EM.MinNominalReceivePercentNominalOffer, pors, clan, calcPercent(nominalOffers, nominalRequests), 1);
            setStat(EM.TradeStrategicReceivePercentStrategicOffer, pors, clan, calcPercent(strategicOffers, strategicRequests), 1);
            setMax(EM.MaxStrategicReceivePercentStrategicOffer, calcPercent(strategicOffers, strategicRequests));
            setMin(EM.MinStrategicReceivePercentStrategicOffer, pors, clan, calcPercent(strategicOffers, strategicRequests), 1);
            setStat(EM.TRADELASTGAVE, pors, clan, offers, 1);
            setStat(EM.TRADEALSOLASTGAVE, pors, clan, offers, 1);
            setStat(EM.TRADEFIRSTRECEIVE, calcPercent(btWrcsgSum, requestsFirst), 1);
            setStat(EM.TRADELASTRECEIVE, pors, clan, calcPercent(btWrcsgSum, requests), 1);
            setStat(EM.TRADERECEIVELASTPERCENTFIRST, pors, clan, requestsFirst > E.PZERO ? requests * 100. / requestsFirst : 0., 1);

            retOffer.setTerm(-5);

          }
          else if (entryTerm == -1) {  // Trade lost, others barter
            tradedShipOrdinal++; // set ordinal of the next ship if any
            //  tradedSuccessTrades++;
            eM.porsVisited[pors]++;
            eM.porsClanVisited[pors][clan]++;
            tradeLost = true;
            prevNotAcceptedYear = yearTradeLost = EM.year;
            tradeMissed = tradeRejected = tradeAccepted = false;
            if (E.debugBarterOut) {
              eM.printHere("---CBa2---", ec, "Assets.CashFlow.barter set tradeLost ");
            }
            EM.tradedCnt++;
            // EM.porsTraded[pors]++;
            // EM.porsClanTraded[pors][clan]++;
            // EM.clanTraded[clan]++;
            setStat(EM.TradeLostValuePerGoal, percentValuePerGoal, 1);
            setStat(EM.TradeLostStrategicGoal, pors, clan, strategicGoal, 1);
            setStat(EM.TradeLostStrategicValue, pors, clan, strategicValue, 1);
            retOffer.setTerm(-5);

          }
          else if (newTerm == -1) { // trade rejected by barter
            tradeRejected = true;
            tradeMissed = tradeLost = tradeAccepted = false;
            if (E.debugBarterOut) {
              eM.printHere("---CBa3---", ec, "Assets.CashFlow.barter set tradeRejected");
            }
            prevNotAcceptedYear = yearTradeRejected = EM.year;
            setStat(EM.TradeRejectValuePerGoal, percentValuePerGoal, 1);
            setStat(EM.TradeRejectedStrategicGoal, pors, clan, strategicGoal, 1);
            setStat(EM.TradeRejectedStrategicValue, pors, clan, strategicValue, 1);
            setStat("WREJTRADEDPINCR", pors, clan, worthIncrPercent, 1);
            setStat(EM.INCRAVAILFRACa, pors, clan, tradeAvailIncrPercent, 1);
            eM.porsVisited[pors]++;
            eM.porsClanVisited[pors][clan]++;
            fav = -2.;
            if (entryTerm == -1) {
              retOffer.setTerm(-3);
              // else leave retOffer.term -1 for the other cn
            }
            else {
              retOffer.setTerm(-1);
            }
          }
          else if (entryTerm == -4) { // should stop in econ
            tradeAccepted = true;
            if (E.debugBarterOut) {
              eM.printHere("---CBa4---", ec, "Assets.CashFlow.barter set TradeAccepted true");
            }
            retOffer.setTerm(-5);
          }
          else if (entryTerm != -2 && entryTerm < -1) { // should stop in econ
            tradeLost = true;
            tradeMissed = tradeRejected = tradeAccepted = false;
            if (E.debugBarterOut) {
              eM.printHere("---CBa5---", ec, "Assets.CashFlow.barter set !TradeAccepted = false");
            }
            prevNotAcceptedYear = yearTradeLost = EM.year;
            retOffer.setTerm(-5);
            fav = -3.;
          } //exitif   Assets.CashFlow.barter
          hist.add(new History("%v", 5, "inCF", " term was=" + entryTerm, "now=" + retOffer.getTerm(), "fav=" + EM.mf(fav)));
          //      setStat("
          // at 0 -1 -2 -3 -5 always xit

          // Desired stats
          if (fav >= 4.5) {
            // gameRes.WTRADEDINCRF5.wet(pors, clan, worthIncrPercent, 1);
            setStat("CRITICALRECEIPTSFRACSYFAV5", pors, clan, criticalStrategicRequestsPercentTWorth, 1);
            setStat("CRITICALRECEIPTSFRADROPT5", pors, clan, criticalStrategicRequestsPercentFirst, 1);
            setStat(EM.INCRAVAILFRAC5, pors, clan, tradeAvailIncrPercent, 1);
          }
          else if (fav >= 3.7) {
            // gameRes.WTRADEDINCRF4.wet(pors, clan, criticalStrategicRequestsPercentTWorth, 1);
            setStat("CRITICALRECEIPTSFRACSYFAV4", pors, clan, criticalStrategicRequestsPercentTWorth, 1);
            setStat("CRITICALRECEIPTSFRADROPT4", pors, clan, criticalStrategicRequestsPercentFirst, 1);
            setStat(EM.INCRAVAILFRAC4, pors, clan, tradeAvailIncrPercent, 1);
          }
          else if (fav >= 2.7) {
            // gameRes.WTRADEDINCRF3.wet(pors, clan, worthIncrPercent, 1);
            setStat("CRITICALRECEIPTSFRACSYFAV3", pors, clan, criticalStrategicRequestsPercentTWorth, 1);
            setStat("CRITICALRECEIPTSFRADROPT3", pors, clan, criticalStrategicRequestsPercentFirst, 1);
            setStat(EM.INCRAVAILFRAC3, pors, clan, tradeAvailIncrPercent, 1);
          }
          else if (fav >= 1.8) {
            // gameRes.WTRADEDINCRF2.wet(pors, clan, worthIncrPercent, 1);
            setStat("CRITICALRECEIPTSFRACSYFAV2", pors, clan, criticalStrategicRequestsPercentTWorth, 1);
            setStat("CRITICALRECEIPTSFRADROPT2", pors, clan, criticalStrategicRequestsPercentFirst, 1);
            setStat(EM.INCRAVAILFRAC2, pors, clan, tradeAvailIncrPercent, 1);
          }
          else if (fav >= .9) {
            // gameRes.WTRADEDINCRF1.wet(pors, clan, worthIncrPercent, 1);
            setStat("CRITICALRECEIPTSFRACSYFAV1", pors, clan, criticalStrategicRequestsPercentTWorth, 1);
            setStat("CRITICALRECEIPTSFRADROPT1", pors, clan, criticalStrategicRequestsPercentFirst, 1);
            setStat(EM.INCRAVAILFRAC1, pors, clan, tradeAvailIncrPercent, 1);
          }
          else if (fav >= 0.) {
            setStat("CRITICALRECEIPTSFRACSYFAV0", pors, clan, criticalStrategicRequestsPercentTWorth, 1);
            setStat("CRITICALRECEIPTSFRADROPT0", pors, clan, criticalStrategicRequestsPercentFirst, 1);
            setStat(EM.INCRAVAILFRAC0, pors, clan, tradeAvailIncrPercent, 1);
          }
          else if (fav >= -1.) {
            setStat("WREJTRADEDPINCR", pors, clan, worthIncrPercent, 1);
            setStat(EM.INCRAVAILFRACa, pors, clan, tradeAvailIncrPercent, 1);
            eM.porsVisited[pors]++;
            eM.porsClanVisited[pors][clan]++;
          }
          else if (fav >= -3.) {
            // gameRes.WLOSTTRADEDINCR.wet(pors, clan, worthIncrPercent, 1);
            setStat("WLOSTTRADEDINCR", pors, clan, worthIncrPercent, 1);
            setStat(EM.INCRAVAILFRACb, pors, clan, tradeAvailIncrPercent, 1);
            eM.porsVisited[pors]++;
            eM.porsClanVisited[pors][clan]++;
          }
          else {   // missed should not be reached
            // gameRes.UNTRADEDWINCR.wet(pors, clan, worthIncrPercent, 1);
            setStat("UNTRADEDWINCR", pors, clan, worthIncrPercent, 1);
            //    setStat(EM.INCRAVAILFRACc, pors, clan, tradeAvailIncrPercent, 1);
            eM.porsVisited[pors]++;
            eM.porsClanVisited[pors][clan]++;
          }
          setStat("TRADES%", pors, clan, fav > NZERO ? 100. : 0., 1);
          if (fav >= 0) {
            setStat("CRITICALRECEIPTSFRACSYFAVA", pors, clan, criticalStrategicRequestsPercentTWorth, 1);

            eM.clanTraded[clan]++;
            eM.porsClanTraded[pors][clan]++;
            eM.clanVisited[clan]++;
            eM.porsClanVisited[pors][clan]++;
            eM.porsTraded[pors]++;
            eM.porsVisited[pors]++;
          }
          hist.add(new History(aPre, 5, name + "CF.barter t=" + retOffer.getTerm(), "before", "xitTrade", "do null", "<<<<<<<<<", "<<<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, 5, name + "CF.barter t=" + retOffer.getTerm(), "before", "xitTrade", "do null", "<<<<<<<<<", "<<<<<<<<<"));
          //  myTrade.xitTrade(); // term= 0 mytrade,-1 my reject,-2 other traded,-3o reject
          myTrade = null;  // terminate mytrade
          // retOffer.setTerm(newTerm - 3);  //
          hist.add(new History(aPre, 5, name + "xit CF.barter t=" + retOffer.getTerm(), "after", "myTrade", "nulled", "<<<<<<<", "<<<<<<<<<"));
          ec.addOHist(ohist, new History(aPre, 5, name + "xit CFbarter t=" + retOffer.getTerm(), "after", "myTrade", "nulled", "<<<<<<<", "<<<<<<<<<"));
        }// newTerm < 1
        hist.add(new History(aPre, 5, "xit2 CF.barter", "o=" + oname, "newTerm=" + newTerm, "entryTerm=" + entryTerm, (myTrade == null ? "!myTrade" : "myTrade"), (eTrad == null ? "!eTrad" : "eTrad"), "lhist" + lhista, "ehist" + ehist, "$=" + EM.mf(sumTotWorth)));
        return retOffer;
      }
      catch (Exception | Error ex) {
        eM.firstStack = eM.secondStack + "";
        ex.printStackTrace(eM.pw);
        ex.printStackTrace(System.err);
        eM.secondStack = eM.sw.toString();
        System.out.flush();
        System.err.flush();
        EM.newError = true;
        EM.tError = ("----CBAf----ERROR Barter Caught " + ex.toString() + ", cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName());
        System.err.println(EM.tError + eM.andMore());
        //     ex.printStackTrace(System.err);
        st.setFatalError();
        //throw new WasStopped(eM.tError);
        return retOffer;
      }
      // return retOffer;
    }

    /**
     * create Trades and initTrade if needed to get trade1YearTravelMaintCosts
     *
     * @return trade1YearTravelMaintCosts
     */
    double getSumTrade1YearTravelMaintCosts() {
      getTradeInit(sumTrade1YearTravelMaintCosts > NZERO);
      return sumTrade1YearTravelMaintCosts;
    }

    /**
     * get one of several Asset values for
     * Assets.getTrade1YearTravelMaintCosts() Assets.getTradeStrategicValues
     * Assets.getInitialNeeds
     *
     * @param forceInit
     */
    void getTradeInit(boolean forceInit) {
      if (myTrade == null) {
        myTrade = new Trades();
      }
      if (forceInit && !myTrade.inited) {
        Offer aOffer = new Offer(eM.year, EM.barterStart, ec, eM, ec, ec);
        myTrade.initTrade(aOffer, this); //sets trade1YearTravelMaintCosts
        myTrade.inited = true;
      }
    }

    /**
     * get tradingGoods and tradingOfferWorth assets.getTradingGoods nulls
     * CashFlow so myTrade is nulled sets flag didGoods
     *
     * @return the reference to bids
     */
    A2Row getTradingGoods() { //Assets.CashFlow.getTradingGoods
      if (!didGoods) {
        if (myTrade == null) {
          Offer inOffer = new Offer(eM.year, EM.barterStart, ec, eM, ec, ec);
          myTrade = new Trades();
          aPre = "b#";
          hist.add(new History(aPre, 5, " " + name + "now init", " a new", " trades"));
          hist.add(new History(aPre, 5, name + " ASYb2 R", resource.balance));
          hist.add(new History(aPre, 5, name + " initT S", staff.balance));
          hist.add(new History(aPre, 5, name + " initT C", c.balance));
          hist.add(new History(aPre, 5, name + " initT G", g.balance));
          inOffer.setMyIx(ec);
          // set bids, set tradingOfferWorth
          yphase = yrphase.PRESEARCH;
          myTrade.initTrade(inOffer, this);
        }
        term = EM.barterStart;
        myTrade.calcStrategicSums();
        didGoods = true;
      }
      return bids;
    }

    /**
     * get Assets.manuals
     *
     * @return return reference to Assets.manuals
     */
    ARow getManuals() { //Assets.CashFlow.getManuals
      return manuals;
    }

    /**
     * get newKnowledge
     *
     * @return Assets.newKnowledge
     */
    ARow getNewKnowledge() { //Assets.CashFlow
      return newKnowledge;
    }

    /**
     * get commonKnowledge
     *
     * @return Assets.commonKnowledge
     */
    ARow getCommonKnowledge() {//Assets.CashFlow
      return commonKnowledge;
    }

    /**
     * init values needed by swaps
     *
     */
    void yinitN() {  // Assets.CashFlow
      prevFlagg = flagg; // grow
      prevFlagh = flagh;  // health priority
      prevFlagf = flagf;   // grow xfer s if needed, ignore h
      prevFlagm = flagm; // xfer h
      int isDoNots = doNot.isDoNot();
      boolean isDoNotTrue = isDoNots > 0;
      histTitles("yinitN");
      hist.add(new History(aPre, 7, "n" + n + " yinitN", (swapped ? "swapped" : "!swapped"), "source=" + (source == null ? "null source" : source.aschar) + srcIx, "dest=" + (dest == null ? "null dest" : dest.aschar) + destIx, (failed ? "!doing" : "doing"), "rrg/rg=" + EM.mf(r.rawGrowth.ave() / r.balance.ave()), "srg/sb=" + EM.mf(s.rawGrowth.ave() / s.balance.ave()), (isDoNotTrue ? "stilldoNot" : "!doNot"), "$=" + EM.mf(sumTotWorth)));
      //     for (int k = 0; k < 4; k++) {
      //      sys[k].balanceWithPartner.set(sys[k].balance, sys[k].partner.balance);
      //     hist.add(new History(aPre, 5, "initN" + n + " " + aChar[k] + " balance", sys[k].balance));
      //    }

      gfFlag = fFlag = gFlag = geFlag = hFlag = nheFlag = heFlag = false;
      gmFlag = hmFlag = false;
      double nFrac = n / eM.maxn[pors];

      switch (yphase) {
        case TRADE:
          fFlag = nFrac <= eM.fFrac[pors];
          gFlag = nFrac <= eM.gFrac[pors];
          geFlag = nFrac > eM.geFrac[pors];
          gfFlag = nFrac <= eM.gfFrac[pors];
          hFlag = nFrac <= eM.hFrac[pors];
          heFlag = nFrac > eM.heFrac[pors];
          nheFlag = nFrac <= eM.nheFrac[pors];
          break;
        case GROW:
          /**
           * Growth and costs occur in the start yphase maintenance, travel, and
           * growth costs are increased if health < 1.0
           */

          gFlag = nFrac <= eM.gFrac[pors];
          gmFlag = nFrac <= eM.gmFrac[pors];
          geFlag = nFrac > eM.geFrac[pors];
          gfFlag = nFrac <= eM.gfFrac[pors];
          //hFlag = nFrac <= eM.hFrac[pors];
          // heFlag = nFrac > eM.heFrac[pors];
          //  nheFlag = nFrac <= eM.nheFrac[pors];
          break;
        case END:
          /**
           * Health must be ok
           */
          gFlag = nFrac <= eM.gFrac[pors];
          //    geFlag = nFrac <= eM.geFrac[pors];
          //   gfFlag = nFrac <= eM.gfFrac[pors];
          hFlag = nFrac <= eM.hFrac[pors];
          heFlag = nFrac > eM.heFrac[pors];
          hmFlag = nFrac <= eM.hmFrac[pors];
          nheFlag = nFrac <= eM.nheFrac[pors];

          break;
        case HEALTH:
          /**
           * end year, evaluate health again, if health < 0. die
           */
          hFlag = true;
          break;
        default:
          break;

      }
      emergHr = emergHs = false;
      doFailed = false;
    }

    /**
     * the efficiency is a function of knowledge and difficultyPercent The
     * relevance of each financial sector to the knowledge of a given sector is
     * in the requirement tables for growth and maintenance. This process logs
     * intermediate products on the way to efficiencies for resource and staff
     * during growth and maintenance/travel
     */
    protected void ycalcEfficiency() { // Assets.CashFlow.ycalcEfficiency
      resource.calcEfficiency();
      cargo.calcEfficiency();
      staff.calcEfficiency();
      guests.calcEfficiency();

    } // yCalcEfficiency   CashFlow

    int goodSBal = 0, bbba = 0, bbbc = 0;

    void start() {  // Assets.CashFlow.start called from initCashFlow
      EM.wasHere = "CashFlow.start() just after entry bbba=" + ++bbba + " didStart=" + didStart;
      if (!didStart) {
        if (iyW == null) { //first year initial only
          iyW = new DoTotalWorths();
          iyWTotWorth = iyW.getTotWorth();
          startYrSumWorth = initialSumWorth = sumTotWorth = iyW.getTotWorth();
          startYrSumKnowledge = initialSumKnowledge = iyW.sumKnowledgeBal;
          startYrSumKnowledgeWorth = initialSumKnowledgeWorth = iyW.sumKnowledgeWorth;

          //setStat(EM.BOTHCREATE, pors, clan, initialSumWorth, 1); //done in StarTrader
        }
        prevYrSumWorth = fyW == null ? iyW.getTotWorth() : fyW.getTotWorth();

        syW = new DoTotalWorths();
        syWTotWorth = syW.getTotWorth();
        pyW = fyW == null ? syW : fyW; // save earlier
        //prevYrSumWorth = startYrSumWorth;
        if (rawProspects2 != null) {
          prevProspects2 = rawProspects2.copy();
        }
        prevBalances = balances.copy(ec);
        prevYrSumKnowledge = startYrSumKnowledge;
        prevYrSumKnowledgeWorth = startYrSumKnowledgeWorth;
        startYrSumWorth = sumTotWorth = syW.getTotWorth();
        startYrSumKnowledge = syW.sumKnowledgeBal;
        startYrSumKnowledgeWorth = syW.sumKnowledgeWorth;
        didStart = true;
      }
    }

    int swapsN = -10; // final end in the loop
    double fracLoopsCost = -10.; // difference between initial and final swaps worth
    //  dif/startYearTot
    double fractradeCost = -10.; // difference between initial and final trade worth
    // dif/startYearTot

    int cccaa = 0, cccab = 0, cccac = 0, cccad = 0, cccae = 0, cccaf = 0;
    int ccca = 0, cccb = 0, cccc = 0, cccd = 0, ccce = 0, cccf = 0, cccg = 0, ccch = 0, ccci = 0, cccj = 0;
    int dddda = 0, ddddb = 0, ddddc = 0, dddde = 0, ddddf = 0, ddddg = 0, ddddh = 0, ddddi = 0, ddddj = 0;

    /**
     * yearEnd is the final routine in the cash flow. It is called after all
     * trades have happened, for ships it is invoked after the one to five
     * trades have been finished, the didYearEnd is set in econ preventing a
     * repeat yearEnd. prepare then do swaps to get the best rawProspects2 a
     * possible trade, to first do any growth, and then costs payments. Costs
     * payments are based on the financial status after a full set of swaps have
     * optimized as much as possible the growth that can occur. The costs are
     * the costs of maintenance, travel and growth. The travel was set at year
     * start if this is a ship
     *
     */
    double yearEnd() {  // Assets.CashFlow.yearEnd() after trading done
      String aPre = "E@";
      if (E.debugBarterOut) {
        eM.printHere("---CBAbb---", ec, " entering Assets.CashFlow.yearEnd Enter " + (tradeAccepted ? " tradeAccepted" : " !tradeAccepted") + (tradeRejected ? " tradeRejected" : " !tradeRejected") + (tradeLost ? " tradeLost" : " !tradeLost") + (tradeMissed ? " tradeMissed" : " !tradeMissed"));
      }
      if (eM.dfe()) {
        return 0.;
      }

      EM.setCurEcon(ec);
      ec.aYearEndTime = eM.now();
      eM.printHere("----YEacy----", ec, " starting Assets.CashFlow.yearEnd " + (tradeAccepted ? " tradeAccepted" : " !tradeAccepted") + (tradeRejected ? " tradeRejected" : " !tradeRejected") + (tradeLost ? " tradeLost" : " !tradeLost") + (tradeMissed ? " tradeMissed" : " !tradeMissed"));
      curGrowGoal = eM.goalGrowth[pors][clan];
      curMaintGoal = eM.goalHealth[pors][clan];
      preveHr = preveHs = emergHr = emergHs = false;
      //   for (int i = 6; i < 4; i++) { // disabled this ran in initCashFlow
      //    balances.A[i+2] = sys[i].balance = bals.getRow(BALANCESIX + i);
      //    sys[i].bonusUnitGrowth = bals.getRow(BONUSUNITSIX + i);
      //    sys[i].bonusYears = bals.getRow(BONUSYEARSIX + i);
      //   sys[i].depreciation = bals.getRow(CUMULATIVEDEPRECIATIONIX + i);
      //    growths.A[i+2] = sys[i].growth = growths.A[2+i] = bals.getRow(GROWTHSEFFIX + i);
      //   growths.A[2+i].setCnt++;
      //   }
      //     ec.saveHist = true;
      didStart = false;
      EM.wasHere = "CashFlow.yearEnd before start cccaa=" + ++cccaa;
      if (eM.dfe()) {
        return 0.;
      }
      start();
      didStart = true;
      if (E.debugBarterOut) {
        eM.printHere("---CBAba---", ec, "Assets.CashFlow.yearEnd after start " + (tradeAccepted ? " tradeAccepted" : " !tradeAccepted") + (tradeRejected ? " tradeRejected" : " !tradeRejected") + (tradeLost ? " tradeLost" : " !tradeLost") + (tradeMissed ? " tradeMissed" : " !tradeMissed"));
      }
      //   DoTotalWorths tW, rawCW, preSwapW,gSwapW, gGrowW, gCostW, fyW;
      preSwapW = new DoTotalWorths();
      preSwapWorth = preSwapW.getTotWorth();
      iyWTotWorth = iyW.getTotWorth();
      syWTotWorth = syW.getTotWorth();
      //     traded = copyy(cur);
      //    double preGrowLoop = totalWorth(), prercGrowLoop = sumRCWorth, presgGrowLoop = sumSGWorth;
      EM.wasHere = "CashFlow.yearEnd at beginning ccca=" + ++ccca;
      swapCosts.zero();
      //    sumTotWorth = doTotalWorth(hist, "preGSwaps", startYearTotalWorths, difTotalWorths, preGSwapsTotalWorths);
      // set travel years for the case of at the initial planet

      if (pors == E.P) {
        lightYearsTraveled = ((lightYearsTraveled < .2)) ? eM.initTravelYears[pors][0] : lightYearsTraveled;
      }
      else {
        lightYearsTraveled = 0.;
        useMTCosts = true;
      }

      //  for (m = startYrs.length - 1; m > 0; m--) {
      //   startYrs[m] = hcopyy(cur);
      // }
      cmd = SwpCmd.NOT;

      if (eM.dfe() || ec.dead || dead) {
        return 0.;
      }
      rawProspects2 = makeZero(rawProspects2);
      rawFertilities2 = makeZero(rawFertilities2);
      // initialize prevns to cmd = not
      HSwaps abc = new HSwaps();
      prevns[0] = abc.copyn(cur);
      eM.printHere("----AEYaa---", ec, "CashFlow.yearEnd before setting prevns array");
      for (m = prevns.length - 1; m > -1; m--) {
        prevns[m] = abc.copyn(cur);
      }
      eM.printHere("----AEYab---", ec, "CashFlow.yearEnd before swap loops after setting prevns array ");
      if (!dead && (pors == E.S) && newTradeYear2) {
        maintCosts10 = tradeTravelMaintCosts10.copy10();
        travelCosts10 = tradeTravelCosts10.copy10();
        lightYearsTraveled = 0.;
      }
      EM.addlErr = ""; // clear addlErr

      yphase = yrphase.DOLOOPS;
      doLoop("G@", yrphase.DOLOOPS, prevns[0]);//do swaps
      eM.printHere("----AEYac---", ec, " CashFlow.yearEnd just after swap doLoop");
      bals.set4(ABalRows.GROWTHS3IX, growths); //in Assets.CashFlow.yCalcRawCosts
      // sLoops[0] = 0;
      gSwapW = new DoTotalWorths();  // worth after swap loops pre grow costs
      sumTotWorth = gSwapWTotWorth = gSwapW.getTotWorth();
      gSwapIncr = gSwapWTotWorth - preSwapWorth;
      fracLoopsCost = (sumTotWorth - startYrSumWorth) / startYrSumWorth;

      if (History.dl > 4) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
        hist.add(new History(aPre, 5, "n" + n + "xloop1", ">>>", a0.getMethodName(), "at", a0.getFileName(), wh(a0.getLineNumber()), (swapped ? "swapped" : "!swapped"), "prevN=" + prevn, "n=" + wh(n)));

      }
      hist.add(new History(aPre, 7, ">>>>end grow n" + prevn + "to" + n, (fFlag ? "f" : "!f") + whole(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + whole(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + whole(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + whole(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "h" : "!he") + whole(eM.maxn[pors] * eM.heFrac[pors]), "max=" + whole(eM.maxn[pors]), "<<<<"));
      hist.add(new History(aPre, 7, ">>>end grow loop", "<<<<"));
      // recalculate growth and costs after all the swaps
      yphase = yrphase.GROW;
      lTitle = "grow & grow costs";
      histTitles(lTitle);
      if (History.dl > 4) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
        StackTraceElement aa = Thread.currentThread().getStackTrace()[2];
        StackTraceElement ab = Thread.currentThread().getStackTrace()[3];
        hist.add(new History(aPre, 5, "n" + n + "set preCosts", ">>>", a0.getMethodName(), "at", a0.getFileName(), wh(a0.getLineNumber()), (swapped ? "swapped" : "!swapped"), "n=" + wh(n), "yCalcCosts", "next", "<<<<<<"));
      }
      if (eM.dfe()) {
        return 0.;
      }
      yCalcCosts(aPre, lightYearsTraveled, curGrowGoal, curMaintGoal); //renew rawProspects2 etc.
      EM.wasHere = "CashFlow.endYear after yCalcCosts cccad=" + ++cccad;
      if (!didInitRawProspects) {
        initRawProspects2 = rawProspects2.copy();
        didInitRawProspects = true;
      }
      sos = rawProspects2.min() < eM.rawHealthsSOS3[0][0];
      bals.set4(ABalRows.GROWTHS5IX, growths);//SWAPPEDGROWTHSIX
      bals.set4(ABalRows.SWAPPEDGROWTHSIX, growths);
      //  bals.set(ABalRows.SWAPPEDRCSGSUMIX, gSwapW.sumRCSGBal);
      // choose only the living for these results/ deaths stats are later
      eM.printHere("----AEYag---", ec,
                   " joint live&dead before live test rawProspects2="
                   + EM.mf(rawProspects2.curMin())
                   + (rawProspects2.curMin() > PZERO ? " stay live" : " start dead")
      );

      // count future fund live or dead
      eM.clanFutureFunds[clan] += yearsFutureFund;
      yearsFutureFundTimes = 0;
      yearsFutureFund = 0.;
      excessFutureFund = emergeFutureFund = 0.;
      int prevAccYears = EM.year - lastAcceptedYear;

      double rawProspectsMax = rawProspects2.max();
      double rawProspectsMin = rawProspects2.min();
      double rawRProspectsMin = rawProspects2.getARow(0).min();
      double rawSProspectsMin = rawProspects2.getRow(1).min();

      //count only the smallest available prospect for any sector
      if (rawProspectsMin < eM.rawHealthsSOS3[0][0]) {
        setStat(eM.ISSOS3, worthIncrPercent);
      }
      else if (rawProspectsMin < eM.rawHealthsSOS2[0][0]) {
        setStat(eM.ISSOS2, worthIncrPercent);
      }
      else if (rawProspectsMin < eM.rawHealthsSOS1[0][0]) {
        setStat(eM.ISSOS1, worthIncrPercent);
      }
      else if (rawProspectsMin < eM.rawHealthsSOS0[0][0]) {
        setStat(eM.ISSOS0, worthIncrPercent);
      }
      else if (rawProspectsMin < eM.rawHealthsLow[0][0]) {
        setStat(eM.ISLOW, worthIncrPercent);
      }
      // incremate entries to cumulative values after 1 year

      // find number of years without trade accepted 3 max
      int ixAccYears = prevAccYears > 3 || prevAccYears < 0 ? 0 : prevAccYears;
      gSwapW = new DoTotalWorths(); // do again, needed for dead
      if (rawProspects2.curMin() > PZERO) { //proceed  live if no min < PZERo
        //========================LIVE LIVE LIVE ========================
        n = 0;
        if (History.dl > History.informationMinor9) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          hist.add(new History(aPre, History.informationMinor9, "n" + n + "preCosts", ">>>", a0.getMethodName(), "at", a0.getFileName(), wh(a0.getLineNumber()), (swapped ? "swapped" : "!swapped"), "n=" + wh(n), "<<<<<<"));
        }

        lTitle = "grow w costs";
        histTitles(lTitle);
        //     sumTotWorth = doTotalWorth(hist, "post2GSwaps", nextTotalWorths, difTs, postMTGCostsTotalWorths);
        setStat(EM.SWAPRINCRWORTH, pors, clan, gSwapIncr, 1);
        setStat(EM.POSTSWAP, pors, clan, gSwapWTotWorth, 1);
        setStat(EM.POSTSWAPRCSG, pors, clan, gSwapW.getSumRCSGBal(), 1);
        bals.copy4BtoC(ABalRows.GROWTHSEFFIX, ABalRows.GROWTHS4IX);
        bals.set4(ABalRows.GROWTHS6IX, growths);
//      EM.gameRes.PREGROWTH.wet(pors, clan, preGrowLoop - preGWorth);
        //     EM.gameRes.CUMPREGROWTH.wet(pors, clan, preGrowLoop - preGWorth);
        growths.sendHist(hist, "G@");
        hist.add(new History("G@", History.valuesMajor6, "r.growth", r.growth));
        eM.printHere("----AEYah---", ec, "CashFlow.endYear before growths");
        if (r.growth != growths.A[2]) {
          eM.aErr("r.growth not the same as growths.A[0] ccca=" + ++ccca);
        }
        if (eM.dfe()) {
          return 0.;
        }
        // do growths of knowledge and each SubAsset
        double tm = 1.;
        //  tm = 100000.0;

        setStat(EM.PREVGROWTHS, pors, clan, bals.sum4(ABalRows.PREVGROWTHSIX), 1);

        setStat(EM.RAWYEARLYUNITGROWTH, pors, clan, tm * bals.sum4(ABalRows.RAWYEARLYUNITGROWTHSIX));
        setStat(EM.MAXRAWUNITGROWTH, pors, clan, tm * bals.sum4(ABalRows.MAXRAWYEARLYUNITGROWTHSIX));
        setStat(EM.RAWGROWTHS, tm * bals.sum4(ABalRows.RAWGROWTHSIX));
        setStat(EM.GROWTHS, bals.sum4(ABalRows.GROWTHSEFFIX));

        setStat(EM.NEWDEPRECIATION, bals.sum4(ABalRows.NEWDEPRECIATIONIX));
        setStat(EM.DEPRECIATION, bals.sum4(ABalRows.DEPRECIATIONIX));
        setStat(EM.REPRECIATION, bals.sum4(ABalRows.REPRECIATIONIX));
        setStat(EM.PRECIATION, bals.sum4(ABalRows.PRECIATIONIX));
        setStat(EM.GROWTHCOSTS, bals.sum4(ABalRows.GROWTHCOSTSIX));

        setStat(EM.FERTILITYGROWTHCOSTS, bals.sum4(ABalRows.FERTILITYGROWTHCOSTSIX));
        setStat(EM.FERTILITYGROWTHS, bals.sum4(ABalRows.FERTILITYGROWTHSIX));
        setStat(EM.PREVGROWTHS, bals.sum4(ABalRows.PREVGROWTHSIX));
        setStat(EM.RAWGROWTHS, bals.sum4(ABalRows.RAWGROWTHSIX));
        setStat(EM.REQMINFRAC2S, bals.sum2(ABalRows.REQFERTMINFRAC2IX));
        setStat(EM.REQGFRAC2S, bals.sum2(ABalRows.RQGFERTFRAC2IX));
        setStat(EM.REQMFRAC2S, bals.sum2(ABalRows.RQMFERTFRAC2IX));

        // setStat(EM.RAWGROWTHS, bals.sum4(ABalRows.RAWGROWTHSIX));
        setStat(EM.RAWPROSPECTS, bals.sum2(ABalRows.RAWPROSPECTS2IX), 1);
        setStat(EM.GROWTHSEFF, pors, clan, bals.sum4(ABalRows.GROWTHSEFFIX), 1);
        setStat(EM.RGROWTHSEFF, pors, clan, bals.rowSum(ABalRows.GROWTHSEFFIX), 1);
        setStat(EM.RGROWTHSEFF + 1, pors, clan, bals.rowSum(ABalRows.GROWTHSEFFIX + 1), 1);
        setStat(EM.RGROWTHSEFF + 2, pors, clan, bals.rowSum(ABalRows.GROWTHSEFFIX + 2), 1);
        setStat(EM.RGROWTHSEFF + 3, pors, clan, bals.rowSum(ABalRows.GROWTHSEFFIX + 3), 1);
        /*
        setStat(EM.RAWRGROWTH, pors, clan,tm* bals.rowSum(ABalRows.RAWYEARLYUNITGROWTHSIX), 1);
        setStat(EM.RAWCGROWTH, pors, clan,tm* bals.rowSum(ABalRows.RAWYEARLYUNITGROWTHSIX+1), 1);
        setStat(EM.RAWSGROWTH, pors, clan,tm* bals.rowSum(ABalRows.RAWYEARLYUNITGROWTHSIX+2), 1);
        setStat(EM.RAWGGROWTH, pors, clan,tm* bals.rowSum(ABalRows.RAWYEARLYUNITGROWTHSIX+3), 1);
         setStat(EM.RAWRUGROWTH, pors, clan,tm* bals.rowSum(ABalRows.RAWUNITGROWTHSIX), 1);
        setStat(EM.RAWCUGROWTH, pors, clan,tm* bals.rowSum(ABalRows.RAWUNITGROWTHSIX+1), 1);
        setStat(EM.RAWSUGROWTH, pors, clan,tm* bals.rowSum(ABalRows.RAWUNITGROWTHSIX+2), 1);
        setStat(EM.RAWGUGROWTH, pors, clan,tm* bals.rowSum(ABalRows.RAWUNITGROWTHSIX+3), 1);
         */
        r.worth.setAmultV(r.balance, eM.nominalWealthPerResource[pors]);
        c.worth.setAmultV(c.balance, eM.nominalWealthPerResource[pors] * eM.cargoWorthBias[0]);
        s.sumGrades(); // sets s worth
        g.sumGrades(); // sets g worth
        /*
        bals.copy4BtoC(ABalRows.GROWTHSIX, ABalRows.GROWTHS7IX);
        bals.copy4BtoC(ABalRows.CURWORTHSIX, ABalRows.PREVWORTHSIX);
        setStat(EM.RGROWTH5, tm * bals.sum(ABalRows.GROWTHS5IX));
        setStat(EM.RGROWTH6, tm * bals.sum(ABalRows.GROWTHS6IX));
        setStat(EM.RGROWTH7, tm * bals.sum(ABalRows.GROWTHS7IX));
        setStat(EM.RGROWTH8, tm * bals.sum(ABalRows.GROWTHS8IX));
         */
        doGrowth(aPre);
        r.worth.setAmultV(r.balance, eM.nominalWealthPerResource[pors]);
        c.worth.setAmultV(c.balance, eM.nominalWealthPerResource[pors] * eM.cargoWorthBias[0]);
        s.sumGrades(); // sets s worth
        g.sumGrades(); // sets g worth
        bals.setA4toBminusC(ABalRows.GROWTHWORTHSIX, ABalRows.CURWORTHSIX, ABalRows.PREVWORTHSIX);
        //  bals.setA4toBmultC(ABalRows.BONUSWHORTHIX, ABalRows.GROWTHWORTHSIX, ABalRows.YEARLYBONUSSUMGROWTHVALIX);
        //  bals.copy4BtoC( ABalRows.YEARLYBONUSSUMGROWTHVALIX,ABalRows.BONUSWHORTHIX);
        // bals.setA4toBaddC(ABalRows.CUMBONUSWORTHIX, ABalRows.CUMBONUSWORTHIX, ABalRows.BONUSWHORTHIX);
        //  double sumBonusWorth = bals.sum4(ABalRows.BONUSWHORTHIX);

        //  setStat(EM.CATWORTHINCR, pors, clan, sumBonusWorth, 1);
        //
        //setStat(EM.CUMCATWORTH, pors, clan, bals.sum4(ABalRows.CUMBONUSWORTHIX), 1);
        //  setStat(EM.GROWTHWORTHINCR, pors, clan, bals.sum4(ABalRows.GROWTHWORTHSIX), 1);
        EM.wasHere = "CashFlow.endYear after doGrowth cccae" + ++cccae;
        gGrowW = new DoTotalWorths(); // worth after years growth
        sumTotWorth = gGrowW.getTotWorth();
        double sumRCSGincr = gGrowW.sumRCSGBal - gSwapW.sumRCSGBal;
        // setStat(EM.INCRGROWRCSG, pors, clan, sumRCSGincr, 1);

        if (History.dl > History.informationMinor9) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          //      hist.add(new History(aPre,History.informationMinor9, "post Growth", ">>>at", wh(a0.getLineNumber()), "H=" + EM.mf(yearStartHealth), "$$ didGrow", EM.mf(postGWorth), EM.mf(postGWorth - preGWorth), "trade$$", EM.mf(preTradeWorth), EM.mf(sumTotWorth - preTradeWorth), EM.mf(sumTotWorth)));

        }
        yphase = yrphase.PAY;
        double rem = 0;
        EM.wasHere = " CashFlow.yearEnd.after yrphase.pay cccd=" + ++cccd;
        if ((rem = bals.curSum() - mtgCosts10.curSum()) < PZERO) {
          E.myTest(true, "year end costsSum= %7.3f exceeds balancesSum= %7.3f,remnantSum= %7.3f age=" + ec.age + ", year=" + EM.year + ", rc sum=" + EM.mf(bals.getRow(0).sum()), mtgCosts10.curSum(), bals.curSum(), rem);
        }
        if (eM.dfe()) {
          return 0.;
        }
        // live accounts
        bals.copy4BtoC(ABalRows.CURWORTHSIX, ABalRows.PREVWORTHSIX);
        doMaintCost(aPre);
        EM.wasHere = "CashFlow yearEnd live after doMaintCost cccaf=" + ++cccaf;
        doTravCost(aPre);
        doGrowthCost(aPre);
        bals.set2(ABalRows.MTGCOSTSIX, mtgCosts10.getRow(0));
        bals.set2(ABalRows.MTGCOSTSIX + 1, mtgCosts10.getRow(1));
        double mtgCosts = bals.sum2(ABalRows.MTGCOSTSIX);
        r.worth.setAmultV(r.balance, eM.nominalWealthPerResource[pors]);
        c.worth.setAmultV(c.balance, eM.nominalWealthPerResource[pors] * eM.cargoWorthBias[0]);
        s.sumGrades(); // sets s worth
        g.sumGrades(); // sets g worth
        //      DoTotalWorths syW, tW, gSwapW, gGrowW, gCostW, fyW;
        gCostW = new DoTotalWorths();  // worth after costs
        sumTotWorth = gCostW.getTotWorth();  //after costs taken
        bals.setA4toBminusC(ABalRows.COSTWORTHSIX, ABalRows.CURWORTHSIX, ABalRows.PREVWORTHSIX);
        setStat(EM.MTGCOSTS, mtgCosts10.curSum());

        setStat(EM.COSTWORTHDECR, bals.sum4(ABalRows.COSTWORTHSIX));
        EM.wasHere = "CashFlow.YearEnd live after doGrowth & do...Cost ccce=" + ++ccce;
        if (History.dl > History.informationMinor9) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          //       hist.add(new History(aPre, History.informationMajor8, "post nCosts", ">>>at", wh(a0.getLineNumber()), "H=" + EM.mf(yearStartHealth), "$$ didCosts", EM.mf(postGrowthTotalWorths[0] - postMTGCostsTotalWorths[0]), "pretrade$$", EM.mf(startYearTotalWorths[0]), EM.mf(sumTotWorth - preTradeTotalWorths[0]), EM.mf(sumTotWorth)));

        }
        yphase = yrphase.END;
        swapCosts.zero();
//      hist.add(new History(20, "yEnd Health", "0Life", "1Struct", "2Energy", "3Propel", "4Defense", "5Gov", "6Col", "Min", "Sum", "Ave"));
        lTitle = "HealthCosts " + name;
        //       doLoop(aPre, yrphase.END, prevns[0]);

        //     sumTotWorth = doTotalWorth(hist, "endYear", postMTGCostsTotalWorths, difTs, endYearTotalWorths);
        //    EM.gameRes.HLTHSWAPCOSTS.set(pors, clan, difTs[0]);
//      health = Math.min(resource.health.min(), staff.health.min());
        if (History.dl > History.informationMinor9) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          hist.add(new History(aPre, History.valuesMinor7, ">>>n" + n + "post Health at", wh(a0.getLineNumber()), (swapped ? "swapped" : "!swapped"), "n=" + wh(n), "H=" + EM.mf(health), "$=" + EM.mf(sumTotWorth)));

        }
        if (eM.dfe()) {
          return 0.;
        }
        //live
        fyW = new DoTotalWorths();
        fyW.setPrev(syW);
        // aiWorth = fyWAIWorth = sumTotWorth = fyW.getTotWorth();
        if (History.dl > History.informationMinor9) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          hist.add(new History(aPre, History.valuesMinor7, "n" + n + "post Health", ">>> at", wh(a0.getLineNumber()), "H=" + EM.mf(rawProspects2.curMin()), "Ntrade$$", EM.mf(startYrSumWorth), EM.mf(sumTotWorth - startYrSumWorth), EM.mf(sumTotWorth)));

        }
        if (E.debugEconCnt) {
          if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
            EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
          }
        }
        EM.isHere1(ec, " CashFlow.yearEnd live near end before stats=" + ++cccf);
        ec.saveHist = false;
        hist.add(new History(History.loopMinorConditionals5, ">>>>end Health", "H=" + EM.mf(health), (fFlag ? "f" : "!f") + whole(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + whole(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + whole(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + whole(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "eh" : "!eh") + whole(eM.maxn[pors] * eM.heFrac[pors]), "max=" + whole(eM.maxn[pors]), "<<<<"));
        //     hist.add(new History(4, ">>>end swap loop", "<<<<"));
        //  startYearTotWorth = syW.getTotWorth();
        double tprev = 0.;
        double totalYearWorthIncr = fyW.sumTotWorth - syW.sumTotWorth;
        double percentYearWorthIncr = (totalYearWorthIncr * 100.) / syW.sumTotWorth;
        //   double sumGrowth = fyW.sumGrowths;
        double sumYearRCSGincr = (fyW.sumRCSGBal - syW.sumRCSGBal);
        double percentYearRCSGincr = sumYearRCSGincr * 100 / syW.sumRCSGBal;

        EM.wasHere = "CashFlow.yearEnd live before many setStat ccci=" + ++ccci;
        setStat(EM.LIVEWORTH, pors, clan, fyW.sumTotWorth, 1);
        setStat(EM.STARTWORTH, pors, clan, Math.sqrt(initialSumWorth), 1);
        setStat(EM.WINNERYEARS, pors, clan, EM.whichScorePosByIncrClan[clan] == 4 ? 1. : 0., 1);
        setStat(EM.WORTHINCR, pors, clan, percentYearWorthIncr, 1);
        setStat(EM.KNOWLEDGEW, pors, clan, fyW.sumKnowledgeWorth, 1);
        setStat(EM.DEPRECIATION, pors, clan, bals.sum4(ABalRows.DEPRECIATIONIX), 1);
        //setStat(EM.PREVGROWTH, pors, clan, bals.sum4(ABalRows.PREVGROWTHSIX), 1);
        //  setStat(EM.RCSG, pors, clan, syW.getSumRCSGBal(), fyW.getSumRCSGBal()), 1);
        // setStat(EM.INCRRCSG, sumYearRCSGincr);
        setStat(EM.LIVERCSG, fyW.getSumRCSGBal());
        setStat(EM.STARTRCSG, syW.getSumRCSGBal());
        setStat(EM.INITRCSG, iyW.getSumRCSGBal());
        setMax(EM.MAXRCSG, fyW.getSumRCSGBal());
        setMin(EM.MINRCSG, pors, clan, fyW.getSumRCSGBal(), 1);
        if (ec.getHiLo()) {
          setStat(EM.HIGHRCSG, pors, clan, fyW.getSumRCSGBal(), 1);
        }
        else {
          setStat(EM.LOWRCSG, pors, clan, fyW.getSumRCSGBal(), 1);
        }
        int[] worthIncrA = {EM.WORTHINCRN0, EM.WORTHINCRN1, EM.WORTHINCRN2, EM.WORTHINCRN3};
        // int[] growthsA = {EM.GROWTHSN0, EM.GROWTHSN1, EM.GROWTHSN2, EM.GROWTHSN3};
        int[] fertilitiesA = {EM.FERTILITYSN0, EM.FERTILITYSN1, EM.FERTILITYSN2, EM.FERTILITYSN3};
        //int[] rcsgIncrA = {EM.RCSGINCRN0, EM.RCSGINCRN1, EM.RCSGINCRN2, EM.RCSGINCRN3};
        setStat(worthIncrA[ixAccYears], pors, clan, percentYearWorthIncr, 1);
        // setStat(growthsA[ixAccYears], pors, clan, bals.sum4(ABalRows.GROWTHSIX), 1);
        // setStat(rcsgIncrA[ixAccYears], pors, clan, sumYearRCSGincr, 1);
        setStat(fertilitiesA[ixAccYears], pors, clan, gSwapW.aveFertility, 1);
        if (tradeAccepted) {
          setStat(EM.TRADEWORTH, pors, clan, fyW.getTotWorth(), 1);
          if (ec.getHiLo()) {
            setStat(EM.HIGHWORTH, pors, clan, fyW.getTotWorth(), 1);
          }
          else {
            setStat(EM.LOWWORTH, pors, clan, fyW.getTotWorth(), 1);
          }
        }
        else {
          setStat(EM.MISCWORTH, pors, clan, fyW.getTotWorth(), 1);
          if (ec.getHiLo()) {
            setStat(EM.MISCHIGHWORTH, pors, clan, fyW.getTotWorth(), 1);
          }
          else {
            setStat(EM.MISCLOWWORTH, pors, clan, fyW.getTotWorth(), 1);
          }
        }
        setStat(EM.WORTHIFRAC, iyW.sumTotWorth == 0.0 ? 0.0 : 100. * (fyW.sumTotWorth - (tprev = iyW.sumTotWorth)) / tprev, 1);
        worthIncrPercent = startYrSumWorth == 0 ? 0. : 100. * (sumTotWorth - doubleTrouble(startYrSumWorth)) / doubleTrouble(startYrSumWorth);
        // setStat(EM.WORTHINCR, worthIncrPercent, 1);
        // (final - start)*100/start get RC worth % increase
        double rcPercentInc = (tprev = syW.getSumRCBal()) == 0.0 ? 0.0 : 100. * (fyW.getSumRCBal() - tprev) / tprev;
        double sgPercentInc = (tprev = syW.getSumSGBal()) == 0.0 ? 0.0 : 100. * (fyW.getSumSGBal() - (tprev)) / tprev;
        setStat(EM.RCTBAL, 100. * fyW.getSumRCBal() / fyW.getSumRCSGBal(), 1);
        setStat(EM.RCBAL, fyW.getSumRCBal(), 1);
        double sBal = s.balance.sum();
        setStat("sWorth", calcPercent(sBal, s.worth.sum()), 1);
        setStat("sWork", calcPercent(sBal, s.work.sum()), 1);
        setStat("sFacultyEquiv", calcPercent(sBal, s.facultyEquiv.sum()));
        setStat("sResearcherEquiv", calcPercent(sBal, s.researcherEquiv.sum()));
        setStat("sColonists", calcPercent(sBal, s.colonists.sum()));
        setStat("sEngineers", calcPercent(sBal, s.engineers.sum()));
        setStat("sFaculty", calcPercent(sBal, s.faculty.sum()));
        setStat("sResearchers", calcPercent(sBal, s.researchers.sum()));
        setStat("sKnowledge", calcPercent(eM.nominalKnowledgeForBonus[0], knowledge.sum()));

        if (E.debugStats) {
          System.out.println("----YEa-----print " + ec.name + " yearEnd rcPercentInc =" + E.mf(rcPercentInc) + "<<<<<");
        }
        if (E.debugMisc && (syW.getSumRCBal() == 0.0)) {
          throw new MyErr("zero syW.getSumRCBal()=" + EM.mf(syW.getSumRCBal()));
        }
        EM.isHere1(ec, "CashFlow.yearEnd live before many setStat dddda=" + ++dddda);
        setStat(EM.RCTGROWTHPERCENT, rcPercentInc, 1);
        if (rcPercentInc < .5) {
          setStat("RCGLT.5PERCENT", pors, clan, rcPercentInc, 1);
        }
        else if (rcPercentInc < 2) {
          setStat("RCGLT2PERCENT", pors, clan, rcPercentInc, 1);
        }
        else if (rcPercentInc < 5) {
          setStat("RCGLT5PERCENT", pors, clan, rcPercentInc, 1);
        }
        else if (rcPercentInc < 10) {
          setStat(EM.RCGLT10PERCENT, rcPercentInc, 1);
        }
        else if (rcPercentInc < 25) {
          setStat("RCGLT25PERCENT", pors, clan, rcPercentInc, 1);
        }
        else if (rcPercentInc < 50) {
          setStat("RCGLT50PERCENT", pors, clan, rcPercentInc, 1);
        }
        else if (rcPercentInc < 100) {
          setStat(EM.RCGLT100PERCENT, pors, clan, rcPercentInc, 1);
        }
        else {
          setStat("RCGGT100PERCENT", pors, clan, rcPercentInc, 1);
        }
        EM.isHere1(ec, "CashFlow.yearEnd before many setStat ddddb=" + ++ddddb);
        if (eM.dfe()) {
          return 0.;
        }
        double rcWorthPercentInc = 100. * (fyW.getSumRCWorth() - syW.getSumRCWorth()) / syW.getSumRCWorth();
        double rcwp = rcWorthPercentInc;
        if (E.debugMisc && (syW.getSumRCWorth() == 0.0)) {
          throw new MyErr("syW.getSumRCWorth() =" + E.mf(syW.getSumRCWorth()));
        }
        setStat(EM.RCTWORTH, 100. * fyW.getSumRCWorth() / fyW.sumTotWorth, 1);
        setStat(EM.RCWORTH, fyW.getSumRCWorth(), 1);
        setStat(EM.RCWORTHGROWTHPERCENT, pors, clan, rcWorthPercentInc, 1);
        if (rcwp < .5) {
          setStat("RCWGLT.5PERCENT", pors, clan, rcWorthPercentInc, 1);
        }
        else if (rcwp < 2) {
          setStat("RCWGLT2PERCENT", pors, clan, rcWorthPercentInc, 1);
        }
        else if (rcwp < 5) {
          setStat("RCWGLT5PERCENT", pors, clan, rcWorthPercentInc, 1);
        }
        else if (rcwp < 10) {
          setStat(EM.RCWGLT10PERCENT, pors, clan, rcWorthPercentInc, 1);
        }
        else if (rcwp < 25) {
          setStat("RCWGLT25PERCENT", pors, clan, rcWorthPercentInc, 1);
        }
        else if (rcwp < 50) {
          setStat("RCWGLT50PERCENT", pors, clan, rcWorthPercentInc, 1);
        }
        else if (rcwp < 100) {
          setStat("RCWGLT100PERCENT", pors, clan, rcWorthPercentInc, 1);
        }
        else {
          setStat("RCWGGT100PERCENT", pors, clan, rcWorthPercentInc, 1);
        }
        setStat("RCWGPERCENT", pors, clan, rcWorthPercentInc, 1);

        double bcurSum = bals.curSum(); //rcsg sum
        double totWorth = syW.getTotWorth();
        setStat(EM.RCMTGC, pors, clan, calcPercent(bcurSum, mtgCosts10.getRow(0).sum()), 1);
        setStat(EM.SGMTGC, pors, clan, calcPercent(bcurSum, mtgCosts10.getRow(1).sum()), 1);
        setStat(EM.RRAWMC, pors, clan, calcPercent(bcurSum, maintCosts10.getRow(0).sum()), 1);
        setStat(EM.SRAWMC, pors, clan, calcPercent(bcurSum, maintCosts10.getRow(1).sum()), 1);
        setStat(EM.RCREQGC, pors, clan, calcPercent(bcurSum, reqMaintCosts10.getRow(0).sum()), 1);
        setStat(EM.SGREQGC, pors, clan, calcPercent(bcurSum, reqMaintCosts10.getRow(1).sum()), 1);
        setStat(EM.RCREQMC, pors, clan, calcPercent(bcurSum, reqGrowthCosts10.getRow(0).sum()), 1);
        setStat(EM.SGREQMC, pors, clan, calcPercent(bcurSum, reqGrowthCosts10.getRow(1).sum()), 1);
        EM.isHere1(ec, "CashFlow.yearEnd before many setStat ddddc=" + ++ddddc);
        setStat(EM.RCTBAL, pors, clan, calcPercent(totWorth, fyW.getSumRCBal()), 1);

        setStat(EM.SGTBAL, pors, clan, calcPercent(totWorth, fyW.getSumSGBal()), 1);
        setStat(EM.SBAL, pors, clan, calcPercent(totWorth, fyW.getSumSBal()), 1);
        setStat(EM.GBAL, pors, clan, calcPercent(totWorth, fyW.getSumGBal()), 1);
        setStat(EM.RBAL, pors, clan, calcPercent(totWorth, fyW.getSumRBal()), 1);
        setStat(EM.CBAL, pors, clan, calcPercent(totWorth, fyW.getSumCBal()), 1);
        //      DoTotalWorths iyW,syW, tW, gSwapW, gGrowW, gCostW, fyW;
        setStat(EM.POORKNOWLEDGEEFFECT, poorKnowledgeAveEffect, 1);
        setStat(EM.POORHEALTHEFFECT, poorHealthAveEffect, 1);
        // gameRes.MANUALSB.wet(pors, clan, manuals.sum(), 1);
        // in live Assets.CashFlow.yearEnd()
        //eM.setStat(EM.MANUALSFRAC, pors, clan, 100. * manuals.sum() * eM.nominalWealthPerTradeManual[pors] / totWorth, 1);
        setStat(EM.MANUALSFRAC, pors, clan, fyW.sumManualsBal / fyW.sumKnowledgeBal, 1);
        // gameRes.NEWKNOWLEDGEB.wet(pors, clan, newKnowledge.sum() / knowledge.sum(), 1);
        setStat(EM.NEWKNOWLEDGEFRAC, pors, clan, fyW.sumNewKnowledgeWorth / fyW.sumTotWorth, 1);
        EM.isHere1(ec, "CashFlow.yearEnd before many setStat dddde=" + ++dddde);
        // gameRes.COMMONKNOWLEDGEB.wet(pors, clan, commonKnowledge.sum() / knowledge.sum(), 1);
        setStat(EM.COMMONKNOWLEDGEFRAC, pors, clan, fyW.sumCommonKnowledgeWorth / fyW.sumTotWorth, 1);
        // gameRes.KNOWLEDGEINCR.wet(pors, clan, (knowledge.sum() - (tprev = asyW.getKnowledgeBal())) / tprev, 1);
        setStat(EM.KNOWLEDGEINCR, pors, clan, fyW.sumKnowledgeBal / syW.sumKnowledgeBal, 1);
        // gameRes.NEWKNOWLEDGEINCR.wet(pors, clan, (newKnowledge.sum() - (tprev = asyW.getNewKnowledgeBal())) / tprev);
        if ((tprev = syW.sumNewKnowledgeWorth) > PZERO) {
          setStat(EM.NEWKNOWLEDGEINCR, pors, clan, fyW.sumNewKnowledgeBal / syW.sumNewKnowledgeBal, 1);
        }
        // gameRes.COMMONKNOWLEDGEINCR.wet(pors, clan, (commonKnowledge.sum() - (tprev = asyW.getCommonKnowledgeBal())) / tprev, 1);
        if ((tprev = syW.getSumCommonKnowledgeWorth()) > PZERO) {
          setStat(EM.COMMONKNOWLEDGEINCR, pors, clan, 100. * (fyW.getSumCommonKnowledgeWorth() - tprev) / tprev, 1);
        }
        // gameRes.MANUALSINCR.wet(pors, clan, (manuals.sum() - (tprev = asyW.getManualsBal())) / tprev, 1);
        if ((tprev = syW.getSumManualsBal()) > PZERO) {
          setStat(EM.MANUALSINCR, pors, clan, 100. * (fyW.getSumManualsBal() - (tprev)) / tprev, 1);
        }

        //double worthIncrPercent = (sumTotWorth - startYrSumWorth)*100 / startYrSumWorth;
        // setStat(EM.WORTHINCR, pors, clan, worthIncrPercent, 1);
        // gameRes.RCTBAL.wet(pors, clan, fyW.sumRCBal, 1);
        setStat(EM.RCfrac, pors, clan, fyW.sumRCWorth / fyW.sumTotWorth, 1);
        // gameRes.SGTBAL.wet(pors, clan, fyW.sumSG, 1);
        setStat(EM.SGfrac, pors, clan, fyW.sumSGWorth / fyW.sumTotWorth, 1);
        setStat(EM.KNOWLEDGEFRAC, pors, clan, fyW.sumKnowledgeWorth / fyW.sumTotWorth, 1);
        setStat(EM.DIEDPERCENT, pors, clan, 0., 1);  // didn't die
        double criticalStrategicRequestsPercentTWorth = sumCriticalStrategicRequests / startYrSumWorth;
        double criticalStrategicRequestsPercentFirst = (criticalStrategicRequestsFirst - sumCriticalStrategicRequests) / criticalStrategicRequestsFirst;
        double criticalNominalReceiptsFracWorth = sumNominalRequests / startYrSumWorth;
        double criticalNominalRequestsFracFirst = criticalNominalRequests / criticalNominalRequestsFirst;
        tW = new DoTotalWorths();
        // double worthincr1 = 100. * (fyW.sumTotWorth - syW.sumTotWorth) / syW.sumTotWorth;
        setStat("WTRADEDINCR", pors, clan, worthIncrPercent, 1);
        if (eM.dfe()) {
          return 0.;
        }
        // check for commit again
        if (tradeAccepted) {
          setStat(EM.DWORTH, pors, clan, fyW.getTotWorth(), 1);
          if (ec.getHiLo()) {
            setStat(EM.DHIGHWORTH, pors, clan, fyW.getTotWorth(), 1);
          }
          else {
            setStat(EM.DLOWWORTH, pors, clan, fyW.getTotWorth(), 1);
          }
        }
        else {
          setStat(EM.DMISCWORTH, pors, clan, fyW.getTotWorth(), 1);
          if (ec.getHiLo()) {
            setStat(EM.DMISCHIGHWORTH, pors, clan, fyW.getTotWorth(), 1);
          }
          else {
            setStat(EM.DMISCLOWWORTH, pors, clan, fyW.getTotWorth(), 1);
          }
        }
        if (tradeAccepted && oClan >= 0) {

          //double rawProspectsMin = rawProspects2.min();
          if (rawProspectsMin < eM.rawHealthsSOS3[0][0]) {
            setStat(eM.TRADESOS3, pors, clan, worthIncrPercent, 1);
            setStat(eM.TRADEOSOS3, oPors, oClan, worthIncrPercent, 1);
          }
          else if (rawProspectsMin < eM.rawHealthsSOS2[0][0]) {
            setStat(eM.TRADESOS2, pors, clan, worthIncrPercent, 1);
            setStat(eM.TRADEOSOS2, oPors, oClan, worthIncrPercent, 1);
          }
          if (rawProspectsMin < eM.rawHealthsSOS1[0][0]) {
            setStat(eM.TRADESOS1, pors, clan, worthIncrPercent, 1);
            // percent worth incr given by other,
            // higer value, more charatible
            setStat(eM.TRADEOSOS1, oPors, oClan, worthIncrPercent, 1); // HELPER
          }
          if (rawProspectsMin < eM.rawHealthsSOS0[0][0]) {
            setStat(eM.TRADESOS0, pors, clan, worthIncrPercent, 1);
            setStat(eM.TRADEOSOS0, oPors, oClan, worthIncrPercent, 1);
          }
          if (rawProspectsMin < eM.rawHealthsLow[0][0]) {
            setStat(eM.TRADELOW, pors, clan, worthIncrPercent, 1);
          }
          // setStat(EM.DIEDPERCENT, pors, clan, 0., 1);
          if (ec.getHiLo()) {
            setStat(EM.HIGHDIEDPERCENT, pors, clan, 0., 1);
          }
          {
            setStat(EM.LOWDIEDPERCENT, pors, clan, 0., 1);
          }
        }
        else { // misc rej, lost, none
          setStat(EM.MISCDIEDPERCENT, pors, clan, 0., 1);
          if (ec.getHiLo()) {
            setStat(EM.MISCHIGHDIEDPERCENT, pors, clan, 0., 1);
          }
          {
            setStat(EM.MISCLOWDIEDPERCENT, pors, clan, 0., 1);
          }
        }
        eM.printHere("----LYE----", ec, "CashFlow.live yearEnd before many setStat");

        if (EM.year == yearTradeAccepted && oClan >= 0) {
          //set of accepted trades
          String[] potentialGrowthStats = {"potentialResGrowthPercent", "potentialCargoGrowthPercent", "potentialStaffGrowthPercent", "potentialGuestGrowthPercent"};
          String[] negRawUnitGrowths = {"rNeg1RawUnitGrowth", "cNeg1RawUnitGrowth", "sNeg1RawUnitGrowth", "gNeg1RawUnitGrowth"};
          String[] neg2RawUnitGrowths = {"rNeg2RawUnitGrowth", "cNeg2RawUnitGrowth", "sNeg2RawUnitGrowth", "gNeg2RawUnitGrowth"};
          // int[] s = {EM.RDEPRECIATIONP, EM.CDEPRECIATIONP, EM.SDEPRECIATIONP, EM.GDEPRECIATIONP};

          for (int sIx = 0; sIx < 4; sIx += 2) {
            double tt = calcPercent(eM.assetsUnitGrowth[sIx][pors], sys[sIx].rawUnitGrowth.sum());
            double ttt = calcPercent(eM.assetsUnitGrowth[sIx][pors], sys[sIx].depreciation.sum());
            if (tt > 0.0) {
              setStat(potentialGrowthStats[sIx], pors, clan, tt, 1);
            }
            if (ttt > -10.0) {
              //   setStat(depreciations[sIx], pors, clan, ttt, 1);
            }
            // check for negative raw unit growth in 1 or more sectors
            if (sys[sIx].rawUnitGrowth.getNegCount() >= 2 && sys[sIx].rawUnitGrowth.getNegSum() > 0.) {
              setStat(neg2RawUnitGrowths[sIx], sys[sIx].rawUnitGrowth.getNegSum(), 1);
            }
            else if (sys[sIx].rawUnitGrowth.getNegCount() >= 1 && sys[sIx].rawUnitGrowth.getNegSum() > 0.) {
              setStat(negRawUnitGrowths[sIx], sys[sIx].rawUnitGrowth.getNegSum(), 1);
            }
          } //sIx
        }
        if (eM.dfe()) {
          return 0.;
        }
        //smallest to largest
        //  if (year == yearTradeLost && oclan >= 0) {
        if (tradeLost && oClan >= 0) {
          if (tradedFirstNegProspectsSum < eM.rawHealthsSOS3[0][0]) {
            setStat(eM.TRADESOSR3, pors, clan, worthIncrPercent, 1);
            setStat(eM.TRADEOSOSR3, oPors, oClan, worthIncrPercent, 1);
          }
          else if (tradedFirstNegProspectsSum < eM.rawHealthsSOS2[0][0]) {
            setStat(eM.TRADESOSR2, pors, clan, worthIncrPercent, 1);
            setStat(eM.TRADEOSOSR2, oPors, oClan, worthIncrPercent, 1);
          }
          else if (tradedFirstNegProspectsSum < eM.rawHealthsSOS1[0][0]) {
            setStat(eM.TRADESOSR1, pors, clan, worthIncrPercent, 1);
            setStat(eM.TRADEOSOSR1, oPors, oClan, worthIncrPercent, 1); // HELPER
          }
          else if (tradedFirstNegProspectsSum < eM.rawHealthsSOS0[0][0]) {
            //       setStat(eM.TRADESOSR0, pors, clan, worthIncrPercent, 1);
            //       setStat(eM.TRADEOSOSR0, opors, oclan, worthIncrPercent, 1); // HELPER
          }

        } // trade rejected/lost
        // in Assets.CashFlow.yearEnd; fav was set in Assets.CashFlow.barter
        EM.wasHere2 = "Assert.CashFlow.yearEnd before WTRADEDINCRF5";
        if (fav >= 4.7) {
          // gameRes.WTRADEDINCRF5.wet(pors, clan, worthincr1, 1);
          setStat("WTRADEDINCRF5", pors, clan, worthIncrPercent, 1);
        }
        else if (fav >= 3.7) {
          // gameRes.WTRADEDINCRF4.wet(pors, clan, worthincr1, 1);
          setStat("WTRADEDINCRF4", pors, clan, worthIncrPercent, 1);
        }
        else if (fav >= 2.8) {
          // gameRes.WTRADEDINCRF3.wet(pors, clan, worthincr1, 1);
          setStat("WTRADEDINCRF3", pors, clan, worthIncrPercent, 1);
        }
        else if (fav >= 1.8) {
          // gameRes.WTRADEDINCRF2.wet(pors, clan, worthincr1, 1);
          setStat("WTRADEDINCRF2", pors, clan, worthIncrPercent, 1);
        }
        else if (fav >= .9) {
          // gameRes.WTRADEDINCRF1.wet(pors, clan, worthincr1, 1);
          setStat("WTRADEDINCRF1", pors, clan, worthIncrPercent, 1);
        }
        else if (fav >= 0.) {
          // gameRes.WTRADEDINCRF0.wet(pors, clan, worthincr1, 1);
          setStat("WTRADEDINCRF0", pors, clan, worthIncrPercent, 1);
        }
        else if (tradeRejected) {

          setStat("WREJTRADEDPINCR", pors, clan, worthIncrPercent, 1);
        }
        else if (tradeLost) {
          setStat("WLOSTTRADEDINCR", pors, clan, worthIncrPercent, 1);
        }
        else {
          if (lastAcceptedYear == eM.year) {
            throw new MyErr("Illegal prev and noPrev barter for the same year=" + eM.year + ", ship=" + tradingShipName);
          }
          // if (prevNotAcceptedYear != eM.year) {
          // prevNotAcceptedYear = eM.year;
          //  }
          // Trade missed
          setStat("UNTRADEDWINCR", pors, clan, worthIncrPercent, 1);
          setStat(EM.TradeMissedStrategicGoal, pors, clan, 1.0, 1); // no goal
          EM.wasHere = "CashFlow.yearEnd before many setStat ddddg=" + ++ddddg;

          if (eM.year - lastAcceptedYear == 1) {
            setStat("WORTHAYRNOTRADEINCR", pors, clan, worthIncrPercent, 1);
          }
          else if (eM.year - lastAcceptedYear == 2) {
            setStat("WORTH2YRNOTRADEINCR", pors, clan, worthIncrPercent, 1);
          }
          else if (eM.year - lastAcceptedYear >= 3) {
            setStat("WORTH3YRNOTRADEINCR", pors, clan, worthIncrPercent, 1);
          }
        } //--- did year missed stats, years traded stats
        if (tradeAccepted) {
          setStat(EM.WTRADEDINCRMULT, pors, clan, worthIncrPercent, 1);
        }
        if (sos && tradeAccepted) {
          setStat(EM.WTRADEDINCRSOS, pors, clan, worthIncrPercent, 1);
        }
        if (prevNotAcceptedYear != eM.year) { // had a barter
          if (eM.year - prevNotAcceptedYear == 1) {
            setStat("WORTHAYRTRADEINCR", pors, clan, worthIncrPercent, 1);
          }
          else if (eM.year - prevNotAcceptedYear == 2) {
            setStat("WORTH2YRTRADEINCR", pors, clan, worthIncrPercent, 1);
          }
          else if (eM.year - prevNotAcceptedYear >= 3) {
            setStat("WORTH3YRTRADEINCR", pors, clan, worthIncrPercent, 1);
          }
        }
        if (eM.dfe()) {
          return 0.;
        }
        /*
        setStat(eM.sumCatEffRBen, pors, clan, catEffRGBen, 1);
        setStat(eM.sumCatEffSBen, pors, clan, catEffSGBen, 1);
        setStat(eM.sumCatEffManualsBen, pors, clan, catEffManualsBen, 1);
        setStat(eM.sumCatEffKnowBen, pors, clan, catEffKnowBen, 1);
        setStat(eM.sumCatEffRDepreciationBen, pors, clan, catEffRDepreciationBen, 1);
        setStat(eM.sumCatEffSDepreciationBen, pors, clan, catEffSDepreciationBen, 1);
        setStat("WORTH3YRTRADEINCR", pors, clan, worthIncrPercent, 1);
        setStat("WORTH3YRTRADEINCR", pors, clan, worthIncrPercent, 1);
        setStat("WORTH3YRTRADEINCR", pors, clan, worthIncrPercent, 1);
         */
        if (E.debugEconCnt) {
          if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
            EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
          }
        }
        setStat(EM.BONUSGROWTH, 100. * bals.sum4(ABalRows.YEARLYBONUSSUMGROWTHVALIX));
        /*
        String[] potentialGrowthStats = {"potentialResGrowthPercent", "potentialCargoGrowthPercent", "potentialStaffGrowthPercent", "potentialGuestGrowthPercent"};
        for (int sIx = 0; sIx < 4; sIx += 1) {
          double tt = calcPercent(eM.assetsUnitGrowth[sIx][pors], sys[sIx].rawUnitGrowth.sum());
          if (tt > 0.0) {
           // setStat(potentialGrowthStats[sIx], tt);
          }
        }
         */
        EM.isHere("--EYEYf--", ec, "end of live stats");
// -----------ENDLIVE---ENDLIVE---ENDLIVE---------------------------------
      }
      else {//=========================DEAD DEAD  DEAD ===========================
        //      ec.dead = true; // set econ to dead
        ec.dyear = EM.year; // set year of death
        // dead, be sure dead is set
        if (eM.dfe()) {
          return 0.;
        }
        EM.isHere1(ec, " CashFlow.yearEnd start of dead cccg=" + ++cccg);
        fyW = new DoTotalWorths(); // dead final values
        double totalYearWorthIncr = fyW.sumTotWorth - syW.sumTotWorth;
        double percentYearWorthIncr = (totalYearWorthIncr * 100.) / syW.sumTotWorth;
        double sumGrowth = bals.sum4(ABalRows.GROWTHSEFFIX);
        double startYearRCSGBal = syW.sumRCSGBal;
        double sumYearRCSGincr = (fyW.sumRCSGBal - syW.sumRCSGBal);
        double percentYearRCSGincr = sumYearRCSGincr * 100 / startYearRCSGBal;
        if (E.debugEconCnt) {
          if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
            EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
          }
        }
        fyW = new DoTotalWorths();  // never tried growth
        eM.printHere(E.debugDoYearEndOut, "-----YEDP---- ", ec, " start dead in Assets.CashFlow.yearEnd() min rawProspects =" + EM.mf(rawProspects2.curMin()));
        if (!dead) {  //not yet flaged, do only once
          // DoTotalWorths iyW, syW, tW, gSwapW, gGrowW, gCostW, fyW;
          ec.dead = dead = true; //set deat in Econ and Assets
          ec.age = -1; // reset the age if used again
          ec.dage = -1;// ec.yearEnd will increase to 0
          double tt3 = 0;

          //100. * (final worth - start year worth)/start year worth is percent increase
          double worthincr1 = 100. * (fyW.sumTotWorth - syW.sumTotWorth) / syW.sumTotWorth;
          setStat(EM.DIED, pors, clan, rawProspects2.curMin(), 1);

          eM.printHere(E.debugDoYearEndOut, "-----YEDPa---- ", ec, " start dead stats in Assets.CashFlow.yearEnd() ");
          setStat(EM.DIEDPERCENT, pors, clan, 100., 1);
          int[] worthIncrA = {EM.DWORTHINCRN0, EM.DWORTHINCRN1, EM.DWORTHINCRN2, EM.DWORTHINCRN3};
          int[] growthsA = {EM.DGROWTHSN0, EM.DGROWTHSN1, EM.DGROWTHSN2, EM.DGROWTHSN3};
          int[] fertilitiesA = {EM.DFERTILITYSN0, EM.DFERTILITYSN1, EM.DFERTILITYSN2, EM.DFERTILITYSN3};
          int[] rcsgIncrA = {EM.DRCSGINCRN0, EM.DRCSGINCRN1, EM.DRCSGINCRN2, EM.DRCSGINCRN3};
          setStat(worthIncrA[ixAccYears], pors, clan, percentYearWorthIncr, 1);
          setStat(growthsA[ixAccYears], pors, clan, sumGrowth, 1);
          setStat(rcsgIncrA[ixAccYears], pors, clan, sumYearRCSGincr, 1);
          setStat(fertilitiesA[ixAccYears], pors, clan, gSwapW.aveFertility, 1);
          if (tradeAccepted && oClan >= 0) {
            setStat(EM.DTRADEACC, pors, clan, worthIncrPercent, 1); // me
            if (ec.getHiLo()) {
              setStat(EM.HIGHDIEDPERCENT, pors, clan, 100., 1);
            }
            {
              setStat(EM.LOWDIEDPERCENT, pors, clan, 100., 1);
            }
          }
          else { // misc rej, lost, none
            setStat(EM.MISCDIED, pors, clan, worthincr1, 1);
            setStat(EM.MISCDIEDPERCENT, pors, clan, 100., 1);
            if (ec.getHiLo()) {
              setStat(EM.MISCHIGHDIEDPERCENT, pors, clan, 100., 1);
            }
            {
              setStat(EM.MISCLOWDIEDPERCENT, pors, clan, 100., 1);
            }
          }
          if (yearCatastrophy == EM.year) {
            setStat(EM.DIEDCATASTROPHY, pors, clan, worthincr1, 1);
          }
          //    setStat("TRADES%", pors, clan, fav > NZERO ? 100. : 0., 1);
          if (tradeAccepted && oClan >= 0) {
            setStat(EM.DSWAPRINCRWORTH, pors, clan, gSwapIncr, 1);
            setStat(EM.DPOSTSWAP, pors, clan, gSwapWTotWorth, 1);
            setStat(EM.DPOSTSWAPRCSG, pors, clan, gSwapW.getSumRCSGBal(), 1);
            setStat("DEADWTRADEDINCR", pors, clan, worthincr1, 1);
            /*
            // String[] potentialGrowthStats = {"DApotentialResGrowthPercent", "DApotentialCargoGrowthPercent", "DApotentialStaffGrowthPercent", "DApotentialGuestGrowthPercent"};
             //String[] negRawUnitGrowths = {"rDANeg1RawUnitGrowth"
            , "cDANeg1RawUnitGrowth", "sDANeg1RawUnitGrowth", "gDANeg1RawUnitGrowth"};
            // String[] neg2RawUnitGrowths = {"rDANeg2RawUnitGrowth", "cDANeg2RawUnitGrowth", "sDANeg2RawUnitGrowth", "gDANeg2RawUnitGrowth"};
            //  int[] depreciations = {EM.RDADEPRECIATIONP, EM.CDADEPRECIATIONP, EM.SDADEPRECIATIONP, EM.GDADEPRECIATIONP};
             */
            for (int sIx = 0; sIx < 4; sIx += 2) {
              /*
              double tt = calcPercent(eM.assetsUnitGrowth[sIx][pors], sys[sIx].rawUnitGrowth.sum());
              double ttt = calcPercent(eM.assetsUnitGrowth[sIx][pors], sys[sIx].depreciation.sum())


              // nzStat(potentialGrowthStats[sIx], calcPercent(eM.assetsUnitGrowth[sIx][pors], sys[sIx].rawUnitGrowth.sum()));
              //nzStat(depreciations[sIx], ttt);
              if (sys[sIx].rawUnitGrowth.getNegCount() >= 2 && sys[sIx].rawUnitGrowth.getNegSum() > 0.) {
                //setStat(neg2RawUnitGrowths[sIx], sys[sIx].rawUnitGrowth.getNegSum(), 1);
              }
              else if (sys[sIx].rawUnitGrowth.getNegCount() >= 1 && sys[sIx].rawUnitGrowth.getNegSum() > 0.) {
                setStat(negRawUnitGrowths[sIx], sys[sIx].rawUnitGrowth.getNegSum(), 1);
              }
               */
            } //sIx

            if (E.debugEconCnt) {
              if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
                EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
              }
            }
            if (tradedFirstNegProspectsSum < eM.rawHealthsSOS3[0][0]) {
              // setStat(eM.TRADESOSR3, pors, clan, worthIncrPercent, 1);
              setStat(eM.DTRADEOSOSR3, oPors, oClan, worthIncrPercent, 1);
            }
            else if (tradedFirstNegProspectsSum < eM.rawHealthsSOS2[0][0]) {
              //setStat(eM.TRADESOSR2, pors, clan, worthIncrPercent, 1);
              setStat(eM.DTRADEOSOSR2, oPors, oClan, worthIncrPercent, 1);
            }
            else if (tradedFirstNegProspectsSum < eM.rawHealthsSOS1[0][0]) {
              // Help that was given but still dead
              setStat(eM.DTRADEOSOSR1, oPors, oClan, worthIncrPercent, 1); // HELPER
              setStat(eM.DTRADESOSR1, pors, clan, worthIncrPercent, 1); // me
            }
            else if (tradedFirstNegProspectsSum < eM.rawHealthsSOS0[0][0]) {
              // Help that was given but still dead
              setStat(eM.DTRADEOSOSR0, oPors, oClan, worthIncrPercent, 1); // HELPER
              setStat(eM.DTRADESOSR0, pors, clan, worthIncrPercent, 1); // me
            }
            if (EM.dfe()) {
              EM.lfe();
              return 0.;
            }
          }
          else { // rejected, lost, missed, not Accepted
            setStat(EM.DNSWAPRINCRWORTH, pors, clan, gSwapIncr, 1);
            setStat(EM.DNPOSTSWAP, pors, clan, gSwapWTotWorth, 1);
            setStat(EM.DNPOSTSWAPRCSG, pors, clan, gSwapW.getSumRCSGBal(), 1);
            if (false) {
              String[] potentialGrowthStats = {"DpotentialResGrowthPercent", "DpotentialCargoGrowthPercent", "DpotentialStaffGrowthPercent", "DpotentialGuestGrowthPercent"};
              String[] negRawUnitGrowths = {"rDNeg1RawUnitGrowth", "cDNeg1RawUnitGrowth", "sDNeg1RawUnitGrowth", "gDNeg1RawUnitGrowth"};
              String[] neg2RawUnitGrowths = {"rDNeg2RawUnitGrowth", "cDNeg2RawUnitGrowth", "sDNeg2RawUnitGrowth", "gDNeg2RawUnitGrowth"};
              //int[] depreciations = {EM.RDDEPRECIATIONP, EM.CDDEPRECIATIONP, EM.SDDEPRECIATIONP, EM.GDDEPRECIATIONP};

              for (int sIx = 0; sIx < 4; sIx += 2) {
                double tt = calcPercent(eM.assetsUnitGrowth[sIx][pors], sys[sIx].rawGrowth.sum());
                double ttt = calcPercent(eM.assetsUnitGrowth[sIx][pors], sys[sIx].depreciation.sum());
                if (tt > 0.0) {
                  setStat(potentialGrowthStats[sIx], calcPercent(eM.assetsUnitGrowth[sIx][pors], sys[sIx].rawUnitGrowth.sum()), 1);
                }
                if (ttt > 0.0) {
                  // setStat(depreciations[sIx], ttt, 1);
                }
                if (sys[sIx].rawUnitGrowth.getNegCount() >= 2 && sys[sIx].rawUnitGrowth.getNegSum() > 0.) {

                  setStat(neg2RawUnitGrowths[sIx], sys[sIx].rawUnitGrowth.getNegSum(), 1);
                }
                else if (sys[sIx].rawUnitGrowth.getNegCount() >= 1 && sys[sIx].rawUnitGrowth.getNegSum() > 0.) {

                  setStat(negRawUnitGrowths[sIx], sys[sIx].rawUnitGrowth.getNegSum(), 1);
                }

              }
            } //sIx

          }// rejected, lost, missed, not accepted
          if (EM.dfe()) {
            EM.lfe();
            return 0.;
          }
          if (tradeLost) {
            setStat(EM.TradeDeadLostStrategicValue, pors, clan, strategicValue, 1);
          }
          if (tradeLost && oClan >= 0) {
            if (tradedFirstNegProspectsSum < eM.rawHealthsSOS1[0][0]) {
              // Help that was given but still dead
              setStat(EM.DLOSTOSOSR1, oPors, oClan, worthIncrPercent, 1); // HELPER
            }
          }
          if (tradeRejected) {
            setStat(EM.TradeDeadRejectedStrategicValue, pors, clan, strategicValue, 1); // self reject
            if (tradedFirstNegProspectsSum < eM.rawHealthsSOS1[0][0]) {
              setStat(EM.DTRADESOSR1, pors, clan, worthIncrPercent, 1); // self reject

            }
          }
          if (tradedFirstNegProspectsSum < eM.rawHealthsSOS2[0][0]) {
            //setStat(eM.TRADESOSR2, pors, clan, worthIncrPercent, 1);
            setStat(eM.DLOSTOSOSR2, oPors, oClan, worthIncrPercent, 1);
          }
          if (tradedFirstNegProspectsSum < eM.rawHealthsSOS3[0][0]) {
            // setStat(eM.TRADESOSR3, pors, clan, worthIncrPercent, 1);
            setStat(eM.DLOSTOSOSR3, oPors, oClan, yearTradeRejected, 1);
          }
          // trade rejected/lost
          // fav was set in Assets.CashFlow.barter
          if (E.debugEconCnt) {
            if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }
          if (EM.dfe()) {
            EM.lfe();
            return 0.;
          }
          if (fav >= 4.7) {
            // gameRes.WTRADEDINCRF5.wet(pors, clan, worthincr1, 1);
            setStat("DEADWTRADEDINCRF5", pors, clan, worthincr1, 1);
          }
          else if (fav >= 3.7) {
            // gameRes.WTRADEDINCRF4.wet(pors, clan, worthincr1, 1);
            setStat("DEADWTRADEDINCRF4", pors, clan, worthincr1, 1);
          }
          else if (fav >= 2.8) {
            // gameRes.WTRADEDINCRF3.wet(pors, clan, worthincr1, 1);
            setStat("DEADWTRADEDINCRF3", pors, clan, worthincr1, 1);
          }
          else if (fav >= 1.8) {
            // gameRes.WTRADEDINCRF2.wet(pors, clan, worthincr1, 1);
            setStat("DEADWTRADEDINCRF2", pors, clan, worthincr1, 1);
          }
          else if (fav >= .9) {
            // gameRes.WTRADEDINCRF1.wet(pors, clan, worthincr1, 1);
            setStat("DEADWTRADEDINCRF1", pors, clan, worthincr1, 1);
          }
          else if (fav >= 0.) {
            // gameRes.WTRADEDINCRF0.wet(pors, clan, worthincr1, 1);
            setStat("DEADWTRADEDINCRF0", pors, clan, worthincr1, 1);
          }
          else if (tradeRejected) {
            setStat("DEADWREJTRADEDINCR", pors, clan, worthincr1, 1);
          }
          else if (tradeLost) {
            setStat("DEADWLOSTTRADEDINCR", pors, clan, worthincr1, 1);
          }
          else {
            // gameRes.UNTRADEDWINCR.wet(pors, clan, worthincr1, 1);
            setStat("DEADUNTRADEDWINCR", pors, clan, worthincr1, 1);
            setStat(EM.TradeDeadMissedStrategicGoal, pors, clan, 1.0, 1);
          }
          if (EM.dfe()) {
            EM.lfe();
            return 0.;
          }
          /*
   static final int DIEDSN4 = ++e4;
  static final int DIEDRN4 = ++e4;
  static final int DIEDSN4RM3X5 = ++e4;
  static final int DIEDSN4RM3X4 = ++e4;
  static final int DIEDSM3X5 = ++e4;
  static final int DIEDRM3X4 = ++e4; // END NOT IN IF ELSE CHAIN
  static final int DIEDSN4RN4 = ++e4;

          if ((mtgAvails6.getRow(4).min(3) < -0.0) && (mtgAvails6.getRow(2).max(2) > mtgAvails6.getRow(4).max(2) * 5.)) {
            // Staff min(3) lt 0, r max(2) 3rd max gt s max(2) * 5   r gt 5*s
            setStat(EM.DIEDSN4RM3X5, pors, clan, worthincr1, 1);
          } else
            if ((mtgAvails6.getRow(4).min(3) < -0.0) && (mtgAvails6.getRow(2).max(2) > mtgAvails6.getRow(4).max(2) * 4.)) {
            setStat(EM.DIEDSN4RM3X4, pors, clan, worthincr1, 1);
          }  else
          if ((mtgAvails6.getRow(4).min(3) < -0.0)) {
            //Staff min(3) 4th min lt 0, 4 out of 7 sectors lt 0
            setStat(EM.DIEDSN4, pors, clan, worthincr1, 1);
          } else
          if ((mtgAvails6.getRow(2).max(2) > mtgAvails6.getRow(4).max(2) * 5.)) {
            setStat(EM.DIEDSM3X5, pors, clan, worthincr1, 1);
          } else
          if ((mtgAvails6.getRow(4).min(2) < -0.0) && (mtgAvails6.getRow(2).min(2) < -0.0)) {
            setStat(EM.DIEDSN3RN3, pors, clan, worthincr1, 1);
          } else

           if ((mtgAvails6.getRow(2).min(3) < -0.0)) {
            setStat(EM.DIEDRN4, pors, clan, worthincr1, 1);
          } else
          if ((mtgAvails6.getRow(4).max(2) > mtgAvails6.getRow(2).max(2) * 4.)) {
            setStat(EM.DIEDRM3X4, pors, clan, worthincr1, 1);
          } else
          if ((mtgAvails6.getRow(4).min(2) < -0.0) && (mtgAvails6.getRow(2).min(1) < -0.0)) {
            setStat(EM.DIEDSN3RN2, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).min(2) < -0.0) && (mtgAvails6.getRow(2).max(2) > mtgAvails6.getRow(4).ave() * 4.)) {
            setStat(EM.DIEDSN3RM3X4, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).min(2) < -0.0) && (mtgAvails6.getRow(2).max(2) > mtgAvails6.getRow(4).ave() * 3.)) {
            setStat(EM.DIEDSN3RM3X3, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).min(2) < -0.0) && (mtgAvails6.getRow(2).min(0) < -0.0)) {
            setStat(EM.DIEDSN3RN1, pors, clan, worthincr1, 1);
          }
           if(EM.dfe()){EM.lfe(); return 0.;}
            /*
  setStat(EM.DIEDSN3RM3X2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN3RM3X1, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN3RM2X4, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN3RM2X3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN3RM2X2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN3RM2X1, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN3RM1X4, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN3RM1X3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN3RM1X2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN3RM1X1, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN2RN3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN2RN2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN2RM3X4, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN2RM3X3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN2RN1, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN2RM3X2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN2RM3X1, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN2RM2X4, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN2RM2X3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN2RM2X2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN2RM2X1, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN2RM1X4, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN2RM1X3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN2RM1X2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN2RM1X1, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN1RN3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN1RN2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN1RM3X4, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN1RM3X3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN1RN1, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN1RM3X2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN1RM3X1, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN1RM2X4, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN1RM2X3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN1RM2X2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN1RM2X1, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN1RM1X4, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN1RM1X3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSN1RM1X2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM3X4RN3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM3X3RN3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM3X2RN3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM3X1RN3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM2X4RN3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM2X3RN3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM2X2RN3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM2X1RN3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM1X4RN3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM1X3RN3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM1X2RN3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM1X1RN3, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM3X4RN2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM3X3RN2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM3X2RN2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM3X1RN2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM2X4RN2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM2X3RN2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM2X2RN2, pors, clan, worthincr1, 1);
  setStat(EM.DIEDSM2X1RN2, pors, clan, worthincr1, 1);
   if(EM.dfe()){EM.lfe(); return 0.;}
           else if ((mtgAvails6.getRow(4).max(0) > mtgAvails6.getRow(2).max(0) * 4.) && (mtgAvails6.getRow(2).min(1) < -0.0)) {
            setStat(EM.DIEDSM1X4RN2, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).max(0) > mtgAvails6.getRow(2).max(0) * 3.) && (mtgAvails6.getRow(2).min(1) < -0.0)) {
            setStat(EM.DIEDSM1X3RN2, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).max(0) > mtgAvails6.getRow(2).max(0) * 2.) && (mtgAvails6.getRow(2).min(1) < -0.0)) {
            setStat(EM.DIEDSM1X2RN2, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).max(0) * 1. > mtgAvails6.getRow(2).max(0)) && (mtgAvails6.getRow(2).min(0) < -0.0)) {
            setStat(EM.DIEDSM1X1RN1, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).max(2) * 4. > mtgAvails6.getRow(2).max(0)) && (mtgAvails6.getRow(2).min(0) < -0.0)) {
            setStat(EM.DIEDSM3X4RN1, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).max(2) * 3. > mtgAvails6.getRow(2).max(0)) && (mtgAvails6.getRow(2).min(0) < -0.0)) {
            setStat(EM.DIEDSM3X3RN1, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).max(2) * 2. > mtgAvails6.getRow(2).max(0)) && (mtgAvails6.getRow(2).min(0) < -0.0)) {
            setStat(EM.DIEDSM3X2RN1, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).max(2) * 1. > mtgAvails6.getRow(2).max(0)) && (mtgAvails6.getRow(2).min(0) < -0.0)) {
            setStat(EM.DIEDSM3X1RN1, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).max(1) * 4. > mtgAvails6.getRow(2).max(0)) && (mtgAvails6.getRow(2).min(0) < -0.0)) {
            setStat(EM.DIEDSM2X4RN1, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).max(1) * 3. > mtgAvails6.getRow(2).max(0)) && (mtgAvails6.getRow(2).min(0) < -0.0)) {
            setStat(EM.DIEDSM2X3RN1, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).max(1) * 2. > mtgAvails6.getRow(2).max(0)) && (mtgAvails6.getRow(2).min(0) < -0.0)) {
            setStat(EM.DIEDSM2X2RN1, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).max(1) * 1. > mtgAvails6.getRow(2).max(0)) && (mtgAvails6.getRow(2).min(0) < -0.0)) {
            setStat(EM.DIEDSM2X1RN1, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).max(0) * 4. > mtgAvails6.getRow(2).max(0)) && (mtgAvails6.getRow(2).min(0) < -0.0)) {
            setStat(EM.DIEDSM1X4RN1, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).max(0) * 3. > mtgAvails6.getRow(2).max(0)) && (mtgAvails6.getRow(2).min(0) < -0.0)) {
            setStat(EM.DIEDSM1X3RN1, pors, clan, worthincr1, 1);
          } else if ((mtgAvails6.getRow(4).max(0) * 2. > mtgAvails6.getRow(2).max(0)) && (mtgAvails6.getRow(2).min(0) < -0.0)) {
            setStat(EM.DIEDSM1X2RN1, pors, clan, worthincr1, 1);
          }
           */
          if (E.debugEconCnt) {
            if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }
          if (EM.dfe()) {
            EM.lfe();
            return 0.;
          }
          EM.wasHere = " CashFlow.yearEnd into deac, and dead ccch=" + ++ccch;
          if (swapsN < 0) {
            setStat(EM.DeadNegN, pors, clan, worthincr1, 1);
          }
          else if (swapsN < 5) {
            setStat(EM.DeadLt5, pors, clan, worthincr1, 1);
          }
          else if (swapsN < 10) {
            setStat(EM.DeadLt10, pors, clan, worthincr1, 1);
          }
          else if (swapsN < 20) {
            setStat(EM.DeadLt20, pors, clan, worthincr1, 1);
          }
          if (E.debugEconCnt) {
            if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }
          if (EM.dfe()) {
            EM.lfe();
            return 0.;
          }
          /*
    doRes("DeadNegProsp", "DeadNegProsp", "Died either R or S had a negative",  2,2,3,  ROWS1 | LIST3 | LIST20 | LIST2YRS | THISYEARUNITS | BOTH | SKIPUNSET, ROWS2 |LIST2 |  LIST3 | LIST20  | LIST0YRS |  CUMUNITS | BOTH | SKIPUNSET,0L, 0L);
    doRes("DeadRatioS", "DeadRatioS", "Resource  S values simply too small",  2,2,3,  ROWS1 | LIST3 | LIST20 | LIST2YRS | THISYEARUNITS | BOTH | SKIPUNSET, ROWS2 |LIST2 |  LIST3 | LIST20  | LIST0YRS |  CUMUNITS | BOTH | SKIPUNSET,0L, 0L);
    doRes("DeadRatioR", "DeadRatioR", "R values simply too small", 2,2,3,  ROWS1 | LIST3 | LIST20 | LIST2YRS | THISYEARUNITS | BOTH | SKIPUNSET, ROWS2 |LIST2 |  LIST3 | LIST20  | LIST0YRS |  CUMUNITS | BOTH | SKIPUNSET,0L, 0L);
    doRes(EM.DIED, "dead", "dead from any set of causes", 2, 2, 3,  ROWS1 | LIST0 | LIST9 | LIST2YRS | THISYEARUNITS | BOTH | SKIPUNSET, ROWS2 |LIST7 | LIST8 | LIST9 | CUMUNITS | LISTYRS | BOTH | SKIPUNSET,0L, 0L);
    doRes(MISSINGNAME, "missing name", "tried an unknown name", 6, 0, list0 | cumUnits | curUnits | curAve | cumAve | both, 0, 0, 0);
    doRes(DEADRATIO, "diedRatio", "dead,average mult year last/initial worth death",2, 2, 3,  ROWS1 | LIST0 | LIST9 | LIST2YRS | THISYEARUNITS | BOTH | SKIPUNSET, ROWS2 |LIST7 | LIST8 | LIST9 | CUMUNITS | LISTYRS | BOTH | SKIPUNSET,0L, 0L);
    doRes(DEADHEALTH, "dead health", "dead,average negative minimum health at death",2, 2, 3,  ROWS1 | LIST0 | LIST9 | LIST2YRS | THISYEARUNITS | BOTH | SKIPUNSET, ROWS2 |LIST7 | LIST8 | LIST9 | CUMUNITS | LISTYRS | BOTH | SKIPUNSET,0L, 0L);
    doRes(DEADFERTILITY, "dead fertility", "dead,average negative minimum fertility at death",2, 2, 3,  ROWS1 | LIST0 | LIST9 | LIST2YRS | THISYEARUNITS | BOTH | SKIPUNSET, ROWS2 |LIST7 | LIST8 | LIST9 | CUMUNITS | LISTYRS | BOTH | SKIPUNSET,0L, 0L);
    doRes(DEADSWAPSMOVED, "diedSwapMoves", "dead,average Swap Moves at death",2, 2, 3,  ROWS1 | LIST0 | LIST9 | LIST2YRS | THISYEARUNITS | BOTH | SKIPUNSET, ROWS2 |LIST7 | LIST8 | LIST9 | CUMUNITS | LISTYRS | BOTH | SKIPUNSET,0L, 0L);
    doRes(DEADSWAPSCOSTS, "diedSwapCosts", "dead,average SwapCosts at death",2, 2, 3,  ROWS1 | LIST0 | LIST9 | LIST2YRS | THISYEARUNITS | BOTH | SKIPUNSET, ROWS2 |LIST7 | LIST8 | LIST9 | CUMUNITS | LISTYRS | BOTH | SKIPUNSET,0L, 0L);
    doRes(DEADTRADED, "diedTraded", "dead,even after trading",2, 2, 3,  ROWS1 | LIST0 | LIST3 | LIST2YRS | THISYEARUNITS | BOTH | SKIPUNSET, ROWS2 |LIST2 | LIST3 | LIST0 | CUMUNITS | LISTYRS | BOTH | SKIPUNSET,0L, 0L);
           */
          double ts3 = 0.;
          if (rawProspects2.curMin() < E.NZERO) {
            setStat(EM.DeadNegProsp, pors, clan, worthincr1, 1);
            setStat(EM.DEADFERTILITY, pors, clan, rawFertilities2.curMin(), 1);
            if (EM.dfe()) {
              EM.lfe();
              return 0.;
            }
          }
          else if ((tt3 = (ts3 = bals.getRow(0).sum()) != 0.0 ? bals.getRow(1).sum() / ts3 : 0.) > 1.5) {
            setStat(EM.DeadRatioS, pors, clan, tt3, 1);
            setStat(EM.DEADRATIO, pors, clan, ((ts3 = bals.getRow(0).sum()) != 0.) ? bals.getRow(1).sum() / ts3 : 0., 1);
            if (EM.dfe()) {
              EM.lfe();
              return 0.;
            }
          }
          else if ((tt3 = (ts3 = bals.getRow(1).sum()) != 0. ? bals.getRow(0).sum() / ts3 : 0.) > 1.5) {
            setStat(EM.DeadRatioR, pors, clan, tt3, 1);
            setStat(EM.DEADRATIO, pors, clan, (ts3 = bals.getRow(0).sum()) != -0. ? bals.getRow(1).sum() / ts3 : 0., 1);
            if (EM.dfe()) {
              EM.lfe();
              return 0.;
            }
          }
          setStat(EM.DEADSWAPSMOVED, pors, clan, swapsN, 1);
          if (EM.dfe()) {
            EM.lfe();
            return 0.;
          }
          double sBal = s.balance.sum();
          if (tradeAccepted) {
            setStat("sDAWorth", calcPercent(sBal, s.worth.sum()));
            setStat("sDAWork", calcPercent(sBal, s.work.sum()));
            setStat("sDAFacultyEquiv", calcPercent(sBal, s.facultyEquiv.sum()));
            setStat("sDAResearcherEquiv", calcPercent(sBal, s.researcherEquiv.sum()));
            setStat("sDAColonists", calcPercent(sBal, s.colonists.sum()));
            setStat("sDAEngineers", calcPercent(sBal, s.engineers.sum()));
            setStat("sDAFaculty", calcPercent(sBal, s.faculty.sum()));
            setStat("sDAResearchers", calcPercent(sBal, s.researchers.sum()));
            setStat("sDAKnowledge", calcPercent(eM.nominalKnowledgeForBonus[0], knowledge.sum()));
          }
          else {
            setStat("sDWorth", calcPercent(sBal, s.worth.sum()));
            setStat("sDWork", calcPercent(sBal, s.work.sum()));
            setStat("sDFacultyEquiv", calcPercent(sBal, s.facultyEquiv.sum()));
            setStat("sDResearcherEquiv", calcPercent(sBal, s.researcherEquiv.sum()));
            setStat("sDColonists", calcPercent(sBal, s.colonists.sum()));
            setStat("sDEngineers", calcPercent(sBal, s.engineers.sum()));
            setStat("sDFaculty", calcPercent(sBal, s.faculty.sum()));
            setStat("sDResearchers", calcPercent(sBal, s.researchers.sum()));
            setStat("sDKnowledge", calcPercent(eM.nominalKnowledgeForBonus[0], knowledge.sum()));
          }
          if (EM.dfe()) {
            EM.lfe();
            return 0.;
          }
          if (E.debugEconCnt) {
            if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }
          eM.cntDead++;
          eM.porsDead[pors]++;
          eM.porsClanDead[pors][clan]++;
          eM.clanDead[clan]++;

          if (E.debugEconCnt) {
            if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
              EM.doMyErr("Counts error, econCnt=" + EM.econCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }
          EM.wasHere6 = "---ELa---Assets.10607 seek lock";
          synchronized (A4Row.econLock) {
            EM.wasHere8 = "---ELa2--- Assets cnt dead has lock";
            EM.clanCnt[clan]--;
            EM.porsClanCnt[pors][clan]--;
            EM.porsCnt[pors]--;
            EM.econCnt--;
            //     EM.econCnt = EM.porsCnt[0] + EM.porsCnt[1];
            EM.deadCnt++;
          }
          if (E.debugEconCnt) {
            if (EM.econCnt != (EM.porsCnt[0] + EM.porsCnt[1])) {
              EM.doMyErr("Counts error, clanCnt=" + EM.clanCnt + " -porsCnt0=" + EM.porsCnt[0] + " -porsCnt1=" + EM.porsCnt[1]);
            }
          }
          if (EM.dfe()) {
            EM.lfe();
            return 0.;
          }
        } // end of first time dead
        if (EM.dfe()) {
          EM.lfe();
          return 0.;
        }
        ec.dead = dead = true;
        EM.wasHere = "CashFlow.yearEnd before many setStat ddddi=" + ++ddddi;
        eM.clanFutureFunds[clan] += yearsFutureFund;
        yearsFutureFund = 0.;
        yearsFutureFundTimes = 0;
        hist.add(new History(aPre, 1, "n" + n + ">>>>>> aDEAD=" + EM.mf(health), "*dead*", "*dead*", "*dead*", "*dead*", "*dead*", "*dead*", "*dead*", "*dead*", "*dead*", "<<<<<<"));
        EM.isHere("--EYEYdg--", ec, "end of dead stats");
      }// Join rest of yearEnd end of dead
      EM.econCountsTest();
      if (EM.dfe()) {
        EM.lfe();
        return 0.;
      }
      //Assets.CashFlow.yearEnd  final cleanup for starting the next year
      if (E.debugDoYearEndOut) {
        eM.printHere("-----YEDPg ---- ", ec, " near end in Assets.CashFlow.yearEnd() " + (tradeAccepted ? " tradeAccepted" : " !tradeAccepted") + (tradeRejected ? " tradeRejected" : " !tradeRejected") + (tradeLost ? " tradeLost" : " !tradeLost") + (tradeMissed ? " tradeMissed" : " !tradeMissed"));
        saveAI(dead ? gSwapW.sumTotWorth : fyW.sumTotWorth, dead); // save the AI live info
      }
      if (!dead) {
        if (EM.dfe()) {
          EM.lfe();
          return 0.;
        }
        didStart = true;
        getTradingGoods(); // pick up new trading goods
        didStart = false; // force start at next initCashFlow
        EM.econCountsTest();
      }
      EM.isHere("--EYEYaf--", ec, "end of yearEnd stats");
      if (EM.dfe()) {
        EM.lfe();
        return 0.;
      }
      didGoods = false;
      n = 0;
      catastrophyBalIncr[n] = catastrophyPBalIncr[n] = 0.;
      catastrophyBalIncr[n]
              = catastrophyDepreciationBalDecr[n] = 0.;
      //   sLoops[n] = 0;
      clanRisk = eM.clanRisk[pors][clan];
      tradedShipOrdinal = 0;  // reset for both planet and ship
      econVisited = 0; // total trades tried
      // near end of Assets.CashFlow.yearEnd() live or dead
      didTrade = false;
      lostTrade = false;
      newTradeYear2 = false;
      newTradeYear1 = false;
      didCashFlowStart = false; // set true at aStartCashFlow
      didCatastrophy = false; // set in calcCatastrohy
      fav = -4;
      oTradedEcons = new Econ[20];
      oTradedEconsNext = 0;
      // syW = null; // get rid of hanging DoTotalWorths
      didDepreciation = false;  // second setting
      prevProspects2 = rawProspects2;
      if (EM.dfe()) {
        EM.lfe();
        return 0.;
      }
      if (!dead) {
        EM.econCountsTest();
        EM.isHere("--EYEYag--", ec, "end of yearEnd stats");
        if (E.debugMisc && syW != null) {
          //   throw new MyErr("in CF.yearEnd end, syW != null");
        }
        if (EM.dfe()) {
          EM.lfe();
          return 0.;
        }
        return health = rawProspects2.curMin();
      } // end not dead
      if (EM.dfe()) {
        EM.lfe();
        return 0.;
      }
      return 0.;
    } // Assets.CashFlow.yearEnd()

    /**
     * routine to build and save the AI memory the previous year in mapfile This
     * builds the key and a value for this pors clan and saves it in mapFile, It
     * only starts saving in the third year with year 2 values It uses values
     * saved in saveAI. It update the "year" value to the current previous year
     * of for this key
     *
     * @param acct true if prev trade was accepted
     * @param worth the last worth of the economy
     * @param offer the last offer of the trade
     * @param prosM last prospects min
     * @param prosA last ave prospects2
     * @param score last score worth;
     *
     */
    void saveAIKey(boolean acct, double worth, double offer, double prosM, double prosA, double score) {//Assets.CashFlow
      // saveAIKey(acct, aiWorth, aiOffer, aiProsM, aiProsA, aiPos,prevAIPos, EM.myScore[clan]);
      aiOper = offers / worth;

      if (ec.age > 2 && prevAIERScore > 0. && aiERScore > 0.) {  // skip age 1 and 2 use prev values,this is before Y2 prev files exist
        assert EM.aiEScoreAve > 0 : "saveAI error EM.aiEScoreAve <= 0. is" + EM.mf(EM.aiEScoreAve);
        assert aiERScore > 0 : "saveAI error aiERScore <= 0. is" + EM.mf(aiERScore);
        assert prevAIERScore > 0 : "saveAI error prevAIERScore <= 0. is" + EM.mf(prevAIERScore);
        int cIx = 7;
        int much = 7;
        int aType = acct ? 1 : 0;
        String prevAKey = new String(EM.psClanChars[pors][clan]);  // string from char
        aKey = new String(EM.psClanChars[pors][clan]);  // string from char
        boolean ifPrint = true;
        aEntries[aType]++;
        aWaits++;
        ifPrint = aWaits > 5;

        if (E.debugAIOut && ifPrint) {

          System.out.println("----BAI1----  len" + EM.psClanChars[pors][clan].length + " TreeMap size=" + EM.myAIlearnings.size() + " put key=" + aKey + "");
        }
        //       fyWAIWorth = fyW.sumTotWorth;

        EM.psClanChars[pors][clan][E.ptype] = E.getAIResChar(aType);//from acct work on your key char
        //finsh building a new key

        String nudge0s[] = {"Nud0p", "Nud0s"};
        String nudge1s[] = {"Nud1p", "Nud1s"};
        aiPos = EM.whichScorePosByIncrClan[clan]; // last years position
        putValueChar(EM.psClanChars[pors][clan], E.ppors, pors, E.AILims123, "EconPors", y);
        putValueChar(EM.psClanChars[pors][clan], E.pLastScP, aiPos, E.AILims123, "lastaiPos", y);
        putValueChar(EM.psClanChars[pors][clan], E.pclanpors, clan * 2 + pors, E.AILims123, "clan*2+pors", y);
        putValueChar(EM.psClanChars[pors][clan], E.pNudge0, tradeFracNudge[nudBoth], E.AILims1, nudge0s[pors], y);
        putValueChar(EM.psClanChars[pors][clan], E.pNudge1, ffTFracNudge[nudBoth], E.AILims1, nudge1s[pors], y);
        //  putValueChar(EM.psClanChars[pors][clan], E.pNudge0, sliderVala, E.AILimsC, "Nudged value0", y);
        //putValueChar(EM.psClanChars[pors][clan], E.pNudge1, sliderValb, E.AILimsC, "Nudged value1", y);
        putValueChar(EM.psClanChars[pors][clan], E.pPrevProsM, prevAIProsM, E.AILims1, "prevProspects.min", ifPrint);
        putValueChar(EM.psClanChars[pors][clan], E.pPrevScP, prevAIPos, E.AILims123, "prevAIpos", ifPrint);
        putValueChar(EM.psClanChars[pors][clan], E.pPrevScW, prevAIScore, E.AILims1, "prevAIScore", ifPrint);
        putValueChar(EM.psClanChars[pors][clan], E.pPrevoPerW, prevAIOper, E.AILims1, "prevAIOperW", ifPrint);
        putValueChar(EM.psClanChars[pors][clan], E.pPrevEScW, prevAIEScore, E.AILims3, "prevEconScore", ifPrint);
        // this is skipped if aiERScore or prevAIERScore are <= 0
        putValueChar(EM.psClanChars[pors][clan], E.pPrevERScW, 30. * prevAIERScore, E.AILims2, "prevEconRScore", ifPrint);
        putValueChar(EM.psClanChars[pors][clan], E.pLastERScW, 30. * aiERScore, E.AILims2, "lastEconRScore", ifPrint);

        aKey = new String(EM.psClanChars[pors][clan]);
        aVal = EM.myAIlearnings.get(aKey);
        synchronized (A6Rowa.ASECS) {
          int vAge = ec.age - 1; // values are from last year
          int vYear = EM.year;
          if (aVal == null) {// no key found cnt,age,score
            aVal = new Integer[E.aValSize];
            aVal[E.aValCnts] = 1;
            aVal[E.aValAge] = vAge;
            aVal[E.aValYear] = vYear; // year of last update
            aVal[E.aValPClan] = clan == 5 ? 11 : pors * 5 + clan;
            aVal[E.aValIxMyScore] = getTestVal(score, E.AILims1, "prevAIScore");
          }
          else {
            aVal[E.aValCnts] += 1;
            aVal[E.aValAge] = vAge; // latest entry year and age usually
            aVal[E.aValYear] = vYear;
            aVal[E.aValPClan] = clan == 5 ? 11 : pors * 5 + clan;
            aVal[E.aValIxMyScore] = getTestVal(prevAIScore, E.AILims1, "prevAIScore"); //latest
          }
          if (ranInt > -1 && ranInt < aiNudges.length) { // redone later
            aiNudges[ranInt][pors] = 0.0; // zero any nudge
          }
          EM.myAIlearnings.put(aKey, aVal); // save last years values
          EM.setCnt++;
          eM.setCntAr(aKey, aVal, false, true, true);  //doSet 1 for the new key in each ars
        } // 
        if (E.debugAIOut || (aWaits++ % 5) == 0) {
          eM.printHere("----SAI2s----", ec, " put aType" + aType + " prevAIPos" + prevAIPos + ":" + " prevAIScore" + EM.mf(prevAIScore) + " lastScore" + EM.mf(EM.myScore[clan]) + " allCnt" + EM.ars[1][EM.iaAllCnt] + "\n" + ":mC" + aVal[E.aValCnts] + "mY" + aVal[E.aValYear] + ":mA" + aVal[E.aValAge] + " scoreIx" + aVal[E.aValIxMyScore] + " Size=" + EM.myAIlearnings.size() + " mapYears" + EM.mapYears + " setCnt" + EM.setCnt + " aKey was=" + prevAKey + " is=" + aKey);

        }
      }// end if year
    } //saveAIKey

    /**
     * routine to save AI values in Assets for saveAIKey that is called at the
     * start of this Assets next year
     *
     */
    void saveAI(double worth, boolean dead) {//Assets.CashFlow

      prevPrevAIScoreI = prevAIScoreI;
      prevAIScoreI = aiScoreI;
      aiScoreI = (aiScore - prevAIScore) / prevAIScore;
      prevPrevAIScore = prevAIScore;
      prevAIScore = EM.myScore[clan]; // start game value
      //  aiScore = EM.myScore[clan]; //
      prevAIPos = EM.whichScorePosByIncrClan[clan];
      prevPrevAIPos = prevAIPos;

      aiProsM = rawProspects2.min(); // these are last values
      aiProsA = rawProspects2.ave();
      aiOffer = tradedOffers;
      // aiEScoreI is this years new value after aiEScore done
      prevPrevAIEScore = prevAIEScore;
      prevAIEScore = aiEScore;
      aiEScore = getScore(aiOffer, aiWorth);
      assert !aiEScore.isInfinite() : "aiEScore isInfinite";
      assert !aiEScore.isNaN() : "aiEScore isNaN";
      prevAIEScoreI = aiEScoreI;
      aiEScoreI = (aiEScore - prevAIEScore) / prevAIEScore;
      prevPrevAIERScore = prevAIERScore;
      prevAIERScore = aiERScore;
      prevPrevAIERScoreI = prevAIERScoreI;
      prevAIERScoreI = aiERScoreI;
      aiERScoreI = (aiERScore - prevAIERScore) / prevAIERScore;

      aiERScore = EM.aiEScoreAve > 0. ? aiEScore / EM.aiEScoreAve : 0.;
      assert !aiERScore.isInfinite() : "saveAI infinite val, aiEScore=" + EM.mf(aiEScore) + " EM.aiEScoreAve=" + EM.mf(EM.aiEScoreAve) + " aiERScore=" + EM.mf(aiERScore);
      assert !aiERScore.isNaN() : "saveAI NaN val, aiEScore=" + EM.mf(aiEScore) + " EM.aiEScoreAve=" + EM.mf(EM.aiEScoreAve) + " aiERScore=" + aiERScore;
      aiOper = aiOffer / aiWorth;
      acct = tradeAccepted;
      prevAIEScoreI = aiEScoreI;
      aiEScoreI = (aiEScore - prevAIEScore) / prevAIEScore;
      prevPrevAIWorth = prevAIWorth;
      prevAIWorth = aiWorth;
      aiWorth = worth;//lastAIworth
      prevAIWorthI = aiWorthI;
      aiWorthI = (aiWorth - prevAIWorth) / prevAIWorth;
      prevPrevAIOffer = prevAIOffer;
      prevAIOffer = aiOffer;
      prevAIOfferI = (prevAIOffer - prevPrevAIOffer) / prevPrevAIOffer;
      prevPrevAIProsM = prevAIProsM;
      prevAIProsM = aiProsM;
      aiProsM = rawProspects2.min();
      // prevPrevAIProsMI = prevAIProsMI;
      prevAIProsMI = (aiProsM - prevAIProsM) / prevAIProsM;
      prevPrevAIProsA = prevAIProsA;
      prevAIProsA = aiProsA;
      aiProsA = rawProspects2.ave();
      prevAIOper = aiOper;
      //prevPrevAIResilience = prevAIResilience;
      // prevAIResilience = aiResilience;
      //prevIncAIResilience = (prevAIResilience - prevPrevAIResilience) / prevPrevAIResilience;
      //aiResilience = resilience; // set in getNeeds
      //prevPrevAIHope = prevAIHope;
      //prevAIHope = aiHope;
      //prevIncAIHope = (prevAIHope - prevPrevAIHope) / prevPrevAIHope;
      //aiHope = hope; // set in getNeeds
      prevPrevStrategicValue = prevStrategicValue;
      prevStrategicValue = strategicValue;
      prevPrevStrategicGoal = prevStrategicGoal;
      prevStrategicGoal = strategicGoal;
      prevSliderVala = sliderVala;
      prevSliderValb = sliderValb;
      setStat(EM.ESCORE, pors, clan, ec.age > 2 ? aiEScore : 0, ec.age > 2 ? 1 : 0);
      if (ec.age > 2) {
        setStat(EM.RELESCORE, pors, clan, aiERScore, 1);
      }
      if (ec.age > 2) {  // do yea rthree
        EM.wasHere8 = "---ELa7--- Assets AI set has lock";
        int cIx = 7;
        int much = 7;

        boolean ifPrint = true;
        aWaits++;
        ifPrint = aWaits > 5;
        if (tradeAccepted) {
          atEntries++;
          atPSEntries[pors]++;
          atClanEntries[pors][clan]++;
          cumOffers += offers;
          cumOffersPS[pors] += offers;
          cumOffersClan[pors][clan] += offers;
          cumBTWorth += btWTotWorth;
          cumBTWorthPS[pors] += btWTotWorth;
          cumBTWorthClan[pors][clan] += btWTotWorth;
          cumOffersPerWorth += cumOffers / cumBTWorth;
          cumOffersPS[pors] += cumOffersPS[pors] / cumBTWorthPS[pors];
          cumOffersClan[pors][clan] += cumOffersClan[pors][clan] / cumBTWorthClan[pors][clan];
          aType = 0;
          if (false && E.debugAIOut && (aWaits > 5)) {
            String str = new String(EM.psClanChars[pors][clan]);
            System.out.println("----BAI1----  len" + EM.psClanChars[pors][clan].length + " TreeMap size=" + EM.myAIlearnings.size() + " put key=" + str + "");
          }
          // if (E.debugAIOut || (++aWaits % 5) == 0) {
          if (E.debugAIOut) {
            String str = new String(EM.psClanChars[pors][clan]);
            System.out.println("----SAI2----" + " " + name + " saveAI put aType" + aType + " lastAIPos" + aiPos + ":" + " prevAIScore" + EM.mf(prevAIScore) + " prevScore" + EM.mf(EM.prevScore[clan]) + "\n" + " put key=" + str + " Map size=" + EM.myAIlearnings.size());
            //  aiERScore = aiEScore/EM.aiEScoreAve;
            System.out.println("----SAI3----" + " " + name + " saveAI aiEScore " + EM.mf(aiEScore) + " EM.aiEScoreAve" + EM.mf(EM.aiEScoreAve) + " aiERScore " + EM.mf(aiERScore));
          }
          // assert EM.aiEScoreAve > 0:"saveAI error EM.aiEScoreAve <= 0. is" + EM.mf(EM.aiEScoreAve);
          // assert aiERScore > 0:"saveAI error aiERScore <= 0. is" + EM.mf(aiERScore);
        }
        else if (dead) { // process dead never seen in saveAIKey
          aType = 2;
        }
        // str = (EM.prosBS = EM.myChars[aType] + EM.myChars[acct] + EM.myChars[pors] + EM.myChars[clan] + EM.myAICvals) + mProspC;
        //aMany = EM.myAIlearnings.get(str); // force valid number if null
        // aMany = aMany == null ? 1 : aMany + 1;
        // EM.myAIlearnings.put(str, aMany);
        if (false) {
          //   eM.printHere("----SAI4----", ec, " put key=" + str + " , =" + aMany + (tradeAccepted ? " tradeAccepted" : " !tradeAccepted") + (tradeRejected ? " tradeRejected" : " !tradeRejected") + (tradeLost ? " tradeLost" : " !tradeLost") + (tradeMissed ? " tradeMissed" : " !tradeMissed") + " aType" + aType + " acct" + acct + " pors" + pors + " clan" + clan);
        }

      }// end if year
    } //saveAI synchronized

    void yDestroyFiles() {
      // DoTotalWorths syW, tW, gSwapW, gGrowW, gCostW, fyW;

      syW = null;
      tW = null;
      gSwapW = null;
      gGrowW = null;
      gCostW = null;
      fyW = null;
//      rawHealths2 = null;
      rawProspects2 = null;
      reqGrowthCosts = null;
      //   A6Row reqGrowthRemnants = new A6Row();
      rawGrowthCosts = null;
      //   A6Row reqFertilities = new A6Row(ec,lev, "reqFertilities");
      rawFertilities2 = null;
//   A6Row limitedFertilities = new A6Row(ec,lev, "limitedFertilities");
      //  A6Row mtCosts = new A6Row();
      //   A6Row mtRemnants = new A6Row();
      mtgCosts = null;
//    A6Row mtgRemnants = new A6Row();
//      mtgEmergNeeds = null;
      mtgNeeds6 = null;
      //     mtgGoalNeeds = null;
      //     mtgGoalCosts = null;
//    A6Row mtgReqRemnants = new A6Row();
      mtggCosts10 = null;
      //   A6Row mtggRemnants = new A6Row();
      //  mtggEmergNeeds = null;
      mtggNeeds6 = null;
      mtgFertilities = null;
      //     rawGoalFertilities = null;
      //     rawGoalHealths = null;
      mtggRawHealths = null;
      mtggRawFertilities = null;
      mtggRawHealths2 = null;
      mtggRawFertilities2 = null;
      fertilities = null;
      growthCosts = null;
      mtgReqFertilities = null;
      reqMaintCosts10 = null;
      reqGrowthCosts10 = null;
      rawGrowthCosts10 = null;
      maintCosts10 = null;
      travelCosts10 = null;
      mtgCosts10 = null;
      growthCosts10 = null;
      consumerHealthMTGCosts10 = null;
      consumerTrav1YrCosts10 = null;
      consumerMaintCosts10 = null;
      consumerReqGrowthCosts10 = null;
      consumerReqMaintCosts10 = null;
      consumerTravelCosts10 = null;
      consumerFertilityMTGCosts10 = null;
      consumerMTC6 = null;
      consumerEMTC6 = null;
      consumerEMTGC6 = null;
      rawFertilities2 = null;
      //  rawHealths2 = null;
      fertilities2 = null;
      mtggRawFertilities2 = null;
      mtggRawHealths2 = null;
      mtgNeeds6 = null;
      growths10 = null;
      bids = null;
      stratVars = null;
// now for the firsts
      bidsFirst = null;
      stratVarsFirst = null;
    }

    void nullNotTrade() {

    }

    void nullTrade() { // null if a trade

    }

    /**
     * test whether swap failed and it is time to move to the next set of
     * conditions do not move on is isDoNot is still active move on to the next
     * flag if notDoing and no isDoNot
     */
    void testForFailure() {

      prevn = n;
      int isDoNots = doNot.isDoNot();
      // if (isDoNots > 0) {
      //   swapped = true;
      //   swapType = 3;
      // }
      //  if (notDoing() && isDoNots <= 0) {
      if (notDoing()) {
        hist.add(new History("FL", History.loopIncrements3, nTitle("FAIL:") + cmd.toString() + " " + srcIx + "->" + destIx, "mov=" + EM.mf(mov),
                             "src=" + (srcIx < 0 || srcIx > E.LSECS ? "none" : EM.mf(balances.get(ixWRSrc, srcIx))),
                             "r$=" + (rChrgIx < 0 || rChrgIx > E.LSECS ? "none" : EM.mf(balances.get(ixWRSrc, rChrgIx))),
                             "s$=" + (sChrgIx < 0 || sChrgIx > E.LSECS ? "none" : EM.mf(balances.get(ixWRSrc, sChrgIx))),
                             "dst=" + (destIx < 0 || destIx > E.LSECS ? "none" : EM.mf(balances.get(ixWRSrc, destIx))),
                             "H" + rawProspects2.curMinIx() + "=" + EM.mf(rawProspects2.curMin()), "NR" + EM.mf(mtgNeeds6.getRow(0).sum()), "NS" + EM.mf(mtgNeeds6.getRow(1).sum()), "mtg=" + EM.mf(mtgNeeds6.curSum()), "bals=" + EM.mf(bals.curSum()), "<<<<<<<"));
        doNot = doNot.zero();
        if (preveHr) {
          emergHr = false;
        }
        if (preveHs) {
          emergHs = false;
        }
        prevn = n;
        fracN = n / eM.maxn[pors];
        double prevnFrac = fracN;
        nextN = 6.;
        // flags are no longer used, default just add 3
        if (true) {
          nextN = fracN + .5; //
        }
        else /**
         * in case no more swaps can be done, change the flags by going to the
         * closest end of any existing flag, that flag will be turned off, and
         * swapping with another set of flags will be tried, until no more flag
         * end exist, or the end of swaps (eM.maxn[pors]) is reached. fracN is
         * the entering "n" fraction of maxn nextN is the proposed next "n"
         * fraction of maxn eM.maxn is the maximum swaps to be tried
         */
        if (fFlag && fracN < eM.fFrac[pors] && nextN > eM.fFrac[pors]) {
          prevNextN = nextN;
          nextN = eM.fFrac[pors];
          swapped = true;
          hist.add(new History(aPre, 7, "f raise n" + prevn + "to" + eM.maxn[pors] * nextN, ">>>>", (fFlag ? "f" : "!f") + whole(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + whole(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + whole(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + whole(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "h" : "!he") + whole(eM.maxn[pors] * eM.heFrac[pors]), "max=" + whole(eM.maxn[pors]), "fracN=" + EM.mf(fracN), "prvNxtN=" + wh(eM.maxn[pors] * prevNextN))
          );
        }
        else if (hFlag && fracN < eM.hFrac[pors] && nextN > eM.hFrac[pors]) {
          prevNextN = nextN;
          nextN = eM.hFrac[pors];
          swapped = true;
          hist.add(new History(aPre, 7, "h raise n=" + prevn + "to" + eM.maxn[pors] * nextN, "fracN=" + wh(eM.maxn[pors] * fracN), "nxN=" + wh(eM.maxn[pors] * nextN), "pNN=" + wh(eM.maxn[pors] * prevNextN), (fFlag ? "f" : "!f") + EM.mf(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + EM.mf(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + EM.mf(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + EM.mf(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "he" : "!he") + EM.mf(eM.maxn[pors] * eM.heFrac[pors]), "max=" + EM.mf(eM.maxn[pors])));

        }
        else if (hmFlag && fracN < eM.heFrac[pors] && nextN > eM.heFrac[pors]) {
          prevNextN = nextN;
          nextN = eM.heFrac[pors];
          swapped = true;
          hist.add(new History(aPre, 7, "he raise n=" + prevn + "to" + eM.maxn[pors] * nextN, "fracN=" + wh(eM.maxn[pors] * fracN), "nxN=" + wh(eM.maxn[pors] * nextN), "pNN=" + wh(eM.maxn[pors] * prevNextN), (fFlag ? "f" : "!f") + EM.mf(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + EM.mf(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + EM.mf(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + EM.mf(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "he" : "!he") + EM.mf(eM.maxn[pors] * eM.heFrac[pors]), "max=" + EM.mf(eM.maxn[pors])));
        }
        else if (gfFlag && fracN < eM.gfFrac[pors] && nextN > eM.gfFrac[pors]) {
          prevNextN = nextN;
          nextN = eM.gfFrac[pors];
          swapped = true;
          hist.add(new History(aPre, 7, "gf raise n" + prevn + "to" + eM.maxn[pors] * nextN + ">>>>", (fFlag ? "f" : "!f") + whole(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + whole(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + whole(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + whole(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "h" : "!he") + whole(eM.maxn[pors] * eM.heFrac[pors]), "fracN=" + EM.mf(fracN), "max=" + whole(eM.maxn[pors]), "prvNxtN=" + wh(eM.maxn[pors] * prevNextN)));
        }
        else if (gFlag && fracN < eM.gFrac[pors] && nextN > eM.gFrac[pors]) {
          prevNextN = nextN;
          nextN = eM.gFrac[pors];
          swapped = true;
          hist.add(new History(aPre, 7, "g raise n=" + prevn + "to" + eM.maxn[pors] * nextN, "fracN=" + wh(eM.maxn[pors] * fracN), "nxN=" + wh(eM.maxn[pors] * nextN), "pNN=" + wh(eM.maxn[pors] * prevNextN), (fFlag ? "f" : "!f") + EM.mf(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + EM.mf(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + EM.mf(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + EM.mf(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "h" : "!he") + EM.mf(eM.maxn[pors] * eM.heFrac[pors]), "max=" + whole(eM.maxn[pors])));
        }
        else if (gmFlag && fracN < eM.gmFrac[pors] && nextN > eM.gmFrac[pors]) {
          prevNextN = nextN;
          nextN = eM.gmFrac[pors];
          swapped = true;
          hist.add(new History(aPre, 7, "gm raise n=" + prevn + "to" + eM.maxn[pors] * nextN, "fracN=" + wh(eM.maxn[pors] * fracN), "nxN=" + wh(eM.maxn[pors] * nextN), "pNN=" + wh(eM.maxn[pors] * prevNextN), (fFlag ? "f" : "!f") + EM.mf(eM.maxn[pors] * eM.fFrac[pors]), (gfFlag ? "gf" : "!gf") + EM.mf(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + EM.mf(eM.maxn[pors] * eM.gFrac[pors]), (hFlag ? "h" : "!h") + EM.mf(eM.maxn[pors] * eM.hFrac[pors]), (emergHr || emergHs ? "h" : "!he") + EM.mf(eM.maxn[pors] * eM.heFrac[pors]), "max=" + whole(eM.maxn[pors])));
        }
        else if (geFlag && fracN < eM.geFrac[pors] && nextN > eM.geFrac[pors]) {
          prevNextN = nextN;
          nextN = eM.geFrac[pors];
          swapped = true;
          hist.add(new History(aPre, 7, "ge raise n=" + prevn + "to" + eM.maxn[pors] * nextN, "fracN=" + wh(eM.maxn[pors] * fracN), "nxN=" + wh(eM.maxn[pors] * nextN), "pNN=" + wh(eM.maxn[pors] * prevNextN), "max=" + whole(eM.maxn[pors]), (gfFlag ? "gf" : "!gf") + EM.mf(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + EM.mf(eM.maxn[pors] * eM.gFrac[pors]), (gmFlag ? "gm" : "!gm") + EM.mf(eM.maxn[pors] * eM.gmFrac[pors]), (geFlag ? "ge" : "!ge") + EM.mf(eM.maxn[pors] * eM.geFrac[pors]), (hFlag ? "h" : "!h") + EM.mf(eM.maxn[pors] * eM.hFrac[pors]), (hmFlag ? "hm" : "!hm") + EM.mf(eM.maxn[pors] * eM.hmFrac[pors]), (heFlag ? "he" : "!he") + EM.mf(eM.maxn[pors] * eM.heFrac[pors]), (emergHr || emergHs ? "h" : "!he") + EM.mf(eM.maxn[pors] * eM.heFrac[pors])));
        }

        E.myTest(nextN < prevnFrac, "attempt to reduce nextN=N=" + EM.mf(prevnFrac) + " to " + EM.mf(nextN));

        nextN = eM.maxn[pors] < nextN ? eM.maxn[pors] : nextN;
        n = (int) Math.ceil(eM.maxn[pors] * nextN);

      }
      if (prevn != n) {
        hist.add(new History(aPre, 4, ((n == prevn) ? swapped ? "swapped n" : "passd n" : isDoNots > 0 ? "raised n" : "doNot raise n=") + prevn + "to" + n + ">>>>", (gfFlag ? "gf" : "!gf") + EM.mf(eM.maxn[pors] * eM.gfFrac[pors]), (gFlag ? "g" : "!g") + EM.mf(eM.maxn[pors] * eM.gFrac[pors]), (gmFlag ? "gm" : "!gm") + EM.mf(eM.maxn[pors] * eM.gmFrac[pors]), (geFlag ? "ge" : "!ge") + EM.mf(eM.maxn[pors] * eM.geFrac[pors]), (hFlag ? "h" : "!h") + EM.mf(eM.maxn[pors] * eM.hFrac[pors]), (hmFlag ? "hm" : "!hm") + EM.mf(eM.maxn[pors] * eM.hmFrac[pors]), (heFlag ? "he" : "!he") + EM.mf(eM.maxn[pors] * eM.heFrac[pors]), "nextN=" + wh(eM.maxn[pors] * nextN), "fracN=" + wh(eM.maxn[pors] * fracN), "max=" + whole(eM.maxn[pors]), "isDoNot=" + isDoNots, "<<<<"));
      }
    }

    /**
     * loop to adjust sector balances for growth, or endYear health
     *
     * @param aPre pefix for hist entries
     * @param yphase phase of year
     * @param xitLoop
     */
    void doLoop(String aPre, yrphase yphase, HSwaps xitLoop) {
      lTitle = " " + name + " Swaps";
      histTitles(lTitle);
//      yinitCosts();  only in startYear
      swapped = true;
      decrCnt = 0;
      decrGain = 0;
      decrCost = 0;
      int prevn = n;
      ARow rcOld = bals.getRow(BALANCESIX + RCIX);
      ARow sgOld = bals.getRow(BALANCESIX + SGIX);
      double rcAve = rcOld.ave();
      double sgAve = sgOld.ave();
      ARow cOld = bals.getRow(BALANCESIX + CIX);
      ARow gOld = bals.getRow(BALANCESIX + GIX);
      ARow rOld = bals.getRow(BALANCESIX + RIX);
      ARow sOld = bals.getRow(BALANCESIX + SIX);
      double cPre, gPre, rPre, sPre, sgPre, rcPre;
      double cFrac = eM.startSwapsCFrac[pors][clan];
      double gFrac = eM.startSwapsGFrac[pors][clan];
      double rFrac = 1. - eM.startSwapsCFrac[pors][clan];
      double sFrac = 1. - eM.startSwapsGFrac[pors][clan];
      // move some reserve to working before swapping for growth
      bals.resum(0);
      bals.resum(1);
      double climit = bals.getRow(BALANCESIX + RCIX).ave() * eM.tradeReservFrac[pors];
      double glimit = bals.getRow(BALANCESIX + SGIX).ave() * eM.tradeReservFrac[pors];
      double rcb = 0., rbal, sbal, cbal, gbal;
      double sgb = 0.;

      // move reserves from trades back to working, leave only tradReservfrac
      for (n = 0; n < E.lsecs - 1; n++) {
        rcb = balances.get(BALANCESIX + RCIX, n);
        rbal = balances.get(BALANCESIX + RIX, n);
        cbal = balances.get(BALANCESIX + CIX, n);
        sbal = balances.get(BALANCESIX + SIX, n);
        gbal = balances.get(BALANCESIX + GIX, n);
        mov = rcb * rFrac - rbal;
        if (E.debugSwaps) {
          E.myTest(cbal < NZERO, "cbal = %7.3f less than zero", cbal);
        }
        mov = Math.min(mov, cbal * .9999);
        if (mov > PZERO) {
          c.putValue(mtgAvails6, mov, n, n, r, 0, .0001);
        }
        hist.add(new History(aPre, 7, "c=>r" + n + "=" + EM.mf(mov), "r=" + EM.mf(rbal), EM.mf(balances.get(BALANCESIX + RIX, n)), "c=" + EM.mf(cbal), EM.mf(balances.get(BALANCESIX + CIX, n))));

        sgb = balances.get(BALANCESIX + SGIX, n);
        mov = sgb * sFrac - sbal;
        E.myTest(gbal < NZERO, "gbal = %7.2f less than zero", gbal);
        mov = Math.min(mov, gbal * .9999);
        if (mov > PZERO) {
          guests.putValue(mtgAvails6, mov, n, n, staff, 0, .0001);
        }
        hist.add(new History(aPre, 7, "g=>s" + n + "=" + EM.mf(mov), "s=" + EM.mf(sbal), EM.mf(balances.get(BALANCESIX + SIX, n)), "g=" + EM.mf(gbal), EM.mf(balances.get(BALANCESIX + GIX, n))));
      }
      EM.isHere("AFm", ec, "doLoops after move pre history");
      double nextN = 2.;
      bals.unzero("balances", BALANCESIX, 4);
      /**
       * reset swap values to their initial value
       */
      cmd = SwpCmd.NOT;
      n = 0;
      doNot.zero();
      done = false;
      nn = 0;
      unDo = reDo = 0;
      // now reset reserves for swaps

      // loop swaps till done or not swappet or maxn
      maxn = (int) eM.maxn[pors];
      for (n = 0; swapped && !eM.dfe() && !done && n < maxn; n++, nn++) {

        //move to swaps
        //  yCalcCosts("C#", lightYearsTraveled, curGrowGoal, curMaintGoal);  //includes yinitN
        lTitle = " Swaps " + name;
        histTitles(lTitle);
        // get the old swap values but the new Cost values
        //  prevns[0].copyn(cur, n);

        if (E.debugCheckBalances) {
          balances.checkBalances(cur);
        }
        yphase = yrphase.SWAPING;
        // update display during swap loops
        if (E.debugAssetsStats) {
          if ((n % 10) == 0) {
            st.paintCurDisplay(eM.curEcon);
          }
        }
        swapped = swaps("S%", lightYearsTraveled); // do possible swaps
        failed = !swapped;
        if (n % 5 == 0) {
          eM.printHere("----SWa---", ec, "after swaps" + n + (swapped ? " swapped " : " failed ") + EM.sinceDoYear());
        }
        if (History.dl > 4) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          hist.add(new History(aPre, 5, "after swap " + wh(a0.getLineNumber()), "n=" + n, (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + EM.mf(sumTotWorth), "srcIx" + srcIx, "ixWRSrc" + ixWRSrc, "destIx=" + destIx, "<<<<<<<<<<<<<"));
        }
        swapResults(aPre);
        testForFailure();  // raise n to the next failed value

        preveHr = emergHr;  // may be unneeded
        preveHs = emergHs;

      } // loop on n
      swapsN = n;

    } // end doLoop

    /**
     * calculate the subAsset costs for a year These costs are used in getNeeds
     * to calculate the yearly need See getNeeds to understand how costs are
     * used
     *
     * @param balances the number of units of each SubAsset per sector
     * @param rawUnitGrowths input rawUnitGrowth of each subasset
     * @param rawGrowths result raw growths before fertility applied
     * @param invMEfficiency inverse of Maint Efficiency
     * @param invGEfficiency inverse of Growth Efficiency
     * @param sIx index which SubAsset being invoked
     * @param tIx line in costs array for this kind of cost, this line adjusts
     * each cargo, staff, guests with a somewhat different set of multipliers
     * @param consumerReqMaintCosts10 result consumer required maint costs
     * @param nReqMaint result services required maint costs
     * @param consumerReqGrowthCosts10 result consumer required growth costs
     * @param nReqGrowth result service required growth costs
     * @param consumerMaintCosts10 result consumer maint costs
     * @param nMaint result service maint costs
     * @param mTravel1Yr result consumer 1 year travel costs
     * @param nTravel1Yr result services 1 year travel costs
     * @param travelYearsCosts result service travel costs
     * @param consumerGrowthCosts10 result consumer growth costs
     * @param nGrowth result service growth costs
     * @param swork unit staff work
     * @param yearsTraveled input years traveled
     */
    void calcRawCosts(A6Row balances, A6Row rawUnitGrowths, A6Row rawGrowths, A6Row invMEfficiency, A6Row invGEfficiency, int sIx, int tIx, A10Row consumerReqMaintCosts10, A10Row nReqMaint, A10Row consumerReqGrowthCosts10, A10Row nReqGrowth, A10Row consumerMaintCosts10, A10Row nMaint, A10Row mTravel1Yr, A10Row nTravel1Yr, A10Row travelYearsCosts, A10Row consumerGrowthCosts10, A10Row nGrowth, ARow swork, double yearsTraveled) {  // Assets.CashFlow.calcRawCosts
      int aage = eM.curEcon.age; // test for null
      double t1, t2, t3, t4 = -999., t5, t6, t7, workG, swork2 = 01.;
      int rcsg = sIx;
      // int ix2 = (int) sIx / 2;
      Double d, d1, d2;
      /**
       * now loop through i = consumer aspect of financial sectors j is the
       * services section of the financial sectors In general we measure the
       * demands on the financial sector, against the resource availability of
       * the financial sector. This process also gathers year totals yj... of
       * service requirements. The health of an economy is how close the weakest
       * sector is to supplying the demands from the consumers. Random factors
       * change relationships each year. The fertility of an economy is measured
       * by how close the service financial sectors, meet the demands from all
       * consumer aspects.
       */
      if (Econ.saveHist && (n < 999 && (sIx == 0 || sIx == 2))) {  //r & s
        hist.add(new History("#a", History.valuesMajor6, "swork", swork));
        String[] balTits = {"r bal", "c bal", "s bal", "g bal"};
        hist.add(new History("#a", History.valuesMajor6, balTits[sIx], balances.A[sIx + 2]));
        String[] maintTitr = {"rrMaint", "rcMaint", "rsMaint", "rgMaint"};
        //   hist.add(new History("#a", History.valuesMajor6, maintTitr[sIx]+"C", nMaint.A[2+sIx]));
        String[] maintTits = {"srMaint", "scMaint", "ssMaint", "sgMaint"};
        //   hist.add(new History("#a", History.valuesMajor6, maintTits[sIx]+"C", nMaint.A[2+4+sIx]));
        //hist.add(new History("#a", History.valuesMajor6, "consumerMaintCosts10 sIx=" + sIx, consumerMaintCosts10.A[6]));
      }
      int bbge = eM.curEcon.age;
      bals.resum(1);
      bals.resum(0);
      bbge = eM.curEcon.age;
      // reset the instance with bals instance, probably there before
      sys[sIx].growth = bals.getRow(ABalRows.GROWTHSEFFIX + sIx);

      double rm = .6; // random multiplier
      // i loops across the consumers, get rawGrowths and workG here
      for (i = 0; i < E.lsecs; i++) {
        int aaage = eM.curEcon.age; // test for null
        aaage = ec.age;// test for null
        ARow kMaint = new ARow(ec);

        //reset growth with current swork values  but getNeeds uses rawUnitGrowth
        //bals.set(ABalRows.GROWTHSIX + sIx, i, (workG = swork.get(i)* bals.get(ABalRows.RAWGROWTHSIX + sIx, i)));
        //  * sys[sIx].rawUnitGrowth.get * cRand(i + sIx + 10)));
        //double iBal = Math.pow(bals.get(ABalRows.BALANCESIX + sIx, i), EM.balanceMult[0][0]);
        double iBal = Math.sqrt(bals.get(ABalRows.BALANCESIX + sIx, i) * (bals.get(ABalRows.BALANCESIX + sIx, i) + 5.) * 0.1);
        double sBal = Math.sqrt(bals.get(ABalRows.BALANCESIX + sIx, i) * (bals.get(ABalRows.BALANCESIX + sIx, i) + 5.) * 0.1);
        //  double iBal = bals.get(ABalRows.BALANCESIX + sIx, i) *1.89;
        // j loops across services that as a sum are used by consumers
        for (j = 0; j < E.lsecs; j++) {
          assert rs[0][0][sIx] > 0.0 : "Error rs[0][0][sIx] zero=" + EM.mf(rs[0][0][sIx]) + ", sIx=" + sIx + ", name=" + getName() + ", i=" + i + ", j=" + j;
          assert invMEfficiency.get(sIx + 2, i) >= 0.0 : "Error invMEfficiency.get(sIx + 2, i) <= 0.0 =" + EM.mf(invMEfficiency.get(sIx + 2, i)) + ", sIx=" + sIx + ", name=" + getName() + ", i=" + i + ", j=" + j;
          assert bals.get(ABalRows.BALANCESIX + sIx, i) >= 0.0 : "Error balances.get(2 + sIx, i) < 0.0 =" + EM.mf(balances.get(2 + sIx, i)) + ", sIx=" + sIx + ", name=" + getName() + ", i=" + i + ", j=" + j;

          // calculate required maintenance, a requirement not a cost subtracted
          // the prospects calculate from this and must be positive for health
          // a negative required maintenance remainder bal -reqm means death
          // crand(31) applies the same random number to each calc for the year
          t1 = iBal * cRand(31) * cRand(i * E.lsecs + j, rm) * E.maintRequired[pors][i][j] * rs[0][0][sIx]
               * (tIx == 0 ? 1. : E.maintRequired[pors][tIx][i])
               * invMEfficiency.get(sIx + 2, i);
          // these values are all staff counts, converted from work counts by bal/swork
          d = swork.get(j);
          // convert illegal d to very very small positive
          swork2 = d = (d.isInfinite() || d.isNaN()) || d < E.PZERO ? E.UNZERO : d;
          t2 = sBal * cRand(31) * cRand(i * E.lsecs + sIx + 8 + j, rm) * E.maintRequired[pors][i][j + E.LSECS] * rs[0][1][sIx] * (tIx == 0 ? 1. : E.maintRequired[pors][tIx][i + E.lsecs]) * invMEfficiency.get(sIx + 2, i);
          // gather 7 service requests to i  (7 j values, service by i
          consumerReqMaintCosts10.add(2 + sIx, i, t1);
          // consumerReqMaintCosts10.add(0, i, t1);  done by auto resum
          consumerReqMaintCosts10.add(6 + sIx, i, t2);
          //  consumerReqMaintCosts10.add(1, i, t2);
          nReqMaint.add(2 + sIx, j, t1);
          nReqMaint.add(6 + sIx, j, t2);
          //  nReqMaint.add(0, j, t1);
          // nReqMaint.add(1, j, t2);

          // calculate requried Growth resources, calculates growth fraction
          // is not part of yearly costs.
          t1 = iBal * cRand(31) * cRand(i * E.lsecs + sIx + j, rm) * E.resourceGrowthRequirementBySourcePerConsumer[pors][i][j] * rs[1][0][sIx] * (tIx == 0 ? 1. : E.resourceGrowthRequirementBySourcePerConsumer[pors][tIx][i]) * invGEfficiency.get(sIx + 2, i);
          // these values are all staff costs, converted from work counts by bal/swork
          t2 = sBal * cRand(31) * cRand(i * E.lsecs + 8 + j, rm) * E.resourceGrowthRequirementBySourcePerConsumer[pors][i][j + E.lsecs] * rs[1][1][sIx] * (tIx == 0 ? 1. : E.resourceGrowthRequirementBySourcePerConsumer[pors][tIx][i + E.lsecs]) * invGEfficiency.get(sIx + 2, i);
          consumerReqGrowthCosts10.add(sIx + 2, i, t1); //subasset costs
          consumerReqGrowthCosts10.add(sIx + 6, i, t2); // subasset costs
          nReqGrowth.add(sIx + 2, j, t1); // the correct subasset
          nReqGrowth.add(sIx + 6, j, t2);
          consumerReqGrowthCosts10.add(0, i, t1);
          consumerReqGrowthCosts10.add(1, i, t2);
          nReqGrowth.add(0, j, t1); //rc
          nReqGrowth.add(1, j, t2); //sg
          if (i == 6 && j == 6) {
            if (sIx == 0) {
              assert iBal > 0.0 : "iBal <= 0.0 iBal=" + EM.mf(iBal);
              assert cRand(31) * cRand(i * E.lsecs + sIx + j, rm) > 0.0 : "cRand(31) * cRand(i * E.lsecs + sIx + j, rm) <= 0.0 =" + EM.mf(cRand(31) * cRand(i * E.lsecs + sIx + j, rm));
              assert E.resourceGrowthRequirementBySourcePerConsumer[pors][i][j] > 0.0 : "E.resourceGrowthRequirementBySourcePerConsumer[pors][i][j] <= 0.0 =" + EM.mf(E.resourceGrowthRequirementBySourcePerConsumer[pors][i][j]);
              assert rs[1][0][sIx] > 0.0 : "rs[1][0][sIx] <= 0.0 =" + EM.mf(rs[1][0][sIx]);
              assert (tIx == 0 ? 1. : E.resourceGrowthRequirementBySourcePerConsumer[pors][tIx][i]) > 0.0 :
                      "(tIx == 0 ? 1. : E.resourceGrowthRequirementBySourcePerConsumer[pors][tIx][i]) <= 0.0 =" + EM.mf((tIx == 0 ? 1. : E.resourceGrowthRequirementBySourcePerConsumer[pors][tIx][i]));
              assert invGEfficiency.get(sIx + 2, i) > 0.0 : "invGEfficiency.get(sIx + 2, i) <= 0.0 =" + EM.mf(invGEfficiency.get(sIx + 2, i));
              assert nReqGrowth.get(2, j) > 0.0 : " nReqGrowth.get(2,j) <= 0.0 =" + EM.mf(nReqGrowth.get(2, j));
              assert nReqGrowth.get(6, j) > 0.0 : " nReqGrowth.get(2,j) <= 0.0 =" + EM.mf(nReqGrowth.get(6, j));
            }
            else {
              assert nReqGrowth.get(2 + sIx, j) > 0.0 : " nReqGrowth.get(2,j) <= 0.0 =" + EM.mf(nReqGrowth.get(2 + sIx, j));
              assert nReqGrowth.get(6 + sIx, j) > 0.0 : " nReqGrowth.get(2,j) <= 0.0 =" + EM.mf(nReqGrowth.get(6 + sIx, j));
            }
          }
          if (n < 5 && i == 6 && j == 6 && sIx == 2) {
            hist.add(new History("#b", History.valuesMajor6, "s bal ij" + i + j, balances.A[4]));
            hist.add(new History("#b", History.valuesMajor6, "mRGroC6 ij" + i + j + " sIx" + sIx + " n" + n, consumerReqGrowthCosts10.A[6]));
            hist.add(new History("#b", History.valuesMajor6, "nRGro6 i=" + i, nReqGrowth.A[6]));
          }

          // maintenance costs
          t1 = iBal * cRand(31) * cRand(i * E.lsecs + sIx + j + 31, rm) * E.maintCost[pors][i][j] * rs[2][0][sIx] * (tIx == 0 ? 1. : E.maintCost[pors][tIx][i]) * invMEfficiency.get(sIx + 2, i);
          t4 = t2 = sBal * cRand(31) * cRand(i * E.lsecs + sIx + j + 41, rm) * E.maintCost[pors][i][j + E.lsecs] * rs[2][1][sIx] * (tIx == 0 ? 1. : E.maintCost[pors][tIx][i + E.lsecs]) * invMEfficiency.get(sIx + 2, i);

          consumerMaintCosts10.add(sIx + 2, i, t1); // the r set of subcosts
          consumerMaintCosts10.add(sIx + 6, i, t2); // the s set of subcosts
          nMaint.add(sIx + 2, j, t1);
          nMaint.add(sIx + 6, j, t2);
          //    consumerMaintCosts10.add(0, i, t1);
          //   consumerMaintCosts10.add(1, i, t2);
          //    nMaint.add(0, j, t1);
          //    nMaint.add(1, j, t2);
          kMaint.add(j, t2);
          if (n < 5 && i == 6 && j == 6) {
            hist.add(new History("#c", History.valuesMajor6, "nM i=" + i + " sIx" + sIx + " n" + n, nMaint.A[6]));
            hist.add(new History("#c", History.valuesMajor6, "mM v=" + EM.mf(t2), consumerMaintCosts10.A[6]));
            hist.add(new History("#c", History.valuesMajor6, "kM i=" + i + " j=" + j, kMaint));
          }

          //travel costs
          t1 = iBal * cRand(31) * cRand(i * E.lsecs + sIx + j + 46, rm) * tCosts[pors][i][j] * rs[3][0][sIx] * (tIx == 0 ? 1. : tCosts[pors][tIx][i]) * invMEfficiency.get(sIx + 2, i);

          t2 = iBal * cRand(31) * cRand(i * E.lsecs + sIx + j + 55, rm) * tCosts[pors][i][j + E.lsecs] * rs[3][1][sIx] * (tIx == 0 ? 1. : tCosts[pors][tIx][i + E.lsecs]) * invMEfficiency.get(sIx + 2, i);

          //   E.myTestDouble(d,"t2","calcRawCosts process sIx=%d, i=%d,j=%d,swork=%7.2f,t2=%7.5f, d string=%s",sIx,i,j,t7,t2,String.valueOf(d));
          mTravel1Yr.add(sIx + 2, i, t1);
          mTravel1Yr.add(sIx + 6, i, t2);
          nTravel1Yr.add(sIx + 2, j, t1);
          nTravel1Yr.add(sIx + 6, j, t2);
          //   mTravel1Yr.add(0, i, t1);
          //   mTravel1Yr.add(1, i, t2);
          //  nTravel1Yr.add(0, j, t1);
          // nTravel1Yr.add(1, j, t2);
          if (n < -5 && sIx == 0) {
            hist.add(new History("#d", History.valuesMajor6, "nM i=" + i + " " + EM.mf(t4), nMaint.A[6]));
            hist.add(new History("#d", History.valuesMajor6, "mM i=" + i, consumerMaintCosts10.A[6]));
            hist.add(new History("#d", History.valuesMajor6, "lYT=" + EM.mf(lightYearsTraveled), nTravel1Yr.A[6]));
          }

          // growth costs
          t1 = iBal * gCosts[pors][i][j] * cRand(31) * rs[4][0][sIx] * (tIx == 0 ? 1. : gCosts[pors][tIx][i]) * invGEfficiency.get(sIx + 2, i);
          t2 = sBal * gCosts[pors][i][j + E.lsecs] * cRand(31) * rs[4][1][sIx] * (tIx == 0 ? 1. : gCosts[pors][tIx][i + E.lsecs]) * invGEfficiency.get(sIx + 2, i);
          if (E.debugCosts && sIx == 0) {
            assert iBal > 0.0 : " iaBal !> 0.0 =" + EM.mf(iBal);
            assert sBal > 0.0 : " sBal !> 0.0 =" + EM.mf(sBal);
            assert rs[4][1][sIx] > 0.0 : " rs[4][1][sIx] !> 0.0 =" + EM.mf(rs[4][1][sIx]);
            assert invGEfficiency.get(sIx + 2, i) >= 0.0 : " invGEfficiency.get(sIx + 2, i) !> 0.0 =" + EM.mf(invGEfficiency.get(sIx + 2, i));
            assert invMEfficiency.get(sIx + 2, i) >= 0.0 : " invGEfficiency.get(sIx + 2, i) !>0.0 =" + EM.mf(invMEfficiency.get(sIx + 2, i)
            );
            assert t1 > 0.0 : " t1 !> 0.0 =" + EM.mf(t1);
            assert t2 > 0.0 : " t2 !> 0.0 =" + EM.mf(t2);
          }

          consumerGrowthCosts10.add(sIx + 2, i, t1);
          consumerGrowthCosts10.add(sIx + 6, i, t2);
          nGrowth.add(sIx + 2, j, t1);
          nGrowth.add(sIx + 6, j, t2);
          if (i == 6 && j == 6) {
            if (sIx == 0) {
              assert iBal > 0.0 : "iBal <= 0.0 iBal=" + EM.mf(iBal);
              assert cRand(31) > 0.0 : "cRand(31) <= 0.0 =" + EM.mf(cRand(31));
              assert E.resourceGrowthRequirementBySourcePerConsumer[pors][i][j] > 0.0 : "E.resourceGrowthRequirementBySourcePerConsumer[pors][i][j] <= 0.0 =" + EM.mf(E.resourceGrowthRequirementBySourcePerConsumer[pors][i][j]);
              assert rs[4][0][sIx] > 0.0 : "rs[4][0][sIx] <= 0.0 =" + EM.mf(rs[4][0][sIx]);
              assert (tIx == 0 ? 1. : gCosts[pors][tIx][i]) > 0.0 :
                      "(tIx == 0 ? 1. : gCosts[pors][tIx][i]) <= 0.0 =" + EM.mf((tIx == 0 ? 1. : gCosts[pors][tIx][i]));
              assert invGEfficiency.get(sIx + 2, i) > 0.0 : "invGEfficiency.get(sIx + 2, i) <= 0.0 =" + EM.mf(invGEfficiency.get(sIx + 2, i));
              assert t1 > 0.0 : "t1 <= 0.0 =" + eM.mf(t1);
              assert t2 > 0.0 : "t2 <= 0.0 =" + eM.mf(t2);
              assert nGrowth.get(sIx + 2, j) > 0.0 : " nGrowth.get(sIx + 2, j) <= 0.0 =" + EM.mf(nGrowth.get(sIx + 2, j));
              assert nGrowth.get(sIx + 6, j) > 0.0 : "nGrowth.get(sIx + 6, j) <= 0.0 =" + EM.mf(nGrowth.get(sIx + 6, j));
              bals.set4(ABalRows.GROWTHCOSTSYYIX, nGrowth);
              assert bals.sum1(ABalRows.GROWTHCOSTSYYIX, 0) > 0.0 : "ABalRows.GROWTHCOSTSYYIX <= 0.0 =" + EM.mf(bals.sum1(ABalRows.GROWTHCOSTSYYIX, 1));
              eM.printHere(E.debugCalcRawCosts, "----CRC----", ec, "bals.sum4(ABalRows.GROWTHCOSTSYYIX)=" + EM.mf(bals.sum4(ABalRows.GROWTHCOSTSYYIX)) + " bal=" + eM.mf(bals.sum1(0, 2)) + " ibal" + eM.mf(iBal) + " sbal" + eM.mf(sBal) + " rs" + eM.mf(rs[4][0][sIx]) + " invGEfficiency.get(sIx + 2, i)=" + eM.mf(invGEfficiency.get(sIx + 2, i)));
            }
            else {
              assert nReqGrowth.get(2 + sIx, j) > 0.0 : " nReqGrowth.get(2,j) <= 0.0 =" + EM.mf(nReqGrowth.get(2 + sIx, j));
              assert nReqGrowth.get(6 + sIx, j) > 0.0 : " nReqGrowth.get(2,j) <= 0.0 =" + EM.mf(nReqGrowth.get(6 + sIx, j));
              assert nGrowth.get(sIx + 2, j) > 0.0 : " nGrowth.get(sIx + 2, j) <= 0.0 =" + EM.mf(nGrowth.get(sIx + 2, j));
              assert nGrowth.get(sIx + 6, j) > 0.0 : "nGrowth.get(sIx + 6, j) <= 0.0 =" + EM.mf(nGrowth.get(sIx + 6, j));
              eM.printHere(E.debugCalcRawCosts, "----CRC2----", ec, "bals.sum4(ABalRows.GROWTHCOSTSYYIX)=" + EM.mf(bals.sum4(ABalRows.GROWTHCOSTSYYIX)) + " bal=" + eM.mf(bals.sum1(0, 2)) + " ibal" + eM.mf(iBal) + " sbal" + eM.mf(sBal) + " rs" + eM.mf(rs[4][0][sIx]) + " invGEfficiency.get(sIx + 2, i)=" + eM.mf(invGEfficiency.get(sIx + 2, i)));
            }
          }
          //       consumerGrowthCosts10.add(0, i, t1);
          //      consumerGrowthCosts10.add(1, i, t2);
          //      nGrowth.add(0, j, t1);
          //     nGrowth.add(1, j, t2);

        } // end j

      } // end i  in SubAsset
      bals.set1(ABalRows.GROWTHCOSTSYIX, sIx, nGrowth.A[sIx + 2]);
      //disable historys
      if (false && n < 999 && (sIx == 0 || sIx == 2)) {  //r & s
        hist.add(new History("#a", History.valuesMajor6, "sWork", swork));
        String[] balTits = {"r bal", "c bal", "s bal", "g bal"};
        hist.add(new History("#a", History.valuesMajor6, balTits[sIx], balances.A[sIx + 2]));
        String[] maintTitr = {"rrMaint", "rcMaint", "rsMaint", "rgMaint"};
        hist.add(new History("#a", History.valuesMajor6, maintTitr[sIx] + "C", nMaint.A[2 + sIx]));
        String[] maintTits = {"srMaint", "scMaint", "ssMaint", "sgMaint"};
        hist.add(new History("#a", History.valuesMajor6, maintTits[sIx] + "C", nMaint.A[2 + 4 + sIx]));
        //hist.add(new History("#a", History.valuesMajor6, "consumerMaintCosts10 sIx=" + sIx, consumerMaintCosts10.A[6]));
      }
      travelYearsCosts.setAmultV(nTravel1Yr, yearsTraveled);
    }  // end calcRawCosts

    /**
     * calculate yearly costs at the cur AssetsYear level all changes are taken
     * from SubAssets added together and left in appropriate working SubAssets
     * variable (resource and staff) all costs are applied to working members,
     * but may involve the partners 9/2/15 convert all staff "work unit" costs
     * to "balance unit" costs immediately after summing them. All remnants etc
     * are in terms of "balance unit" costs.
     *
     * @param aPre prefix for log(History) entries
     * @param lightYearsTraveled ship:distance traveled, planet: plaines,trains
     * etc
     * @param curMaintGoal goal for maintenance
     * @param curGrowthGoal goal for growth
     */
    void yCalcCosts(String aPre, double lightYearsTraveled, double curMaintGoal, double curGrowthGoal) {  //CashFlow
      int mcnt = E.msgcnt;
      bals.unzero("bals", BALANCESIX, 4);
      E.msgcnt = mcnt;
      bals.unzero("growth", GROWTHSIX, 4);
      E.msgcnt = mcnt;
      rawGrowthCosts.unzero("rawGC", 2, 4);
      E.msgcnt = mcnt;
      rawGrowths.unzero("rawGr", 2, 4);
      //do not count all messages in the unzero
      E.msgcnt = mcnt;
      yCalcRawCosts(lightYearsTraveled, aPre, curMaintGoal, curGrowthGoal);
      //     xitCalcCosts = hcopyn(cur);
    } // trade.Assets.CashFlow.yCalcCosts

    /**
     * CashFlow variables created in yCalcRawCosts
     *
     */
    /**
     * calculate the Raw Server costs for Maintenance and Growtb Requirements,
     * Maintenance and Travel costs increased by any health penalty full growth
     * costs, These are all costs against the elements as servers, and do not
     * separate whether costs came from resource or staff consumers.
     *
     * @task call the SubAssets CalcRawCosts
     * @Task reaggregate SubAsseets Costs using A6Row variables and
     * constructions
     * @task use the A6Row setNeeds to calculate, health, rawHealths,
     * rawFertililties, growths, growthCosts, mtgCosts and needs setNeeds
     * derives the variables for both swaps, and costsAndGrowth YSwaps is called
     * in method yearEnd
     *
     * yCalcCosts is also called from Assets.CashFlow.Trade to set up the
     * initial offer for ether seekPlanet, or barter
     *
     * The swaps attempt to raise the server poductivity, increasing health and
     * fertility, and the actual growth of both resources and staff. All of
     * these costs are subject to random multipliers which are different for
     * each year Thus the tactics that worked for one year, may not work for
     * another year. The challenge of how large growth is for resources and
     * staff is left for after the swaps, assuming that the better server
     * productivity creates more growth. Because of the random factors the
     * function between server productivity and growth is complex and not
     * necessarily the best possible.
     *
     * @see exit costs and growth values
     * @see s.hptMTGCosts. exit cost maint, trav, growth against staff/guests
     * balance
     *
     * @param lightYearsTraveled
     */
    void yCalcRawCosts(double lightYearsTraveled, String aPre, double curMaintGoal, double curGrowthGoal) {  //CashFlow.yCalcRawCosts
      int aage = eM.curEcon.age; // test curEcon null
      double t1, t2, t3, t4, t5, t6;
      // zero output objects
      reqMaintCosts = makeZero(reqMaintCosts, "reqGCosts");
      reqGrowthCosts = makeZero(reqGrowthCosts, "reqGCosts");
      maintCosts = makeZero(maintCosts, "maintCosts");
      travelCosts = makeZero(travelCosts, "travelCosts");
      rawGrowthCosts = makeZero(rawGrowthCosts, "rawGCosts");
      maintCosts10 = makeZero(maintCosts10, "mCosts10");
      travelCosts10 = makeZero(travelCosts10, "tCosts10");
      rawGrowthCosts10 = makeZero(rawGrowthCosts10, "rawGCosts10");
      reqMaintCosts10 = makeZero(reqMaintCosts10, "reqMCosts10");
      reqGrowthCosts10 = makeZero(reqGrowthCosts10, "reqGCosts10");
      rawFertilities2 = makeZero(rawFertilities2, "rawFertilities2");
      rawProspects2 = makeZero(rawProspects2, "rawProspects2");
      //  rawHealths2 = makeZero(rawHealths2, "rawHealths2");
      mtggRawProspects2 = makeZero(mtggRawProspects2, " mtggRawProspects2");
      mtggRawFertilities2 = makeZero(mtggRawFertilities2, "mtggRawF2");
      mtggRawHealths2 = makeZero(mtggRawHealths2, "mtggRawH2");
      mtggNeeds6 = makeZero(mtggNeeds6, "mtggNeeds6");
      mtGNeeds6 = makeZero(mtGNeeds6, "mtGNeeds6");
      mtNeeds6 = makeZero(mtNeeds6, "mtNeeds6");
      mtgCosts10 = makeZero(mtgCosts10, "mtgCosts10");
      mtCosts10 = makeZero(mtCosts10, "mtCosts10");
      mtggCosts10 = makeZero(mtggCosts10, " mtggCosts10");
      growthCosts10 = makeZero(growthCosts10, "growthCosts10");
      // growths10 = makeZero(growths10, "growths10");
      // mtggGrowths6 = makeZero(mtggGrowths6, "mtggGrowths6");
      mtgNeeds6 = makeZero(mtgNeeds6, "mtgNeeds6");
      goalmtg1Needs6 = makeZero(goalmtg1Needs6, "goalmtg1Needs6");
      goalmtg1Neg10 = makeZero(goalmtg1Neg10, "goalmtg1Neg10");
      consumerReqMaintCosts10 = makeZero(consumerReqMaintCosts10, "consReqMCosts");
      consumerReqGrowthCosts10 = makeZero(consumerReqGrowthCosts10, "consReqGCosts");
      consumerMaintCosts10 = makeZero(consumerMaintCosts10, "consMCosts");
      consumerTrav1YrCosts10 = makeZero(consumerTrav1YrCosts10, "consT1Costs");
      consumerTravelCosts10 = makeZero(consumerTravelCosts10, "consTCosts");
      consumerRawGrowthCosts10 = makeZero(consumerRawGrowthCosts10, "consRawGCosts");
      consumerHealthMTGCosts10 = makeZero(consumerHealthMTGCosts10, "consHMTGCosts");
      consumerFertilityMTGCosts10 = makeZero(consumerFertilityMTGCosts10, "consFMTGCosts");
      consumerHealthEMTGCosts10 = makeZero(consumerHealthEMTGCosts10, "consEHMTGCosts");
      consumerFertilityEMTGCosts10 = makeZero(consumerFertilityEMTGCosts10, "consEFMTGCosts");
      nTrav1Yr = makeZero(nTrav1Yr, "nTrav1Yr");
      consumerTrav1YrCosts10 = makeZero(consumerTrav1YrCosts10, "constrv1YrCosts");

// gather the input for the SubAsset calcRawCosts
      growths.sendHist(hist, "C@");
      hist.add(new History("C@", History.valuesMajor6, "r.growth", r.growth));
      //   if (r.growth != growths.A[2]) {
      //  E.myTest(true, "r.growth not the same as growths.A[2]");
      //    }
      bals.resum(1);
      staff.checkSumGrades();
      guests.checkSumGrades();
      hist.add(new History("#s", History.valuesMajor6, "sbal", staff.balance));
      hist.add(new History("#g", History.valuesMajor6, "gbal", guests.balance));
      hist.add(new History("#s", History.valuesMajor6, "swork", staff.work));
      hist.add(new History("#g", History.valuesMajor6, "gwork", guests.work));

      travelCosts.zero();
      if (ec.getAge() > 1) { // after one yeaEnd with prevGrowth
        assert bals.getRow(ABalRows.DEPRECIATIONIX).get(0) > 0.0 : " bals.getRow(ABalRows.DEPRECIATIONIX).get(0) <= 0.0=" + EM.mf(bals.getRow(ABalRows.DEPRECIATIONIX).get(0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }
      // instantiate other objects we may want to list
      //    A10Row nTrav1Yr = new A10Row(ec,History.valuesMinor7, "nTrav1Yr");
      int defeat789 = 1; // 1 to not defeat
      //   growths.sendHist(hist, "C@");
      //   hist.add(new History("C@", History.valuesMajor6, "r.growth", r.growth));
      //  if (r.growth != growths.A[2]) {
      //     E.myTest(true, "r.growth not the same as growths.A[0]");
      //   }
      lTitle = "calcRawCosts " + ec.name;
      histTitles(lTitle);
      //    lightYearsTraveled = newTradeYear2? 0.9:lightYearsTraveled;
      calcRawCosts(balances, rawUnitGrowths, rawGrowths, invMEff, invGEff, 0, 0, consumerReqMaintCosts10, reqMaintCosts10, consumerReqGrowthCosts10, reqGrowthCosts10, consumerMaintCosts10, maintCosts10, consumerTrav1YrCosts10, nTrav1Yr, travelCosts10, consumerRawGrowthCosts10, rawGrowthCosts10, s.work, lightYearsTraveled);
      calcRawCosts(balances, rawUnitGrowths, rawGrowths, invMEff, invGEff, 1, 9 * defeat789, consumerReqMaintCosts10, reqMaintCosts10, consumerReqGrowthCosts10, reqGrowthCosts10, consumerMaintCosts10, maintCosts10, consumerTrav1YrCosts10, nTrav1Yr, travelCosts10, consumerRawGrowthCosts10, rawGrowthCosts10, s.work, lightYearsTraveled);
      calcRawCosts(balances, rawUnitGrowths, rawGrowths, invMEff, invGEff, 2, 7 * defeat789, consumerReqMaintCosts10, reqMaintCosts10, consumerReqGrowthCosts10, reqGrowthCosts10, consumerMaintCosts10, maintCosts10, consumerTrav1YrCosts10, nTrav1Yr, travelCosts10, consumerRawGrowthCosts10, rawGrowthCosts10, s.work, lightYearsTraveled);
      calcRawCosts(balances, rawUnitGrowths, rawGrowths, invMEff, invGEff, 3, 8 * defeat789, consumerReqMaintCosts10, reqMaintCosts10, consumerReqGrowthCosts10, reqGrowthCosts10, consumerMaintCosts10, maintCosts10, consumerTrav1YrCosts10, nTrav1Yr, travelCosts10, consumerRawGrowthCosts10, rawGrowthCosts10, s.work, lightYearsTraveled);
      lTitle = "yCalcRawCosts " + ec.name;
      histTitles(lTitle);
      r.worth.setAmultV(r.balance, eM.nominalWealthPerResource[pors]);
      c.worth.setAmultV(c.balance, eM.nominalWealthPerResource[pors] * eM.cargoWorthBias[0]);
      s.sumGrades(); // sets worth
      g.sumGrades(); // sets worth

      //   travelCosts10.setAmultV(nTrav1Yr, lightYearsTraveled);
      consumerTravelCosts10.setAmultV(consumerTrav1YrCosts10, lightYearsTraveled);
      int blev = History.debuggingMinor11;
      int alev = History.debuggingMajor10;
      if ((pors == E.S) && newTradeYear2) {
        maintCosts10 = tradeTravelMaintCosts10.copy10();
        travelCosts10 = tradeTravelCosts10.copy10();
      }
      /**
       * the resource server costs include costs from resources,cargo,staff and
       * guests. The staff servers costs include staff charges for
       * resource,cargo,staff and guest. cargo costs are included above in
       * resource, guests included above in staff
       */
      if (n < 2 && ec.age < 2 && hist.size() < 4000) {
        alev = History.loopMinorConditionals5;
        blev = alev + 2;
        lTitle = "initResource " + ec.name;
        //  histTitles(lTitle);
        hist.add(new History("c$", 20, "postCosts " + ec.name, "0Life", "1Struct", "2Energy", "3Propel", "4Defense", "5Gov", "6Col", "Min", "Sum", "Ave"));
        //       reqMaintCosts10.addJointCosts();
        //       consumerReqMaintCosts10.addJointCosts();
        //      maintCosts10.addJointCosts();
        //      consumerMaintCosts10.addJointCosts();
        //      rawGrowthCosts10.addJointCosts();

        reqMaintCosts10.sendHist(blev, alev, aPre, "reqMCsts10");
        consumerReqMaintCosts10.sendHist(blev, alev, aPre, "consReqMCosts");
        reqGrowthCosts10.sendHist(blev, alev, aPre, "reqGCsts10");
        consumerReqGrowthCosts10.sendHist(hist, aPre);
        maintCosts10.sendHist(blev, alev, aPre, "maintCosts10");
        consumerMaintCosts10.sendHist(blev, alev, aPre, "mMaintC");
        hist.add(new History("##", alev, "lightYTrav=", EM.mf(lightYearsTraveled), "sum T Cost=", EM.mf(travelCosts10.curSum()), "<<<<<<<"));
        travelCosts10.sendHist(blev, alev, aPre, "TravCosts10");
        rawGrowthCosts10.sendHist(blev, alev, aPre, "rawGCosts10");
        consumerRawGrowthCosts10.sendHist(blev, alev, aPre, "mGCosts10");
        invMEff.sendHist(blev, aPre, alev, "invMEff");
        invGEff.sendHist(blev, aPre, alev, "invGEff");
      }
      //   reqGrowthCosts10.sendPercent8(hist, blev, alev, aPre, "percents", balances, rawGrowths, reqMaintCosts10, reqGrowthCosts10, maintCosts10, travelCosts10, rawGrowthCosts10, growthCosts10, mtgCosts10);}
      // rebuild and zero mtgEmergNeeds
      //  mtgEmergNeeds = new A6Row(ec,History.valuesMajor6, "mtgENeeds");

      growths.sendHist(hist, "C@");
      hist.add(new History("C*", History.valuesMajor6, "r.growth", r.growth));
      //   if (r.growth != growths.A[2]) {
      //      E.myTest(true, "r.growth not the same as growths.A[0]");
      //    }

      double mtgResults10[] = new double[40];
      histTitles("D$", "get needs process");

      if (ec.getAge() > 1) { // after one yeaEnd with prevGrowth
        assert bals.getRow(ABalRows.PRECIATIONIX).get(0) > 0.0 : " bals.getRow(ABalRows.CUMULATIVEUNITDEPRECIATIONIX).get(0) <= 0.0=" + EM.mf(bals.getRow(ABalRows.DEPRECIATIONIX).get(0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }
      if (ec.getAge() > 1) { // after one yeaEnd with prevGrowth
        assert bals.getRow(ABalRows.GROWTHSEFFIX).get(0) > 0.0 : " bals.getRow(ABalRows.GROWTHSIX).get(0) <= 0.0=" + EM.mf(bals.getRow(ABalRows.GROWTHSEFFIX).get(0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }
      mtgNeeds6 = getNeeds("mtgNeeds6", "goals and  nyear,fracGrowth", yphase, n, n < 2 && !ec.clearHist() ? History.debuggingMinor11 : History.loopMinorConditionals5, bals, maintCosts10, travelCosts10, rawGrowthCosts10, rawGrowths, reqMaintCosts10, reqGrowthCosts10, rawFertilities2, rawProspects2, mtCosts10, growthCosts10, mtgCosts10, growths, curMaintGoal, curGrowthGoal, eM.futGrowthFrac[pors][clan], eM.futGrowthYrMult[pors][clan], mtggNeeds6, mtNeeds6, mtgAvails6, mtGNeeds6, goalmtg1Needs6, goalmtg1Neg10);
      if (ec.getAge() > 1) { // after one yeaEnd with prevGrowth
        assert bals.getRow(ABalRows.GROWTHSEFFIX).get(0) > 0.0 : " bals.getRow(ABalRows.GROWTHSIX).get(0) <= 0.0=" + EM.mf(bals.getRow(ABalRows.GROWTHSEFFIX).get(0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }
      r.worth.setAmultV(r.balance, eM.nominalWealthPerResource[pors]);
      c.worth.setAmultV(c.balance, eM.nominalWealthPerResource[pors] * eM.cargoWorthBias[0]);
      s.sumGrades(); // sets s worth
      g.sumGrades(); // sets g worth
      balances.checkBalances(cur);
      histTitles("C%", "rtd yCalcRawCosts");
      growths.sendHist(hist, "C%");
      hist.add(new History((aPre = "C%"), History.valuesMajor6, "r.growth", r.growth));
      //    if (r.growth != growths.A[2]) {
      //      E.myTest(true, "r.growth not the same as growths.A[2]");
      //   }
      totNeeds = mtgNeeds6.curSum();
      hist.add(new History(aPre, History.loopMinorConditionals5, "mtgNeeds6", "set all the costs10 etc, poorHealthEfft, fertility, health"));

      mNeeds.setMax(mtgNeeds6, mtggNeeds6); // largest need, smallest available
      growths.sendHist(hist, aPre);
      hist.add(new History(aPre, History.valuesMajor6, "r.growth", r.growth));
      if (ec.getAge() > 1) { // after one yeaEnd with prevGrowth
        assert bals.getRow(ABalRows.DEPRECIATIONIX).get(0) > 0.0 : " bals.getRow(ABalRows.CUMULATIVEUNITDEPRECIATIONIX).get(0) <= 0.0=" + EM.mf(bals.getRow(ABalRows.DEPRECIATIONIX).get(0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }
      if (r.growth != growths.A[2]) {
        E.myTest(true, "r.growth not the same as growths.A[2]");
      }
      //   rawFertilities2 = makeZero(rawFertilities2, "rawFertilities2");
      //  rawFertilities2.set(rawFertilities2);
      // rawHealths2 = makeZero(rawHealths2, "rawHealths2");
      //   rawHealths2.set(rawHealths2);
      health = rawProspects2.curMin();
      //     poorHealthEffect = mtgResults[A6Row.resPoorHealtEffect];
      fertility = rawFertilities2.ave();
      minFert = rawFertilities2.min();
      minH = rawProspects2.min();
      bLev = Math.min(History.informationMinor9, History.dl);
      rlev = History.valuesMajor6;
      if (!ec.myClearHist && hist.size() < 4000) {
        lev = rlev;
        ec.aPre = aPre = "c#";
        //  hist.add(new History(aPre, rlev, "reqMaintFractions", "mtgg=" + EM.mf(mtggResults[0]), "he=" + EM.mf(heResults[0]), "hg=" + EM.mf(hgResults[0]), "H=" + EM.mf(health), "fertility", "mtgg=" + EM.mf(mtggResults[2]), "fe=" + EM.mf(heResults[2]), "fg=" + EM.mf(hgResults[2]), "F=" + EM.mf(mtgResults[2]), EM.mf(eM.maintMinPriority)));
        hist.add(new History(aPre, rlev, "health=", EM.mf(health), EM.mf(minH), "fertility=", EM.mf(fertility), EM.mf(minFert)));
        balances.sendHist(bLev, aPre, rlev, "balances");
        mtggNeeds6.sendHist(bLev, aPre, rlev, "mtggN6");
        //     mtgEmergNeeds.sendHist(bLev, rlev, aPre, "emergN");
//        mtgGoalNeeds.sendHist(bLev, rlev, aPre, "goalN");
        mtgCosts10.sendHist(bLev, rlev, aPre, "mtgC10");
        mtgNeeds6.sendHist(bLev, aPre, rlev, "mtgNeeds");
        mtNeeds6.sendHist(blev, aPre, rlev, "mtNeeds");
        mtGNeeds6.sendHist(blev, aPre, rlev, "mtGNeeds6");
        if (n < 2 && !ec.clearHist()) {
          ec.lev = alev = rlev; //6
          ec.aPre = aPre = "&d";
          reqGrowthCosts10.sendPercent8(hist, blev, alev, aPre, "percents", balances, growths, reqMaintCosts10, reqGrowthCosts10, maintCosts10, travelCosts10, rawGrowthCosts10, growthCosts10, mtgCosts10);
          reqGrowthCosts10.sendPercent2(hist, blev, alev, aPre, "percents", balances, growths, reqMaintCosts10, reqGrowthCosts10, maintCosts10, travelCosts10, rawGrowthCosts10, growthCosts10, mtgCosts10);
        }

      }

      double tot = 0;
      ec.lev = rlev = History.informationMajor8;
      ec.aPre = aPre = "&e";
      histTitles(lTitle);
      sos = rawProspects2.min() < eM.sosTrigger[pors];
      int lev = History.informationMinor9;

      if (yphase == yrphase.TRADE || yphase == yrphase.SEARCH || yphase == yrphase.PRESEARCH) {
        histTitles("G$", "process strategic values");
        /* planet ship */
        // healths limits  rawProspects2  mtggRawProspects2
        double dAve = 1. / E.l2secs;  // inverse of 2Secs
        // boolean hGood = gfFlag || (gFlag && !(hFlag || heFlag) && rawProspects2.curMin() > .2);
        if (false) { // skip non needs
          double[] hLim = {1.3, 1.25};
          double[] hLim3 = {1.3, 1.2};
          double[] hLim2 = {1.4, 1.3};
          double[] fLim = {1.25, 1.15};
          double[] fLim2 = {1.25, 1.15};
          double[] fLim3 = {1.01, 1.01};
          double[] hggLim = {1.15, 1.35};
          double[] hLim5 = {1.35, 1.2};
          double[] hLim6 = {1.4, 1.5};
          double[] hggLim2 = {1.01, 1.01};
          double[] fggLim = {1.25, 1.25};
          double[] fLim4 = {1.18, 1.15};
          double[] fggLim2 = {1.01, 1.01};

          // greater hmtgg greater Strat value
          double[] gLim = {1.5, 1.5};
          double[] gLim2 = {1.01, 1.01};
          double[] gLim3 = {1.2, 1.03};
          double[] unitGrowLim = {1.17, 1.03};
          if (yphase == yrphase.TRADE || yphase == yrphase.SEARCH || yphase == yrphase.PRESEARCH) {// replace vals if TRADE
            hLim = hLim3;
            fLim = fLim2;
            hggLim = hLim5;
            fggLim = fLim4;
          }
          else if (eM.mtgWEmergency[pors][clan] > rawProspects2.min()) {
            hLim = hLim2;
            fLim = fLim3;
            hggLim = hggLim2;
            fggLim = fggLim2;
          }
          if (!gFlag) {
            gLim = gLim2;
          }
          if (yphase == yrphase.TRADE) {
            gLim = gLim3;
          }
          emergHr = rawProspects2.getRow(0).min() < eM.mtgWEmergency[pors][clan];
          emergHs = rawProspects2.getRow(1).min() < eM.mtgWEmergency[pors][clan];
          hEmerg = emergency = emergHr || emergHs;
          bLev = n < 2 && ec.age < 2 ? History.debuggingMinor11 : History.valuesMajor6;
          lev = History.valuesMinor7;
          ec.aPre = aPre = "&f";
          blev = 13;
          lev = History.loopMinorConditionals5;
          EM.wasHere = " YCalcRawCosts at  calc strategic newTradeYear 1=" + (newTradeYear1 ? "yes" : "no") + ", 2=" + (newTradeYear2 ? "yes" : "no");
          if (E.debugYcalcCosts) {
            rawProspects2.sendHist(hist, bLev, aPre, lev, "r rawProspects2", "s rawProspects2");
            rawFertilities2.sendHist(hist, bLev, aPre, lev, "r rawFerti2", "s rawFerti2");
          }
          rawUnitGrowths = new A6Row(ec).setUseBalances(History.valuesMajor6, "rawUGrowths", r.rawUnitGrowth, c.rawUnitGrowth, s.rawUnitGrowth, g.rawUnitGrowth);
          mtCosts10.sendHist(hist, blev, aPre, lev, "mtCosts10");
          A2Row mtFrac = new A2Row(ec, lev, "mtFrac").setFracAsubBdivByA(balances, mtCosts10);
          mtFrac.sendHist(hist, bLev, aPre, lev, "r mtFrac", "s mtFrac");
          if (E.debugYcalcCosts) {
            mtgNeeds6.sendHist2(hist, bLev, aPre, lev, "RCmtgNeeds", "SGmtgNeeds");
            mtggNeeds6.sendHist2(hist, bLev, aPre, lev, "RCmtggNeeds", "SGmtggNeeds");
          }
          // lower healths or growths => higher strategic value
          // more positive needs => higher strategic value
          //only uise needs, mostly current needs, not future needs
          A2Row stratHealths = new A2Row(ec, bLev, "stratHealths").strategicRecipValBbyLim("stratHealths", rawProspects2, hLim[pors]);
          A2Row stratMT = new A2Row(ec, lev, "stratMT").strategicRecipValBbyLim("stratMT", mtFrac, hLim[pors]);
          A2Row stratFertilities = new A2Row(ec, lev, "stratFertilities").strategicRecipValBbyLim("stratFertilities", rawFertilities2, fLim[pors]);
        }
        /*
          static double[][] strategicCurrentNeedsMult= {{-1.22,-1.22,-1.22,-1.22,-1.22},{ -1.22,-1.22,-1.22,-1.22,-1.22}};
  static double[][] strategicFutureNeedsMult= {{-1.05,-1.05,-1.05,-1.05,-1.05},{ -1.05,-1.05,-1.05,-1.05,-1.05}};
  static double[][] strategicEmergencyNeedsMult= {{-1.32,-1.32,-1.32,-1.32,-1.32},{ -1.32,-1.32,-1.32,-1.32,-1.32}};
         */
        double[] nLimG = {EM.strategicCurrentNeedsMult[0][clan], EM.strategicCurrentNeedsMult[1][clan]};
        double[] nLimGG = {EM.strategicFutureNeedsMult[0][clan], EM.strategicFutureNeedsMult[1][clan]};
        emergHr = rawProspects2.getRow(0).min() < eM.mtgWEmergency[pors][clan];
        emergHs = rawProspects2.getRow(1).min() < eM.mtgWEmergency[pors][clan];
        hEmerg = emergency = emergHr || emergHs;
        if (hEmerg) {
          double[] nnLG = {EM.strategicEmergencyNeedsMult[0][clan], EM.strategicEmergencyNeedsMult[1][clan]};
          nLimG = nnLG;
        }
        A2Row stratGNeeds = new A2Row(ec, lev, "stratGNeeds").strategicRecipValBbyLim("stratGNeeds", mtgNeeds6, nLimG[pors]);
        A2Row stratGGNeeds = new A2Row(ec, lev, "stratGGNeeds").strategicRecipValBbyLim("stratGGNeeds", mtggNeeds6, nLimGG[pors]);
        balances.checkBalances(cur);
        if (E.debugYcalcCosts) {
          blev = 13;
          lev = History.loopMinorConditionals5;
          rawProspects2.sendHist(hist, bLev, aPre, lev, "r rawProspects2", "s rawProspects2");
          // stratHealths.sendHist(hist, bLev, aPre, lev, "r stratHealths", "s stratHealths");
          //rawFertilities2.sendHist(hist, bLev, aPre, lev, "r rawFerti2", "s rawFerti2");
          //stratFertilities.sendHist(hist, bLev, aPre, lev, "r stratFertil", "s stratFertil");
          //mtFrac.sendHist(hist, bLev, aPre, lev, "r mtFrac", "s mtFrac");
          // stratMT.sendHist(hist, bLev, aPre, lev, "r stratMT", "s stratMT");
          mtgNeeds6.sendHist2(hist, bLev, aPre, lev, "RCmtgNeeds", "SGmtgNeeds");
          stratGNeeds.sendHist(hist, bLev, aPre, lev, "r stratGNeeds", "s stratGNeeds");
          mtggNeeds6.sendHist2(hist, bLev, aPre, lev, "RCmtggNeeds", "SGmtggNeeds");
          stratGGNeeds.sendHist(hist, bLev, aPre, lev, "r stratGGNeeds", "s stratGGNeeds");
        }
        // stratVarsHG = stratVarsHG.mult(stratHealths, stratFertilities, stratMT, stratGNeeds, stratGGNeeds);
        stratVarsHG = stratVarsHG.mult(stratGNeeds, stratGGNeeds);

        stratVarsHG.sendHist(hist, bLev, aPre, lev, "rStratHg", "sStratHg");
        stratVars = stratVarsHG.normalize(EM.strategicMultMin[pors][clan], lev, "stratVars");
        //stratVars = rawProspects2.normalize(.5,lev, "stratVars");
        ec.aPre = aPre = "&g";
        stratMult = stratVars;
        stratMult.sendHist(hist, bLev, aPre, lev, "rstratMult", "sstratMult");
        EM.wasHere2 = "CashFlow.yCalcRawCosts after stratMult calc";
        // r.unitWorth.set(eM.nominalWealthPerResource[pors]);
        //  stratVars.getRow(1).mult(stratUGrowths.getRow(0)).mult(stratUGrowths.getRow(1));
        ec.aPre = aPre = "&g";
        //stratVars.sendHist(hist, bLev, aPre, "r stratVars", "s stratVars");
      }// if trade or search
      bals.sendHist2(lev, aPre);
      // this is good for both trades and yearEnd (swaps)
      ec.blev = bLev = n < 2 && ec.age < 2 ? History.debuggingMinor11 : History.valuesMajor6;
      rawCW = new DoTotalWorths();  //totWorth at end of Assets.CashFlow.yCalcRawCosts
      sumTotWorth = rawCWTotWorth = rawCW.getTotWorth();
      NeedsPlusSum = mtgNeeds6.curPlusSum();
      NeedsNegSum = mtgNeeds6.curNegSum();
      rawProspectsMin = rawProspects2.min();
      rawProspectsMin2 = rawProspects2.min(2);
      rawProspectsNegSum = rawProspects2.negSum();

    } //CashFlow.yCalcRawCosts

    /**
     * check all rows of an A10Row for a negative value
     *
     * @param ccc The A10Row
     * @param who Description of the A10Row
     */
    void checkNegCosts(A10Row ccc, String who) {
      if (E.debugNegCosts) {
        for (int p = 0; p < 10; p++) {
          checkNegCostsR(ccc.getRow(p), who + "+r" + p);
        }
      }
    }

    /**
     * check for a neg value in any sector of the ARow
     *
     * @param rr the ARow to be checked
     * @param who The discription of the ARow
     */
    void checkNegCostsR(ARow rr, String who) {
      if (E.debugNegCosts) {
        for (int r = 0; r < E.LSECS; r++) {
          if (rr.get(r) < E.NZERO) {
            throw new MyErr(who + " sector=" + r + "=" + EM.mf(rr.get(r)));
          }
        }
      }
    }

    /**
     * Calculate the needs for each sector to either reach the goals, The costs
     * come from calcRawCosts through yCalcRawCosts and through yCalcCosts If at
     * the end of yearEnd any financial sector has an unmet need (a positive
     * need in a sector) the economy cannot survive the next year and it is set
     * dead. If at the end of year a sector balance is not greater than twice
     * the cost the sector has a poor health effect, the cost is proportionally
     * higher but the economy survives to the year.
     * <P>
     * Trades are done before calculating the end of year need, to prevent
     * deaths and reduce poor health effects.
     * </P>
     * <P>
     * The parameters rawFertilities, rawHealth, rawProspects are fractions
     * related to the goals, any negative fraction means an unmet goal. The
     * fractions of rawHealth and rawFertility are calculated with the surplus
     * after the required amounts, so that rawHealth of .5 = (balance -
     * reqHealthCost)/reqHealthCost, &lt; 0 means no survival, &gt; 0 and &lt;
     * .5 is poor health with a cost penalty, &gt; 1 means super health with a
     * bonus against costs
     * <p>
     * The effective growth and growth cost in units are calculated after the
     * maintenance and travel costs for each sector, the costs then the growth
     * is also calculated.
     * <p>
     * Finally param rawProspects2 the available units are calculated, if
     * negative the economy will die at yearEnd In addition PHE (Poor Health
     * Effect) is increases costs, larger PHE woorse health
     *
     * @param title title of the returned file
     * @param description description of the purpose of the invocation
     * @param yphase the phase at call TRADE or GROWTH or pay
     * @param rawCostsN total raw costs
     * @param aDl adjustable Dl display level, no log=hist.adds above this
     * @param bals balances of SubAssets rc,sg,r,c,s,g, also growth for 4
     * SubAssets, plus other Assets leval ARow s
     * @param maintCosts input annual service costs of maintenance from
     * yCalcCosts
     * @param travelCosts input annual service travel costs: from yCalcCosts
     * @param rawGrowthCosts input cost of services to this sector for the
     * rawGrowths:
     * @param rawGrowths input growths before fertility is applied
     * @param reqMaintCosts input cost of services to this sector for
     * maintenence healths = (bal-rqM)/rqM
     * @param reqGrowthCosts input service cost of growth balances to ensure
     * possible growth fertilities = (bal - rqC)/rqC
     * @param rawFertilities2 output Min frac of required Growth&Maint and
     * growth before any growth limits
     * <ol start=0><li>rc<li>sg<li></ol>
     * @param rawProspects2 output each sector availW*14/(rcSum+sgSum) a
     * proportional measure of availability against sum of balances. so size
     * independent
     * @param rawHealths output SubAssets ??? mostly not used
     * @param mtNegs output costs of maint and travel with phe
     * @param growthNegs output cost of growths
     * @param mtgNegs output costs SubAssets: the sum of maint,travel,growth
     * costs * including needGoal output<br>
     * @param growths output amount of growths (also part of bals)
     * @param maintGoal input if &gt; 0 force health and maint calc costs
     * 1+maint goal
     * @param growthGoal input if &gt; 0 force fertility and growth cost
     * 1+growthGoal
     * @param growMult input if &gt; 0 and growYears &gt 0 mult mtgNegs & growth
     * for growYears -1 by growMult
     * @param growYears input if &gt; 0 and growYears &gt 0 mult mtgNegs &
     * growth for growYears -1 by growMult
     * @param goalmtgNeeds needs using goals and growYears, growMult
     * @param mtNeeds needs without costs and benefits of growth
     * @param mtgAvails6 -needs, available after costs and benefits of growth
     * @param goalmtNeeds goal needs without costs and benefits of growth
     * @param goalmtg1Needs needs with goal only 1 year, no growMult
     * @param goalmtg1Negs Negs for 1 year with goals, no growMult
     * @return mtgNeeds survivalNeeds only for r,s, c,g are 0 or less, rc=r+c,
     * sg=s+g
     */
    // Assets.CashFlow.getNeeds
    public A6Row getNeeds(String title, String description, Assets.yrphase yphase, int rawCostsN, int aDl, ABalRows bals, A10Row maintCosts, A10Row travelCosts, A10Row rawGrowthCosts, A6Row rawGrowths, A10Row reqMaintCosts, A10Row reqGrowthCosts, A2Row rawFertilities2, A2Row rawProspects2, A10Row mtNegs, A10Row growthNegs, A10Row mtgNegs, A6Row growths, double maintGoal, double growthGoal, double growMult, double growYears, A6Row goalmtgNeeds, A6Row mtNeeds, A6Row mtgAvails6, A6Row goalmtNeeds, A6Row goalmtg1Needs, A10Row goalmtg1Negs) {
      int aage = eM.curEcon.age; // test curEcon
      A6Row rtn = new A6Row(ec, History.valuesMajor6, "needs");
      if (aDl > 3) {
        hist.add(new History("@n", History.valuesMinor7, title, ec.name + " >getNeeds", "phase=" + yphase.name() + ", " + description));
      }
      String aPre = "@n"; // local only
      ec.blev2 = aDl;

      if (maintCosts == null) {
        maintCosts = new A10Row(ec, History.informationMinor9, "maintCosts");
      }

      if (rawGrowthCosts == null) {
        rawGrowthCosts = new A10Row(ec, History.informationMinor9, "rawGrowthCosts");
      }
      rawGrowthCosts.setType(TCOST);
      if (rawGrowths == null) {
        growMult = 0.;
        growYears = 0.;
        rawGrowths = new A6Row(ec, History.informationMinor9, "rawGrowths");
      }
      rawGrowths.setType(TBAL);
      if (mtgNegs == null) {
        mtgNegs = new A10Row(ec, History.informationMinor9, "mtgNegs");
      }

      if (growthNegs == null) {
        growthNegs = new A10Row(ec, History.valuesMajor6, "growthNegs");
      }
      double mtgMult = 1.;
      if (growYears > PZERO) {
        mtgMult = growYears;
      }
      goalmtgNeeds.zero();
      // a 0 goal is treated as an unset goal, just with regular costs
      double gGoal = growthGoal > .01 ? growthGoal : .01;
      double mGoal = maintGoal > .01 ? maintGoal : .01;
      int pors = ec.getPors();
      int clan = ec.getClan();
      Double t1;
      double s1;
      aPre = "#a";
      if (ec.blev2 > 3) {
        histTitles(aPre, "in getNeeds");
        hist.add(new History(History.valuesMinor7, "enter getNeeds", "aDl=" + aDl,
                             "maintGoal=", EM.mf(maintGoal), "growthGoal=", EM.mf(growthGoal), "growMult", EM.mf(growMult), "growYears", EM.mf(growYears)));
      }
      // ensure cost rows 0,1 are set correctly

      double minLimHealths = 999., minLimFertilities = 999.;

      // A10Row reqGCMore.set(reqMCMore.set(balsMore.set(mCMore.set(tCMore.set(gCMore = new A10Row())))));
      lev = History.valuesMajor6;
      int bLev = ec.blev;
      ec.aPre = aPre = "i#";

      A10Row rqGC = reqGrowthCosts;  // make another reference name
      A10Row rqMC = reqMaintCosts;
      A6Row rqGCRem = new A6Row(ec, lev, "rqGCRem");//rem after force growthGoal
      A6Row rqMCRem = new A6Row(ec, lev, "rqMCRem");//rem after maintGoal
      A2Row rqGFrac = new A2Row(ec, lev, "rqGFrac");
      A2Row rqMFrac = new A2Row(ec, lev, "rqMFrac");
      A6Row rqNeed = new A6Row(ec, lev, "rqNeed");
      A2Row rqGFertFrac = new A2Row(ec, lev, "rqGFertFrac");
      A2Row rqMFertFrac = new A2Row(ec, lev, "rqMFertFrac");
      A2Row maddMC = new A2Row(ec, lev, "maddMC");
      A2Row maddGC = new A2Row(ec, lev, "maddGC");
      A2Row maddrqMC = new A2Row(ec, lev, "maddrqMC");
      A2Row maddrqGC = new A2Row(ec, lev, "maddrqGC");

      A6Row rqNeedGG = new A6Row(ec, lev, "rqNeedGG");
      A6Row rqNeedGM = new A6Row(ec, lev, "rqNeedGM");
      A2Row mrqMaxC = new A2Row(ec, lev, "mrqMax");
      A10Row mAddC = new A10Row(ec, lev, "mAddC"); // does .zero
      double dmrqMCFmin = 9999;
      A2Row mrqGCLimitedFrac = new A2Row(ec, lev, "mrqGCLimFrac");
      A2Row mrqMCLimitedFrac = new A2Row(ec, lev, "mrqMCLimFrac");

      //  A6Row effectiveFertilities = new A6Row(ec,lev, "effFert");
      double mGrowthGoal = growthGoal > PZERO ? growthGoal : 1.;
      double mMaintGoal = maintGoal > PZERO ? maintGoal : 1.;
      // use the sum of resouce and staff costs to derive the
      // health or fertility fraction (balance -cost)/cost s
      // divid the r,s balance in proportion to the costs being subtracted
      // calculate req Growth and Maint costs using growthGoal and MaintGoAL
      //    A10Row dmores = new A10Row(6,"dmores");
      double subMoreBals = 0.; // each SubAsset excess needs by maint or growth goal
      double submBalsSum = 0.; // each SubAsset sum of real bals
      // double subCostSum=0.; // each SubAsset sum of real costs;
      // if there are no goals, still use sumIx... which holds the original values
      Double t2, t3, t4;
      for (int secIx : E.ASECS) {
        for (int sumIx : IA01) {  // for rc and sg (bals-mcosts) - (bals-cost)
          // put in initial balances int remaindersA6Row below
          rqGCRem.set(2 + 2 * sumIx, secIx, bals.get(2 + 2 * sumIx, secIx));  //r,s
          rqMCRem.set(2 + 2 * sumIx, secIx, bals.get(2 + 2 * sumIx, secIx)); //rem after maint
          rqGCRem.set(3 + 2 * sumIx, secIx, bals.get(3 + 2 * sumIx, secIx));// c,g
          rqMCRem.set(3 + 2 * sumIx, secIx, bals.get(3 + 2 * sumIx, secIx));
          rqGCRem.set(sumIx, secIx, bals.get(2 + 2 * sumIx, secIx) + bals.get(3 + 2 * sumIx, secIx));// rc = r + c
          rqMCRem.set(sumIx, secIx, bals.get(2 + 2 * sumIx, secIx) + bals.get(3 + 2 * sumIx, secIx)); //sg = s + g
          //initialize needs (cost-bal), the negative of available balances
          rqNeedGG.set(2 + 2 * sumIx, secIx, -bals.get(2 + 2 * sumIx, secIx)); // r, s
          rqNeedGM.set(2 + 2 * sumIx, secIx, -bals.get(2 + 2 * sumIx, secIx)); //r,s
          rqNeedGG.set(3 + 2 * sumIx, secIx, -bals.get(3 + 2 * sumIx, secIx)); // c,g
          rqNeedGM.set(3 + 2 * sumIx, secIx, -bals.get(3 + 2 * sumIx, secIx));

          // calculate remainders bal-required cost to find % cost
          for (int subsIx : IA03) {
            // Remainders after subtrace units costs type A10Row
            // Note A10row subtracts from SG && RC
            rqGCRem.add(2 + 2 * sumIx, secIx, -(reqGrowthCosts.get(2 + 4 * sumIx + subsIx, secIx))); //-sum r,s costs
            rqMCRem.add(2 + 2 * sumIx, secIx, -(reqMaintCosts.get(2 + 4 * sumIx + subsIx, secIx)));
            //rqGCRem.add(sumIx, secIx, -(reqGrowthCosts.get(2 + 4 * sumIx + subsIx, secIx))); //-sum rc,sg costs
            //rqMCRem.add(sumIx, secIx, -(reqGrowthCosts.get(2 + 4 * sumIx + subsIx, secIx)));

            // needs -bal + costs all units using goals or 1 without goals
            rqNeedGG.add(2 + 2 * sumIx, secIx, (1. + mGrowthGoal) * reqGrowthCosts.get(2 + 4 * sumIx + subsIx, secIx)); // R,S
            rqNeedGM.add(2 + 2 * sumIx, secIx, (1. + mMaintGoal) * reqMaintCosts.get(2 + 4 * sumIx + subsIx, secIx)); // R,S not C G but took all costs
            //rqNeedGG.add(sumIx, secIx, (1. + mGrowthGoal) * reqGrowthCosts.get(2 + 4 * sumIx + subsIx, secIx));
            //rqNeedGM.add(sumIx, secIx, (1. + mMaintGoal) * reqMaintCosts.get(2 + 4 * sumIx + subsIx, secIx));
          } // xit subsIx
          // set 0,1 rows
          rqGCRem.set(sumIx, secIx, rqGCRem.get(2 + 2 * sumIx, secIx) + rqGCRem.get(3 + 2 * sumIx, secIx));// rc,sg
          rqMCRem.set(sumIx, secIx, rqMCRem.get(2 + 2 * sumIx, secIx) + rqMCRem.get(3 + 2 * sumIx, secIx));
          rqNeedGG.set(sumIx, secIx, rqNeedGG.get(2 + 2 * sumIx, secIx) + rqNeedGG.get(3 + 2 * sumIx, secIx));// rc,sg
          rqNeedGM.set(sumIx, secIx, rqNeedGM.get(2 + 2 * sumIx, secIx) + rqNeedGM.get(3 + 2 * sumIx, secIx));
          // fracs just the 0,1 rows units/units
          //         E.myTest((t2=reqGrowthCosts.get(sumIx,secIx)) == 0.0 ||t2 == -0. ,"reqGrowthCosts[%d][%d]=%7.2f zero",sumIx,secIx,t2);
          //       E.myTest((t2=reqGrowthCosts.get(sumIx,secIx)) == 0.0 ||t2 == -0.,"reqGrowthCosts[%d][%d]=%7.2f zero",sumIx,secIx,t2);
          //calculate the req growth and maint costs
          // decide that zero cost is legal, so just make results a very large Frac
          t4 = ((t3 = reqGrowthCosts.get(sumIx, secIx)) < E.PZERO) || t3.isInfinite() || t3.isNaN() ? E.UNZERO : t3; //r,s
          // take the positive rem / calc reqCosts for reqgrowth and reqmaint  = a frac
          rqGFrac.set(sumIx, secIx, rqGCRem.get(2 + 2 * sumIx, secIx) / t4); //r,s
          t4 = (t3 = reqMaintCosts.get(sumIx, secIx)) < E.PZERO || t3.isInfinite() || t3.isNaN() ? E.UNZERO : t3; //r,s
          rqMFrac.set(sumIx, secIx, rqMCRem.get(2 + 2 * sumIx, secIx) / t4);
          rqGFertFrac.set(sumIx, secIx, rqGCRem.get(sumIx, secIx) / bals.get(sumIx, secIx));
          rqMFertFrac.set(sumIx, secIx, rqMCRem.get(sumIx, secIx) / bals.get(sumIx, secIx));
        } // xit sumIx
      } // xit secIx
      /*
        A10Row consumerHealthMTGCosts10, consumerTrav1YrCosts10, consumerMaintCosts10;
    A10Row consumerReqGrowthCosts10, consumerReqMaintCosts10, consumerTravelCosts10, consumerFertilityMTGCosts10;
    A10Row consumerHealthEMTGCosts10, consumerFertilityEMTGCosts10;
    A10Row consumerRawGrowthCosts10;
       */
      //now the point of required growth and maint is an input to the poorHealthEffect
      // phe is muoltiplied against costs, the smaller min the higher the effect
      // calculation. start with min of the 2 fracs
      minH = Math.min(rqGFrac.min(), rqMFrac.min());
      A2Row rqFertMinFrac = (new A2Row(ec, lev, "rqFertMinFrac")).setMin(rqGFertFrac, rqMFertFrac);
      bals.set2(ABalRows.REQFERTMINFRAC2IX, rqFertMinFrac);
      bals.set2(ABalRows.RQGFERTFRAC2IX, rqGFertFrac);
      bals.set2(ABalRows.RQMFERTFRAC2IX, rqMFertFrac);
      //  poorHealthAveEffect = poorHealthEffect = phe = eM.poorHealthPenalty[pors]
      // phe goals
      // minH < 0 increases 2 - minh  result > 2.--3.
      // minH < 0.5 ? 2 - minH ==  2. -- 1.5
      // minH < 1.? 2,- minH  = 1.5 --1.0.
      // minH < 1.5? 2 - minH 1.0  -- ,.5
      // minH > 1.5  ? .5 -- .5
      phe = poorHealthEffect = minH < 1.5 ? 2. - minH : .5;
      bals.set(ABalRows.POORHEALTHEFFECTIX, 0, phe);
      // = phe = minH < 0. ? 2. - minH : minH < .5 ? (2. - minH * .2) * .7
      //  : minH < 1. ? 1.7 + (minH - .5) * 2. * .3 : minH <= 2. ? 1. - (minH - 1.) * .3 : .7;

      ec.blev2 = bLev = Math.min(History.debuggingMinor11, aDl);
      // now compute the effective reqhealth and reqfertility
      //   A6Row effectiveHealths = new A6Row(ec,History.debuggingMinor11, "effHealths");
      ec.aPre = aPre = "#c";
      int alev = History.valuesMajor6;
      int alev2 = History.valuesMinor7;
      growths.titl = "growths";
      A10Row rawGC = rawGrowthCosts;
      A6Row rawG = rawGrowths;

      if (alev <= bLev) {
        hist.add(new History(aPre, History.loopMinorConditionals5, " values", "minMFrac", EM.mf(minH), "mGoal", EM.mf(mMaintGoal), "mGrowthGoal", EM.mf(growthGoal), "growthYrs", "" + growYears, "growthMult", EM.mf(growMult)));
        bals.sendHist(alev, aPre);
        rawGrowthCosts.sendHist(blev, alev, aPre, "rawGrowthCosts");
        rawG.sendHist(blev, aPre, alev, "rawG");
        //   mbals.sendHist24(bLev, aPre, alev, "r mbal", "s mbal");
        reqGrowthCosts.sendHist(blev, alev, aPre, "reqGrowthCosts");
        reqGrowthCosts.sendHist(blev, alev, aPre, "reqGrowthCosts");
        rqGCRem.sendHist(alev, aPre);
        rqMCRem.sendHist(alev, aPre);
        rqGFrac.sendHist(alev, aPre);
        rqMFrac.sendHist(alev, aPre);
        ec.aPre = aPre = "*C";
        rqNeedGG.sendHist(alev, aPre);
        rqNeedGM.sendHist(alev, aPre);
        rawGrowthCosts.sendHist(alev, aPre);//rawGCosts10
      }
      A10Row mtCosts10 = new A10Row(ec, alev, "mtCosts10").setAdd(maintCosts, travelCosts);
      bals.set2(ABalRows.MTCOSTS2IX, mtCosts10);
      consumerMTC6 = new A6Row(ec, alev, "ConMTC6").setAdd(make6(consumerMaintCosts10, "CMC6"), make6(consumerTravelCosts10, "CTC6"));
      checkNegCosts(mtCosts10, "mtCosts10");
      checkNegCosts(maintCosts, "maintCosts");
      checkNegCosts(travelCosts, "travelCosts");
      //  mtNegs.setAmultV(mtCosts10, phe);  // output
      // apply the poor health penalty to mt costs
      A10Row mtEC10 = new A10Row(ec, alev, "mtEC").setAmultV(mtCosts10, phe);
      bals.set2(ABalRows.MTECCOSTS2IX, mtEC10);
      consumerEMTC6 = new A6Row(ec, alev, "ConEMTC6").setAmultV(consumerMTC6, phe);
      checkNegCosts(mtEC10, "mtEC" + " P=" + EM.mf(phe));
      pmNegs.setAmultV(maintCosts, phe);
      ptNegs.setAmultV(travelCosts, phe);
      mtNegs.set(mtEC10);
      A10Row rawEGC = new A10Row(ec, alev, "rawEGC").setAmultV(rawGrowthCosts, phe);
      checkNegCosts(rawGrowthCosts, "rawGrowthCosts");
      checkNegCosts(rawEGC, "rawEGC" + " P=" + EM.mf(phe));

      A6Row pRemMT = new A6Row(ec, alev, "pRemMt");
      // (bals-mt) = remMt amount left for growth cost
      // remMt/gCost = mtgFraqc possible growth frac
      A2Row mtgFrac = new A2Row(ec, alev, "mtgFrac").setFracAsubBdivByCnRem(balances, mtEC10, rawEGC, pRemMT);
      // rawFertilities2 is the frac min of mtg frac, the the required fracs
      // A2Row minFertFracs = rawFertilities2.setMin(mtgFrac, rqGFrac);
      A2Row minFertFracs = (new A2Row(ec, alev, "minFertFracs")).setMin(mtgFrac, rqFertMinFrac);
      bals.set2(ABalRows.RQFERTMINS2IX, rqFertMinFrac);
      bals.set2(ABalRows.MTGFERTFRAC2IX, mtgFrac);
      // set limits on fertility
      rawFertilities2 = rawFertilities2.setLimits(minFertFracs, eM.minFertility[pors], eM.maxFertility[pors]);
      bals.set2(ABalRows.RAWFERTILITIES2IX, rawFertilities2);
      //    ABalRows.REQFERTMINFRAC2IX   ABalRows.POORHEALTHEFFECTIX  ABalRows.RAWFERTILITIES2IX  ABalRows.MTCOSTS2IX, ABalRows.RQFERTMINS2IX, ABalRows.MTGFERTFRAC2IX, ABalRows.MTGCOSTS2IX, ABalRows.MTGCOSTS2IX,
      consumerEMTGC6 = new A6Row(ec, alev, "conEMTGC6").setAmultF(consumerEMTC6, rawFertilities2);
      // now apply limited fertility to get fertility growths
      // fertilities can be less than zero, so apply the min for growths
      if (ec.getAge() > 1) { // late in getNeeds
        assert rawGrowths.get(0, 0) > 0.0 : " rawGrowths.get(0,0) <= 0.0=" + EM.mf(rawGrowths.get(0, 0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }
      //   A6Rowa1(EM.eM, newEc, nRows, t, alev, atitl);
      // A6Row fertilityGrowths = new A6Row(ec, alev, "fertilityGrowth").setAmultFminM(rawGrowths, rawFertilities2, .002);
      A6Row fertilityGrowths = growths.setAmultFminM(rawGrowths, rawFertilities2, .002);
      if (ec.getAge() > 1) { // late in getNeeds
        assert growths.get(0, 0) > 0.0 : " growths.get(0,0) <= 0.0=" + EM.mf(growths.get(0, 0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }
      if (ec.getAge() > 1) { // late in getNeeds
        assert growths.get(0) > 0.0 : " growths.get(0) <= 0.0=" + EM.mf(growths.get(0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }
      bals.useRef4(ABalRows.GROWTHSEFFIX, growths);
      bals.set4(ABalRows.GROWTHS1IX, growths);
      bals.useRef4(ABalRows.FERTILITYGROWTHSIX, fertilityGrowths);
      if (ec.getAge() > 1) { // late in getNeeds
        assert growths.get(0) > 0.0 : " growths.get(0) <= 0.0=" + EM.mf(growths.get(0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }
      if (ec.getAge() > 1) { // late in getNeeds
        assert fertilityGrowths.get(0) > 0.0 : " fertilityGrowths.get(0) <= 0.0=" + EM.mf(fertilityGrowths.get(0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }
      if (ec.getAge() > 1) { // late in getNeeds
        assert bals.getRow(ABalRows.GROWTHSEFFIX).get(0) > 0.0 : " bals.getRow(ABalRows.GROWTHSIX).get(0) <= 0.0=" + EM.mf(bals.getRow(ABalRows.GROWTHSEFFIX).get(0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }

// for the fertilityGrowths s get growthNegs (costs)
      growthNegs = growthNegs.setAmultF(rawEGC, rawFertilities2);
      checkNegCosts(growthNegs, "growthNegs");
      bals.set2(ABalRows.FERTILITYGROWTHCOSTSIX, growthNegs);
      bals.set2(ABalRows.GROWTHCOSTSIX, growthNegs);

      // now get total costs mt and growth
      mtgNegs.setAdd(mtEC10, growthNegs);
      checkNegCosts(mtgNegs, "mtgNegs");
      bals.set2(ABalRows.MTGCOSTS2IX, mtgNegs);
      if (ec.getAge() > 1) { // late in getNeeds
        assert bals.getRow(ABalRows.GROWTHSEFFIX).get(0) > 0.0 : " bals.getRow(ABalRows.GROWTHSEFFIX).get(0) <= 0.0=" + EM.mf(bals.getRow(ABalRows.GROWTHSEFFIX).get(0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }
      // finish the return value
      // now start needs6 calculation for C and G & R and S
      // recalc rawProspects using only working R & S
      // save the least remnant of bal - mtgNegs: rqMCRem:rqGCrem
      double balSum = bals.curSum();
      double tt1 = 0., tt2 = 0., tt3 = 0.;
      for (int secIx = 0; secIx < LSECS; secIx++) {
        for (int sumIx = 0; sumIx < 2; sumIx++) {
          tt1 = rqMCRem.get(sumIx, secIx);
          tt2 = rqGCRem.get(sumIx, secIx);
          tt3 = bals.get(2 + 2 * sumIx, secIx) + growths.get(sumIx, secIx) - mtgNegs.get(sumIx, secIx); // +2 r, +4 s
          rtn.set(3 + 2 * sumIx, secIx, -mtgAvails6.set(3 + 2 * sumIx, secIx, bals.get(3 + 2 * sumIx, secIx))); // +3 c, +5 g
          //now needs are -bal + negs(costs) - any additional growth
          // avails is the least remnant
          rtn.set(2 + 2 * sumIx, secIx, -mtgAvails6.set(2 + 2 * sumIx, secIx, tt1 < tt2 ? tt1 < tt3 ? tt1 : tt3 : tt2 < tt3 ? tt2 : tt3));
        }
      }
      /*
        A10Row consumerHealthMTGCosts10, consumerTrav1YrCosts10, consumerMaintCosts10;
    A10Row consumerReqGrowthCosts10, consumerReqMaintCosts10, consumerTravelCosts10, consumerFertilityMTGCosts10;
    A10Row consumerHealthEMTGCosts10, consumerFertilityEMTGCosts10;
    A10Row consumerRawGrowthCosts10;
    A6Row consumerMTC6,consumerEMTC6m=,,consumerEMTGC6;
       */
      // rawProspects, one way of predicting future need based on current availability over ave balance
      for (int secIx = 0; secIx < LSECS; secIx++) {
        for (int sumIx = 0; sumIx < 2; sumIx++) {
          rawProspects2.set(sumIx, secIx, (mtgAvails6.get(sumIx, secIx)) * 14 / balSum);
        }
      }
      if (ec.getAge() > 1) { // late in getNeeds
        assert bals.getRow(ABalRows.GROWTHSEFFIX).get(0) > 0.0 : " bals.getRow(ABalRows.GROWTHSEFFIX).get(0) <= 0.0=" + EM.mf(bals.getRow(ABalRows.GROWTHSEFFIX).get(0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }
      bals.set2(ABalRows.RAWPROSPECTS2IX, rawProspects2);
      /*
         double maintGoal, double growthGoal, double growMult, double growYears, A6Row goalmtgNeeds, A6Row mtNeeds, A6Row goalmtNeeds) {
       */
      // =========== start process goal costs =================================
      // bals + needs = mtEC + (rawEGC -rawG) * growthGoal
      // needs = -bals + gYears*(mtEC + (rawEGC - gMult*rawG) * growthGoal)
      double gYears = growYears > 1. ? growYears : 1.;
      double gMult = growMult > .4 ? growMult : 1.;
      mtNegs.setAmultV(mtNegs, gYears);
      A6Row mtgGNeeds = new A6Row(ec, alev, "mtgGNeeds");
      A6Row mtg1GNeeds = new A6Row(ec, alev, "mtg1GNeeds");
      A2Row mRG = new A2Row(ec, alev, "mRg");
      A2Row pRawGGC = new A2Row(ec, alev, "pRawGGC");
      if (ec.getAge() > 1) { // late in getNeeds
        assert bals.getRow(ABalRows.GROWTHSEFFIX).get(0) > 0.0 : " bals.getRow(ABalRows.GROWTHSEFFIX).get(0) <= 0.0=" + EM.mf(bals.getRow(ABalRows.GROWTHSEFFIX).get(0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }
      //  A6Row goalmtgNeed = goalNeeds;
      // now calculate goalmtNeeds and goalmtgNeeds
      // mGG = (gYear* (bal - (pMt + gmult*gGC))/gmult*gGC
      double na, nb, nc, nd;
      for (int secIx = 0; secIx < LSECS; secIx++) {
        for (int sumIx = 0; sumIx < 2; sumIx++) {
          nb = rqNeedGG.get(2 + 2 * sumIx, secIx); // required Growth Need
          nc = rqNeedGM.get(2 + 2 * sumIx, secIx);  // required Maint Need
          //r,s needs total g costs - r,s g growths
          mtgGNeeds.set(2 + 2 * sumIx, secIx, na = -bals.get(2 + 2 * sumIx, secIx) + gYears * (mtEC10.get(sumIx, secIx) + (mGrowthGoal * gMult * (rawEGC.get(sumIx, secIx) - rawG.get(2 + 2 * sumIx, secIx)))));
          goalmtNeeds.set(2 + 2 * sumIx, secIx, -bals.get(2 + 2 * sumIx, secIx) + gYears * (mtEC10.get(sumIx, secIx)));
          goalmtNeeds.set(3 + 2 * sumIx, secIx, -bals.get(3 + 2 * sumIx, secIx) + gYears * (mtEC10.get(sumIx, secIx)));
          mtg1GNeeds.set(2 + 2 * sumIx, secIx, nd = -bals.get(2 + 2 * sumIx, secIx) + (mtEC10.get(sumIx, secIx) + (mGrowthGoal * gMult * (rawEGC.get(sumIx, secIx) - rawG.get(2 + 2 * sumIx, secIx)))));
          goalmtg1Negs.set(2 + 2 * sumIx, secIx, -bals.get(2 + 2 * sumIx, secIx) + (mtEC10.get(sumIx, secIx) + mGrowthGoal * rawEGC.get(sumIx, secIx)));
          goalmtgNeeds.set(2 + 2 * sumIx, secIx, na > nb ? na > nc ? na : nc : nb > nc ? nb : nc);
          goalmtg1Needs.set(2 + 2 * sumIx, secIx, nd > nb ? nd > nc ? nd : nc : nb > nc ? nb : nc);
          nd = -bals.get(3 + 2 * sumIx, secIx);
          goalmtg1Needs.set(3 + 2 * sumIx, secIx, -bals.get(3 + 2 * sumIx, secIx) - (mGrowthGoal * gMult * rawG.get(3 + 2 * sumIx, secIx)));
          mtgGNeeds.set(3 + 2 * sumIx, secIx, na = -bals.get(3 + 2 * sumIx, secIx) - (gYears * mGrowthGoal * gMult * rawG.get(3 + 2 * sumIx, secIx)));
          goalmtgNeeds.set(3 + 2 * sumIx, secIx, na > nb ? na > nc ? na : nc : nb > nc ? nb : nc);
          na = -bals.get(2 + 2 * sumIx, secIx) + gYears * (mtEC10.get(sumIx, secIx));
          goalmtNeeds.set(2 + 2 * sumIx, secIx, na > nb ? na > nc ? na : nc : nb > nc ? nb : nc);
          na = -bals.get(3 + 2 * sumIx, secIx);
          goalmtNeeds.set(3 + 2 * sumIx, secIx, na > nb ? na > nc ? na : nc : nb > nc ? nb : nc);
        }
      }
      if (ec.getAge() > 1) { // late in getNeeds
        assert bals.getRow(ABalRows.GROWTHSEFFIX).get(0) > 0.0 : " bals.getRow(ABalRows.GROWTHSEFFIX).get(0) <= 0.0=" + EM.mf(bals.getRow(ABalRows.GROWTHSEFFIX).get(0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }
      A6Row goalGG = new A6Row(ec, alev, "goalGG").setAmultV(rawGrowths, mGrowthGoal);
      bals.set2(ABalRows.FERTILITYGROWTHCOSTSIX, growthNegs);
      bals.set2(ABalRows.FERTILITYGROWTHCOSTSIX, growthNegs);
      // A10Row mmtRemnants = new A10Row(ec,alev2, "mmtRemnants");
      //  A2Row mmtgFertilities = new A2Row(ec,alev2, "mmtgFert"); // before min with reqFertility
      // mtggCosts sum of (maint,travel,growth costs)*phe*growYrs
      // - growth*growMult*growYears
      ec.aPre = aPre = "#d";
      if (alev <= bLev) {
        mtEC10.sendHist(hist, blev, aPre, alev, "mtEC");
        rawEGC.sendHist(blev, aPre, alev, "rawEGC");
        pRemMT.sendHist(blev, aPre, alev, "pRemMT");
        mtgFrac.sendHist(alev, aPre);
        //  minFracs.sendHist(alev, aPre);
        //  minLFrac.sendHist(alev, aPre);
        rawGrowths.sendHist(bLev, aPre, alev, "rawGrowths");
        growths.sendHist(alev, aPre);
        growthNegs.sendHist(alev, aPre);
        mtgNegs.sendHist(alev, aPre);
        rqNeedGG.sendHist(alev, aPre);
        rqNeedGM.sendHist(alev, aPre);
        goalmtgNeeds.sendHist(alev, aPre);
        goalmtNeeds.sendHist(alev, aPre);
        // goalNeeds.sendHist(alev, aPre);
        // rawProspects.sendHist(alev,aPre);
      }
      //  bLev = aDl;
      lev = alev = History.valuesMajor6;
      //    lev = alev = 5;

      hist.add(new History(aPre, History.valuesMajor6, " phe=" + EM.mf(poorHealthEffect), "gy=" + EM.mf(gYears), "gm=" + EM.mf(growMult), "maintGoal=", EM.mf(maintGoal), "mGrowthGoal=", EM.mf(mGrowthGoal), "<<<<<<<<<<"));
      if (ec.getAge() > 1) { // late in getNeeds
        assert bals.getRow(ABalRows.GROWTHSEFFIX).get(0) > 0.0 : " bals.getRow(ABalRows.GROWTHSEFFIX).get(0) <= 0.0=" + EM.mf(bals.getRow(ABalRows.GROWTHSEFFIX).get(0)) + " Y" + EM.year + " name=" + ec.name + " EM.curEconName=" + EM.curEconName + " age" + ec.getAge();
      }
      growths.sendHist(hist, aPre);
      growthNegs.sendHist(hist, bLev, aPre, alev, "growthNegs");
      //   rawFRem.sendHist01(bLev, aPre, lev, " rc rawFRem", " sg rawFRem");
      //  rawF.sendHist(hist, bLev, aPre, lev, " rc rawF", " sg rawF");
      mtgNegs.sendHist(hist, bLev, aPre, lev, "mtgNegs");

      // mtgNegs.sendHist(alev, aPre);
      //   j6Remnants.titl = "j6Remnants";
      // now set the result = needs,  needed>0, available = -this;
      alev = History.loopMinorConditionals5;
      rtn.blev = aDl;   // set this blev
      aPre = "secIx#";
      ARow resil = new ARow(ec);
      for (int resilIx = 0; resilIx < E.LSECS; resilIx++) {
        resil.set(resilIx, rawProspects2.get(0, resilIx) * rawProspects2.get(1, resilIx));
      }
      resilience = resil.max() / resil.ave();
      hope = resilience * phe;
      lev = alev;  // set this level
      if (aDl > 3 || true) {
        hist.add(new History(History.valuesMinor7, "xit getNeeds", "aDl=" + aDl,
                             "mainGoal=", EM.mf(maintGoal), "growthGoal=", EM.mf(growthGoal), "growMult", EM.mf(growMult), "growYears", EM.mf(growYears)));

        hist.add(new History(aPre, History.loopMinorConditionals5, " health=" + EM.mf(rawProspects2.min()), "phe=" + EM.mf(poorHealthEffect), "F=" + EM.mf(rawFertilities2.min()), "sumB=" + EM.mf(rtn.curSum()), "sumR=" + EM.mf(rtn.getRow(2).sum()), "sumC=" + EM.mf(rtn.getRow(3).sum()), "sumS=" + EM.mf(rtn.getRow(4).sum()), "sumG=" + EM.mf(rtn.getRow(5).sum()), "<<<<"));
        //  rawHealths.sendHist(hist, bLev, aPre, alev, "rc rawHealths", "sg rawHealths");
        rawProspects2.sendHist(hist, bLev, aPre, alev, "rrawProspects2", "srawProspects2");
        rawFertilities2.sendHist(hist, bLev, aPre, alev, "r rawFertilities2", "s rawFertilities2");
        mtggNeeds6.sendHist2(History.valuesMajor6, aPre);
        bals.listBalances(aDl, aPre, alev);
        rtn.sendHist(History.dl, "x#", History.valuesMajor6, "needs");
      }
      return rtn;

    }  // xit getNeeds

    int swapXtra = 0;

    /**
     * utility tool to generate name with resource type and index
     *
     * @param aname first part of name
     * @param pNq the array of resource type
     * @param aa the index into A2Row
     * @param d the value to be displayed
     * @return aname + pNq[si] + ix = value
     */
    String nameXnIx(String aname, String[] pNq, int aa, double d) {
      int ix = aa % E.lsecs;
      int si = (int) (aa / E.lsecs);
      String ret1 = pNq[si];
      return aname + ret1 + ix + "=" + EM.mf(d);
    }

    /**
     * utility tool to generate name with resource type and index
     *
     * @param aname first part of name
     * @param pNq the array of resource type
     * @param a1 major index int pNq
     * @param a2 index into lsecs of the value
     * @return aname + pNq[si] + ix
     * @param d the value to be displayed
     * @return aname + pNq[si] + ix = value
     */
    String nameXnIx(String aname, String[] pNq, int a1, int a2, double d) {
      int ix = a2;
      int si = a1;
      String ret1 = pNq[si];
      return aname + ret1 + ix + "=" + EM.mf(d);
    }

    /**
     * set swap title prefix
     *
     * @param titl
     * @return prefixed title
     */
    String nTitle(String titl
    ) {
      return n + (swapXtra > 0 ? ":" + swapXtra + ":" : " ") + titl;
    }

    class HSwaps {  // Assets.CashFlow.HSwaps

      // swap requests and activities
      SwpCmd cmd;
      // set values to illegal to catch unset values
      int srcIx, destIx, ixWRSrc, ixFor, forIx, sourceIx;
      int nSource, nDest, swapType = 10, n, rChrgIx, sChrgIx;
      int reDo, unDo = 0, rt;
      String resTypeName = "anot";
      double yearsFutureFund = 0;
      int yearsFutureFundTimes = 0;
      double rsval = 0;
      A10Row doNot;

      // balances
      ABalRows hbals;
      // needs and requirements
      A2Row rawProspects2 = new A2Row(ec);
      A2Row rawFertilities2 = new A2Row(ec);
      A6Row healths;
      A6Row mtgNeeds6 = new A6Row(ec), mtgAvails6 = new A6Row(ec);
      A2Row fertilities;
      Assets as;

      int[] stopped;
      boolean doingTrade, swapped;
      double health, fertility, sumTotWorth, totNeeds, scost, rcost;

      ;
      HSwaps() {
      }

      String df(double v) {
        return EM.mf(v);
      }

      /**
       * copy swap values from CashFlow to HSwaps
       *
       * @param ay a reference to the current CashFlow
       * @return the updated HSwaps object
       */
      HSwaps copyn(CashFlow ay) {
        cmd = ay.cmd;
        this.as = ay.as;
        this.n = as.n - 1;
        this.resTypeName = as.resTypeName;
        this.yearsFutureFund = as.yearsFutureFund;
        this.yearsFutureFundTimes = as.yearsFutureFundTimes;
        this.rsval = as.rsval;
        hbals = new ABalRows(ec, BALSLENGTH, TBAL, 7, "bals").copyValues(as.bals);
        // rawProspects2 = as.rawProspects2.copy();
        rawFertilities2 = as.rawFertilities2.copy();
        rawProspects2.copyValues(as.rawProspects2);
        mtgNeeds6.copyValues(as.mtgNeeds6);
        mtgAvails6.copyValues(as.mtgAvails6);
        totNeeds = ay.totNeeds;
        srcIx = ay.srcIx;
        sourceIx = ay.sourceIx;
        destIx = ay.destIx;
        ixWRSrc = as.ixWRSrc;
        ixFor = ay.ixWRFor;
        sChrgIx = ay.sChrgIx;
        rChrgIx = ay.rChrgIx;
        scost = ay.scost;
        rcost = ay.rcost;
        forIx = ay.forIx;;
        nSource = ay.source.sIx;
        nDest = ay.dest.sIx;
        healths = ay.healths;
        fertilities = ay.fertilities;
        health = as.health;
        fertility = ay.fertility;
        doingTrade = ay.source != null && ay.dest != null && ay.source.as1 != ay.dest.as1;
        swapped = ay.swapped;
        swapType = ay.swapType;
        sumTotWorth = as.sumTotWorth;
        stopped = ay.stopped;
        doNot = ay.doNot.copy10();
        reDo = ay.reDo;
        unDo = ay.unDo;
        return this;
      } // copyn

      /**
       * copy request type swap values from CashFlow to HSwaps
       *
       * @param ay a reference to the current CashFlow
       * @return the updated HSwaps object
       */
      HSwaps copyReq(CashFlow ay) {
        cmd = ay.cmd;
        this.as = ay.as;
        this.n = as.n - 1;
        hbals = new ABalRows(ec, BALSLENGTH, TBAL, 7, "bals").copyValues(as.bals);
        rawProspects2 = as.rawProspects2.copy();
        srcIx = ay.srcIx;
        destIx = ay.destIx;
        ixWRSrc = as.ixWRSrc;
        ixFor = ay.ixWRFor;
        sChrgIx = ay.sChrgIx;
        rChrgIx = ay.rChrgIx;
        scost = ay.scost;
        rcost = ay.rcost;
        forIx = ay.forIx;;
        nSource = ay.source.sIx;
        nDest = ay.dest.sIx;
        healths = ay.healths;
        doingTrade = ay.source != null && ay.dest != null && ay.source.as1 != ay.dest.as1;
        swapped = ay.swapped;
        swapType = ay.swapType;
        sumTotWorth = as.sumTotWorth;
        stopped = ay.stopped;
        doNot = ay.doNot.copy10();
        reDo = ay.reDo;
        unDo = ay.unDo;
        return this;
      } // copyReq

      /**
       * copy need type swap values from CashFlow to HSwaps
       *
       * @param ay a reference to the current CashFlow
       * @return the updated HSwaps object
       */
      HSwaps copyNeeds(CashFlow ay) {
        cmd = ay.cmd;
        this.as = ay.as;
        this.n = as.n - 1;
        rawProspects2 = as.rawProspects2.copy();
        rawFertilities2 = as.rawFertilities2.copy();
        mtgNeeds6.copyValues(as.mtgNeeds6);
        mtgAvails6.copyValues(as.mtgAvails6);
        totNeeds = ay.totNeeds;
        healths = ay.healths;
        fertilities = ay.fertilities;
        health = as.health;
        fertility = ay.fertility;
        doingTrade = ay.source != null && ay.dest != null && ay.source.as1 != ay.dest.as1;
        swapped = ay.swapped;
        sumTotWorth = as.sumTotWorth;
        stopped = ay.stopped;
        doNot = ay.doNot.copy10();
        reDo = ay.reDo;
        unDo = ay.unDo;
        return this;
      } // copyNeeds

      /**
       * call from the current prevns to redo the swap restore the good values
       * to go into yCalcCosts to redo the swap however, prevent repeating the
       * same swap, let n increase and redo increase redo should not increase
       * beyond 3
       *
       * @param ay current Assets.CashFlow, ay.as points to Assets
       * @param good the good HSwaps to restore to cur
       *
       */
      void restoreUpdate(CashFlow ay, HSwaps good) {
        ay.as.bals.copyValues(good.hbals); // do not change references to balances
        //    as.balances = as.bals.getBalances(as.balances.lev,"balances");
        ay.source = ay.sys[good.nSource];
        // remove the bad setStat Freedom Fund change if resTypeName is other than anot
        // and swapType == 3
        if (resTypeName != "anot" && this.swapType == 3) {
          setStat(resTypeName, pors, clan, -as.rsval, -1);
          setStat(resTypeName.contains("Emerg") ? "EmergFF" : "SizeFF", pors, clan, -as.rsval, -1);
          setStat("Redo FutureFund", pors, clan, -as.rsval, -1);
        }
        as.resTypeName = good.resTypeName;
        as.rsval = good.rsval;
        as.yearsFutureFund = good.yearsFutureFund;
        as.yearsFutureFundTimes = good.yearsFutureFundTimes;
        ay.dest = ay.sys[good.nDest];
        ay.srcIx = good.srcIx;
        ay.destIx = good.destIx;
        ay.as.ixWRSrc = good.ixWRSrc;
        ay.ixWRFor = good.ixFor;
        // as.mtgNeeds6.copyValues(good.mtgNeeds6);
        //   as.mtgAvails6.copyValues(good.mtgAvails6);
        ay.forIx = good.forIx;
        ay.sChrgIx = good.sChrgIx;
        ay.rChrgIx = good.rChrgIx;
        ay.scost = good.scost;
        ay.rcost = good.rcost;
        if (this.swapType == 1) { //DECR
          good.doNot.setDoNot(1, this.ixWRSrc, this.srcIx, doNotDays5); //avoid srcIx sect0r 5 times
          good.stopped[1] = 2;  // no DECR 1 times (2 .age())
        }
        else if (this.swapType == 0) { // incr
          good.doNot.setDoNot(0, this.ixWRSrc, this.srcIx, doNotDays2);  //no incr from sector for 2 times
        }
        else if (this.swapType == 2) { // XDECR
          good.doNot.setDoNot(2, this.ixWRSrc, this.srcIx, doNotDays2);  // no xdecr from this sector 2 times
        }
        ay.swapType = good.swapType;
        ay.swapped = good.swapped;
        ay.stopped = good.stopped;
        ay.doNot = good.doNot;
        ay.reDo = good.reDo += 1;
        if (good.reDo < 2) {
          ay.unDo = good.unDo += 1; // only at first redo
        }
        as.sumTotWorth = ay.rawCWTotWorth = good.sumTotWorth;
        mtgAvails6 = as.mtgAvails6.copy6(0, History.valuesMinor7, "mtgAvails6");
        // as.n = this.n;

      }

      /**
       * Find if the last swap increased value of the Econ if swapType &lt; 0,
       * swap failed rt=10; if prospects sum increased rt = 1 if prospects
       * negSum decreased rt = 2 if prospects min inceased rt = 3 if worth
       * increased rt = 4 if need decreased rt = 5 if future fund rt= 6 ,
       * otherwise leave 0 failed to increase if rt %gt; 0 then result is better
       * and no redo
       *
       * @return rt if swapType &lt; 0, swap failed rt=10; if prospects sum
       * increased rt = 1 if prospects negSum decreased rt = 2 if prospects min
       * inceased rt = 3 if worth increased rt = 4 if need decreased rt = 5 if
       * future fund rt=6 otherwise leave 0 failed to increase
       */
      int betterResult(HSwaps prev) {
        double t1 = 0., t2 = 0., t3 = 0., t4 = 0.;
        rt = 0;
        if (swapType < 0) {
          rt = -10;
        }
        else if (swapType == 3) {
          rt = 6;
        }// future fund
        else if (prev == this) {
          rt = -11;
        }
        else if (rawProspects2.curSum() > prev.rawProspects2.curSum() && rawProspects2.curMin() > .1) {
          rt = 1;
        }
        else if (rawProspects2.negSum() < prev.rawProspects2.negSum() && rawProspects2.curMin() > .1) {
          rt = 2;
        }
        else if (rawProspects2.min() < prev.rawProspects2.min()) {
          rt = -1;
        }
        else if (rawProspects2.min() > prev.rawProspects2.min()) {
          rt = 3;
        }
        else if (sumTotWorth > prev.sumTotWorth) {
          rt = 4;
        }
        else if (mtgNeeds6.curSum() < prev.mtgNeeds6.curSum()) {
          rt = 5;
        }
        if (n > 1 && (srcIx < 0 || srcIx > 6)) {
          EM.doMyErr("net srcIx=" + srcIx + " n=" + n);
        }
        if (rt > -20) {
          prevns[1].listRes("&g", 5);
          prev.listRes("&sumIx", 5);
          this.listRes("&n", 4);
        }
        if (n > 1 && (srcIx < 0 || srcIx > 6)) {
          EM.doMyErr("illegal srcIx=" + srcIx + " n=" + n);
        }
        return rt;
      }

      /**
       * List a HSwap entry
       *
       * @param pre prefix
       */
      void listRes(String pre, int level) {
        int xt;
        hist.add(new History(pre, level, n + "=" + reDo + "B" + this.rt + "st=" + swapType + " " + E.rNsIx(ixWRSrc, srcIx) + "->" + E.rNsIx(ixWRSrc, destIx),
                             "mov=" + EM.mf(mov),
                             "src" + E.rNsIx(ixWRSrc, srcIx) + "=" + EM.mf(balances.get(ixWRSrc, srcIx)),
                             "p" + E.rNsIx(ixWRSrc, srcIx) + "=" + EM.mf(rawProspects2.get(ixWRSrc, srcIx)),
                             "dst" + E.rNsIx(ixWRSrc, destIx) + "=" + EM.mf(balances.get(ixWRSrc, destIx)),
                             "p" + E.rNsIx(ixWRSrc, destIx) + "=" + EM.mf(rawProspects2.get(ixWRSrc, destIx)),
                             "M" + rNs[(int) (xt = rawProspects2.curMinIx()) / E.LSECS] + (int) xt % E.LSECS + "=" + EM.mf(rawProspects2.curMin()),
                             "Ps" + EM.mf(rawProspects2.curSum()),
                             "-" + EM.mf(rawProspects2.curNegSum()),
                             // "B=" + EM.mf(bals.curSum()),
                             "r$" + EM.mf(rcost),
                             "s$" + EM.mf(scost),
                             "<<<<<<<"));
      }

    } //HSwaps

    // strings arrays for nameXnIx
    /*
     void swapHist(ARow A, String titl) {
     hist.add(new History("swp", E.debuggingInformation, n + (swapXtra > 0 ? ":" + swapXtra : "") + titl, A));
     }

     void swapHist(A2Row A, String titl0, String titl1) {
     A.sendHist(hist, E.debuggingInformation, "swp", n + (swapXtra > 0 ? ":" + swapXtra : "") + titl0, n + (swapXtra > 0 ? ":" + swapXtra : "") + titl1);
     }

     void swapHist4(A6Row A, String titl2, String titl3, String titl4, String titl5) {
     A.sendHist4(hist, E.debuggingInformation, "swp", n + (swapXtra > 0 ? ":" + swapXtra : "") + titl2, n + (swapXtra > 0 ? ":" + swapXtra : "") + titl3, n + (swapXtra > 0 ? ":" + swapXtra : "") + titl4, n + (swapXtra > 0 ? ":" + swapXtra : "") + titl5);
     }

     void swapHist2(A6Row A, String titl0, String titl1) {
     A.sendHist2(hist, E.debuggingInformation, "swp", E.debuggingInformation, n + (swapXtra > 0 ? ":" + swapXtra : "") + titl0, n + (swapXtra > 0 ? ":" + swapXtra : "") + titl1);
     }
     */
    int aaab = 0, aaaa = 0, aaac = 0, aaad = 0, aaae = 0, aaaf = 0, aaag = 0, aaah = 0, aaai = 0, aaaj = 0;
    int aaaba = 0;

    /**
     * swap resources and staff
     * <p>
     * Phase SWAPPING<br>
     * Goal: get health above 0 at any cost, starting with highest strategic
     * value:<br>
     * <ol start=0><li> move reserves to working
     * <li> try moving max working to reserve, to lower other costs
     * <ol><li> only if at least 2 rawHealths below 0
     * <li>move upto .5 + Assets.clanRisk*.5 of max available, &lt .4 +
     * Assets.clanRisk*.6 of partner sum to reserve.
     * <li>use upto 5 least strategic value staff or resource (high balances)
     * </ol>
     * <li> if a staff rawHealths &lt 0, move staff from higher available
     * guest/staff
     * <ul><li>minimum cost
     * <li>redo with smaller move if this leaves another staff rawHealths below
     * the first
     * <li>undo and try another option if 2 redo's do not succeed
     * </ul>
     * <li>if a resource rawHealths &lt 0, move resources from a higher
     * available resource
     * <ul><li>if now another resource/staff is &lt the first, redo with a
     * smaller move
     * <li>if 2 redo's fail continue, than go to next &lt 0
     * <li> terminate done if all rawHealths &gt .0001
     * </ul>
     * <p>
     * phase GROW: Goal get high growth, grow staff where resource rawHealths
     * &lt 0.
     * <ol><li>for top 7 high Strat value if reserve exists move reserve to
     * working set donot decrease this sector for 5 swaps.
     * * <ol><li> only if at least 2 rawHealths below 0
     * <li>move upto Assets.clanRisk*.8 of max available, &lt Assets.clanRisk*.6
     * of partner sum to reserve.
     * <li>if move decreases the sum2 of rawFertilities or decreases the sum2 of
     * rawHealths undo it and donot do it for 5 swap iterations
     * <li>no more than 5 least strategic value staff or resource
     * <li> if 2 successive swap decreases do not increase rawFertilities.sum2,
     * stop decreases for 5 swaps.
     * </ol>
     * <li>for High Strat Resource rawHealths &lt 0 and associated staff &lt
     * 5*resource and no reserve move upto Assets.clanRisk*.6 of max available,
     * and &lt Assets.clanRisk*.5 staffGuestPartners from low Strat partner sums
     * <li>for High Strat r or s and rawHealths &lt .1 move from a lower strat
     * corresponding r or s upto Assets.clanRisk*.5 of available and &lt
     * Assets.clanRisk*.5 of partner sum with penalty.
     * <li>when moving between sectors if staff*Assets.gameStaffMoveCostFactor
     * &gt resource then charge staff the move high cost.
     * <li>if moving between sectors does not increase the sum2 of
     * rawFertilities than undo and donot try this sector again for 5 swap
     * iterations .
     * </ol>
     *
     * between active and reserve then if needed exchange resources and staff
     * between financial sectors the cost of exchanges is high, and is done only
     * if no swap between active and reserve works
     * [CRes,CStaf][TR,TX,TT][iW,iR][oW,oR][Plan,Ship] double [][][][][]
     * swapcosts = {swapRrxtcost,swapSrxtcost}; int
     * resource=0,staff=1,top=0,middle=1,bottom=2,none=3;
     */
    boolean swaps(String aPre, double travelyears
    ) {
      final double nFlag = -99.;
      int sr = 0, ss = 1, ps = pors;
      double t1 = 0., t2 = 0., gradeCost3 = 1., nmov = 1, gmov1 = 1., gmov = 1.;
      try {
        // save sourceIx
        if (n > 0 && (ixWRSrc < 0 || ixWRSrc > 1)) {
          EM.doMyErr("ixWRSrc=" + ixWRSrc + " n=" + n);
        }
        if (n > 0 && (srcIx < 0 || srcIx > 6)) {
          EM.doMyErr("srcIx=" + srcIx + " n=" + n);
        }
        int savIxSrc = sourceIx = ixWRSrc * E.LSECS + srcIx;

        prevprevns = prevns;  // all references good for later
        if (n > 0) { // move prevns up only if last round did something.
          EM.wasHere = "CashFlow.swaps at entry n>0 cnt=" + ++aaaa + " n=" + n;
          for (m = prevns.length - 1; m > 1; m--) {
            if (prevns[m - 2] != null) {
              prevns[m] = prevns[m - 2];
            }
          }
          prevns[0] = new HSwaps();
        }
        if (eM.dfe()) {
          return false;
        }
        EM.wasHere = "CashFlow.swaps after new prevns[o] aaab=" + ++aaab + " n=" + n;
        prevns[1].copyn(cur);
        lTitle = " Costs " + name;
        // add another prevns except if n==0, then no swap for results

        histTitles(lTitle);
        yCalcCosts("C#", lightYearsTraveled, curGrowGoal, curMaintGoal);  //includes yinitN
        EM.wasHere = "CashFlow.endYear.swaps after yCalcCosts before test savIxSrc too large";
        if (n > 0 && (savIxSrc >= E.L2SECS || savIxSrc < 0)) {
          throw new MyErr(String.format("savIxSrc " + savIxSrc + " would make ixWRSrc more than 0 or 1"));
        }
        ixWRSrc = (int) (savIxSrc / E.LSECS) % 2;   // restore to 0,1
        if (n > 1 && (ixWRSrc < 0 || ixWRSrc > 1)) {
          throw new MyErr(String.format("ixWRSrc bad=" + ixWRSrc + " n=" + n));
        }
        if (n > 1 && (srcIx < 0 || srcIx > 6)) {
          throw new MyErr(String.format("srcIx bad=" + srcIx + " n=" + n));
        }
        srcIx = savIxSrc % E.LSECS;
        lTitle = " Swaps " + name;
        prevns[0].copyn(cur);
        histTitles(lTitle);
        int xt, maxReDo = 4, bres = n > 2 ? 8 : 0;
        if (n > 2) { // n0 not yet, n1 first try set prevgood, n2 second set prevns,
          bres = prevns[0].betterResult(prevgood[0]);
        }
        EM.wasHere = "CashFlow.endYear after resetting ixWRSrc,srcIx aaac=" + ++aaac + "  n=" + n;

        if (eM.dfe()) {
          return false;
        }
        // exit only if goals are met and rawProspect (worst balance problem > 0
        // bail out if bad bres, quit if you can't fix it
        if ((n > eM.maxn[pors] * .5 && rawProspects2.curMin() > eM.minProspects[0]) || (rawFertilities2.curMin() > curGrowGoal && rawProspects2.curMin() > curMaintGoal && rawProspects2.curMin() > eM.mtgWEmergency[pors][clan]) || (n > 2 && bres > 0 && reDo >= maxReDo && rawProspects2.curMin() < E.NZERO)) {
          done = true;  // terminate looping
          hist.add(new History("GR", History.loopIncrements3, nTitle("TERM ") + cmd.toString() + srcIx + "->" + destIx,
                               "mov=" + EM.mf(mov),
                               "src=" + (srcIx < 0 || srcIx > E.LSECS ? "none" : EM.mf(balances.get(ixWRSrc, srcIx))),
                               "r$=" + (rChrgIx < 0 || rChrgIx > E.LSECS ? "none" : EM.mf(balances.get(ixWRSrc, rChrgIx))),
                               "s$=" + (sChrgIx < 0 || sChrgIx > E.LSECS ? "none" : EM.mf(balances.get(ixWRSrc, sChrgIx))),
                               "dst=" + (destIx < 0 || destIx > E.LSECS ? "none" : EM.mf(balances.get(ixWRSrc, destIx))),
                               "Hl" + rawProspects2.curMinIx() + "=" + EM.mf(rawProspects2.curMin()),
                               "HlB" + rawProspects2.curMinIx() + "=" + EM.mf(rawProspects2.get(rawProspects2.curMinIx())), "Ha" + "=" + EM.mf(rawProspects2.ave()),
                               "mtgC=" + EM.mf(mtgCosts10.curSum()),
                               "bals=" + EM.mf(bals.curSum()), "<<<<<<<"));
          EM.wasHere = " CashFlow.swaps just before return if done n=" + n;
          eM.printHere("---SDa---", ec, EM.wasHere);
          return swapped = false;  // terminate looping success
        } // exit if we have satisfied END health

        // continuing with swap., save for redo
        //get the previous swap values and the Cost values for that swap
        balances.checkBalances(cur);
        double dstCst = 0;
        double dstRCst = 0, dstSCst = 0;
        int rlev = History.loopMinorConditionals5;

        if (n > 2 && prevns[0] == prevgood[0]) { // no test until results for n=1
          // at n=2, prevns[1] are n=0 swap results, prevns[0] n=1 swap results
          EM.doMyErr("prevens[0] match prevgood[0] but should not n=" + n + " prevgood[0].n=" + prevgood[0].n + " prevns[0].n=" + prevns[0].n);
        }
        if (n > 1 && (srcIx < 0 || srcIx > 6)) {
          EM.doMyErr("srcIx=" + srcIx + " n=" + n);
        }
        if (n > 1 && (ixWRSrc < 0 || ixWRSrc > 1)) {
          EM.doMyErr("ixWRSrc=" + ixWRSrc + " n=" + n);
        }
        if (n > 2) { // n0 not yet, n1 first try set prevgood, n2 second set prevns,
          //   bres = prevns[0].betterResult(prevgood[0]);
        }
        if (n > 1 && (ixWRSrc < 0 || ixWRSrc > 1)) {
          EM.doMyErr("ixWRSrc=" + ixWRSrc + " n=" + n);
        }
        if (n > 1 && (srcIx < 0 || srcIx > 6)) {
          EM.doMyErr("srcIx=" + srcIx + " n=" + n);
        }
        if (n > 1 && (destIx < 0 || destIx > 6)) {
          EM.doMyErr("destIx=" + destIx + " n=" + n);
        }
        if (n > 1) {
          hist.add(new History("##", History.loopMinorConditionals5, n + " " + cmd + " " + E.rNsIx(ixWRSrc, srcIx), "m" + EM.mf(mov), "src" + E.rNsIx(ixWRSrc, srcIx) + EM.mf(bals.get(ixWRSrc, srcIx)), "d" + E.rNsIx(ixWRSrc, destIx) + "=" + EM.mf(bals.get(ixWRSrc, destIx)), "bres=" + bres, "reDo=" + reDo, "st=" + "<<<<<<<<<<"));
        }
        if (n > 1 && (ixWRSrc < 0 || ixWRSrc > 1)) {
          EM.doMyErr("ixWRSrc=" + ixWRSrc + " n=" + n);
        }
        if (n > 3 && n % 5 == 4) {
          eM.printHere("---SDb---", ec, "swapping" + n + " betterResult" + bres + " reDo" + reDo);
        }
        // if result not better restore the old balances and recalc
        if (n > 2 && bres < 1 && reDo < maxReDo) { // redo bad swaps up to 4 times, than accept
          // prevns = prevprevns; // restore the prevns
          balances.checkBalances(this);
          hist.add(new History("##", History.loopMinorConditionals5, n + "OLD " + cmd + " " + E.rNsIx(ixWRSrc, srcIx), "m" + EM.mf(mov), "src" + E.rNsIx(ixWRSrc, srcIx) + EM.mf(bals.get(ixWRSrc, srcIx)), "d" + E.rNsIx(ixWRSrc, destIx) + "=" + EM.mf(bals.get(ixWRSrc, destIx)), "bres=" + bres, "reDo=" + reDo, "st=" + "<<<<<<<<<<"));
          // input is the current bad, to the current good`
          prevns[0].restoreUpdate(cur, prevgood[0]);
          hist.add(new History("##", History.loopIncrements3, n + "RESTORD " + cmd + " " + E.rNsIx(ixWRSrc, srcIx), "m" + EM.mf(mov), "src" + E.rNsIx(ixWRSrc, srcIx) + EM.mf(bals.get(ixWRSrc, srcIx)), "d" + E.rNsIx(ixWRSrc, destIx) + "=" + EM.mf(bals.get(ixWRSrc, destIx)), "bres=" + bres, "reDo=" + reDo, "st=" + "<<<<<<<<<<"));
          balances.checkBalances(this);
          // now recalculate costs, may be slightly different than original
          yCalcCosts("X*", lightYearsTraveled, curGrowGoal, curMaintGoal);
          // ignore the result we are overwriting
//        prevns[0].copyn(cur);
        } // accept current swap as good, do the next one
        else {
// then continue the swap was good with the new setDoNot s
          reDo = 0;
          // doing not undo or redo, increased rawFertilities and or rawHealths
          // things got better
          doNot.age(); //age only once per n, reduce doNots
          // and reduce stopped back toward 0
          stopped[0] += stopped[0] > 0 ? -1 : stopped[0];
          stopped[1] += stopped[1] > 0 ? -1 : stopped[1];
          stopped[2] += stopped[2] > 0 ? -1 : stopped[2];
          //prevgood is the record of good swaps, conflicts iwth Trades.prevgood
          if (n > 0) { // move prevgood up.
            for (m = prevgood.length - 1; m > 0; m--) {
              if (prevgood[m - 1] != null) {
                prevgood[m] = prevgood[m - 1];
              }
            }
          }
        }
        if (eM.dfe()) {
          return false;
        }
        if (n > 0) {
          // save a new HSwaps from cur into prevgood[0]
          prevgood[0] = new HSwaps();
          prevgood[0] = prevgood[0].copyn(cur);
        }
        doNot.sendHist(History.loopMinorConditionals5, "g^");

        //    ARow[] bwp = {r.balanceWithPartner, s.balanceWithPartner};
        balances.checkBalances(this);
        A2Row swapNeeds = new A2Row(ec, 7, "swapNeeds");
        SubAsset[] sources = {resource, staff, cargo, guests};
        String[] srcNames = {" r ", " s "};
        //resource.balance.negError("resource.balance");
        double src0Bal = 0., src1Bal = 0., src01Bal = 0., destBal = 0.;
        int swap4Steps[] = {0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3};
// 0,1,2,3
        int swap7Steps[] = {0, 1, 2, 3, 4, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6};
        // loop through tests with decreasing requirements, to see if anything
        // can be swapped
        //for (swapLoops = 0; swapLoops < 7; swapLoops++) {
        for (swapLoops = 0; swapLoops < swapLoopMax; swapLoops++) {
          swap4Step = swap4Steps[swapLoops];
          swap7Step = swap7Steps[swapLoops];
          int swapLoop2 = swapLoops / 2;  // 0,0,1,1
          int swapLoop1 = swapLoops % 2;  // 0,1
          minRF = rawFertilities2.min();
          minRFIx = rawFertilities2.curMinIx();
          minRH = rawProspects2.min();
          minRHIx = rawProspects2.curMinIx();
          //double mSumRem = mtgSumRemnant;

          //see description at start of swaps
          boolean hNot1 = prevFlagm && prevFlagh;
          if (swapLoops == 0 && n > 1) {

            rawFertilities2.sendHist(hist, 20, "C", 4, "r rawF", "s rawF");
            prevns[0].rawFertilities2.sendHist(hist, 20, "C", 4, "r prvrawF", "s prvrawF");
            rawProspects2.sendHist(hist, 20, "C", 4, "r rawH", "s rawH");
            prevns[0].rawProspects2.sendHist(hist, 20, "C", 4, "r prvrawH", "s prvrawH");
            growths.sendHist2(20, "C", 4, "r G", "s G");
            //          prevns[0].growths.sendHist2(20, "C", 4, "r prvG", "s prvrawG");
            balances.sendHist2(20, "C", 4, "r bal", "s bal");
            //           prevns[0].balances.sendHist2(20, "C", 4, "r prvbal", "s prvbal");
            //           balances.set(prevns[0].balances);
            rawFertilities2.set(prevns[0].rawFertilities2);
            rawProspects2.set(prevns[0].rawProspects2);

            doNot.sendHist();
            //       doNot.sendHist4(hist, History.informationMinor9, "do", History.informationMinor9, "r dec doNot", "s dec doNot", "r xfr doNot", "s xfr doNot");
            //   prevSwapResults(aPre);

            if (History.dl > 40) {
              StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

              hist.add(new History(aPre, History.debuggingMinor11, ">>>>" + nTitle("swaps"), wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + EM.mf(sumTotWorth)));
            }
            errHistory = null;
          }

          //  }
          //     calcSwapValues(aPre, lightYearsTraveled);
          // do some calculations for a previous decr
          balances.checkBalances(this);
          //     swapType = 4;
          // index on swap4Step
          // A6Row[] avails = {mtgGoalNeeds, mtgGoalNeeds, mtgEmergNeeds, mtgNeeds6};
          //A6Row[] avails = {mtgNeeds6, mtgNeeds6, mtgNeeds6, mtgNeeds6};
          // A6Row[] needs = {mtggNeeds6, mtggNeeds6, mtgNeeds6, mtgNeeds6};
          A2Row stratV = new A2Row(ec).set(stratVars);
          A2Row aorder = new A2Row(ec);
          mtgAvails6.sendHist2(rlev, aPre, rlev, "rc avails", "sg avails");
          mtgNeeds6.sendHist2(rlev, aPre, rlev, "rc needs", "sg needs");
//        mtggEmergNeeds.sendHist2(rlev, aPre, rlev, "rc mEmergN", "sg mggEmergN");
          //  mtgNeeds6.sendHist2(rlev, aPre, rlev, "rc mtgN", "sg mtgN");
          rawProspects2.sendHist(hist, 29, aPre, rlev, "rc rawProspects2", "sg rawProspects2");

          //   mNeeds.addJointBalances();
          //balances.addJointBalances();
          double frac1 = 0., frac2 = 0., tmp1 = 0., sum1 = 0., sum2 = 0., dif1 = 0.;
          int minFx = rawFertilities2.curMinIx();
          int min1Fx = rawFertilities2.curMinIx(1);
          int minHx = rawProspects2.curMinIx();
          int min1Hx = rawProspects2.curMinIx(1);
          int minNx = mtgNeeds6.curMinIx();
          int min1Nx = mtgNeeds6.curMinIx(1);
          int maxNx = mtgNeeds6.curMaxIx(0);
          double maxNd = mtgNeeds6.curMax(0);
          int maxSx = stratVars.curMaxIx();
          double maxSd = stratVars.curMax();
          int max1Sx = stratVars.maxIx(1);
          int minHSx = stratVars.findMinIx(minHx);
          int min1HSx = stratVars.findMinIx(min1Hx);
          int minFSx = stratVars.findMinIx(minFx);
          int min1FSx = stratVars.findMinIx(min1Fx);
          double minFd = rawFertilities2.get(minFx);
          double min1Fd = rawFertilities2.get(min1Fx);
          double minHd = rawProspects2.get(minHx);
          double min1Hd = rawProspects2.get(min1Hx);
          double minNd = mtgNeeds6.curGet(minNx);
          double min1Nd = mtgNeeds6.curGet(min1Nx);
          double bav = balances.getRow(0).ave();
          double[] maxMove = {.45 * bav, .65 * bav, .75 * bav, .75 * bav};
          // double needAvailValue = -mtgNeeds6.curMax(needIx); // value available at needIx
          int incLeastStrategicIx[] = {6, 7, 8, 8};//most is most strategic
          // Index into the first 2 rows
          int stratIx = stratVars.maxIx(incLeastStrategicIx[swap4Step]);
          double leastStrategicValue = mtgNeeds6.curMax(stratIx);
          double theSum = bals.curSum();

          hist.add(new History("i@", History.loopMinorConditionals5, "loop data", nameXnIx("minH", rNs, minHx, minHd), nameXnIx("N", rNs, minHx, mtgNeeds6.curGet(minHx)), nameXnIx("b", rNs, minHx, balances.curGet(minHx)), nameXnIx("strat", rNs, minHSx, balances.curGet(minHSx)), nameXnIx("min1H", rNs, min1Hx, min1Hd), nameXnIx("N", rNs, min1Hx, mtgNeeds6.curGet(min1Hx)), nameXnIx("b", rNs, min1Hx, balances.curGet(min1Hx)), nameXnIx("strat", rNs, min1HSx, balances.curGet(min1HSx))));

          hist.add(new History("i@", History.loopMinorConditionals5, "loop data", nameXnIx("minF", rNs, minFx, minFd), nameXnIx("N", rNs, minFx, mtgNeeds6.curGet(minFx)), nameXnIx("b", rNs, minFx, balances.curGet(minFx)), nameXnIx("strat", rNs, minFSx, balances.curGet(minFSx)), nameXnIx("min1F", rNs, min1Fx, min1Fd), nameXnIx("N", rNs, min1Fx, mtgNeeds6.curGet(min1Fx)), nameXnIx("b", rNs, min1Fx, balances.curGet(min1Fx)), nameXnIx("strat", rNs, min1FSx, balances.curGet(min1FSx))));

          // now check for futureFund processing, loop if futureFund was given
          if (calcFutureFund()) {
            swapType = 3;
            destIx = srcIx;
            done = false;
            EM.wasHere = "CashFlow.endYear just after calcFutureFund aaaba=" + aaaba;
            return swapped = true;
          }
          /**
           * ****************** RINCR SINCR
           * *************************************
           */
          // swap   RINCR  SINCR  move reserve to working (cargo->resource,quests->staff
          // swaploops increase avails, increase needs
          // do charge for a swap after a trade, trade moved units all to working
          //  boolean ncharg = yphase == yrphase.GROW && n < 10 && didTrade > 0;
          // fraction of source balances to use in swap
          if (stopped[0] > 0) {  // incr stopped
            hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(cmd.name() + ">>stopped "), "mov" + EM.mf(mov), "dest" + EM.mf(bals.getRow(3 + 2 * ixWRSrc).get(srcIx)), "src" + EM.mf(bals.getRow(2 + 2 * ixWRSrc).get(srcIx))));
          }
          else {

            double bFrac = bals.curSum() * .33 / 14; // bFrac maxMove 1/3 of balances ave
            double iMaxMove = bFrac;
            // double incrBalFrac = 1.; // fraction of C or G sources to use in swap
            double incrBalFracs[] = {.9, .92, .94, .98, 1., 1., 1., 1., 1.};
            double incrBalFrac = incrBalFracs[swap4Step]; // fraction of R or S sources to use in swap
            double incrAvailFracs[] = {.85, .87, .89, .91, .93, 1., 1., 1., 1.};
            double incrAvailFrac = incrAvailFracs[swap4Step];  // fraction of C or G source to use in swap
            double incrResrvFrac = 1. - incrAvailFrac;
            // int ixAF = n < incrAvailFrac.length ? n : incrAvailFrac.length - 1;

            // decrease min as steps increase as n increases
            movMin = .005 * bFrac * 3 / (3. + swap4Step) * (2 * maxn / (2 * maxn + n));  // skip INCR if move < movMin
            // mult against incrAvail.sum()
            double[] incrMovMult = {.15, .27, .25, .27};;

            //Initialize variables possibly used in swaps
            ixWRFor = forIx = srcIx = destIx = -1;
            rchrg = schrg = osource = forRes = dest = source = null;
            rcost = scost = mov = fmov = smov = 0;

            // set to a general incr, specific is later
            cmd = SwpCmd.RSINCR;
            swapType = 0;
            aorder = rawFertilities2;
            if (rawProspects2.curMin() < .2) {
              aorder = rawProspects2;
            }
            needIx = aorder.curMinIx();  // greatest need
            ec.aPre = aPre = "H@";
            // [ixWRSrc][r/scost][pors]
            double swapCost[][][] = {{eM.swapCtoRRcost, eM.swapCtoRScost}, {eM.swapGtoSRcost, eM.swapGtoSRcost}};
            // calculate the max move from C to R or G to S for each position
            A2Row incrAvail0 = new A2Row(ec).setAvailableToIncr(incrBalFrac, incrAvailFrac, iMaxMove, mtgAvails6, swapCost[sr][sr][ps], swapCost[sr][ss][ps], swapCost[ss][sr][ps], swapCost[ss][ss][ps]);
            A2Row incrAvail1 = new A2Row(ec);
            incrAvail1 = doNot.filterByDoNot(0, incrAvail0, nFlag);
            // Eliminate positions recently changed
            // A2Row incrAvail1 = new A2Row(ec).set(incrAvail0);

            hist.add(new History(History.valuesMajor6, "Incr vals", "movMin=", EM.mf(movMin), "iMaxMov", EM.mf(iMaxMove), "iBalFrac", EM.mf(incrBalFrac), "iAvailFrac", EM.mf(incrAvailFrac)));
            incrAvail0.sendHist(hist, History.valuesMinor7, "inc", "r incrAvail0", "s incrAvail0");
            incrAvail1.sendHist(hist, History.valuesMinor7, "inc", "r incrAvail1", "s incrAvail1");
            bals.sendHist(hist, aPre);
            //  rawFertilities2.sendHist(hist, 21, "$a", "rcFertilities", "sgFertilities");
            ec.aPre = aPre = "I@";
            stratVars.sendHist(hist, History.valuesMinor7, aPre, nTitle("r stratVars"), nTitle("s stratVars"));
            rawProspects2.sendHist(hist, aPre);

            // A2Row aorder1 = doNot.filterByDoNot(0,aorder,999.);
            A2Row aStrat1 = doNot.filterByDoNot(0, stratVars, nFlag);
            // now find a need that increment can help
            needIx = -2;
            mov = 999. + movMin;  // preset very large
            int imax = incLeastStrategicIx[swap4Step];

            // find r move = j = srcIx  if move greater than movMin
            for (int i = 0; i < imax; i++) {
              j = aStrat1.curMaxIx(i); // start with lowest val, highest strategic value
              ixWRSrc = (int) j / LSECS;
              destIx = srcIx = (int) j % LSECS;
              // gradeCost3 is the max Avail mov
              gradeCost3 = incrAvail1.get(j);
              // nmov is the amount needed to meet all current needs
              nmov = bals.get(3 + ixWRSrc * 2, srcIx) * incrBalFrac; // c or g.balance(srcIx)
              mov3 = mtgAvails6.curGet(j); //rc or sg values
              mov2 = mov3 < PZERO ? movMin * 3. : mov3;
              // gmov is the amount needed to reach the goals
              gmov1 = goalmtg1Needs6.curGet(j);
              gmov = gmov1 < PZERO ? movMin * 3 : gmov1;

              // may exceed nmov if prospects are good over .1
              mov = rawProspects2.curGet(j) < eM.mtgWEmergency[pors][clan] ? Math.min(nmov, Math.min(mov2, gradeCost3)) : Math.min(nmov, Math.min(gradeCost3, Math.max(mov2, gmov)));
              if (mov > movMin) {  // only accept moves above the min
                break;
              }
            } // end for i or break

            source = sources[ixWRSrc + 2]; // c or g
            dest = source.partner; // r or s
            destIx = srcIx;
            rchrg = resource;
            schrg = staff;
            rcost = mov * swapCost[ixWRSrc][sr][pors];
            scost = mov * swapCost[ixWRSrc][ss][pors];
            // dstCst = mov + (ixWRSrc == 0?rcost:scost);
            dstCst = mov + mov * swapCost[ixWRSrc][ixWRSrc][pors];
            swapCosts.getRow(0).add(rcost);
            swapCosts.getRow(1).add(scost);
            prevsrc = source.balance.get(srcIx);
            prevdest = dest.balance.get(destIx);
            cmd = incrs[ixWRSrc][swap4Step]; //revise to detail cmd

            ec.aPre = aPre = "K@";

            if (mov < movMin) { //mov < movMin
              hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(">SKIP " + cmd.name() + " too small"), "mov" + EM.mf(mov), "movMin=", EM.mf(movMin))); //
              doNot.setDoNot(0, ixWRSrc, srcIx, 3); //disallow decrement
            }
            else if (mov < PZERO) { // neg move
              hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(cmd.name() + ">SKIP " + " -mov"), "mov" + EM.mf(mov), "dest" + EM.mf(bals.getRow(3 + 2 * ixWRSrc).get(srcIx)), "src" + EM.mf(bals.getRow(2 + 2 * ixWRSrc).get(srcIx)), "nmov" + EM.mf(nmov), "gmov" + EM.mf(gmov), "dest big enough")); //
            }
            else if (bals.getRow(3 + 2 * ixWRSrc).get(srcIx) < mov) {  // C too small
              hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(cmd.name() + ">>too large "), "mov" + EM.mf(mov), "dest" + EM.mf(bals.getRow(3 + 2 * ixWRSrc).get(srcIx)), "src" + EM.mf(bals.getRow(2 + 2 * ixWRSrc).get(srcIx)), "nmov" + EM.mf(nmov), "gmov" + EM.mf(gmov), "dest big enough"));

            }
            else {
              hist.add(new History(aPre = "L@", History.loopMinorConditionals5, nTitle(cmd.name()) + " " + i + "=>" + j + (j < E.lsecs ? "r" + j : "s" + (j - E.lsecs)), "bal" + EM.mf(bals.get(2 + 2 * ixWRSrc, srcIx)), "prt" + EM.mf(bals.get(3 + 2 * ixWRSrc, srcIx)), "mov" + EM.mf(mov), "min" + EM.mf(movMin), "golm" + EM.mf(gmov), "nedm" + EM.mf(nmov), "incm" + EM.mf(gradeCost3)));
              hist.add(new History(aPre, History.loopIncrements3, nTitle("Pre") + cmd.name() + srcIx + "->" + destIx, "mov=" + EM.mf(mov), "mMin" + EM.mf(movMin), "r$" + rChrgIx + "=" + EM.mf(rcost), "s$" + sChrgIx + "=" + EM.mf(scost), "H" + rawProspects2.curMinIx() + "=" + EM.mf(rawProspects2.curMin()), "HS" + rawProspects2.curSum(), "rS" + EM.mf(bals.getRow(0).sum()), "sS" + EM.mf(bals.getRow(1).sum()), "mtg" + EM.mf(mtgNeeds6.getRow(0).sum()), EM.mf(mtgNeeds6.getRow(1).sum()), "<<<<<<<"));
              // a pretest for problems with putValue and cost2
              if ((balances.get(ixWRSrc * 2 + 3, srcIx) - dstCst) < NZERO) {
                E.myTest(true, "incr " + srcIx + " cost too high, balance=" + EM.mf(balances.getRow(ixWRSrc).get(srcIx)) + " -cost " + EM.mf(dstCst) + " => " + EM.mf((balances.getRow(ixWRSrc).get(srcIx) - dstCst)));
              }
              if (ixWRSrc == 0) {
                if (bals.getRow(0).sum() > 0.0) {
                  setStat("swapRIncr", pors, clan, 100. * mov / bals.getRow(0).sum(), 1);
                }
              }
              else if (bals.getRow(1).sum() > 0.0) {
                setStat("swapSIncr", pors, clan, 100. * mov / bals.getRow(1).sum(), 1);
              }
              source.putValue(balances, mov, srcIx, destIx, dest, 0, 0.);
              rchrg.cost3(rcost, srcIx, .0001);
              schrg.checkGrades();
              schrg.cost3(scost, srcIx, .0001);
              setStat(EM.SWAPRINCRCOST, pors, clan, rcost / bals.getRow(2).sum(), 1);
              setStat(EM.SWAPSINCRCOST, pors, clan, scost / bals.getRow(4).sum(), 1);
              swapType = 0;
              rxfers = sxfers = 0;
              // allow repeat incr of min values
              // doNot.setDoNot(0, ixSrc, srcIx, E.doNotYears - n / 7);
              // do not decrement ever
              doNot.setDoNot(1, ixWRSrc, srcIx, 100);
              hist.add(new History(aPre, History.valuesMajor6, nTitle(" INCR ") + cmd.toString() + source.aschar + srcIx + "->" + source.partner.aschar + srcIx, "mov=" + EM.mf(mov), "src=" + EM.mf(balances.get(ixWRSrc, srcIx)), "r$" + rChrgIx + "=" + EM.mf(rcost), "s$" + sChrgIx + "=" + EM.mf(scost), "dst=" + EM.mf(balances.get(ixWRSrc, destIx)), "Hl" + rawProspects2.curMinIx() + "=" + EM.mf(rawProspects2.curMin()), "HlB" + rawProspects2.curMinIx() + "=" + EM.mf(rawProspects2.get(rawProspects2.curMinIx())), "Ha" + "=" + EM.mf(rawProspects2.ave()), "mtgC=" + EM.mf(mtgCosts10.curSum()), "bals=" + EM.mf(bals.curSum()), "<<<<<<<"));
              return swapped = true;
            }
          }// end of stopped else

          // look for null pointer errors
          String ccc = source.aschar;

          //      hist.add(new History(aPre, History.valuesMajor6, nTitle(" doNotINCR ")+source.aschar + srcIx,  prevns[0].cmd.name(), prevns[1].cmd.name(), prevns[2].cmd.name(), prevns[3].cmd.name(), prevns[4].cmd.name()));
          hist.add(new History(aPre, History.valuesMajor6, nTitle(" DidNot INCR ") + source.aschar + srcIx, "mov" + EM.mf(mov), "mMin" + EM.mf(movMin), prevns[0].cmd.name(), prevns[1].cmd.name(), prevns[2].cmd.name(), prevns[3].cmd.name(), prevns[4].cmd.name()));


          /*---------------------- RDECR SDECR ----------------------------*/
          // SDECR  RDECR decrease some S or R to reduce services request
          // use S or R with low strategic values to be swapped to G or C
          // don't decr for small needs, it is not cost effective
          // fraction of source -needs to use in swap
          // use neg because bal not need is expected
          double decrBalFrac[] = {.5, .5, .6, .7};
          // osst balances limits
          double[] decrResrvWRFrac = {.8, .9, .94, .95};
          aPre = "#g";
          // row 3 is cargo
          double dbav = balances.curSum() * E.invL2secs;
          double[] decrMovMin = {dbav * .0003, dbav * .00007, dbav * .000003, dbav * .000002,};
          // [ixWRSrc][r/scost][pors]
          double swapCostd[][][] = {{eM.swapRtoCRcost, eM.swapRtoCScost}, {eM.swapStoGRcost, eM.swapStoGScost}};
          //    double[] dSwapRcost = {E.swapRtoCRcost[pors], E.swapStoGRcost[pors]};
          //    double[] dSwapScost = {E.swapRtoCScost[pors], E.swapStoGScost[pors]};
          int dR = 0, dS = 1;
          //       mtgNeeds6.addJointBalances();
          //      balances.addJointBalances();
          double[] dmaxMove = {.95 * dbav, 1.4 * dbav, 1.8 * dbav, 2.3 * dbav};

          A2Row decrAvail1 = new A2Row(ec, History.loopMinorConditionals5, "decrAvail1").setAvailableToDecrement(decrBalFrac[swap4Step], decrResrvWRFrac[swap4Step], dmaxMove[swap4Step], mtgAvails6, swapCostd[sr][sr][ps], swapCostd[sr][ss][ps], swapCostd[ss][sr][ps], swapCostd[ss][ss][ps]);
          decrAvail1.sendHist(hist, History.informationMinor9, aPre = "f%", "r decrAvail1", "s decrAvail1");
          A2Row decrAvail = doNot.filterByDoNot(1, decrAvail1, nFlag);
          if (swapLoops < 20 && n < 20) { // try to list only 1? time per swap
            hist.add(new History(aPre, History.informationMajor8, nTitle("4mins,4maxs"), "dbav" + EM.mf(dbav), "swap4Step=" + swap4Step, EM.mf(decrMovMin[0]), EM.mf(decrMovMin[1]), EM.mf(decrMovMin[2]), EM.mf(decrMovMin[3]), EM.mf(dmaxMove[0]), EM.mf(dmaxMove[1]), EM.mf(dmaxMove[2]), EM.mf(dmaxMove[3])));
            stratVars.sendHist(History.loopMinorConditionals5, aPre);
            mtgNeeds6.sendHist(History.loopMinorConditionals5, aPre);
            decrAvail.sendHist(History.loopMinorConditionals5, aPre);
          }

          aorder = rawFertilities2;
          if (rawProspects2.curMin() < .2) {
            aorder = rawProspects2;
          }
          needIx = aorder.curMinIx();  // greatest need
          mov2 = mtgNeeds6.curGet(needIx);
          movMin = decrMovMin[swap4Step];
          forIx = needIx % E.lsecs;
          ixWRSrc = ixWRFor = needIx / E.lsecs;  // 0,1 find row of need
          srcIx = decrAvail.getRow(ixWRFor).maxIx();  // largest value in need row
          maxAvail = decrAvail.get(ixWRFor, srcIx);
          destIx = srcIx;
          maxAvail = Math.min(maxAvail, bals.get(ixWRSrc * 2 + 2, srcIx));
          mov1 = Math.max(Math.min(Math.min(bals.get(ixWRSrc * 2 + 2, srcIx), mov2), maxAvail), PZERO);
          mov = mov1 > PZERO && PZERO < maxAvail ? mov1 : maxAvail;
          EM.addlErr = "";
          // if incr just transfered from reserve to working reduce transfer prevent cycle
          cmd = decrs[ixWRSrc][swap4Step];
          if (swapLoops < 3) {
            hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(" skip" + swapLoops + " < 3 " + cmd.name()), "mov" + EM.mf(mov), "dest" + EM.mf(bals.getRow(3 + 2 * ixWRSrc).get(srcIx)), "src" + EM.mf(bals.getRow(2 + 2 * ixWRSrc).get(srcIx)), "nmov" + EM.mf(nmov), "gmov" + EM.mf(gmov), "dest big enough"));
          }
          else if (bals.get(2 + 2 * ixWRSrc, srcIx) < (bals.getRow(2 + 2 * ixWRSrc).ave() * 4)) {
            hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(" decr too small" + swapLoops + " " + cmd.name()), "mov" + EM.mf(mov), "dest" + EM.mf(bals.getRow(3 + 2 * ixWRSrc).get(srcIx)), "src" + EM.mf(bals.getRow(2 + 2 * ixWRSrc).get(srcIx)), "nmov" + EM.mf(nmov), "gmov" + EM.mf(gmov), "dest big enough"));
          }
          else if (stopped[1] > 0) {
            hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(" stop" + swapLoops + " " + cmd.name()), "mov" + EM.mf(mov), "dest" + EM.mf(bals.getRow(3 + 2 * ixWRSrc).get(srcIx)), "src" + EM.mf(bals.getRow(2 + 2 * ixWRSrc).get(srcIx)), "nmov" + EM.mf(nmov), "gmov" + EM.mf(gmov), "dest big enough"));
          }
          else if (mov < movMin) {
            hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(" skip") + swapLoops + " " + cmd.toString() + " for" + rNs[ixWRFor] + forIx, "mov=" + EM.mf(mov), "lt" + EM.mf(decrMovMin[swap4Step])));
          }
          else {

            // but no more than is available from forIx
            cmd = decrs[ixWRSrc][swap4Step];
            hist.add(new History(aPre, History.loopMinorConditionals5, nTitle("check xDecr"), "forIx=" + rNs[ixWRFor] + forIx, "destIx=" + rNs[ixWRSrc] + destIx, "mov" + EM.mf(mov), "m1=" + EM.mf(mov1), "m2=" + EM.mf(mov2), " max=" + EM.mf(maxAvail), "<<<<<<<<<<<"));
            //       hist.add(new History("dec", History.loopMinorConditionals5, nTitle(" prev swap1"), "prv0=" + prevns[0].swapType, "prv1=" + prevns[1].swapType, "prv2=" + prevns[2].swapType, "prv3=" + prevns[3].swapType, "prv4=" + prevns[4].swapType, "prv5=" + prevns[5].swapType, "dm=" + EM.mf(decrMostMaxGap), "abcdefghijklmnopqrst"));
            //      hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(" prev cmds="), "prev=", prevns[1].cmd.toString(), prevns[2].cmd.toString(), prevns[3].cmd.toString(), prevns[4].cmd.toString(), prevns[5].cmd.toString(), prevns[6].cmd.toString()));

            //        hist.add(new History("d@", History.loopMinorConditionals5, nTitle(" Strategic Value !!too low "), "max=", EM.mf(maxAvail), "m2=" + EM.mf(mov2), "mov=" + EM.mf(mov), "<<<<<<"));
            chrgIx = srcIx;
            forRes = sources[ixWRFor];
            source = sources[ixWRSrc];
            osource = source.other;
            dest = source.partner;
            //    rcost = mov * dSwapRcost[ixWRSrc];
            //    scost = mov * dSwapScost[ixWRSrc];
            balances.checkBalances(cur);
            if (E.debugDouble) {
              EM.addlErr = String.format("mov%7.3g, swapCostd[ixWRSrc%d][sr%d][pors%d]%7.3g", mov, ixWRSrc, sr, pors, swapCostd[ixWRSrc][sr][pors]);
              EM.wasHere = "before rcost&scost";
              rcost = doubleTrouble(doubleTrouble(mov) * doubleTrouble(swapCostd[ixWRSrc][ss][pors]));
              EM.addlErr = String.format("mov%7.3g, swapCostd[ixWRSrc%d][ss%d][pors%d]%7.3g", mov, ixWRSrc, ss, pors, swapCostd[ixWRSrc][ss][pors]);
              scost = doubleTrouble(doubleTrouble(mov) * doubleTrouble(swapCostd[ixWRSrc][ss][pors]));
            }
            else {
              rcost = mov * swapCostd[ixWRSrc][sr][pors];
              scost = mov * swapCostd[ixWRSrc][ss][pors];
            }
            rchrg = resource;
            schrg = staff;
            EM.addlErr = String.format("mov%7.3g, xcost%7.3g", mov, (ixWRSrc == 0 ? rcost : scost));
            EM.wasHere = "before dstCst";
            if (E.debugDouble) {
              dstCst = doubleTrouble(mov + doubleTrouble(ixWRSrc == 0 ? rcost : scost));
            }
            else {
              dstCst = mov + (ixWRSrc == 0 ? rcost : scost);
            }
            EM.wasHere = "after dstCst";
            prevsrc = source.balance.get(srcIx);
            prevdest = dest.balance.get(destIx);
            prevosrc = osource.balance.get(srcIx);
            swapCosts.getRow(2).add(rcost);
            swapCosts.getRow(3).add(scost);
            EM.wasHere = "before setStat SWAPRDECRCOST";
            setStat(EM.SWAPRDECRCOST, pors, clan, rcost / bals.getRow(2).sum(), 1);
            setStat(EM.SWAPSDECRCOST, pors, clan, scost / bals.getRow(4).sum(), 1);
            doNot.setDoNot(0, ixWRSrc, srcIx, doNotDays5);
            balances.sendHist(hist, aPre);
            //      decrAvail.sendHist(hist, bLev, aPre, "r decrAvail", "s decrAvail");
            //         hist.add(new History(aPre, 3, nTitle("Pre") + cmd.toString() + " " + source.aschar + srcIx + " -> " + dest.aschar + destIx, "mov=" + EM.mf(mov), "src=" + EM.mf(source.balance.get(srcIx)), "dst=" + EM.mf(dest.balance.get(destIx)), "Fl" + minFx + "=" + EM.mf(minFd), "Hl" + minHx + "=" + EM.mf(minHd), "Nm" + maxNx + "=" + EM.mf(maxNd), "Nl" + minNx + "=" + EM.mf(minNd), "Sm" + maxSx + "=" + EM.mf(maxSd)));
            hist.add(new History(aPre, History.loopIncrements3, nTitle("Pre") + swapLoops + cmd.toString() + srcIx + "->" + destIx, "mov=" + EM.mf(mov), "mMin" + EM.mf(movMin), "r$" + rChrgIx + "=" + EM.mf(rcost), "s$" + sChrgIx + "=" + EM.mf(scost), "H" + rawProspects2.curMinIx() + "=" + EM.mf(rawProspects2.curMin()), "HS" + rawProspects2.curSum(), "rS" + EM.mf(bals.getRow(0).sum()), "sS" + EM.mf(bals.getRow(1).sum()), "mtg" + EM.mf(mtgNeeds6.getRow(0).sum()), EM.mf(mtgNeeds6.getRow(1).sum()), "<<<<<<<"));
            hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(cmd.toString() + " " + source.aschar + srcIx + " -> " + dest.aschar + destIx), "mov=" + EM.mf(mov), "rcst=" + EM.mf(rcost), "scst=" + EM.mf(scost), "dsrc=" + EM.mf(source.balance.get(srcIx)), "=>" + EM.mf(source.balance.get(srcIx) - dstCst), "ddst=" + EM.mf(dest.balance.get(destIx)), "=>" + EM.mf(dest.balance.get(destIx) + mov)));
            rmov1 = ixWRSrc == 0 ? mov : 0.;
            smov1 = ixWRSrc == 0 ? 0. : mov;
            //now test balances before doing move
            if (bals.get(2, srcIx) < (rmov1 + rcost)) {
              E.myTest(true, "ERR  %s, rcbal%d=%7.2g <  rmov1=%7.2g + rcost=%7.2g = rChrg=%7.5g * mov=%7.2g ,rshort=%7.2g, age=%d, n=%d, swap4Step=%d, redo=%d<<<<<", cmd.toString(), srcIx, bals.get(2, srcIx), rmov1, rcost, rChrg, mov, bals.get(2, srcIx) - rmov1 - rcost, ec.age, n, swap4Step, reDo);
            }
            if (bals.get(4, srcIx) < (smov1 + scost)) {
              E.myTest(true, "ERR  %s, scbal%d=%7.2g <  smov1=%7.2g + scost=%7.2g = sChrg%7.5g * mov=%7.2g ,sshort=%7.2g, age=%d, n=%d, swap4Step=%d, redo=%d<<<<<", cmd.toString(), srcIx, bals.get(4, srcIx), smov1, scost, sChrg, mov, bals.get(4, srcIx) - smov1 - scost, ec.age, n, swap4Step, reDo);
            }
            rchrg.cost3(rcost, srcIx, .0001);
            schrg.cost3(scost, srcIx, .0001);
            source.putValue(balances, mov, srcIx, destIx, dest, E.sSwapPenalty[pors], 0.);
            swapped = true;
            rxfers = sxfers = 0;
            hist.add(new History(aPre, History.loopMinorConditionals5, nTitle("POST " + cmd.toString() + " " + source.aschar + srcIx + " -> " + dest.aschar + destIx), (swapped ? "swapped" : "!swapped"), "mov=" + EM.mf(mov), "rcst=" + EM.mf(rcost), "scst=" + EM.mf(scost), "dsrc=" + EM.mf(prevsrc - source.balance.get(srcIx)), "ddst=" + EM.mf(dest.balance.get(destIx) - prevdest)));
            swapType = 1;
            return swapped = true;
          }

          /*------------------XFER both, ---------------------------*/
          // XFER both,
          //       curSum >  clanStartFutureFundDues[pors][clan]
          ec.aPre = aPre = "#J";

          if (History.dl > 40) { // prevent listing
            StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
            hist.add(new History(aPre, rlev, "n" + n + " xfer crossa", wh(a0.getLineNumber()), (failed ? "" : "!") + "failed", (swapped ? "" : "!") + "swapped", swapLoops + "=swapLoops"));
          }
          //boolean xferCross = false;
          int rev = 0;   // 0=not cross charged, 1=cross charged
          /**
           * limit using the available (-need) amount to prevent loops because
           * of swapping too much
           */
          bav = balances.curSum() / 14.; //balance ave.
          //  double xchgBalFrac[] = {.9, .95, .98, .99};
          double xchgBalFrac[] = {.8, .85, .9, .92};

          // oost balances limits
          // double[] xchgWRReservFrac = {.95, .96, 97., 98.};
          // double xferMaxMove[] = {.9 * bav, .9 * bav, .90 * bav, .98 * bav};
          double[] xchgWRReservFrac = {.3, .26, .22, .17};
          double xferMaxMove[] = {1.2 * bav, 1.8 * bav, 2.4 * bav, 3.1 * bav};
          /**
           * if a high need, relax the limits a little, but be careful to avoid
           * loops
           */
          //---if ((yrphase.SWAPING == yphase && (rawProspects2.curMin() < .15 ))) {
          if ((rawProspects2.curMin() < .15)) {
            double xF[] = {.75, .80, .84, .90};
            xchgBalFrac = xF;
            //     double xaf[] = {1.02, 1.03, 1.05, 1.05};
            double xaf[] = {.75, .80, .84, .90};
            xchgWRReservFrac = xaf;
          }
          src1Bal = 0.;
          //  double src2Bal = 0.;
          //      double dest1Bal = 0.;
          //      double dest2Bal = 0.;

          double s1, r1, r2, s2;
          int s1ix, r1ix, s2ix, r2ix;
          maxAvail = -999;
          A6Row[] xferAvails = {mtggNeeds6, mtggNeeds6, mtgNeeds6, mtgNeeds6};
          double[] xchgMovMin = {balances.curSum() * .0003, balances.curSum() * .0002, balances.curSum() * .0001, balances.curSum() * .00005};
          double xferBalMin = xferMovMin = movMin = xchgMovMin[swap4Step] * redoFrac[reDo];
          A6Row[] xferNeeds = {mtgNeeds6, mtgNeeds6, mtgNeeds6, mtgNeeds6};
          //  mtgNeeds6.addJointBalances();
          // swapNeeds has no filter, it is straight stratVars
          //    swapNeeds = stratVars;

          /**
           * force planet resource xfers if resource availability (-mtgneeds) is
           * higher than staff availability, and resource rawHealths.min are &lt
           * .5 and GROW rawFertilities2 &lt.5
           */
          double rNeedSum = mtgNeeds6.getRow(0).sum();
          double sNeedSum = mtgNeeds6.getRow(1).sum();
          double maxNeed = mtgNeeds6.curMax(0);
          int maxNeedIx = mtgNeeds6.curMaxIx(0);
          // needIx = stratVars.curMaxIx(); // highest need
          double stratH0 = stratVars.max();
          double stratH4 = stratVars.max(4); //high 0,1,2,3
          double stratL3 = stratVars.min(3); //low 0,1,2
          double stratL0 = stratVars.min();
          double stratML0 = 0;
          int stratML0Ix = 0;
          int fillNeedIx = stratVars.curMinIx(); // the least need,high bal
          double dRNeedSum = maxNeed - rNeedSum;
          double dSNeedSum = maxNeed - sNeedSum;
          boolean sNeedy = false; // false: force possible r xfer

          double swaprr = eM.swapXRtoRRcost[pors];
          double swaprs = eM.swapXRtoRScost[pors];
          double swapsr = eM.swapXStoSRcost[pors];
          double swapss = eM.swapXStoSScost[pors];

          // now compute possible avails
          // find the max stratVars
          rawProspects2.sendHist(hist, aPre);
          //stratV.set(stratVars); no change stratVars
          stratV = doNot.filterByDoNot(2, stratVars, nFlag); //elim recent xfers
          needIx = stratV.curMaxIx(); // highest need after doNot
          ixWRSrc = needIx / E.lsecs; // 0:1
          destIx = needIx % E.lsecs;

          mov4 = Math.max(movMin + 1., mtgNeeds6.get01(needIx) * 3.);
          // deal with -need: available, set to part of average
          mov3 = mov4 < PZERO ? bav * .1 : mov4;
          hist.add(new History(aPre, History.loopMinorConditionals5, name + " need" + E.rNsIx(needIx) + " =", EM.mf(mov4), EM.mf(mov3), "rH" + rawProspects2.curMinIx() + "=" + EM.mf(rawProspects2.curMin()), "r=" + EM.mf(balances.getRow(2).get(needIx % E.lsecs)), "s=" + EM.mf(balances.getRow(4).get(needIx % E.lsecs)), "mvMin=" + EM.mf(movMin), "xAF=" + EM.mf(xchgWRReservFrac[swap4Step]), "mMax" + EM.mf(xferMaxMove[swap4Step])));
          hist.add(new History("@x", History.loopMinorConditionals5, name + " step=" + swap4Step, "MovMins", EM.mf(xchgMovMin[0]), EM.mf(xchgMovMin[1]), EM.mf(xchgMovMin[2]), EM.mf(xchgMovMin[3]), "availFracs", EM.mf(xchgWRReservFrac[0]), EM.mf(xchgWRReservFrac[1]), EM.mf(xchgWRReservFrac[2]), EM.mf(xchgWRReservFrac[3])));
          A2Row rAvail = new A2Row(ec, History.loopMinorConditionals5, "rAvail");
          A2Row sAvail = new A2Row(ec, History.loopMinorConditionals5, "sAvail");
          A2Row rRevAvail = new A2Row(ec, History.loopMinorConditionals5, "rRevAvail");
          A2Row sRevAvail = new A2Row(ec, History.loopMinorConditionals5, "sRevAvail");
          A2Row rAvail1 = new A2Row(ec, History.loopMinorConditionals5, "rAvail1");
          A2Row sAvail1 = new A2Row(ec, History.loopMinorConditionals5, "sAvail1");
          A2Row rRevAvail1 = new A2Row(ec, History.loopMinorConditionals5, "rRevAvail1");
          A2Row sRevAvail1 = new A2Row(ec, History.loopMinorConditionals5, "sRevAvail1");

          //now determine size of possible moves
          rAvail1.setAvailableToExchange(sAvail1, rRevAvail1, sRevAvail1, xchgBalFrac[swap4Step], xchgWRReservFrac[swap4Step], xferMaxMove[swap4Step], balances, xferNeeds[swap4Step], swaprr, swaprs, swapsr, swapss);

          //   planet resource, staff needix in highest 4 stratVal
          // first select staff to increase then resource
          if (needIx < E.lsecs && pors == E.P && stratVars.get(1, needIx) > stratH4 && rawProspects2.curMin() > .1) {
            needIx += E.lsecs;  // move needIx to a staff sector
          }
          ixWRSrc = needIx / E.lsecs; // =1:staff
          destIx = needIx % E.lsecs; //sector of most need

          // do not take need as a possible source
          doNot.setDoNot(2, ixWRSrc, needIx, doNotDays2);
          aPre = "#a";
          doNot.sendDoNot(0, bLev, aPre, History.loopMajorConditionals4);
          doNot.sendDoNot(1, bLev, aPre, History.loopMajorConditionals4);
          doNot.sendDoNot(2, bLev, aPre, History.loopMajorConditionals4);
          sAvail = doNot.filterByDoNot(2, 1, sAvail1, nFlag);
          sRevAvail = doNot.filterByDoNot(2, 1, sRevAvail1, nFlag);
          rAvail = doNot.filterByDoNot(2, 0, rAvail1, nFlag);
          rRevAvail = doNot.filterByDoNot(2, 0, rRevAvail1, nFlag);

          int alev = History.valuesMajor6;
          aPre = "#a";
          rAvail.sendHist(hist, blev, "#wa", alev, "rc rAvail", "sg rAvail");
          rAvail1.sendHist(alev, aPre);
          aPre = "#b";
          sAvail.sendHist(hist, blev, aPre, alev, "rc sAvail", "sg sAvail");
          sAvail1.sendHist(alev, aPre);
          aPre = "#c";
          rRevAvail.sendHist(hist, blev, aPre, alev, "rc rRevAvail", "sg rRevAvail");
          rRevAvail1.sendHist(alev, aPre);
          aPre = "#d";
          sRevAvail.sendHist(hist, blev, aPre, alev, "rc sRevAvail", "sg sRevAvail");
          sRevAvail1.sendHist(alev, aPre);

          // start with default r straight
          // r xfer
          if (ixWRSrc < 1) {  //do r:0 option

            if (false && rAvail.get(0, destIx) != nFlag) {
              String aLine = String.format("ERROR in rAvail needIx %d,destIx %d  should be nFlag %6.2g not %7.5g%n", needIx, destIx, nFlag, rAvail.get(0, destIx));
              rAvail.sendHist(3, "!!");
              //   new Throwable().printStackTrace(System.err);
              throw new MyErr(String.format(String.format("ERROR in rAvail needIx %d,destIx %d  should be nFlag %6.2g not %7.5g%n", needIx, destIx, nFlag, rAvail.get(0, destIx))));
            }
            if (false && rRevAvail.get(0, destIx) != nFlag) {
              // System.err.format("ERROR in rRevAvail needIx %d,destIx %d  should be nFlag %6.2g not %7.5g%n",needIx,destIx,nFlag,rRevAvail.get(0,destIx));
              rRevAvail.sendHist(3, "!!");
              //        new Throwable().printStackTrace(System.err);
              throw new MyErr(String.format("ERROR in rRevAvail needIx %d,destIx %d  should be nFlag %6.2g not %7.5g%n", needIx, destIx, nFlag, rRevAvail.get(0, destIx)));
            }
            //   rAvail.sendHist(hist, History.informationMinor9, "x#", "r rAvail", "s rAvail");
            //  rRevAvail.sendHist(hist, History.informationMinor9, "x#", "r rRevA", "s rRevA");
            r1ix = rAvail.getRow(0).maxIx();
            r1 = rAvail.getRow(0).get(r1ix);
            s1ix = rAvail.getRow(1).maxIx();
            s1 = rAvail.getRow(1).get(s1ix);
            r2ix = rRevAvail.getRow(0).maxIx();
            r2 = rRevAvail.getRow(0).get(r2ix);
            s2ix = rRevAvail.getRow(1).maxIx();
            s2 = rRevAvail.getRow(1).get(s2ix);
            aPre = "#f";
            rAvail.sendHist(alev, aPre);
            rRevAvail.sendHist(alev, aPre);
            mtgNeeds6.sendHist2(alev, aPre);
            source = dest = r;

            // xfer go straight if straight is better
            // remember avail is really size of possible move
            //  1. straight min r avail > rev r min avail--higher max avail
            //  2. stratVars.min r (r health) *1.1 < min s ( S health)
            //      and rhealth better than shealth, rstrat > sstrat
            //  3. mtgNeeds6.ave (Maxed needs) r less need than s (r avail > s avail)
            if (Math.min(r1, s1) > Math.min(r2, s2) || stratVars.getRow(0).min() * 1.1 < stratVars.getRow(1).min() || mtgNeeds6.getRow(0).ave() * 1.1 < mtgNeeds6.getRow(1).ave()) { // do straight option
              rev = 0;
              srcIx = rChrgIx = rAvail.getRow(0).maxIx();
              sChrgIx = rAvail.getRow(1).maxIx();
              rChrg = swaprr;
              if (false && (srcIx == destIx || rAvail.get(0, destIx) != nFlag || doNot.get(4 + 0, destIx) < doNotDays2)) {
                System.err.format("Error3 xfer source==dest straight r, srcIx %d,destIx %d,rAvail[0][%d] %7.5g, doNot xFer,r,destIx %3.1g %n", srcIx, destIx, destIx, rAvail.get(0, destIx), doNot.get(4 + 0, destIx));
                rAvail.sendHist(3, "!!");
                throw new MyErr(String.format("Error3 xfer source==dest straight r, srcIx %d,destIx %d,rAvail[0][%d] %7.5g, doNot xFer,r,destIx %3.1g %n", srcIx, destIx, destIx, rAvail.get(0, destIx), doNot.get(4 + 0, destIx)));
              }
              dest = source = r;
              sChrg = swaprs;
              maxavail1 = maxAvail = Math.min(r1, s1);//max mov
              // mov is min of the 2 max's min'd with original move
              mov2 = Math.min(mov3, Math.min(r1, s1));

            }
            else { // r Rev Avail
              rev = 1;// largest charge to s
              srcIx = rChrgIx = rRevAvail.getRow(0).maxIx();
              sChrgIx = rRevAvail.getRow(1).maxIx();
              if (false && (srcIx == destIx || rRevAvail.get(0, destIx) != nFlag || doNot.get(4 + 0, destIx) < doNotDays2)) {
                System.err.format("Error3 xfer source==dest strt r, srcIx %d,destIx %d,rRevAvail[0][%d] %7.5g, doNot xFer,s,destIx %3.1g %n", srcIx, destIx, destIx, rRevAvail.get(0, destIx), doNot.get(4 + 0, destIx));
                rRevAvail.sendHist(3, "!!");
                new Throwable().printStackTrace(System.err);
                throw new MyErr();
              }
              if (false && rRevAvail.get(1, destIx) != nFlag) {
                rRevAvail.sendHist(3, "!!");
                throw new MyErr(String.format("----SWPa---- xfer source==dest strt r, srcIx=%d,destIx=%d,rRevAvail[0][%d] %7.5g, doNot xFer,s,destIx %3.1g %n", srcIx, destIx, destIx, rRevAvail.get(1, destIx), doNot.get(4 + 0, destIx)));
              }
              dest = source = r;
              rChrg = swaprs;
              sChrg = swaprr;
              maxavail2 = maxAvail = Math.min(r2, s2);
              // mov is min of the 2 max's min'd with original move
              mov2 = Math.min(mov3, Math.min(r2, s2));
            }
            // src dest = s
          }
          else {  // now do the s option
            sAvail.sendHist(hist, History.informationMinor9, "x@", "r sAvail", "s sAvail");
            sRevAvail.sendHist(hist, History.informationMinor9, "x@", "r sRevAvail", "s sRevAvail");
            r1 = sAvail.getRow(0).max();
            s1 = sAvail.getRow(1).max();
            r2 = sRevAvail.getRow(0).max();
            s2 = sRevAvail.getRow(1).max();
            r1ix = sAvail.getRow(0).maxIx();
            r1 = sAvail.getRow(0).get(r1ix);
            s1ix = sAvail.getRow(1).maxIx();
            s1 = sAvail.getRow(1).get(s1ix);
            r2ix = sRevAvail.getRow(0).maxIx();
            r2 = sRevAvail.getRow(0).get(r2ix);
            s2ix = sRevAvail.getRow(1).maxIx();
            s2 = sRevAvail.getRow(1).get(s2ix);
            source = dest = s;
            // go straight if
            //  1. straight min s avail > rev min avail
            //  2. stratVars.max(how bad) of RC > SG
            //  3. balances of RC * .8 > SG
            if (Math.min(r1, s1) > Math.min(r2, s2) || stratVars.getRow(0).max() * 1.1 < stratVars.getRow(1).max() || balances.getRow(0).ave() * 1.1 < balances.getRow(1).ave()) { // do straight option
              rev = 0;  // straight s
              rChrgIx = sAvail.getRow(0).maxIx();
              srcIx = sChrgIx = sAvail.getRow(1).maxIx();
              // never xfer from the destination (should have been a INCR)
              if (false && (srcIx == destIx || sAvail.get(1, destIx) != nFlag || doNot.get(4 + 1, destIx) < PZERO)) {
                sAvail.sendHistcg();
                throw new MyErr(String.format("Error3 xfer source==dest strt s, srcIx=%d,destIx=%d,sAvail[1][%d] %7.5g, doNot xFer,s,destIx %3.1g %n", srcIx, destIx, destIx, sAvail.get(1, destIx), doNot.get(4 + 1, destIx)));
              }
              maxavail3 = maxAvail = Math.min(r1, s1);
              rChrg = swapsr;
              sChrg = swapss;
              // mov is min of the 2 max's min'd with original move
              mov2 = Math.min(mov3, Math.min(r1, s1));
            }
            else {  // s  xferCross
              rev = 1;
              rChrgIx = sRevAvail.getRow(0).maxIx();
              srcIx = sChrgIx = sRevAvail.getRow(1).maxIx();
              double sMax = sRevAvail.getRow(1).get(srcIx);
              if (false && (srcIx == destIx || sRevAvail.get(1, destIx) != nFlag || doNot.get(4 + 1, destIx) < doNotDays2)) {
                sRevAvail.sendHistcg();
                //  new Throwable().printStackTrace(System.err);
                throw new MyErr(String.format("Error3 %s n=%d xfer source==dest strt s, srcIx=%d,destIx=%d,sRevAvail[1][%d] %7.5g, sMax %7.5g doNot xFer,s,destIx %3.1g, doNot[5] srcIx %3.1g doNot[1] %3.1g %n", ec.name, n, srcIx, destIx, destIx, sRevAvail.get(1, destIx), sMax, doNot.get(4 + 1, destIx), doNot.get(4 + 1, srcIx), doNot.get(1, srcIx)));
              }
              if (sRevAvail.get(0, destIx) != nFlag) {
                sRevAvail.sendHistcg();
                throw new MyErr(String.format("Error3 xfer source==dest strt s, srcIx=%d,destIx=%d,sRevAvail[0][%d] %7.5g, doNot xFer,s,destIx %3.1g %n", srcIx, destIx, destIx, sRevAvail.get(0, destIx), doNot.get(4 + 1, destIx)));
              }
              maxavail4 = maxAvail = Math.min(r2, s2);
              // mov is min of the 2 max's min'd with original move
              mov2 = Math.min(mov3, Math.min(r2, s2));
              rChrg = swapss;
              sChrg = swapsr;

            }// end srev type
          } // end staff xfer

          // join all 4 options
          //     double xferMin[] = {.0001, .00005, .00001, .000003};
          //     double xferBalMin = Math.max(balances.curSum() * xferMin[swap4Step], 1.);
          //  movMin = xchgMovMin[swap4Step]; previously set
          aPre = "#g";

          //       xferAvail.sendHist(hist, "xfer");
          // swapNeeds.titl = "swapNeeds";
          stratV.sendHist(alev, aPre);
          bals.listBalances(History.dl, aPre, History.valuesMajor6);
          // recalculate the maxMov= min of r and s try to ignore
          double rdiv = ixWRSrc < 1 ? 1.09 + rChrg : .09 + rChrg;
          double sdiv = ixWRSrc < 1 ? .09 + sChrg : 1.09 + sChrg;
          //determine index for each SubAsset
          // srcIx means the source of move and the cost for the same sector
          int rmIx = ixWRSrc < 1 ? srcIx : rChrgIx;
          int smIx = ixWRSrc < 1 ? sChrgIx : srcIx;
          rmov = xchgBalFrac[swap4Step] * bals.get(0, rmIx) / rdiv;
          smov = xchgBalFrac[swap4Step] * bals.get(1, smIx) / sdiv;
          double rFracC = -xchgWRReservFrac[swap4Step] * mtgNeeds6.get(0, rmIx) / rdiv;
          double sFracC = -xchgWRReservFrac[swap4Step] * mtgNeeds6.get(1, smIx) / sdiv;
          double rsmov = Math.min(Math.min(rmov, smov), Math.min(rFracC, sFracC));
          // above looks right

          mov1 = mov2; // no change
          // mov1 least of both max in each case
          mov = Math.min(mov1, bals.get(ixWRSrc, srcIx) - xchgBalFrac[swap4Step] * bals.get(ixWRSrc, srcIx));
          // mov = Math.min(mov, (source.balance.get(srcIx) + source.partner.balance.get(srcIx)) * .5);

          //determine where xfer mov is applied to r or s cost
          rmov1 = ixWRSrc < 1 ? mov : 0.;
          smov1 = ixWRSrc < 1 ? 0. : mov;
          // source is from the same SubAsset as the dest
          cmd = uxdecrs[rev][ixWRSrc][swap4Step];
          if (E.debugDouble) {
            rcost = doubleTrouble(
                    doubleTrouble(mov)
                    * doubleTrouble(rChrg));
            scost = doubleTrouble(
                    doubleTrouble(mov)
                    * doubleTrouble(sChrg));

          }
          else {
            rcost = mov * rChrg;
            scost = mov * sChrg;
          }
          // 9/6/15 change to charge reserve SubAssets first
          hist.add(new History(aPre, 4, nTitle(cmd.toString() + source.aschar + srcIx + " => " + dest.aschar + destIx),
                               "m2=" + EM.mf(mov2), "m1=" + EM.mf(mov1), "m=" + EM.mf(mov),
                               "r$" + rChrgIx + "=" + EM.mf(rChrg), EM.mf(rcost), "s$" + sChrgIx + "=" + EM.mf(sChrg), EM.mf(scost),
                               "rev=" + rev, "mA" + EM.mf(maxAvail), "src" + EM.mf(balances.get01(ixWRSrc, srcIx)), "<<<<<<<L"));
          hist.add(new History(aPre, History.valuesMajor6, nTitle("more"), "movMin", EM.mf(movMin), "Nds" + (ixWRSrc == 0 ? "r" : "s") + srcIx + "=", EM.mf(mtgNeeds6.get(ixWRSrc, srcIx)), "bal" + (ixWRSrc == 0 ? "r" : "s") + srcIx + "=", EM.mf(balances.get01(ixWRSrc, srcIx)), "<<<<<<3L"));
          balances.checkBalances(this);
          if (stopped[2] > 0) {
            hist.add(new History(aPre, History.loopMinorConditionals5, nTitle("stop" + swapLoops + " " + cmd.name()), "mov" + EM.mf(mov), "dest" + EM.mf(bals.getRow(3 + 2 * ixWRSrc).get(srcIx)), "src" + EM.mf(bals.getRow(2 + 2 * ixWRSrc).get(srcIx)), "nmov" + EM.mf(nmov), "gmov" + EM.mf(gmov), "dest big enough"));
          }
          else if (mov < PZERO) {
            hist.add(new History(aPre, 4, nTitle("skip" + swapLoops + " " + cmd.name() + srcIx), " mov=" + EM.mf(mov), "lt 0", "needIx=" + needIx, "nv=" + swapNeeds.max()));
          }
          else if (mov < movMin) {
            hist.add(new History(aPre, 4, nTitle(" skip" + swapLoops + " " + cmd.name() + srcIx), "mov=", EM.mf(mov), "lessThan", "required", EM.mf(movMin)));
          }
          else if (destIx == srcIx) {
            hist.add(new History(aPre, History.loopMajorConditionals4, nTitle("SKIP ==" + " " + cmd.name() + srcIx), "mv" + EM.mf(mov), "destIx" + destIx + "==srcIx" + srcIx, "<<<<<<<<<<"));
          }
          else {
            hist.add(new History(aPre, History.loopMinorConditionals5, nTitle(cmd + source.aschar + srcIx + " => " + dest.aschar + destIx), " mov=" + EM.mf(mov), "rc=" + EM.mf(balances.get(0, rChrgIx)), "r$" + rChrgIx + "=" + EM.mf(rcost), "=>" + EM.mf(balances.get(0, rChrgIx) - (ixWRSrc < 1 ? rcost + mov : rcost)), "s=" + EM.mf(balances.get(1, sChrgIx)), "s$" + sChrgIx + "=" + EM.mf(scost), "=>" + EM.mf(balances.get(1, sChrgIx) - (ixWRSrc < 1 ? scost : scost + mov)), dest.aschar + destIx + "=" + EM.mf(balances.get(ixWRSrc, destIx)), "=>" + EM.mf(balances.get(ixWRSrc, destIx) + mov), "<<<<<<<<<<"));
            resource.balance.negError("resource.balance");
            balances.checkBalances(this);
            // continue the join both branches
            prevsrc = balances.get(ixWRSrc, srcIx);
            prevdest = balances.get(ixWRSrc, destIx);
            prevosrc = source.partner.balance.get(srcIx);
            prevodest = dest.partner.balance.get(destIx);
            setStat(EM.SWAPRXFERCOST, pors, clan, rcost / bals.getRow(2).sum(), 1);
            setStat(EM.SWAPSXFERCOST, pors, clan, scost / bals.getRow(4).sum(), 1);
            bals.sendHist(hist, aPre);

            String strRS[] = {"r", "s"};
            int tt1 = rawProspects2.curMinIx();
            int tt2 = (int) tt1 / E.lsecs;
            int tt3 = tt1 % E.lsecs;
            int tt4 = rawProspects2.curMinIx(1);
            int tt5 = (int) tt4 / E.lsecs;
            int tt6 = tt4 % E.lsecs;
            hist.add(new History(aPre, History.loopIncrements3, nTitle("p") + cmd + srcIx + "->" + destIx, "mov=" + EM.mf(mov), "mMin" + EM.mf(movMin), "r$" + rChrgIx + "=" + EM.mf(rcost), "s$" + sChrgIx + "=" + EM.mf(scost), "H" + rorss[tt2] + tt3 + "=" + EM.mf(rawProspects2.curMin()), "HS" + rawProspects2.curSum(), "rS" + EM.mf(bals.getRow(0).sum()), "sS" + EM.mf(bals.getRow(1).sum()), "mtg" + EM.mf(mtgNeeds6.getRow(0).sum()), EM.mf(mtgNeeds6.getRow(1).sum()), "<<<<<<<"));

            hist.add(new History(aPre, History.loopMajorConditionals4, "n" + n + " " + cmd.toString() + " " + source.aschar + srcIx + " -> " + dest.aschar + destIx, "mov=" + EM.mf(mov), "rcst=" + EM.mf(rcost), "scst=" + EM.mf(scost), "dR=" + EM.mf(dstRCst), "dS=" + EM.mf(dstSCst), "dosrc=" + EM.mf(prevosrc - source.partner.balance.get(srcIx)), "dodst=" + EM.mf(dest.partner.balance.get(destIx) - prevodest)));

            hist.add(new History(aPre, rlev, "n" + n + " resource" + srcIx, r.balance));

            //now test balances before doing move
            if (bals.get(0, rmIx) < (rmov1 + rcost)) {
              throw new MyErr(String.format("---SWP3---- Err  %s, rcbal%d=%7.2g <  rmov1=%7.2g + rcost=%7.2g = rChrg=%7.5g * mov=%7.2g ,rshort=%7.2g, age=%d, n=%d, swap4Step=%d, redo=%d<<<<<", cmd.toString(), rmIx, bals.get(0, rmIx), rmov1, rcost, rChrg, mov, bals.get(0, rmIx) - rmov1 - rcost, ec.age, n, swap4Step, reDo));
            }
            if (bals.get(1, smIx) < (smov1 + scost)) {
              E.myTest(true, "----SWP4---- %s, scbal%d=%7.2g <  smov1=%7.2g + scost=%7.2g = sChrg%7.5g * mov=%7.2g ,sshort=%7.2g, age=%d, n=%d, swap4Step=%d, redo=%d<<<<<", cmd.toString(), smIx, bals.get(1, smIx), smov1, scost, sChrg, mov, bals.get(1, smIx) - smov1 - scost, ec.age, n, swap4Step, reDo);
            }
            balances.checkBalances(this);
            r.cost3(rcost, rChrgIx, .00);
            hist.add(new History(aPre, rlev, "n" + n + " resource" + srcIx, r.balance));

            resource.balance.negError("resource.balance");
            s.checkGrades();
            s.cost3(scost, sChrgIx, .00);
            bals.sendHist(hist, "xc");
            source.putValue(balances, mov, srcIx, destIx, dest, 1, 0.);

            hist.add(new History(aPre, History.loopMinorConditionals5, "n" + n + " " + cmd.toString() + " " + source.aschar + srcIx + " -> " + dest.aschar + destIx, "mov=" + EM.mf(mov), "rcst=" + EM.mf(rcost), "scst=" + EM.mf(scost)));
            bals.sendHist(hist, aPre);
            // count successive r or s xfers
            if (ixWRSrc == 1) {
              rxfers++;
              //sxfers = 0;
            }
            else {
              sxfers++;
              //  rxfers = 0;
            }
            swapped = true;
            swapType = 2;
            doNot.setDoNot(swapType, ixWRSrc, destIx, doNotDays3);
            if (History.dl > 4) {
              StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

              hist.add(new History(aPre, rlev, ">>>>n" + n + "XFERD ", a0.getFileName(), wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + EM.mf(sumTotWorth), "<<<<<<<<<<"));
            }

            return true;
          }

          // no success this loop
        } // loop back from end on swapLoops
        if (History.dl > History.valuesMajor6) {
          StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
          hist.add(new History(aPre, rlev, "n" + n + " end swaps", a0.getFileName(), wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + EM.mf(sumTotWorth)));
        }

        //  }// for swapLoops
        swapType = -5;
        return false;
      }
      catch (Exception | Error ex) {
        eM.firstStack = eM.secondStack + "";
        ex.printStackTrace(eM.pw);
        eM.secondStack = eM.sw.toString();
        System.out.flush();
        System.err.flush();
        EM.newError = true;
        EM.tError = ("----ACS2---- swaps Caught " + ex.toString() + ", cause=" + ex.getCause() + " message=" + ex.getMessage() + " string=" + ex.toString() + Thread.currentThread().getName());
        System.err.println(EM.tError + eM.andMore());
        ex.printStackTrace(System.err);
        st.setFatalError();
        return false;
        //throw new WasFatalError(eM.tError);
      }
    } // end CashFlows.swaps

    void swapResults(String aPre
    ) {
      if (History.dl > 4) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

        hist.add(new History(aPre, 5, ">>>>n" + n + " swapres", a0.getFileName(), wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + EM.mf(sumTotWorth), "srcIx=" + srcIx, "destIx=" + destIx, "ixWRSrc=" + ixWRSrc, "<<<<<<<<<<<"));
      }
      if (source == null || dest == null || srcIx < 0 || destIx < 0) {
        hist.add(new History(aPre, 4, "n" + prevns[0].n + " notswapped?", (cmd == null ? "!cmd" : cmd.toString()), "s=" + (source == null ? "null" : source.aschar) + srcIx, "d=" + (dest == null ? "null" : dest.aschar) + destIx, "f=" + (forRes == null ? "null" : forRes.aschar) + forIx, "-----", "-----"));
        return;
      }
      String sw = (swapped ? source.as1 != dest.as1 ? "Trd=" : srcIx != destIx ? "xfr=" : "swp=" : " !!! ");
      sw = " " + cmd.toString() + " " + srcIx + (forIx >= 0 ? (" for " + forIx) : "") + "=>" + destIx;
      if (ixWRSrc < 2) {
        hist.add(new History(aPre, 4, "n" + n + sw, "F" + rawFertilities2.curMinIx() + "=" + EM.mf(rawFertilities2.min()), "f" + rawFertilities2.curMinIx(1) + "=" + EM.mf(rawFertilities2.curMin(1)), "H" + rawProspects2.curMinIx() + "=" + EM.mf(rawProspects2.min()), "h" + rawProspects2.curMinIx(1) + "=" + EM.mf(rawProspects2.curMin(1)), " m=" + EM.mf(mov), "rc=" + EM.mf(rcost), "sc=" + EM.mf(scost), "Fd=" + EM.mf(rawFertilities2.get(ixWRSrc, destIx)), "Hd=" + EM.mf(rawProspects2.get(ixWRSrc, srcIx))));
      }
      if (History.dl > 4) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

        hist.add(new History(aPre, 7, ">>>>n" + n + " skips", wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + EM.mf(sumTotWorth), "<<<<<<<<<"));
      }
    }

    void prevSwapResults(String aPre
    ) {
      if (History.dl > 4) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

        hist.add(new History(aPre, 5, "enter n" + n + " prevSwapResults at", wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops"));
      }
      if (n == 0) {
        hist.add(new History(aPre, 4, "n=" + n, "no Prev swap ", "results", "------", "------", "------", "--------", "-------", "-------", "-------", "-------"));
        return;  // no previous results yet
      }
      if (source == null || dest == null || srcIx < 0 || destIx < 0 || prevns[0].nSource == 4 || prevns[0].nDest == 4) {
        hist.add(new History(aPre, 4, "n" + prevns[0].n + " unswapped", "s=" + (source == null ? "null" : source.aschar) + srcIx, "d=" + (dest == null ? "null" : dest.aschar) + destIx, "f=" + (forRes == null ? "null" : forRes.aschar) + forIx, "?????????????????", "", "", ""));
        return;
      }

      if (healths != null && (prevns[0].healths != null) && fertilities != null && prevns[0].rawFertilities2 != null) {
        String sw = (prevns[0].swapped ? prevns[0].doingTrade ? "Trad=" : prevns[0].srcIx != prevns[0].destIx ? "xfr=" : "swp=" : " !!! ");
        sw = cmd.toString() + " ";
        // so current swap values are what is being shown, not prevns[0] values
        hist.add(new History(aPre, 4, "n=" + prevns[0].n + " "
                                      + sw
                                      + aChar[source.sIx] + srcIx
                                      + (forIx >= 0 ? " for " + forIx : " to "
                                                                        + aChar[dest.sIx] + destIx), " m=" + EM.mf(mov), "rc=" + EM.mf(rcost), "sc=" + EM.mf(scost), "difH=" + (EM.mf(health - prevns[0].health)), "H=" + EM.mf(prevns[0].health), "=>" + EM.mf(health), "F=" + EM.mf(prevns[0].fertility), "=>" + EM.mf(fertility), "$" + EM.mf(prevns[0].sumTotWorth), "=>$" + EM.mf(sumTotWorth)));

        /*
         hist.add(new History(aPre, 4, "n=" + prevns[0].n + " moreH "
         + aChar[source.sIx] + srcIx
         + (forIx >= 0 ? " for " + forIx : " to "
         + aChar[dest.sIx] + destIx), "HR" + healths.getRow(0).minIx() + "=" + EM.mf(rawHealths.getRow(0).min()), rawHealths.getRow(0).minIx(1) + "=" + EM.mf(rawHealths.getRow(0).min2()), "ave=" + EM.mf(prevns[0].rawHealths.getRow(0).ave()), "HS" + rawHealths.getRow(1).minIx() + "=" + EM.mf(rawHealths.getRow(1).min()), rawHealths.getRow(1).minIx(1) + "=" + EM.mf(rawHealths.getRow(1).min2()), "ave=" + EM.mf(rawHealths.getRow(0).ave()), "", "", ""));

         hist.add(new History(4, "n=" + prevns[0].n + " moreF "
         + aChar[source.sIx] + srcIx
         + (forIx >= 0 ? " for " + forIx : " to "
         + aChar[dest.sIx] + destIx), "FR" + rawFertilities.getRow(0).minIx() + "=" + EM.mf(rawFertilities.getRow(0).min()), rawFertilities.getRow(0).minIx(1) + "=" + EM.mf(rawFertilities.getRow(0).min2()), "ave=" + EM.mf(prevns[0].rawFertilities.getRow(0).ave()), "FS" + rawFertilities.getRow(0).minIx() + "=" + EM.mf(rawFertilities.getRow(0).min()), rawFertilities.getRow(0).minIx(1) + "=" + EM.mf(rawFertilities.getRow(0).min2()), "ave=" + EM.mf(rawFertilities.getRow(0).ave())));
         */
      }
      else {
        hist.add(new History(aPre, 4, "n=" + prevns[0].n, "no swap this n"));
      }

      //  double difrc = prevns[0].sumRCWorth - sumRCWorth;
      //    double difsg = prevns[0].sumSGWorth = sumSGWorth;
      if (setIncr.contains(cmd)) {
        //      EM.gameRes.SWAPINCRWRCOSTSCUM.wet(pors, clan, difrc);
        // setStat("SWAPINCRWRCOSTSCUM", pors, clan, difrc);
        //       EM.gameRes.SWAPINCRWSCOSTSCUM.wet(pors, clan, difsg);
        // setStat("SWAPINCRWSCOSTSCUM", pors, clan, difsg);
      }
      else if (setDecr.contains(cmd)) {
        //     EM.gameRes.SWAPDECRWRCOSTSCUM.wet(pors, clan, difrc);
        //  setStat("SWAPDECRWRCOSTSCUM", pors, clan, difrc);
        //     EM.gameRes.SWAPINCRWSCOSTSCUM.wet(pors, clan, difsg);
        // setStat("SWAPINCRWSCOSTSCUM", pors, clan, difsg);
      }
      else if (setXXdecr.contains(cmd)) {
        //   EM.gameRes.SWAPXCHGWRCOSTSCUM.wet(pors, clan, difrc);
        //setStat("SWAPXCHGWRCOSTSCUM", pors, clan, difrc);
        //   EM.gameRes.SWAPXCHGWSCOSTSCUM.wet(pors, clan, difsg);
        //setStat("SWAPXCHGWSCOSTSCUM", pors, clan, difsg);
      }
      //    histdifs(7, "resource", resource.balance, prevns[0].resource.balance);
      //   histdifs(7, "cargo", cargo.balance, prevns[0].cargo.balance);
      //   histdifs(7, "staff", staff.balance, prevns[0].staff.balance);
      //   histdifs(7, "guests", guests.balance, prevns[0].guests.balance);
      //   histdifs(7, "work", staff.work, prevns[0].staff.work);
      //    histdifs(4, "RFertility", r.fertility, prevns[0].r.fertility);
      //   histdifs(4, "SFertility", s.fertility, prevns[0].s.fertility);
      //   histdifs(4, "RFertility1", r.fertility1, prevns[0].r.fertility1);
      //    histdifs(4, "SFertility1", s.fertility1, prevns[0].s.fertility1);
      //   histdifs(4, "RReqFertility", r.tReqGrowthFertility, prevns[0].r.tReqGrowthFertility);
      //  histdifs(4, "SReqFertility", s.tReqGrowthFertility, prevns[0].s.tReqGrowthFertility);
      //   histdifs(4, "RHealth", r.health, prevns[0].r.health);
      //   histdifs(4, "SHealth", s.health, prevns[0].s.health);
      if (History.dl > 44) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

        hist.add(new History(aPre, 5, "n" + n + " prevSwapRes", wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + EM.mf(sumTotWorth)));
      }
      /*
       histdifs(7, "r.tReqMaintRemnant", r.tReqMaintRemnant, prevns[0].r.tReqMaintRemnant);
       histdifs(7, "r.nptMTCosts", r.nptMTCosts, prevns[0].r.nptMTCosts);
       histdifs(7, "r.nptMTRemnant", r.nptMTRemnant, prevns[0].r.nptMTRemnant);
       histdifs(7, "yRReqGroMTHPenRemnant", yRReqGrowthMTHPenRemnant, prevns[0].yRReqGrowthMTHPenRemnant);
       histdifs(7, "yRRawGroMTHPenRemnant", r.hptRawGrowthRemnant, prevns[0].r.hptRawGrowthRemnant);
       histdifs(7, "yRMTLimGHPenRemnant", yRMTLimitedGHPenRemnant, prevns[0].yRMTLimitedGHPenRemnant);
       histdifs(7, "s.tReqMaintRemnant", s.tReqMaintRemnant, prevns[0].s.tReqMaintRemnant);
       histdifs(7, "s.nptMTCosts", s.nptMTCosts, prevns[0].s.nptMTCosts);
       histdifs(7, "s.nptMTRemnant", s.nptMTRemnant, prevns[0].s.nptMTRemnant);
       histdifs(7, "r.tReqMaintHealth", r.tReqMaintHealth, prevns[0].r.tReqMaintHealth);
       histdifs(7, "s.tReqMaintHealth", s.tReqMaintHealth, prevns[0].s.tReqMaintHealth);
       histdifs(7, "r.tMTHealth", r.tMTHealth, prevns[0].r.tMTHealth);
       histdifs(7, "s.tMTHealth", s.tMTHealth, prevns[0].s.tMTHealth);

       histdifs(7, "ySReqGMTHPenRemnant", ySReqGrowthMTHPenRemnant, prevns[0].ySReqGrowthMTHPenRemnant);
       histdifs(7, "ySRawGMTHPenRemnant", s.hptRawGrowthRemnant, prevns[0].s.hptRawGrowthRemnant);
       histdifs(7, "ySMTLimGHPenRemnant", ySMTLimitedGHPenRemnant, prevns[0].ySMTLimitedGHPenRemnant);
       */
 /*
       histdifs(7, "RjRSerReqForMaintenance", resource.jRServerRequiredForMaintenance, prevns[0].resource.jRServerRequiredForMaintenance);
       histdifs(7, "CjRSerReqForMaintenance", cargo.jRServerRequiredForMaintenance, prevns[0].cargo.jRServerRequiredForMaintenance);

       histdifs(7, "CiRConT1yrCost", cargo.iRConsumerT1yrCost, prevns[0].cargo.iRConsumerT1yrCost);
       histdifs(7, "CiConMTCost", cargo.iConsumerMTCost, prevns[0].cargo.iConsumerMTCost);
       histdifs(7, "CiConReqForMaintenance", cargo.iConsumerRequiredForMaintenance, prevns[0].cargo.iConsumerRequiredForMaintenance);
       histdifs(7, "CiRConReqFGrowth", cargo.iRConsumerRequiredForGrowth, prevns[0].cargo.iRConsumerRequiredForGrowth);
       histdifs(7, "CiConMTGCst", cargo.iConsumerMTGCost, prevns[0].cargo.iConsumerMTGCost);
       */
      if (rawFertilities2.getRow(0).min(3) > 1. && rawFertilities2.getRow(0).min() > 1. && rawProspects2.getRow(0).min() > 1. && rawProspects2.getRow(1).min() > 1.) {
        hist.add(new History(4, "N" + n + " Finished", "RF=" + EM.mf(rawFertilities2.getRow(0).min()), "SF=" + EM.mf(rawFertilities2.getRow(1).min()), "RH=" + EM.mf(rawProspects2.getRow(0).min()), "SH=" + EM.mf(rawProspects2.getRow(1).min()), "Health=", EM.mf(health), "$=" + EM.mf(sumTotWorth)));
        dest = source = null;
      }
      if (History.dl > 50) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];

        hist.add(new History(aPre, 7, ">>>>n" + n + " skips", wh(a0.getLineNumber()), (failed ? "failed" : "!!!failed"), (swapped ? "swapped" : "!!!swapped"), swapLoops + "=swapLoops", "$=" + EM.mf(sumTotWorth)));
      }
    }

    void histdifs(int lev, String title, ARow A, ARow prevA
    ) {
      A.get(0);
      prevA.get(0);
      hist.add(new History(lev, "n" + n + " new " + title, A));
      hist.add(new History(lev, "n" + n + " old " + title, prevA));
      hist.add(new Difhist(ec, lev, "n" + n + " dif " + title, A, prevA));
    }

    /**
     * do the growth of each of the subAssets Set Maxs: maxKnowledge,
     * maxColonists, maxResources
     *
     * @param aPre the prefix of the hist entry for that growth
     */
    void doGrowth(String aPre
    ) {
      //maxKnowledge, maxColonists, maxResources
      //   bals.getRow(GROWTHSEFFIX + RIX).set(r.growth);
      //  bals.getRow(GROWTHSEFFIX + CIX).set(c.growth);
      //  bals.getRow(GROWTHSEFFIX + SIX).set(s.growth);
      //  bals.getRow(GROWTHSEFFIX + GIX).set(g.growth);
      double preGrow = sumTotWorth;

      resource.doGrow(aPre);
      cargo.doGrow(aPre);
      guests.doGrow(aPre);
      staff.doGrow(aPre);
    }

    void doMaintCost(String aPre
    ) {
      r.doCost(aPre, "r mtgCosts", mtgCosts10.getRow(0));
      s.doCost(aPre, "s mtgCosts", mtgCosts10.getRow(1));
    }

    void doTravCost(String aPre
    ) {
      //  r.doCost(aPre, "r travelCosts", travelCosts.getRow(0));
      //  s.doCost(aPre, "s travelCosts", travelCosts.getRow(1));
    }

    void doGrowthCost(String aPre
    ) {
      //  r.doCost(aPre, "r growthCosts", growthCosts.getRow(0));
      //  s.doCost(aPre, "s growthCosts", growthCosts.getRow(1));
    }

    void costsAndGrowth(A6Row mtgCosts
    ) {
      ARow prevrbal = new ARow(ec), prevcbal = new ARow(ec), prevsbal = new ARow(ec), prevgbal = new ARow(ec);
      ARow prevrbal2 = new ARow(ec), prevcbal2 = new ARow(ec), prevsbal2 = new ARow(ec), prevgbal2 = new ARow(ec);
      ARow prevKnowledge = new ARow(ec), prevcommonKnowledge = new ARow(ec), prevnewKnowledge = new ARow(ec), prevManuals = new ARow(ec);;
      if (History.dl > 6) {
        prevrbal = new ARow(ec).set(resource.balance);
        prevcbal = new ARow(ec).set(cargo.balance);
        prevsbal = new ARow(ec).set(staff.balance);
        prevgbal = new ARow(ec).set(guests.balance);
        prevKnowledge = new ARow(ec).set(knowledge);
        prevcommonKnowledge = new ARow(ec).set(commonKnowledge);
        prevnewKnowledge = new ARow(ec).set(newKnowledge);
        prevManuals = new ARow(ec).set(manuals);
        hist.add(new History(3, "R Grow", r.growth));
        hist.add(new History(3, "S Grow", s.growth));
        hist.add(new History(3, "C Grow", c.growth));
        hist.add(new History(3, "G Grow", g.growth));
        //   hist.add(new History(3, "r cost", r.hptMTGCosts));
        //  hist.add(new History(3, "s cost", s.hptMTGCosts));
        // ARow rgrowunit = new ARow(ec).setAdivbyB(resource.growth, resource.balance);
        histdifs(7, "r growdif", resource.rawGrowth, r.growth);
        // ARow cgrowunit = new ARow(ec).setAdivbyB(cargo.growth, cargo.balance);
        histdifs(7, "c growdif", cargo.rawGrowth, c.growth);
        //  ARow sgrowunit = new ARow(ec).setAdivbyB(staff.growth, staff.balance);
        histdifs(7, "s growdif", staff.rawGrowth, s.growth);
        //  ARow ggrowunit = new ARow(ec).setAdivbyB(guests.growth, guests.balance);
        histdifs(7, "g growdif", guests.rawGrowth, g.growth);
      }
      resource.doGrow(aPre);
      cargo.doGrow(aPre);
      guests.doGrow(aPre);
      staff.doGrow(aPre);
      // resource.sumGrades(-4,0.); // should be in doGrow
      // cargo.sumGrades(-4,0.);
      //staff.sumGrades(-4,0.);
      // guests.sumGrades(-4,0.);
      if (History.dl > 6) {

        histdifs(7, "rcost bal", resource.balance, prevrbal);
        histdifs(7, "ccost bal", cargo.balance, prevcbal);
        histdifs(7, "scost bal", staff.balance, prevsbal);
        histdifs(7, "gcost bal", guests.balance, prevsbal);

      }
      prevrbal2 = new ARow(ec).set(resource.balance);
      prevcbal2 = new ARow(ec).set(cargo.balance);
      prevsbal2 = new ARow(ec).set(staff.balance);
      prevgbal2 = new ARow(ec).set(guests.balance);

      resource.doCost(aPre, mtgCosts.getRow(2));
      staff.doCost(aPre, mtgCosts.getRow(4));
      staff.checkSumGrades();
      if (History.dl > 6) {
        histdifs(7, "rgrow bal", resource.balance, prevrbal2);
        histdifs(7, "cgrow bal", cargo.balance, prevcbal2);
        histdifs(7, "sgrow bal", staff.balance, prevsbal2);
        histdifs(7, "ggrow bal", guests.balance, prevsbal2);
        histdifs(7, "rdo bal", resource.balance, prevrbal);
        histdifs(7, "cdo bal", cargo.balance, prevcbal);
        histdifs(7, "sdo bal", staff.balance, prevsbal);
        histdifs(7, "gdo bal", guests.balance, prevsbal);
        histdifs(7, " newKnowledge", newKnowledge, prevnewKnowledge);
        histdifs(7, " commonKnowledge", commonKnowledge, prevcommonKnowledge);
        histdifs(7, " knowledge", knowledge, prevKnowledge);
        histdifs(7, " manuals", manuals, prevManuals);

      }
    }

  } // end trade.Assets.CashFlow   }  //end calcRawCosts
}//end class Assets

