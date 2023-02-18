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
 *
 * @author Albert Steiner
 * This class holds references to files in Assets which are processed by Assets.CashFlow.SubAsset
 * When aStartCashFlow initializes CashFlow it creates the 4 subassets and puts copies of
 * the references in ABalRows bal back into each SubAsset where they are used
 * When the instance of CashFlow is destroyed, the SubAssets are destroyed but the files
 * remain as part of ABalRows bal
 */
public class ABalRows extends A6Rowa {

  double[][][] aGrades;
  // Assets these are indexes for rows in bal
  static final int TCOST = A6Row.tcost;
  static final int TBAL = A6Row.tbal;
  static final int RIX = 0, CIX = 1, SIX = 2, GIX = 3, SGIX = -1, RCIX = -2;
  // static int rcIx = -2;
  static final int RCIX2 = -2;
  static final int BALS = 0;
  static int BALANCESIX = E.lsums; // L4
  static int lsums = E.lsums;
  static int lsubs = E.lsubs;
  static final int MCOSTSIX = BALANCESIX + lsubs; // L2 mtcosts are travel costs of ship
  static final int TCOSTSIX = MCOSTSIX + lsums;  //L2  6
  static final int GROWTHSIX = TCOSTSIX + lsums; //L4  8
  static int bonusYearsIx = GROWTHSIX + lsubs; //L4  12
  static final int BONUSYEARSIX = GROWTHSIX + lsubs; //16 L4
  static int bonusUnitsIx = BONUSYEARSIX + lsubs;//20 L4
  static final int BONUSUNITSIX = BONUSYEARSIX + lsubs;//24 L4
  static int CUMULATIVEUNITBONUSIX = BONUSUNITSIX+lsubs;//26 L4
  static final int CUMULATIVEUNITDECAYIX = CUMULATIVEUNITBONUSIX + lsubs; //32 L4
  static final int RAWUNITGROWTHSIX = CUMULATIVEUNITDECAYIX + lsubs; //36
  static final int BONUSYEARLYUNITGROWTHSIX = RAWUNITGROWTHSIX + lsubs; //40
  static final int RAWGROWTHSIX = BONUSYEARLYUNITGROWTHSIX + lsubs; //44 
  static final int TRADEDGROWTHSIX = RAWGROWTHSIX + lsubs; //48
  static final int SWAPPEDGROWTHSIX = TRADEDGROWTHSIX + lsubs; //52
  static final int COMMONKNOWLEDGEIX = SWAPPEDGROWTHSIX + lsubs; //56
  static final int NEWKNOWLEDGEIX = COMMONKNOWLEDGEIX + 1; //60
  static final int MANUALSIX = NEWKNOWLEDGEIX + 1; //61
  static final int BALSLENGTH = MANUALSIX + 2; //63
  static int balancesSums[] = {BALANCESIX + RCIX, BALANCESIX + SGIX};
  static int balancesSubSum1[] = {BALANCESIX + RIX, BALANCESIX + CIX};
  static int balancesSubSum2[] = {BALANCESIX + SIX, BALANCESIX + GIX};
  static int balancesSubSums[][] = {balancesSubSum1, balancesSubSum2};
  // end of index values for bals
  static final String[] titls = {" bals rc ", " bals sg ", " bals r ", " bals c ", " bals s", " bals g ", "MTCOSTS r", "MTCOSTS s", "growths r ", " growths c ", " growths s ", " growths g ", " bonusYears r ", " bonusYears c ", " bonusYears s ", " bonusYears g ", " bonusUnits r ", " bonusUnits c ", " bonusUnits s ", " bonusUnits g", " cumDecay r ", " cumDecay c ", " cumDecay s ", " cumDecay g","rawUnitsGrowth r", "rawUnitsGrowth c", "rawUnitsGrowth s", "rawUnitsGrowth g","rawGrowth r","rawGrowth c","rawGrowth s","rawGrowth g","tradedGrowth r","tradedGrowth c","tradedGrowth s","tradedGrowthg", "swappedGrowth r", "swappedGrowth c", "swappedGrowth s", "swappedGrowth g","commonKnowledge","newKnowledge","manuals"};

  /**
   * principal constructor of ABalRows a set of rows that are balances
   *
   * @param n count of rows in object
   * @param t flag tbal = balances, tcost = consts
   * @param h level for any send to hist methods
   * @param tit title for any send to hist methods
   */
  ABalRows(Econ ec,int n, int t, int h, String tit) {
    super(ec,n, t, h, tit);
//    System.out.println("instantiate ABalRows A.length=" + A.length + ", lA=" + lA + ", n=" + n + ", titl =" + titl);
    //dResums = Assets.balancesSums;
    //   mResum1 = Assets.balancesSubSum1;
    //  mResum2 = Assets.balancesSubSum2;
    // mResum = Assets.balancesSubSums;
  }

  /**
   * constructor assuming 15 ARows, balance, History.informationMajor8,bals
   *
   */
  ABalRows(Econ ec) {
    super(ec,BALSLENGTH, tbal, History.informationMajor8, "bals");
  }

  /**
   * constructor assuming 15ARows, balances, level=aLev, title=bals
   *
   * @param aLev level for any send to hist methods
   */
  ABalRows(Econ ec,int aLev) {
    super(ec,BALSLENGTH, tbal, aLev, "bals");
  }

  /**
   * copy first 6 rows of ABalRows object to an A6Row object , b is a new object
   * lev,titl,balances,costs,blev are all copied as well as A[] values
   *
   * @param level for this A6Row
   *
   * @return new object copy new references for the values
   */
  public A6Row copy6(int lev, String atitl) {
    A6Row rtn = new A6Row(ec);
    rtn.lev = lev;
    rtn.titl = atitl;
    rtn.balances = balances;
    rtn.costs = costs;
    rtn.blev = blev;
    // int len = A.length;
    for (int m = 0; m < 6; m++) {
      for (int n : E.alsecs) {
        rtn.set(m, n, get(m, n));
      }
    }
    return rtn;
  }

  /**
   * copy the values from one ABalRows to another but do not change any
   * references. This is used for swap redo and must not change the ar
   * references
   *
   * @param prev a new value for HSwaps a previous value from HSwaps
   * @return the revised values of this with no references changed
   */
  public ABalRows copyValues(ABalRows prev) {
    for (int m = 0; m < BALSLENGTH; m++) {

      for (int n = 0; n < E.LSECS; n++) {
        A[m].set(n, prev.A[m].get(n));
      }
    }// end m
    // ABalRows always has these grades define with some values
    for (int i = 2; i < 4; i++) {
      for (int m = 0; m < LSECS; m++) {
        for (int n = 0; n < LGRADES; n++) {
          gradesA[i][m][n] = prev.gradesA[i][m][n];
        }
      }
    }// end i
    return this;
  }

  /** get the balances A6Row from bals, including copy grades
   * only move reverence for the A[2] thru A[4]
   * 
   * @param lev  level of the copied A6Row
   * @param atitl title of the A6Row
   * @return the A6Row
   */
  public A6Row getBalances(int lev, String atitl) {
    A6Row rtn = new A6Row(ec,tbal, lev, atitl);
    for (int i = 0; i < 6; i++) {
      rtn.A[i] = A[i];
      rtn.A[i].addCnt();
    }
    return rtn;
  }
  
  /** copy the balances A6Row from bals,
   * grades must be copied separately
   * 
   * @param lev  level of the copied A6Row
   * @param atitl title of the A6Row
   * @return the A6Row all values copied not references moved
   */
   public A6Row copyBalances(int lev, String atitl) {
    A6Row rtn = new A6Row(ec,tbal, lev, atitl);
    for (int m = 2; m < 6; m++) {
      rtn.A[m] = A[m].copy();
      rtn.A[m].addCnt();
    }// end m
    return rtn;
  }

  /**
   * create an A6Row using references to 4 rows starting at iX this is used
   * primarily by other methods
   *
   * @param iX index of the start of rows in bals
   * @param lev level of the new A6Row
   * @param titl title of the new A6Row
   * @return A6Row row0 = add rows iX,iX+1, row1=add rows iX+2,ix+3 followed by
   * references to the rows iX thru iX+3
   */
  public A6Row use4(int iX, int lev, String titl) {
    A6Row rtn = new A6Row(ec,lev, titl);
    resum(0);
    resum(1);
    for (int m : I03) {
      rtn.A[m + 2] = A[iX + m].copy();
      rtn.aCnt[m]++;
    }
    rtn.A[0] = new ARow(ec).setAdd(rtn.A[2] , rtn.A[3]);
    rtn.A[1] = new ARow(ec).setAdd(rtn.A[4] , rtn.A[5]);
    return rtn;
  }

  /**
   * zero m & t costs internally
   *
   */
  void zeroMtCosts() {
    getRow(MCOSTSIX + 0).zero();
    getRow(MCOSTSIX + 1).zero();
    getRow(TCOSTSIX + 0).zero();
    getRow(TCOSTSIX + 1).zero();
  }

  /**
   * get an A10Row for the R & S costs for travel
   *
   * @return A10Row for Travel
   */
  A10Row getTrows() {
    A10Row rtn = new A10Row(ec,7, "travelC10");
    rtn.A[2] = A[TCOSTSIX];
    rtn.A[6] = A[TCOSTSIX + 1];
    double t;
    for (int j = 0; j < LSECS; j++) {
      t = rtn.A[6].get(j);
      E.myTestDouble(t, "t", "in getTrows get(6,%d) = %s", j, String.valueOf(t));
    }
    return rtn;
  }
  
   /** set ABalRows bal for the saved travel costs
   * 
   * @param tcosts10 Copy of current travelcosts
   * @return new copy of bal
   */
  ABalRows setTRows(A10Row tcosts10){
      A[TCOSTSIX] = tcosts10.A[0];
      A[TCOSTSIX + 1] = tcosts10.A[1];
      
      return this;
  }

  /**
   * get an A10Row for the R & S costs for maintenance
   *
   * @return A10Row for Travel
   */
  A10Row getMrows() {
    A10Row rtn = new A10Row(ec,7, "maintC10");
    rtn.A[2] = A[MCOSTSIX];
    rtn.A[6] = A[MCOSTSIX + 1];
    return rtn;
  }
  
  /** set ABalRows bal for the saved maintenance costs
   * 
   * @param mcosts10 Copy of current maintenance costs
   * @return new copy of bal
   */
  ABalRows setMRows(A10Row mcosts10){
      A[MCOSTSIX] = mcosts10.A[2];
      A[MCOSTSIX + 1] = mcosts10.A[6];
      return this;
  }

  /**
   * an internal routine to send a set of titled rows to hist
   *
   * @param iXa index of the starting row
   * @param iXb index of the final row
   * @param blev highest alev that will be listed
   * @param apre prefix of the listed lines
   * @param alev level of the listed lines
   */
  private void sendHist(int iXa, int iXb, int blev, String apre, int alev) {
    if (alev <= blev) {
      for (int m = iXa; m <= iXb; m++) {
        resum(m);
        hist.add(new History(apre, alev, titls[m], A[m]));
      }
    }
  }

  /**
   * set the balances portion of bals to the supplied balances
   *
   * @param lev level of the new A6Row
   * @param titl titl of the new A6Row
   * @return A6Row of the first 6 rows of bals
   */
  void setBalances(A6Row balances) {
    for (int m : I05) {
      for (int n : E.ASECS) {
        A[m].set(n,balances.A[m].get(n));
      }
    }
  }

  /**
   * make a copy of the balances part of ABalRows
   *
   * @return balances copy not the reference
   */
  A6Row copyBalances() {
    A6Row rtn = new A6Row(ec,lev, titl);
    for (int m : I05) {
      for (int n : E.ASECS) {
        rtn.A[m].set(n,A[m].get(n));
      }
    }
    return rtn;
  }

  /**
   * list the balances rows in bals
   *
   * @param blev highest level that will be listed
   * @param apre prefix of listed rows
   * @param alev level of listed rows
   */
  void listBalances(int blev, String apre, int alev) {
    if (alev <= blev) {

      sendHist(BALANCESIX + RCIX, BALANCESIX + GIX, blev, apre, alev);
    }
  }

  /**
   * get the Growths references of bals
   *
   * @param lev level of the new A6Row
   * @param titl titl of the new A6Row
   * @return a mew A6Row using the growths section of bals
   */
  A6Row getGrowths(int lev, String titl) {
    return use4(GROWTHSIX, lev, titl);
  }

  /**
   * get reference to a single ARow corresponding to the index
   *
   * @param m index of the growths ARow s
   * @return growth ARow for index m
   */
  ARow getGrowthsRow(int m) {
    return A[GROWTHSIX + m];
  }

  /**
   * list the growths rows in bals
   *
   * @param blev highest level that will be listed
   * @param apre prefix of listed rows
   * @param alev level of listed rows
   */
  void listGrowths(int blev, String apre, int alev) {
    sendHist(GROWTHSIX, GROWTHSIX + 3, blev, apre, alev);
  }
  
  /**
   * get the rawUnitGrowths references of bals
   *
   * @param lev level of the new A6Row
   * @param titl titl of the new A6Row
   * @return a mew A6Row using the growths section of bals
   */
  A6Row getRawUnitGrowths(int lev, String titl) {
    return use4(RAWUNITGROWTHSIX, lev, titl);
  }

  /**
   * get reference to a single ARow corresponding to RAWUNITGROWTHSIX
   *
   * @param m index of the growths ARow s
   * @return growth ARow for index m
   */
  ARow getRawUnitGrowthsRow(int m) {
    return A[RAWUNITGROWTHSIX + m];
  }

  /**
   * list the rawUnitGrowths rows in bals
   *
   * @param blev highest level that will be listed
   * @param apre prefix of listed rows
   * @param alev level of listed rows
   */
  void listRawUnitGrowths(int blev, String apre, int alev) {
    sendHist(RAWUNITGROWTHSIX, RAWUNITGROWTHSIX + 3, blev, apre, alev);
  }

  /**
   * get the Bonus Years references of bals
   *
   * @param lev level of the new A6Row
   * @param titl titl of the new A6Row
   * @return the selected bals references in an A6Row
   */
  A6Row getBonusYears(int lev, String titl) {
    return use4(BONUSYEARSIX, lev, titl);
  }

  /**
   * get reference to a single ARow corresponding to the index
   *
   * @param m index of the BonusYears ARow s
   * @return growth ARow for index m
   */
  ARow getBonusYearsRow(int m) {
    return A[BONUSYEARSIX + m];
  }

  /**
   * list the bonus years rows in bals
   *
   * @param blev highest level that will be listed
   * @param apre prefix of listed rows
   * @param alev level of listed rows
   */
  void listBonusYears(int blev, String apre, int alev) {
    sendHist(BONUSYEARSIX, BONUSYEARSIX + 3, blev, apre, alev);
  }

  /**
   * get the Bonus Units references of bals
   *
   * @param lev level of the new A6Row
   * @param titl titl of the new A6Row
   * @return the selected bals references in an A6Row
   */
  A6Row getBonusUnits(int lev, String titl) {
    return use4(BONUSUNITSIX, lev, titl);
  }

  /**
   * get reference to a single ARow corresponding to the index
   *
   * @param m index of the BonusUnits ARow s
   * @return growth ARow for index m
   */
  ARow getBonusUnitsRow(int m) {
    return A[BONUSUNITSIX + m];
  }

  /**
   * list the bonus units rows in bals
   *
   * @param blev highest level that will be listed
   * @param apre prefix of listed rows
   * @param alev level of listed rows
   */
  void listBonusUnits(int blev, String apre, int alev) {
    sendHist(BONUSUNITSIX, BONUSUNITSIX + 3, blev, apre, alev);
  }

  /**
   * get the Bonus Units references of bals
   *
   * @param lev level of the new A6Row
   * @param titl titl of the new A6Row
   * @return the selected bals references in an A6Row
   */
  A6Row getCumDecay(int lev, String titl) {
    return use4(CUMULATIVEUNITDECAYIX, lev, titl);
  }

  /**
   * list the cumulative decay rows in bals
   *
   * @param blev highest level that will be listed
   * @param apre prefix of listed rows
   * @param alev level of listed rows
   */
  void listCumDecay(int blev, String apre, int alev) {
    sendHist(CUMULATIVEUNITDECAYIX, CUMULATIVEUNITDECAYIX + 3, blev, apre, alev);
  }

}
