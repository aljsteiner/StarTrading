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

 Offer is the communication object between 2 bartering economies.  They revise
 the offer, and return it so that the ship Econ process moves to the
 next barter.   Offer contains a lot of helper data,
 Some data for the final trade if the offer is accepted.
 Some data to enable a ship to select the next trading partner
 /** StarTrader contains the used set of stats descriptors
   * 
  static public String statsButton0Tip = "0: Current Game Worths";
  static public String statsButton1Tip = "1: Favors and trade effects";
  static public String statsButton0Tip = "0: Current Game Worths";
  static public String statsButton1Tip = "1: Favors and trade effects";
  static public String statsButton2Tip = "2: Catastrophies, deaths, randoms, forward fund";
  static public String statsButton3Tip = "3: years 0,1,2,3 worth inc, costs, efficiency,knowledge,phe";
  static public String statsButton4Tip = "4: years 4,5,6,7 worth inc, costs, efficiency,knowledge,phe ";
  static public String statsButton5Tip = "5: years 8->15 worth inc, costs, efficiency,knowledge,phe ";
  static public String statsButton6Tip = "6: years 16->31 worth inc, costs, efficiency,knowledge,phe ";
  static public String statsButton7Tip = "7: years 32+ worth inc, costs, efficiency,knowledge,phe ";
  static public String statsButton8Tip = "8: swap factors";
  static public String statsButton9Tip = "9: Resource, staff values";
  static public String statsButton10Tip = "10: growth and costs details";
  static public String statsButton11Tip = "11: Fertility, health and effects";
 */
package trade;

import java.text.NumberFormat;
import java.util.ArrayList;

/**
 *
 * @author albert Steiner
 */
public class Offer {

  static int searchL = 3; // the max high & low planet goods
  //static final int[] MS = {0, 1};
  Econ[] cn = new Econ[2]; //0=planet or ship,1=primaryShip
  EM eM;
  Econ ec = EM.curEcon;
  String[] cnName = {"aPlanetOther", "aShip"};
  A2Row goods = new A2Row(ec); //only one instance of goods, for both cn's
  A2Row[] secondGoods = {new A2Row(ec),new A2Row(ec)}; // after the first all I got entry
 // A2Row[] entryGoods = {new A2Row(ec),new A2Row(ec)}; // 
  A2Row[] prev2Goods = {new A2Row(ec),new A2Row(ec)}; // rows at entry to prev prev barter
  A2Row[] prev3Goods = {new A2Row(ec),new A2Row(ec)}; // rows at entry to prev prev barter
  A2Row[] prevGoods = {new A2Row(ec),new A2Row(ec)};  // rows at entry to prev barter
  A2Row[] lastGoods = {new A2Row(ec),new A2Row(ec)};  // rows at entry to barter
  A2Row[] initialGoods = {new A2Row(ec),new A2Row(ec)};  //entryGoods keep in list
  //A2Row secondPlanetGoods = new A2Row(ec);// see secondGoods[0]
  //A2Row planetGoods = new A2Row(ec); //goods[0]
  double[] secondSends, secondReceipts, secondTotalSends;
  double[] secondTotalReceipts, secondStrategicFrac,
          secondStrategicValue;
  double[] sends={0.,0.};
  double[] receipts={0.,0.};
  double[] totalSends={9.,9.};
  double[] totalReceipts={0.,0.};
  double[] strategicFrac={0.,0.};
  double[] strategicValue= {0.,0.};
  double[] startUnitsSum={0.,0.};
  double[] startWorth = {0.,0.}; // worth at start trade
  double[] endWorth = {0.,0.}; // worth after trade   
  double[] distanceMoved = {0.,0.};
  /**
   * the following shipCG,otherCG,guestGrades are pointers to current values in
   * Assets.cur for the trading ship and other planet or ship these are used in
   * doTrade to actually move values between economies
   */
  ARow[] cargos = new ARow[2], guests = new ARow[2];
  double[][][] guestGrades = new double[2][][]; // dynamic
  double cash = 0;  // **
  double cashes[] = new double[2];
  ARow newKnowledge[] = {new ARow(ec), new ARow(ec)}; // dynamic
  ARow commonKnowledge[] = {new ARow(ec), new ARow(ec)}; // dynamic
  ARow manuals[] = {new ARow(ec), new ARow(ec)};  // dynamic
  ARow[][] know = {newKnowledge, commonKnowledge, manuals}; //dynamic
  int pors[] = {0, 1};   //1=ship, 0=other planet or ship
  //start with planet, first flip is at entry to barterStart-1
  int myIx = 0, oIx = 1, prevMyIx, prev2MyIx, goodIx = 0,prevGoodIx=1;
  int bb;   // always set so that (bb+EM.barterStart) == 0 planet
 
  int term = 60;
  int age[] = {1, 1};

  ARow[] valueMoreManuals = {new ARow(ec), new ARow(ec)}; //dynamic
  ARow[] moreManuals = {new ARow(ec), new ARow(ec)};
  int year = -10;  // year of the offer **
  int tradedShipOrdinal = 0; // 1=first ship trade this year, 2= second ...
  double[][] xyzs = {{-40, -41, -42}, {-43, -44, -45}}; //**
  int[] clans = {0, 1}; // **
  //double[] sends = {0., 0.};  // offers without cash and manuals
  //double[] receipts = {0., 0.};  // requests without cash and manuals
  boolean[] sos = {false, false}; //
  int[] sosClan = {1, 1};
  ArrayList<History>[] hists;
  NumberFormat dFrac = NumberFormat.getNumberInstance();
  NumberFormat whole = NumberFormat.getNumberInstance();
  NumberFormat dfo = dFrac;
  //ArrayList<Offer> offerList;
  // subject to myIx[0=other,1=ship];
  int[] lastTerm = {-999999, -999999}; // term at normal barter xit
  double[] firstNominalCriticalRequests = {-9999999., -99999999.};
  double[] firstNominalCriticalOffers = {-9999999., -99999999.};
  double[] finalNominalCriticalRequests = {-9999999., -99999999.};
  double[] finalNominalCriticalOffers = {-9999999., -99999999.};
  double[] firstNominalTotalRequests = {-9, -9};
  double[] firstNominalTotalOffers = {-9, -9};
  double[] finalNominalTotalRequests = {-9, -9};
  double[] finalNominalTotalOffers = {-9, -9};
  Offer searchPrev = this, searchNext = this, searchLast = this;
  double searchDistance;
  double searchCumVal = 0;
  double searchCumDivisor = 0;
  
  
  Offer(Econ ec) {
    eM = StarTrader.eM;
    this.ec = ec;
    hists = new ArrayList[2];
    year = eM.year;
  }

  /**
   * initial offer created in Econ of the ship
   *
   * @param year current year
   * @param term term value should be 18
   * @param myEcon Ship econ  myIx == 0
   * @param oEcon Planet econ myIx==1
   */
  Offer(int year, int term, Econ ec,EM eem, Econ myEcon, Econ oEcon) {
    
    int[] nxyz = {0, 1, 2};
    eM = eem;
    this.ec = ec;
    cn[1] = myEcon;  // always a ship
    cn[0] = oEcon;  // planet or ship??
    bb = (1 + eM.barterStart) % 2; // force (bb+E.barterStart)%2 = 1
    myIx = (bb + term) % 2;
    oIx = (bb + term + 1) % 2;
    this.term = term;
    this.year = year;
    hists = new ArrayList[2];
    for (int m=0;m< 2;m++) {  //this for syntax seems clearer
      sos[m] = cn[m].getSOS();
      age[m] = cn[m].getAge();
      clans[m] = cn[m].getClan();
      cnName[m] = cn[m].getName();
      hists[m] = cn[m].getHist();
      cargos[m] = cn[m].getCargo();
      guests[m] = cn[m].getGuests();
      guestGrades[m] = cn[m].getGuestGrades();
      know[0][m] = cn[m].getNewKnowledge(); // get references
      know[1][m] = cn[m].getCommonKnowledge();
      know[2][m] = cn[m].getManuals();
      manuals[m] = cn[m].getManuals();
      distanceMoved[m] = cn[m].getDistance();
      valueMoreManuals[m] = calcValueMoreManuals(m);
      for (int n : nxyz) {
        xyzs[m][n] = cn[m].getXyz(n);
      };
    } // end for m
   
  }

  /** make a new instance with all of the old values
   * really should be Offer a = b; just creating another reference
   * This new Offer(Offer A) may be unused
   * @param curOffer
   * @param nono 
   */
  Offer(Offer curOffer,int nono) {
    int[] nxyz = {0, 1, 2};
    hists = new ArrayList[2];
    eM = curOffer.eM;
    for (int m=0;m< 2;m++) {  //this for syntax seems clearer
      cn[m] = curOffer.cn[m];
      sos[m] = cn[m].getSOS();
      age[m] = cn[m].getAge();
      clans[m] = cn[m].getClan();
      cnName[m] = cn[m].getName();
      hists[m] = cn[m].getHist();
      cargos[m] = cn[m].getCargo();
      guests[m] = cn[m].getGuests();
      guestGrades[m] = cn[m].getGuestGrades();
      know[0][m] = cn[m].getNewKnowledge(); // get references
      know[1][m] = cn[m].getCommonKnowledge();
      know[2][m] = cn[m].getManuals();
      manuals[m] = cn[m].getManuals();
      distanceMoved[m] = cn[m].getDistance();
      valueMoreManuals[m] = calcValueMoreManuals(m); //good all trade
      for (int n : nxyz) {
        xyzs[m][n] = cn[m].getXyz(n);
      };
    } // end for m

  }

  /**
   * return the planet Name
   *
   * @return
   */
  String getPlanetName() {
    return cnName[0];
  }

  int getAge() {
    return eM.year - year;
  }

  int setNominalValues(Econ ec, double tRequests, double tOffers, double cRequests, double cOffers) {
    setMyIx(ec);
    finalNominalCriticalRequests[myIx] = cRequests;
    finalNominalTotalRequests[myIx] = tRequests;
    finalNominalCriticalOffers[myIx] = cOffers;
    finalNominalTotalOffers[myIx] = tOffers;
    if (term > eM.barterStart - 2) {
      firstNominalCriticalRequests[myIx] = cRequests;
      firstNominalTotalRequests[myIx] = tRequests;
      firstNominalCriticalOffers[myIx] = cOffers;
      firstNominalTotalOffers[myIx] = tOffers;
    }
    return myIx;
  }

  /**
   * reduce storage, null arrays etc where possible
   *
   */
  Offer clean() {
    // goods = null;
    //  entryGoods = null;
    prev3Goods = null;
    prev2Goods = null; // rows at entry to prev prev barter
    prevGoods = null;  // rows at entry to prev barter
   // lastGoods = null;  // rows at entry to barter
   // initialGoods = null;  // **keep in list
    
    cargos = null;
    guestGrades = null;
    //double cash = 0;  // **
    newKnowledge = null;
    // ARow commonKnowledge[] = {new ARow(ec), new ARow(ec)}; // **
    //ARow manuals[] = {new ARow(ec), new ARow(ec)};  // **
    //ARow[][] know = {newKnowledge, commonKnowledge, manuals};
    //int pors[] = {0, 1};   //might allow ship to ship offers
    // myIx = null;
    // oIx = null, prevMyIx=null, prev2MyIx=null;
    //int int bb;   // always set so that (bb+EA.barterStart) == 1 ship
    //Econ[] cn = new Econ[2]; //0=planet or ship,1=primaryShip
    // String[] cnName = {"aPlanetOther", "aShip"};
    //int term = 60;
    //int lastTerm = -1;
    //int age[] = {1, 1};

    // ARow[] valueMoreManuals = {new ARow(ec), new ARow(ec)};
    // ARow[] moreManuals = {new ARow(ec), new ARow(ec)};
    // int year = -10;  // year of the offer **
    //int[][] xyzs = {{-40, -41, -42}, {-43, -44, -45}}; //**
    // int[] clans = {0, 1}; // **
    // boolean[] sos = {false, false}; //
    // int[] sosClan = {1, 1};
    hists = null;
    return this;
  }

  /*
   Offer(Offer prevOffer, Econ myCn) {
   int[] nxyz = {0, 1,2};
   int[] nknow = {0, 1, 2};
   if (prevOffer.cn[myIx] != myCn) {
   for (int m :E.alsecs) {
   // change sign of goods if prevOffer is for the other cn
   guests.set(m, -prevOffer.guests.);
   cargo.set(m,-prevOffer.cargo.get(m));
   } } else { guests.set(prevOffer.guests);
   * cargo.set(prevOffer.cargo); } for (int m : MS) { cn[m] = prevOffer.cn[m];
   * for (int n : nknow) { know[n][m] = prevOffer.know[m][m];//?? }
   * moreManuals[m].set(prevOffer.moreManuals[m]);
   * valueMoreManuals[m].set(prevOffer.valueMoreManuals[m]); } myIx = cn[0] ==
   * myCn ? 0 : cn[1] == myCn ? 1 : 2; E.myTest(myIx > 1, " unknown myCn name="
   * + myCn.getName()); goods = new A2Row(cargo, guests); prevGoods =
   * prevGoods.set2(goods); cash = prevOffer.cash;
   *
   * term = prevOffer.term; year = prevOffer.year; }
   */
  protected String df(double n) {
    NumberFormat dFrac = NumberFormat.getNumberInstance();
    NumberFormat whole = NumberFormat.getNumberInstance();
    NumberFormat dfo = dFrac;
    dFrac.setMaximumFractionDigits(5);
    dFrac.setMinimumFractionDigits(2);
    return dFrac.format(n);
  }

  String whole(double n) {
    whole.setMaximumFractionDigits(0);
    return whole.format(n);
  }

  void listPOffer(ArrayList<History> hist) {
    hist.add(new History("O", History.valuesMajor6, cnName[1] + " P=" + cnName[0], "snd" + df(sends[myIx]), "ts" + df(sends[myIx]), "rec" + df(receipts[myIx]), "tr" + df(totalReceipts[myIx]), "sf" + df(strategicFrac[myIx]), "SV" + df(strategicValue[myIx])));
  }

  /**
   * get the manuals myIx can offer as part of the trade
   *
   * @param ix which is myIx index 0=planet, 1=ship about manuals
   */
  ARow calcMoreManuals(int ix) {
    ARow ret = new ARow(ec);
    int oIx = (ix+1) % 2;
    int mIx = ix % 2;
    double tmpm=0.;
    for (int m : E.alsecs) {
      // how much more knowledge other has than myIx;
      double commonDif = commonKnowledge[oIx].get(m) - commonKnowledge[mIx].get(m);
      ret.set(m, (tmpm=newKnowledge[mIx].get(m) * eM.newKnowledgeTradeManualFrac[0]
              + manuals[mIx].get(m) * eM.manualTradeManualFrac[0]
              + commonKnowledge[mIx].get(m) * eM.commonKnowledgeTradeManualFrac[0]
              + commonDif > 0. ? commonDif: 0) * eM.commonKnowledgeDifTradeManualFrac[0]  + manuals[mIx].get(m) < EM.manualsMin[0] ? EM.manualsMin[0] - manuals[mIx].get(m) : tmpm);
    }
    return moreManuals[ix].set(ret);
  }

  /**
   * get values that myIx can offer for manuals in a trade
   *
   * @see getMoreManuals for amount of manuals
   * @param ix index of caller 0= planet, 1=ship
   * @return ARow of value more manuals by sector
   */
  ARow calcValueMoreManuals(int ix) {
    calcMoreManuals(ix);
    return valueMoreManuals[ix] = valueMoreManuals[ix].set(moreManuals[ix]).mult(eM.nominalWealthPerTradeManual[ix]);
  }

  /** get the manuals myIx can offer in a trade  
  ARow getMoreManuals() {
    return calcMoreManuals(myIx);
  }

  /** the value of manuals myIx can offer for each sector
   * 
   * @return a row of values
   */
  ARow getValueMoreManuals() {
    return valueMoreManuals[myIx];
  }
  /** get the values of more manuals for Econ ix
   * 
   * @param ix index of Econ
   * @return value of more manual from Econ ix
   */
  ARow getValueMoreManuals(int ix){
    return valueMoreManuals[ix];
  }
  
  double getSumValueMoreManuals(int ix){
    calcValueMoreManuals(ix);
    return getValueMoreManuals(ix).sum();
  }

  /**
   * get offer current term
   *
   * @return value of current term
   */
  int getTerm() {
    return term;
  }
  /**
   * get offer next term
   *
   * @return value of term term
   */
  int setNextTerm() {
    return --term;
  }

  /**
   * get the ordinal of the ship with this offer
   *
   * @return
   */
  int getShipOrdinal() {
    return tradedShipOrdinal;
  }

  /**
   * set ordinal of the ship trade with this planet this year
   *
   * @param o
   */
  void setShipOrdinal(int o) {
    tradedShipOrdinal = o;
  }

  /**
   * get the lightYears that the ship will travel
   *
   * @return lightYears of travel;
   */
  double getTravelYears() {
    return cn[0].calcLY(cn[0], cn[1]);
  }

  /**
   * return true if this Offer Ship is mine
   *
   * @param aCn my cn
   * @return
   */
  boolean isMyShip(Econ aCn) {
    return aCn == cn[1];
  }

  /**
   * match offer b against age of this
   *
   * @param b
   * @return -1 insert before this, 0 match keep this, +1 later loop +2
   * different ship name, +3 different planet name end of this planet
   */
  int match(Offer b) {

    if (year > b.year) {
      return 1;
    } // b older later than this
    if (year < b.year) {
      return -1;
    } // b earlier than this
    if (tradedShipOrdinal > b.tradedShipOrdinal) {
      return -1;
    } // b younger
    if (tradedShipOrdinal < b.tradedShipOrdinal) {
      return 1;
    } // b older
    if (!cnName[1].equals(b.cnName[1])) {
      return +2;
    }
    if (!cnName[0].equals(b.cnName[0])) {
      return +3;
    }
    return 0;
  }
  /** get hist reference of the my econ
   * this is the other hist before the flip
 * 
 * @return my
 */
  ArrayList<History> getHist() {
    return hists[myIx];
  }
/** get hist reference of the other econ
 * 
 * @return other hist reference
 */
  ArrayList<History> getOHist() {
    return hists[oIx];
  }
  /** get hist reference of the other econ
 * 
 * @return other hist reference
 */
  ArrayList<History> getOtherHist() {
    return hists[oIx];
  }

/** get SOS value of the other econ
 * 
 * @return other sos value
 */
  boolean getOtherSOS() {
    return sos[oIx];
  }
  /** get the name of the my econ
   * this is the other nome before the flip is done
 * 
 * @return return my name
 */
   String getName() {
    return cnName[myIx];
  }
/** get the name of the other econ
 * 
 * @return return other's name
 */
   String getOName() {
    return cnName[oIx];
  }
   /** get the name of the other econ
 * 
 * @return return other's name
 */
  String getOtherName() {
    return cnName[oIx];
  }

  /**
   * get only the strategic sum of goods sent from ship
   *
   * @return
   */
  double getSends() {
    return sends[1];
  }

  /**
   * get only the strategic sum of the goods received by the ship
   *
   * @return
   */
  double getReceipts() {
    return receipts[1];
  }

  /**
   * set the strategic sum of goods sent
   *
   * @param s
   */
  void setSends(double s
  ) {
    sends[myIx] = s;
  }

  /**
   * set the strategic sum of goods received
   *
   * @param r
   */
  void setReceipts(double r
  ) {
    receipts[myIx] = r;
  }

  /**
   * set new termination number
   *
   * @param term new value for term in offer
   */
  void setTerm(int term
  ) {
    this.term = term;
  }

  /**
   * set the existing ARows to the values of the parameter
   * if term %gt; barterStart-2 set initialGoods[term%2] to goods.copy()
   * always set lastGoods[term%2] to goods.copy()
   * assume barterStart is always an even number so %2 gives 0
   *
   * @param goods
   * @return reference to goods
   */
  A2Row set2Goods(A2Row goods
  ) {
    goodIx = myIx;
    lastGoods[term%2] = goods.copy();
    if(term > EM.barterStart -2){ // 18, 17
      initialGoods[term%2] = lastGoods[term%2];
    }
    if(E.debutNoLastGoods){
      if(term%2 == E.P && lastGoods[term%2] == null){
        throw new MyErr("in set2Goods lastGoods was null");
      }
    }
    // set the reference only
    return this.goods = goods;
  }

  /**
   * set the inital goods value for the planet
   *
   * @param goods
   * @return
   */
  A2Row set2InitialPlanetGoods(A2Row goods
  ) {

    return this.initialGoods[myIx].set(goods);
  }

  /**
   * at term = eM.barterStart set planet goods after goods have been limited by the ship, set the other
   * values also
   *
   * @param goods
   * @param sends
   * @param receipts
   * @param totalSends
   * @param totalReceipts
   * @param strategicFrac
   * @param strategicValue
   *
   * @return
   */
  A2Row set2SecondPlanetValues(A2Row goods, double sends, double receipts, double totalSends, double totalReceipts, double strategicFrac, double strategicValue) {
    secondReceipts[0] = receipts;
    secondSends[0] = sends;
    secondTotalSends[0] = totalSends;
    secondTotalReceipts[0] = totalReceipts;
    secondStrategicFrac[0] = strategicFrac;
    secondStrategicValue[0] = strategicValue;
    return this.secondGoods[0].set(goods);
  }

  /**
   * set values for planets to save final planet values, set goods for both
   * planet and ship
   *
   * @param goods
   * @param sends
   * @param receipts
   * @param totalSends
   * @param totalReceipts
   * @param strategicFrac
   * @param strategicValue
   * @return goods
   */
  A2Row set2Values(A2Row goods, double sends, double receipts, double totalSends, double totalReceipts, double strategicFrac, double strategicValue) {
 //   int myIx  = cn[0] == myEcon ? 0 : cn[1] == ec ? 1 : 2;
      this.receipts[myIx] = receipts;
      this.sends[myIx] = sends;
      this.totalSends[myIx] = totalSends;
      this.totalReceipts[myIx] = totalReceipts;
      this.strategicFrac[myIx] = strategicFrac;
      this.strategicValue[myIx] = strategicValue;
      set2Goods(goods);
      if(E.debutNoLastGoods){
        if (term % 2 == E.P && lastGoods[term % 2] == null) {
          throw new MyErr("in set2Values lastGoods was null");
        }
      }
      return lastGoods[term%2];
  }
  
  void set2Values(Econ ec, double startWorth, double startUnitsSum,double endWorth){
       int myIx  = cn[0] == ec ? 0 : cn[1] == ec ? 1 : 2;
       this.startWorth[myIx] = startWorth;
       this.startUnitsSum[myIx] = startUnitsSum;
       this.endWorth[myIx] = endWorth;
  }

  /**
   * get the reference to Goods
   *
   * @return reference goods
   */
  A2Row getGoods() {
    return goods; //references
  }

  /**
   * get the reference to prevGoods
   *
   * @return reference prevGoods
   */
  A2Row getPrevGoods() {
    return prevGoods[myIx];
  }

  double getCash() {
    return cashes[myIx];
  }

  double setCash(double val
  ) {
    return cashes[myIx] = val;
  }

  /**
   * Set myIx to 0 or 1 depending on which cn (economy) ec matches myIx is used
   * to store planet or ship values for each store
   *
   * @param ec reference to my Econ economy
   * @return
   */
  int setMyIx(Econ ec) {
   // Econ cn0 = cn[0];
 //  Econ cn1 = cn[1];
    boolean cn0b = cn[0] == ec;
    boolean cn1b = cn[1] == ec;
    prev2MyIx = prevMyIx;
    prevMyIx = myIx;
    myIx = cn[0] == ec ? 0 : cn[1] == ec ? 1 : 2;
    oIx = (1 + myIx) % 2; // set to the other
    E.myTest(myIx > 1, "ec for " + ec.getName() + " not in offer");
    return myIx;
  }

  /**
   * reset back to ship ix
   *
   * @return shipIx = 1;
   */
  int resetIx() {
    prevMyIx = 0;
    return myIx = 1;
  }

  /** get myIx, index into values my,other
   * 
   * @return return 0 or 1 the index of my Econ in cn
   */
  int getMyIx() {
    return myIx;
  }

  /** get the myIx before the last flip
   * 
   * @return myIx before last flip
   */
  int getPrevMyIx() { 
    return prevMyIx;
  }

  int getPrev2MyIx() {
    return prev2MyIx;
  }
/** get the clan of the other
 * 
 * @return clan number of the other
 */
  int getOClan() {
    return clans[oIx];
  }
  /** get the Econ of the other
   * 
   * @return other Econ
   */
  Econ getOEcon() {return cn[oIx]; }

  /** return my clan
   * 
   * @return my clan number
   */
  int getClan() {
    return clans[myIx];
  }

  /**
   * return reference to entryGoods, initial value of goods before this barter
   * makes changes
   *
   * @return entryGoods
   */
  A2Row getEntryGoods() {
    return initialGoods[myIx];
  }
  /**
   * reference to goods entering barter before flip
   *
   * @return
   */
  A2Row getLastGoods() {
    return lastGoods[myIx];
  }

  A2Row getprev2Goods() {
    return prev2Goods[myIx];
  }

  /**
   * flip the signs of values in the ARows of goods cash is also set to -cash
   * save entryGoods for getEntryGoods do nothing, if this is the same ec as the
   * last time.
   *
   * @Parm ec the Econ of the caller, flip only if not duplicate prev flip
   * @return this with flipped signs if ec different
   */
  Offer flipOffer(Econ ec
  ) {
    
    Econ prevEc = cn[myIx];
    setMyIx(ec);
    if (myIx == goodIx) {
      if (History.dl > 5) {
        StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
        StackTraceElement a1 = Thread.currentThread().getStackTrace()[2];
        StackTraceElement a2 = Thread.currentThread().getStackTrace()[3];

        ec.hist.add(new History("fp", History.loopMinorConditionals5, "dup flip=" + ec.name, "cn0=" + cn[0].name, "cn1=" + cn[1].name, a1.getFileName(), " " + a1.getLineNumber(), a2.getFileName(), " " + a2.getLineNumber()));
      }
      return this;
    }  // else
    prevGoodIx = goodIx;
    goodIx = myIx;
    A2Row[] prevNG = {new A2Row(ec),new A2Row(ec)};
    prev3Goods[oIx] = prev2Goods == null?prevNG[myIx]:prev2Goods[oIx] ==null?new A2Row(ec):prev2Goods[oIx];
    prev2Goods[oIx] = prevGoods == null?prevNG[oIx]:prevGoods[oIx] ==null?new A2Row(ec):prevGoods[oIx];
    prevGoods[oIx] = lastGoods[oIx];  // entry prev flip
    lastGoods[oIx] = goods.copy(); // entry before flip
    if(term > eM.barterStart-3)initialGoods[oIx] = lastGoods[oIx];
    goods.flip();    
    cash = -cash; // flip offer cash
    
    if (History.dl > 5) {
      StackTraceElement a0 = Thread.currentThread().getStackTrace()[1];
      StackTraceElement a1 = Thread.currentThread().getStackTrace()[2];
      StackTraceElement a2 = Thread.currentThread().getStackTrace()[3];

      ec.hist.add(new History("fp", History.loopMinorConditionals5, "flipped=" + ec.name, "cn0=" + cn[0].name, "cn1=" + cn[1].name, a0.getFileName(), " " + a0.getLineNumber(), a1.getFileName(), " " + a1.getLineNumber(), a2.getFileName(), " " + a2.getLineNumber()));
    }
    return this;  // an update but not new Offer
  }

  /**
   * set pointer to ship Cargo and Guests your trade amounts
   *
   * @param ashipC pointer to pointers to trade Cargo Guests
   */
  void setC(ARow cargo
  ) {
    
      if(E.debugOfferCargos && cargos[myIx] != cn[myIx].as.bals.A[2+1])
      {E.myTest(true,"c != bals.A[3], c %4.3g, bals c %4.3g, term=%d,n=%d,m=%d",cargos[myIx].sum(),cn[myIx].as.bals.A[1+2],term,cn[myIx].as.n,cn[myIx].as.m);
    }
       if(E.debugOfferCargos && cargos[myIx] != cn[myIx].as.cur.c.balance  ){
      E.myTest(true,"c != cBalance, c %4.3g cbal %4.3g, term=%d,n=%d,m=%d",cargos[myIx].sum(), cn[myIx].as.cur.c.balance.sum(),term,cn[myIx].as.n,cn[myIx].as.m);}
       if(E.debugOfferCargos && this.cargos[myIx] != cargo ){
      E.myTest(true,"c != c in, c %4.3g cin %4.3g, term=%d,n=%d,m=%d",this.cargos[myIx].sum(), cargo.sum(),term,cn[myIx].as.n,cn[myIx].as.m);}
    
      
    cargos[myIx] = cargo; // reference only
  }

  void setG(ARow guests, double[][] guestGrades
  ) {
     if(E.debugOfferCargos && this.guests[myIx] != guests ){
      throw new MyErr(String.format("g != g in, g %4.3g g in %4.3g, term=%d,n=%d,m=%d",this.guests[myIx].sum(), guests.sum(),term,cn[myIx].as.n,cn[myIx].as.m));}
     if(E.debugOfferCargos && this.guests[myIx] != cn[myIx].as.cur.g.balance  ){
      throw new MyErr(String.format("g != gBalance, g %4.3g gbal %4.3g, term=%d,n=%d,m=%d",this.guests[myIx].sum(), cn[myIx].as.cur.g.balance.sum(),term,cn[myIx].as.n,cn[myIx].as.m));}
      if(E.debugOfferCargos && this.guests[myIx] != cn[myIx].as.bals.A[2+3])
      {throw new MyErr(String.format("g%4.3g != bals.A[2+3]%4.3g, term=%d,n=%d,m=%d",this.guests[myIx].sum(),cn[myIx].as.bals.A[3+2],term,cn[myIx].as.n,cn[myIx].as.m));
    }
    this.guests[myIx] = guests;
    this.guestGrades[myIx] = guestGrades;
  }

  ARow getC() {
    if(E.debugOfferCargos && cargos[myIx] != cn[myIx].as.cur.c.balance  ){
      E.myTest(true,"c != cBalance, c %4.3g cbal %4.3g, term=%d,n=%d,m=%d",cargos[myIx].sum(), cn[myIx].as.cur.c.balance.sum(),term,cn[myIx].as.n,cn[myIx].as.m);}
      if(E.debugOfferCargos && cargos[myIx] != cn[myIx].as.bals.A[2+1])
      {E.myTest(true,"c != bals.A[3], c %4.3g, bals c %4.3g, term=%d,n=%d,m=%d",cargos[myIx].sum(),cn[myIx].as.bals.A[1+2],term,cn[myIx].as.n,cn[myIx].as.m);
    }
    return cargos[myIx];
  }

  ARow getG() {
     if(E.debugOfferCargos && this.guests[myIx] != cn[myIx].as.cur.g.balance  ){
      E.myTest(true,"g != gBalance, g %4.3g gbal %4.3g, term=%d,n=%d,m=%d",this.guests[myIx].sum(), cn[myIx].as.cur.g.balance.sum(),term,cn[myIx].as.n,cn[myIx].as.m);}
      if(E.debugOfferCargos && this.guests[myIx] != cn[myIx].as.bals.A[2+3])
      {E.myTest(true,"g != bals.A[2+3], g %4.3g, bals g %4.3g, term=%d,n=%d,m=%d",this.guests[myIx].sum(),cn[myIx].as.bals.A[3+2],term,cn[myIx].as.n,cn[myIx].as.m);
    }
    return guests[myIx];
  }
  
   /** sum all the elements of two domensional double array
   * 
   * @param aray array of double[][]
   * @return sum of all non null elements of aray
   */
  static public double twoSum(double[][] aray){
    double sum = 0.0;
    int mMax = aray.length;
    for(int m=0;m < mMax;m++){
      int nMax = aray[m] == null?0:aray[m].length;
      for(int n=0;n<nMax;n++){
        sum += aray[m][n];
      }//n
    }//m
    return sum;
  }

  double[][] getGrades() {
    if(E.debugOfferCargos && this.guestGrades[myIx] != cn[myIx].getGuestGrades() ){
      throw new MyErr(String.format("g.grades%7.3g != offer grades%7.3g , term=%d,n=%d,m=%d",twoSum(guestGrades[myIx]), twoSum(cn[myIx].getGuestGrades()),term,cn[myIx].as.n,cn[myIx].as.m));}
    return guestGrades[myIx];
  }

  /** accept offer
   * 
   * @param ec reference to the calling Econ
   */
  void accepted(Econ ec
  ) {
    // assume myIx is set to the current econ
    //   lastTerm = term;
    term = 0;  // accepted value

    flipOffer(cn[1]); // flip to ship, no flip if ship
    // now save ship name in planet assets
    ARow goodCargo = goods.getARow(0); // ship cargo
    ARow goodGuests = goods.getARow(E.lsecs);
    if (History.dl > 3) {
      hists[myIx].add(new History("tj", History.loopMinorConditionals5, cnName[myIx] + "ntr accepted" + ">>>>>>>>>>", "myIx" + 0, "c " + (cargos[myIx] == cn[myIx].as.cur.c.balance ? "c == cargos" : " c != cargos"), "<<<<<<<<<<<<<"));
      hists[myIx].add(new History("tj", History.loopMinorConditionals5, cnName[oIx] + "ntr accepted" + ">>>>>>>>>>", "oIx" + 0, "c " + (cargos[oIx] == cn[oIx].as.cur.c.balance ? "== cargos" : " != cargos"), "<<<<<<<<<<<<<"));
      hists[oIx].add(new History("tj", History.loopMinorConditionals5, cnName[myIx] + "ntr accepted" + ">>>>>>>>>>", "myIx" + 0, "g " + (guests[myIx] == cn[myIx].as.cur.g.balance ? "== guests" : " not guests"), "<<<<<<<<<<<<<"));
      hists[oIx].add(new History("tj", History.loopMinorConditionals5, cnName[oIx] + "ntr accepted" + ">>>>>>>>>>", "oIx" + 0, "g " + (guests[oIx] == cn[oIx].as.cur.g.balance ? "== guests" : " !!= guests"), "<<<<<<<<<<<<<"));

      hists[oIx].add(new History(5, cnName[oIx] + " cofr t" + term, goodCargo));
      hists[myIx].add(new History(5, cnName[myIx] + " cofr t" + term, goodCargo));
      hists[oIx].add(new History(5, cnName[oIx] + " gofr", goodGuests));
      hists[myIx].add(new History(5, cnName[myIx] + " gofr", goodGuests));

      hists[oIx].add(new History(5, cnName[1] + " old s c", cargos[1]));
      hists[myIx].add(new History(5, cnName[0] + " old s cargo", cargos[1]));
      hists[oIx].add(new History(5, cnName[1] + " old s guests", guests[1]));
      hists[myIx].add(new History(5, cnName[0] + " old s guests", guests[1]));
      hists[oIx].add(new History(5, cnName[1] + " old o cargo", cargos[0]));
      hists[myIx].add(new History(5, cnName[0] + " old o cargo", cargos[0]));
      hists[oIx].add(new History(5, cnName[1] + " old o guests", guests[0]));
      hists[myIx].add(new History(5, cnName[0] + " old o guests", guests[0]));
      hists[oIx].add(new History(5, cnName[1] + " old o manuals", manuals[0]));
      hists[myIx].add(new History(5, cnName[0] + " old o manuals", manuals[0]));
      hists[oIx].add(new History(5, cnName[1] + " old s manuals", manuals[1]));
      hists[myIx].add(new History(5, cnName[0] + " old s manuals", manuals[1]));
      hists[oIx].add(new History(5, cnName[1] + " old o moreManuals", moreManuals[0]));
      hists[myIx].add(new History(5, cnName[0] + " old o moreManuals", moreManuals[0]));
      hists[oIx].add(new History(5, cnName[1] + " old s moreManuals", moreManuals[1]));
      hists[myIx].add(new History(5, cnName[0] + " old s moreManuals", moreManuals[1]));
    }

    double t;
    // good is oriented to myIx  either P or S depending on the accept
    for (int m : E.alsecs) {
      double cbid = goodCargo.get(m); // ship values myIx
      double gbid = goodGuests.get(m);
    // fix some possible fractional errors deriving cbid, gbid
      if (cbid < E.nzero) { // a request by myIx ship, offer by oix planet
        t= -cbid - cargos[oIx].get(m);  // -ship cargo request - planet cargo allow to transfer
        cbid = (t > 0. && t < .0001)? -cargos[oIx].get(m): cbid; // rounding error
        E.myTest(cargos[oIx].get(m) < -cbid, "%7.2f ocargos[oIx=%1d].get(m=%1d) < cbid =%7.2f", cargos[oIx].get(m), oIx, m, -cbid);
        cargos[myIx].addCargoValue(m, -cbid, cargos[oIx]); // add to my from o
      }
      else if (cbid > E.pzero) { // my offer, o request
        t= cbid- cargos[myIx].get(m); 
        cbid = (t > 0. && t < .0001)? cargos[myIx].get(m): cbid;
        E.myTest(cargos[myIx].get(m) < cbid, "%7.2f=  mycargos[myIx=%2d].get(m=%1d) < cbid=%7.2f", cargos[myIx].get(m), myIx, m, cbid);
        cargos[oIx].addCargoValue(m, cbid, cargos[myIx]);//add to o from my
      }
      if (gbid < E.nzero) {
         t= -gbid- guests[oIx].get(m); 
         gbid = (t > 0. && t < .0001)? -guests[oIx].get(m): gbid;
        E.myTest(guests[oIx].get(m) < -gbid, "%5.2f oguests[oIx=%2d].get(m=%1d) < %5.2f=gbid", guests[oIx].get(m), oIx, m, -gbid);
        // from o to my guests
        guests[myIx].addGuestsValue(m, -gbid, guestGrades[myIx][m], guests[oIx], guestGrades[oIx][m], 0);
      }
      else if (gbid > E.pzero) {
         t= gbid- guests[myIx].get(m); 
        gbid = (t > 0. && t < .0001)? guests[myIx].get(m): gbid;
        E.myTest(guests[myIx].get(m) <gbid, "%5.2f guests[myIx=%2d].get(m=%1d]) < %5.2f=gbid", guests[myIx].get(m), myIx, m, gbid);
        // to o from my guests
        guests[oIx].addGuestsValue(m, gbid, guestGrades[oIx][m], guests[myIx], guestGrades[myIx][m], 0);
      }
      manuals[0].add(m, moreManuals[0].get(m));
      manuals[1].add(m, moreManuals[1].get(m));
    }
    if (History.dl > 3) {
      hists[1].add(new History(5, cnName[1] + " new s cargo", cargos[1]));
      hists[0].add(new History(5, cnName[1] + " new s cargo", cargos[1]));
      hists[1].add(new History(5, cnName[1] + " new s guests", guests[1]));
      hists[0].add(new History(5, cnName[1] + " new s guests", guests[1]));
      hists[1].add(new History(5, cnName[1] + " new o cargo", cargos[0]));
      hists[0].add(new History(5, cnName[1] + " new o cargo", cargos[0]));
      hists[1].add(new History(5, cnName[1] + " new o guests", guests[0]));
      hists[0].add(new History(5, cnName[1] + " new o guests", guests[0]));
      hists[1].add(new History(5, cnName[1] + " new o manuals", manuals[0]));
      hists[0].add(new History(5, cnName[1] + " new o manuals", manuals[0]));
      hists[1].add(new History(5, cnName[1] + " new s manuals", manuals[1]));
      hists[0].add(new History(5, cnName[1] + " new s manuals", manuals[1]));
    }
    // is cash 0 nothing happens, if cash neg ship==1 adds, else
    // cash > 0, other gets cash, ship pays cash
    double mcash = (myIx == 1 ? cash : -cash); //ship cash only if myIx == 1 ship
    double pcash = cn[0].addCash(mcash);
    double scash = cn[1].addCash(-mcash);
    hists[0].add(new History(5, "accptd cash term=" + term, "ship offer=", df(mcash), "o cash=", df(pcash), "scash=", df(scash)));
    hists[1].add(new History(5, "accptd cash term=" + term, "ship offer=", df(mcash), "o cash=", df(pcash), "scash=", df(scash)));

  }

  /**
   * determine a value for this planet potential trade as part of the planet
   * search. use the current trade values for this ship, with the travel values
   * recalculated after cargo and guests have been set.
   *
   * @param myStratValues
   * @param myGoods
   * @param myCommonKnowledge
   * @param myNewKnowledge
   * @param myManuals
   * @return nominal values of offers - requests + value of manuals at trades
   */
  double valueInitPlanetOffer(A2Row myStratValues, A2Row myGoods, A2Row myCommonKnowledge, A2Row myNewKnowledge, A2Row myManuals) {
    double hSum = 0., lSum = 0., mSum = 0.;
    double nMan = 0.;
    double nSVal = 0.;
    double good = 0., commonDif1 = 0., manualDif = 0., moreManuals = 0., manualValue = 0.;
    double nominalValue;
    int n;

    // done by ship on planet
    for (int m = 0; m < searchL; m++) {
      n = myStratValues.maxIx(m);  // sguo top requests  negatives
      nSVal = myStratValues.get(n);
      // what could my request be from this planet
      good = Math.min(initialGoods[0].get(n), -myGoods.get(n));
      good = good > E.pzero ? good : 0.;
      hSum += good * eM.nominalRSWealth[(n / E.lsecs)][E.S];
      if (good > E.pzero) {// only add manuals if a trade done
        commonDif1 = myCommonKnowledge.get(n) - commonKnowledge[E.P].get(n);
        manualDif = commonDif1 > 0. ? commonDif1 * E.commonKnowledgeDifTradeManualFrac : 0.;
        moreManuals = myNewKnowledge.get(n) * E.newKnowledgeTradeManualFrac
                + myManuals.get(n) * E.manualTradeManualFrac
                + myCommonKnowledge.get(n) * E.commonKnowledgeTradeManualFrac
                + manualDif;
        manualValue = moreManuals * eM.nominalWealthPerTradeManual[E.S];
        mSum += moreManuals * eM.nominalWealthPerTradeManual[E.S];
      }
      n = myStratValues.minIx(m);   // my top offers
      nSVal = myStratValues.get(n);
      // see if planet wants anything of my offers
      good = Math.min(-initialGoods[0].get(n), -myGoods.get(n));
      good = good > E.pzero ? good : 0.;
      lSum += good * eM.nominalRSWealth[(n / E.lsecs)][E.S];
      if (good > E.pzero) { // only if a trade
        commonDif1 = myCommonKnowledge.get(n) - commonKnowledge[E.P].get(n);
        manualDif = commonDif1 > 0. ? commonDif1 * E.commonKnowledgeDifTradeManualFrac : 0.;
        moreManuals = myNewKnowledge.get(n) * E.newKnowledgeTradeManualFrac
                + myManuals.get(n) * E.manualTradeManualFrac
                + myCommonKnowledge.get(n) * E.commonKnowledgeTradeManualFrac
                + manualDif;
        manualValue = moreManuals * eM.nominalWealthPerTradeManual[E.S];
        mSum += moreManuals * eM.nominalWealthPerTradeManual[E.S];
      }
    }
    return ((lSum - hSum) > E.pzero ? (lSum - hSum + mSum) : 0);

  }

}
