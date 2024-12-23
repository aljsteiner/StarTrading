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
 *
 * ABalRows is the between year holder of assets that will be needed for
 * processing each year. This is an array of ARow's and many of the named
 * memories have separate ARow's for each of the 4 SubAssets. Memory is saved
 * during each year process by mulling the CashFlow, SubAsset and Trqde
 * subClasse. The essential ARow's are kept and organized in ABalRows. Many of
 * the processes in A6Row class also appear in a similar for here. There are a
 * number of naming conventions: "useRef..." implies that ARow references are
 * preserved "set..." implies new reference but the values are saved "use..."
 * implies using ABalRow ARow instances in a new A6Row or A10Row
 *
 *
 */
public class ABalRows extends A6Rowa {

  double[][][] aGrades;
  // Assets these are indexes for rows in bal
  static final int TCOST = A6Row.tcost;
  static final int TBAL = A6Row.tbal;
  static final int RIX = 0, CIX = 1, SIX = 2, GIX = 3, SGIX = -1, RCIX = -2;
  // static int rcIx = -2;
  static final Integer lock=0;
  static final int RCIX2 = -2;
  static final int BALS = 0;
  //static final int BALANCESIX = E.lsums; // L4
  static final int lsums = E.lsums;
  static final int LSUMS = E.lsums;
  static final int lsubs = E.lsubs;
  static final int LSUBS = E.lsubs;
  static final int A01[] = {0, 1};
  static final int A02[] = {0, 2};
  static final int A03[] = {0, 1, 2, 3};
  static int balz = 0;
  //static String[] titls 
  static final int BALANCESIX = balz += LSUMS; //2
  static final int INITIALASSETSBALANCESIX = balz += LSUBS; //6
  static final int STARTYEARBALANCESIX = balz += LSUBS; //10
  static final int MANUALSIX = balz += LSUBS; //14 space for balancesIx
  static final int COMMONKNOWLEDGEIX = balz += 1; //15
  static final int NEWKNOWLEDGEIX = balz += 1; //16
  static final int INITIALCOMMONKNOWLEDGEIX = balz += 1; //15
  static final int INITIALNEWKNOWLEDGEIX = balz += 1; //16
  static final int MCOSTSIX = balz += 1; // space for NewKnowledge
  static final int TCOSTSIX = balz += LSUBS;  //10
  static final int GROWTHSEFFIX = balz += LSUBS; //AFTER SWAPS
  static final int PREVGROWTHSIX = balz += LSUBS; // copy of last year growth
  static final int SECTORRAWGROWTHSIX = balz += LSUBS; //
  static final int PREVWORTHSIX = balz += LSUBS; //

  static final int CUMULATIVESECTORBONUSIX = balz += LSUBS;//32 L4 bonus applied to growth
  static final int CUMULATIVEBONUSWHORTHIX = balz += LSUBS; //limited bonus worth increase
  static final int BONUSYEARSIX = balz += LSUBS; //16 L4 years bonus wor
  static final int BONUSUNITSIX = balz += LSUBS;// current bonus,before limits
  static final int REPRECIATIONIX = balz += LSUBS;  //cumulative repreciation
  static final int DEPRECIATIONIX = balz += LSUBS;  // cumulative depreciation
  static final int PRECIATIONIX = balz += LSUBS;  // cumulative depreciation -repreciation
  static final int SURPLUSCUMULATIVEUNITDEPRECIATIONIX = balz += LSUBS; // removed too much
  static final int RAWUNITGROWTHSIX = balz += LSUBS; //
  static final int RAWPRIORITYUNITGROWTHSIX = balz += LSUBS; //
  static final int RAWYEARLYUNITGROWTHSIX = balz += LSUBS;
  static final int MAXRAWYEARLYUNITGROWTHSIX = balz += LSUBS;
  static final int STARTYEARENDNULLIX = balz + LSUBS; // Assets.CashFlow.yearEnd zeros up to BALSLENGTH
  // the following rows can be nulled after yearEnd. but kept between yearStart and yearEnd
  // they are zerod when restored at Assets.CashFlow.aStartCashFlow
  static final int NEWDEPRECIATIONIX = balz += LSUBS; //this year depreciation from prevgrowth
  static final int NEWREPRECIATIONIX = balz += LSUBS; //this year depreciation from prevgrowth
  static final int YEARLYBONUSSUMGROWTHVALIX = balz += LSUBS; //aStartCashFlow zero fills
  static final int GROWTHWORTHSIX = balz += LSUBS; //
  static final int CURWORTHSIX = balz += LSUBS; //16 L4 WORTH VALUES
  static final int INITIALASSETSWORTHSIX = balz += LSUBS; //16  WORTH VALUES
  static final int INVMEFFICIENCYIX = balz += LSUBS;
  static final int INVGEFFICIENCYIX = balz += LSUBS;
  static final int RAWGROWTHSIX = balz += LSUBS; // rawGrowth in calcGrowth
  static final int SWAPPEDGROWTHSIX = balz += LSUBS; //?? growths after swap
  static final int SWAPPEDRCSGSUMIX = balz += 1; //?? RCSG SUM after swap
  static final int POORHEALTHEFFECTIX = balz += LSUBS; //FIRST SEC OF ROW ONLY
  static final int POORKNOWLEDGEEFFECTIX = balz += 1; //FIRST SED OF ROW ONLY
  static final int YEARINCRWORTHSIX = balz += 1; //lsub
  static final int MTGCOSTSWORTHSIX = balz += LSUBS; //
  static final int COSTWORTHSIX = balz += LSUBS; //
  static final int MTGCOSTSIX = balz = balz += LSUBS; //
  static final int STARTCURENDNULLIX = balz + LSUBS; // Assets.CashFlow null  up to BALSLENGTH
  static final int TRADEDGROWTHSIX = balz += LSUBS; //
  static final int MTCOSTS2IX = balz += LSUBS; //
  static final int MTECCOSTS2IX = balz += 2; //
  static final int MTGCOSTS2IX = balz += 2; //
  static final int RQFERTMINS2IX = balz += 2; //
  static final int MTGFERTFRAC2IX = balz += 2; //
  static final int RAWFERTILITIES2IX = balz += 2; //RAWPROSPECTS2
  static final int RAWPROSPECTS2IX = balz += 2; //RAWPROSPECTS2
  static final int REQFERTMINFRAC2IX = balz += 2; //
  static final int RQMFERTFRAC2IX = balz += 2; //
  static final int RQGFERTFRAC2IX = balz += 2; //
  static final int FERTILITYGROWTHSIX = balz += 2; //4
  static final int FERTILITYGROWTHCOSTSIX = balz += LSUBS; //
  static final int GROWTHCOSTSIX = FERTILITYGROWTHCOSTSIX; //
  static final int GROWTHCOSTSYIX = balz += LSUBS; //
  static final int GROWTHCOSTSYYIX = balz += LSUBS; // a later calculation
  static final int STARETYEARWORTHSIX = balz += LSUBS; // WORTH VALUES
 static final int GROWTHS1IX = balz += LSUBS; //represent succesive values calculating growth
static final int GROWTHS2IX = balz += LSUBS; //
  static final int GROWTHS3IX = balz += LSUBS; //
  static final int GROWTHS4IX = balz += LSUBS; //
  static final int GROWTHS5IX = balz += LSUBS; //
  static final int GROWTHS6IX = balz += LSUBS; //
  static final int GROWTHS7IX = balz += LSUBS; //
  static final int GROWTHS8IX = balz += LSUBS; //
  static final int endOfArrays = balz += LSUBS; //
  static final int BALSLENGTH = balz += 2; //
  static int balancesSums[] = {BALANCESIX + RCIX, BALANCESIX + SGIX};
  static int balancesSubSum1[] = {BALANCESIX + RIX, BALANCESIX + CIX};
 // static int balancesSubSum2[] = {BALANCESIX + SIX, BALANCESIX + GIX};
//  static int balancesSubSums[][] = {balancesSubSum1, balancesSubSum2};
  // end of index values for bals
  static final String[] titls = {" bals rc ", " bals sg ", " bals r ", " bals c ", " bals s", " bals g "};

  /**
   * principal constructor of ABalRows a set of rows that are balances
   *
   * @param n count of rows in object
   * @param t flag tbal = balances, tcost = consts
   * @param h level for any send to hist methods
   * @param tit title for any send to hist methods
   */
  ABalRows(Econ ec, int n, int t, int h, String tit) {
    super(ec, n, t, h, tit);
//    System.out.println("instantiate ABalRows A.length=" + A.length + ", lA=" + lA + ", n=" + n + ", titl =" + titl);
    //dResums = Assets.balancesSums;
    //   mResum1 = Assets.balancesSubSum1;
    //  mResum2 = Assets.balancesSubSum2;
    // mResum = Assets.balancesSubSums;
  }

  /**
   * constructor assuming BALSLENGTH ARows, balance, History.informationMajor8, "bals"
   *
   */
  ABalRows(Econ ec) {
    super(ec, BALSLENGTH, tbal, History.informationMajor8, "bals");
  }

  /**
   * constructor assuming 15ARows, balances, level=aLev, title=bals
   *
   * @param aLev level for any send to hist methods
   */
  ABalRows(Econ ec, int aLev) {
    super(ec, BALSLENGTH, tbal, aLev, "bals");
  }
  /**
   * copy the values from ABalRows prev to this but do not change any
   * references. This is used for swap redo and must not change the references
   * of this
   *
   * @param prev new values from a HSwaps previous value
   * @return the revised values for this with no this references changed
   */
  public ABalRows copyValues(ABalRows prev) {
    int m = 0;
    for (m = 0; m < BALSLENGTH; m++) {
      if (A[m] != null) {
        if (prev.A[m] == null) {
          prev.A[m] = new ARow(ec);
        }
        for (int n : E.ASECS) {
          {
            //  A[m].set(n, prev.A[m].get(n));
            if (prev.A[m] != null) {
              this.A[m].values[n] = prev.A[m].values[n];
            }
          }
        }
      }
    }// end m
    // ABalRows always has these grades define with some values
    for (int i = 2; i < 4; i++) {
      for (m = 0; m < LSECS; m++) {
        for (int n = 0; n < LGRADES; n++) {
          this.gradesA[i][m][n] = prev.gradesA[i][m][n];
        }
      }
    }// end i
    return this;
  }

  /**
   * copy first 6 rows of ABalRows object to an new A6Row object , b is a new
   * object * lev,titl,balances,costs,blev are all copied as well as A[] values
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
   * fill any null rows with an zeroed new row
   *
   */
  void emptyFill() {
    for (int rowIx = 0; rowIx < BALSLENGTH - 1; rowIx++) {
      if (A[rowIx] == null) {
        A[rowIx] = new ARow(ec);
      }
    }
  }

  /**
   * null the rows that are not needed by Assets from yearEnd to the next
   * Assets.CashFlow.aStartCostFlow
   *
   */
  void nullYearEndRows() {
    for (int rowIx = STARTYEARENDNULLIX; rowIx < BALSLENGTH - 1; rowIx++) {
      A[rowIx] = null;
    }
  }
  /**
   * null the rows that are not needed by Assets cur is nulled
   * Assets.CashFlow.aStartCostFlow
   *
   */
  void nullCurEndRows() {
    for (int rowIx = STARTCURENDNULLIX; rowIx < BALSLENGTH - 1; rowIx++) {
      A[rowIx] = null;
    }
  }

  /**
   * get the balances A6Row from bals, only move references from the A[0] thru
   * A[5]
   *
   * @param lev level of the copied A6Row
   * @param atitl title of the A6Row
   * @return the A6Row
   */
  public A6Row getBalances(int lev, String atitl) {
    A6Row rtn = new A6Row(ec, tbal, lev, atitl);
    for (int i = 0; i < 6; i++) {
      rtn.A[i] = A[i];
      // rtn.A[i].addCnt();
    }
    return rtn;
  }

  /**
   * copy the balance references to A6Row return from bals, * grades must be
   * copied separately
   *
   * @param lev level of the copied A6Row
   * @param atitl title of the A6Row
   * @return the A6Row all values copied not references moved
   */
  public A6Row copyBalances(int lev, String atitl) {
    A6Row rtn = new A6Row(ec, tbal, lev, atitl);
    for (int m = 2; m < 6; m++) {
      rtn.A[m] = A[m].copy();
      rtn.A[m].addCnt();
    }// end m
    return rtn;
  }

  /**
   * set 2 rows in ABalRows from an A10Row 0,1 starting at bias
   *
   * @param bias index of the start of rows in an ABalRows
   */
  public void set2(int bias, A10Row b) {
    for (int rowIx : I01) {
      if (this.A[rowIx + bias] == null) {
        this.A[rowIx + bias] = new ARow(ec);
      }
      for (int secIx : E.ASECS) {
        this.A[rowIx + bias].set(secIx, b.A[rowIx].get(secIx));
      }
    }
  }

  /**
   * set 2 rows in ABalRows from an A6Row 0,1 starting at bias
   *
   * @param bias index of the start of rows in an ABalRows
   */
  public void set2(int bias, A6Row b) {
    for (int rowIx : I01) {
      for (int secIx : E.ASECS) {
        A[rowIx + bias].set(secIx, b.A[rowIx].get(secIx));
      }
    }
  }

  /**
   * set 2 rows in ABalRows from an A2Row 0,1 starting at bias
   *
   * @param bias index of the start of 2 rows in an ABalRows
   * @param b the input A2Row to be copied
   */
  public void set2(int bias, A2Row b) {
    for (int rowIx : I01) {
      if (this.A[rowIx + bias] == null) {
        this.A[rowIx + bias] = new ARow(ec);
      }
      for (int secIx : E.ASECS) {
        this.A[rowIx + bias].set(secIx, b.A[rowIx].values[secIx]);
      }
    }
  }

  /**
   * set 2 rows in ABalRows min for each sector from 2 A2Row 0,1 starting at
   * bias
   *
   * @param bias index of the start of 2 rows in an ABalRows
   * @param b the input A2Row to be copied
   * @param c the input A2Row to be copied
   */
  public void setMin2(int bias, A2Row b, A2Row c) {
    for (int rowIx : I01) {
      for (int secIx : E.ASECS) {
        boolean bb = b.A[rowIx].values[secIx] < c.A[rowIx].values[secIx];
        A[rowIx + bias].values[secIx] = bb ? b.A[rowIx].values[secIx] : c.A[rowIx].values[secIx];
      }
    }
  }

  /**
   * set 4 rows in ABalRows from an A6Row 2-5 starting at bias
   * do not copy ref
   *
   * @param bias index of the start of rows in an ABalRows
   * @param b A6Rows row ix set value SubAssets for each bias+rowIx
   */
  public void set4(int bias, A6Row b) {
    for (int subIx : I03) {
      if (this.A[subIx + bias] == null) {
        this.A[subIx + bias] = new ARow(ec);
      }
      for (int secIx : E.ASECS) {
        this.A[subIx + bias].set(secIx, b.A[2 + subIx].values[secIx]); //copy value not ref of value
        //A[subIx + bias].add(secIx, b.A[sumIx * 4 + 2 + subIx].get(secIx));
      }
    }
  }

  /**
   * set references to bals this starting at row bias from rows 2-5 of an A6
   * use useRef4
   *
   * @param bias
   * @param a an A6
   *
   */
  void use4Refnot(int bias, A6Row a) {
    A[bias] = a.A[2];
    A[bias + 1] = a.A[3];
    A[bias + 2] = a.A[4];
    A[bias + 3] = a.A[5];
  }

  /**
   * set values in a single row at bias + ix in ABalRows from an A6Row 2-5
   * specified by ix
   *
   * @param bias index of the start of rows in an ABalRows
   * @param ix row in ABalRows ix+{bias and in A6Row xi+2
   * @param b A6row from which 1 row used
   */
  public void set1(int bias, int ix, A6Row b) {
    for (int secIx : E.ASECS) {
      A[ix + bias].set(secIx, b.A[2 + ix].values[secIx]);
    }
  }

  /**
   * set a row bias + ix in ABalRows from an A10Row 2-9 sum of rows at 2+ix and
   * 6+ix
   *
   * @param bias index of the start of rows in an ABalRows
   * @param ix row in ABalRows ix+{bias and in A6Row xi+2
   * @param b A10row from which 1 row used
   */
  public void set1(int bias, int ix, A10Row b) {

    for (int secIx : E.ASECS) {
     // A[ix + bias].values[secIx] = b.A[2 + ix].values[secIx] + b.A[6 + ix].values[secIx];
      A[ix + bias].set(secIx,b.A[2 + ix].values[secIx] + b.A[6 + ix].values[secIx]);
    }
  }

  /**
   * set values of a row bias+ix in ABalRows from an ARow
   * does not copy the ref
   *
   * @param bias index of the start of rows in an ABalRows
   * @param ix an index added to bias
   * @param b ARow used
   */
  public void set1(int bias, int ix, ARow b) {
    if (this.A[bias + ix] == null) {
      this.A[bias + ix] = new ARow(ec);
    }
    for (int secIx : E.ASECS) {
      A[ix + bias].set(secIx , b.values[secIx]);
    }
  }

  /**
   * for A[bias] use the reference for the ARow
   *
   * @param bias the bias to the row in this
   * @param b the ARow reference to be set into this.A[bias]
   */
  public void useRef(int bias, ARow b) {
    A[bias] = b;
  }

  /**
   * sum a row in ABalRows from an ARow starting at bias+ix
   *
   * @param bias index of the start of rows in an ABalRows
   * @param ix index added to bias in ABalRows ix+{bias]
   * @return the sum of that row
   */
  public double sum1(int bias, int ix) {
    double sum = 0.;
    for (int secIx : E.ASECS) {
      sum += A[ix + bias].values[secIx];
    }
    return sum;
  }

  /**
   * sum a row in ABalRows from an ARow at bias
   *
   * @param bias index of the start of rows in an ABalRows
   * @return the sum of that row
   */
  public double sum(int bias) {
    double sum = 0.;
    if (this.A[bias] != null) {
    for (int secIx : E.ASECS) {
        sum += this.A[bias].values[secIx];
      }
    }
    return sum;
  }
  /**
   * create an new A6Row using references of 4 rows starting at ABalRows[bias}
   *
   * @param bias index of the start of rows in bals
   * @param lev level of the new A6Row
   * @param titl title of the new A6Row
   * @return A6Row references in rows2thru5, row0 =sumRows(2,3),row1=sum(4,5)
   * references to the rows iX thru iX+3
   */
  public A6Row use4(int bias, int lev, String titl) {
    A6Row rtn = new A6Row(ec, lev, titl);
    for (int rowIx : I03) {
      rtn.A[rowIx + BALANCESIX] = A[bias + rowIx];
      for (int secIx : E.ASECS) {
        rtn.A[(int) (rowIx / 2)].values[secIx] += rtn.A[rowIx + 2].values[secIx];// rc sg
        //rtn.A[(int)rowIx/2].values[secIx] += rtn.A[2+rowIx].values[secIx] = A[bias].values[secIx];
        rtn.aCnt[rowIx + 2]++;// also effect the same instance in ABalRows
      }
    }
    return rtn;
  }

  /**
   * put 4 rows of values from rows biasA to biasB
   *
   * @param biasA the index of the first row of the sources
   * @param biasB the index of the second row of targets
   */
  /*
  void put4AtoB(int biasA, int biasB) {
    for (int rowIx : A03) {
      for (int secIx : E.ASECS) {
        // A[biasB + rowIx].set(secIx, A[biasA + rowIx].get(secIx));
        A[biasB + rowIx].values[secIx] = A[biasA + rowIx].values[secIx];
      }
    }
  }
*/

  /**
   * get the percent of sum of row biasA over sum of row biasB
   *
   * @param biasA divisor row
   * @param biasB dividend row
   * @return sumA * 100/sumB
   */
  double getPercentSumsAofB(int biasA, int biasB) {
    double sumA = 0., sumB = 0.;
    for (int secIx : E.ASECS) {
      sumA += A[biasA].get(secIx);
      sumB += A[biasB].get(secIx);
    }
    return sumB * 100. / sumA;
  }

  /**
   * get the percent of sum4 of sum of rows biasA over sum4 of sum of rows biasB
   *
   * @param biasA divisor row
   * @param biasB dividend row
   * @return sumB * 100/sumA
   */
  double getPercentSum4AofB(int biasA, int biasB) {
    double sumA = 0., sumB = 0.;
    for (int rowIx : A03) {
      for (int secIx : E.ASECS) {
        sumA += A[biasA + rowIx].get(secIx);
        sumB += A[biasB + rowIx].get(secIx);
      }
    }
    return sumA * 100. / sumB;
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
    A10Row rtn = new A10Row(ec, 7, "travelC10");
    rtn.A[2] = A[TCOSTSIX];
    rtn.A[6] = A[TCOSTSIX + 1];
    double t;
    for (int j = 0; j < LSECS; j++) {
      t = rtn.A[6].get(j);
      E.myTestDouble(t, "t", "in getTrows get(6,%d) = %s", j, String.valueOf(t));
    }
    return rtn;
  }

  /**
   * set ABalRows bal for the saved travel costs
   *
   * @param tcosts10 Copy of current travelcosts
   * @return new copy of bal
   */
  ABalRows setTRows(A10Row tcosts10) {
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
    A10Row rtn = new A10Row(ec, 7, "maintC10");
    rtn.A[2] = A[MCOSTSIX];
    rtn.A[6] = A[MCOSTSIX + 1];
    return rtn;
  }

  /**
   * set ABalRows bal for the saved maintenance costs
   *
   * @param mcosts10 Copy of current maintenance costs
   * @return new copy of bal
   */
  ABalRows setMRows(A10Row mcosts10) {
    A[MCOSTSIX] = mcosts10.A[2];
    A[MCOSTSIX + 1] = mcosts10.A[6];
    return this;
  }

  /**
   * set 4 result rows at biasA from rows at biasB - rows at biasC
   *
   * @param biasA The start row number of rows to be set
   * @param biasB The start row number of values to be subtracted from
   * @param biasC The start row number of values to be subtracted
   */
  void setA4toBminusC(int biasA, int biasB, int biasC) {
    for (int rowIx : A03) {
      if (this.A[rowIx + biasA] == null) {
        this.A[rowIx + biasA] = new ARow(ec);
      }
      for (int secIx : E.ASECS) {
        this.A[rowIx + biasA].set(secIx, A[rowIx + biasB].get(secIx) - A[rowIx + biasC].get(secIx));
      }
    }
  }

  /**
   * set 4 result rows at biasA from rows at biasB mult by rows at biasC
   *
   * @param biasA The start row number of rows to be set
   * @param biasB The start row number of values to be multiplied
   * @param biasC The start row number of values of the multiplier
   */
  void setA4toBmultC(int biasA, int biasB, int biasC) {
    for (int rowIx : A03) {
      for (int secIx : E.ASECS) {
        A[rowIx + biasA].set(secIx, A[rowIx + biasB].get(secIx) * A[rowIx + biasC].get(secIx));
      }
    }
  }

  /**
   * set 4 result rows at biasA from rows at biasB multby value
   *
   * @param biasA The start row number of rows to be set
   * @param biasB The start row number of values to be multiplied
   * @param c The value of the multiplier
   */
  void setA4toBmultV(int biasA, int biasB, double c) {
    for (int rowIx : A03) {
      for (int secIx : E.ASECS) {
        A[rowIx + biasA].set(secIx, A[rowIx + biasB].get(secIx) * c);
      }
    }
  }

  /**
   * set 4 result rows at biasA from rows at biasB added to rows at biasC
   *
   * @param biasA The start row number of rows to be set
   * @param biasB The start row number of values to be multiplied
   * @param biasC The start row number of values of the multiplier
   */
  void setA4toBaddC(int biasA, int biasB, int biasC) {
    for (int rowIx : A03) {
      for (int secIx : E.ASECS) {
        //doubleTrouble upgrade the val from double to Double
        if (E.debugDouble) {
          A[rowIx + biasA].values[secIx] = ec.doubleTrouble(
                  ec.doubleTrouble(A[rowIx + biasB].values[secIx], "to B")
                  + ec.doubleTrouble(A[rowIx + biasC].get(secIx), "addC"), "saveA");
        }
        else {
          A[rowIx + biasA].values[secIx] = A[rowIx + biasB].values[secIx] + A[rowIx + biasC].values[secIx];
        }
        // A[rowIx + biasA].set(secIx, A[rowIx + biasB].get(secIx) + A[rowIx + biasC].get(secIx));
      }
    }
  }

  /**
   * set 1 result rows at biasA from rows at biasB added to rows at biasC
   *
   * @param rowIx the index of which row in each bias
   * @param biasA The start row number of rows to be set
   * @param biasB The start row number of values to be multiplied
   * @param biasC The start row number of values of the multiplier
   */
  void setA1toBaddC(int rowIx, int biasA, int biasB, int biasC) {
    for (int secIx : E.ASECS) {
      //doubleTrouble upgrade the val from double to Double
      if (E.debugDouble) {
        A[rowIx + biasA].values[secIx] = ec.doubleTrouble(
                ec.doubleTrouble(A[rowIx + biasB].values[secIx], "to B")
                + ec.doubleTrouble(A[rowIx + biasC].get(secIx), "addC"), "saveA");
      }
      else {
        A[rowIx + biasA].values[secIx] = A[rowIx + biasB].values[secIx] + A[rowIx + biasC].values[secIx];
      }
      // A[rowIx + biasA].set(secIx, A[rowIx + biasB].get(secIx) + A[rowIx + biasC].get(secIx));
    }

  }

  /**
   * Trim each sector in row A+rowIx by max and move any surplus above max in A to B+rowIx
   *
   * @param max the max values for all sectors
   * @param rowIx the index(0-3) of which SubAsset row with each bias
   * @param biasA The start of a 4row set of rows to be trimed to max
   * @param biasB the surplus at the start of a 4row set of ARows of surplussed   * A
   */
  void moveMaxSurplusWithIxA4ToB(double max, int rowIx, int biasA, int biasB) {
    for (int secIx : E.ASECS) {
      double tt = A[rowIx + biasA].values[secIx] - max;
      A[rowIx + biasA].values[secIx] = tt > 0.0 ? max : A[rowIx + biasA].values[secIx];
      A[rowIx + biasB].values[secIx] = tt > 0.0 ? tt : 0.0;
    }
  }

  /**
   * set 4 result rows at biasA from rows at biasC to divide rows at biasB
   *
   * @param biasA The start row number of rows to be set
   * @param biasB The start row number of values to be divided
   * @param biasC The start row number of values of the divisor
   */
  void setA4toCdividB(int biasA, int biasB, int biasC) {
    for (int rowIx : A03) {
      for (int secIx : E.ASECS) {
        if (E.debugDouble) {
          A[rowIx + biasA].values[secIx] = ec.doubleTrouble(
                  ec.doubleTrouble(A[rowIx + biasB].values[secIx], "biasB")
                  / ec.doubleTrouble(A[rowIx + biasC].values[secIx], "biasC"),
                  "biasA");

        }
        else {
          A[rowIx + biasA].set(secIx, A[rowIx + biasB].get(secIx) / A[rowIx + biasC].get(secIx));
        }
      }
    }
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
        A[m].set(n, balances.A[m].get(n));
      }
    }
  }

  /**
   * make a copy of the balances part of ABalRows
   *
   * @return balances copy not the reference
   */
  A6Row copyBalances() {
    A6Row rtn = new A6Row(ec, lev, titl);
    for (int m : I05) {
      for (int n : E.ASECS) {
        rtn.A[m].set(n, A[m].get(n));
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
    return use4(GROWTHSEFFIX, lev, titl);
  }

  /**
   * get reference to a single ARow corresponding to the index
   *
   * @param m index of the growths ARow s
   * @return growth ARow for index m
   */
  ARow getGrowthsRow(int m) {
    return A[GROWTHSEFFIX + m];
  }


  /**
   * list the growths rows in bals
   *
   * @param blev highest level that will be listed
   * @param apre prefix of listed rows
   * @param alev level of listed rows
   */
  void listGrowths(int blev, String apre, int alev) {
    sendHist(GROWTHSEFFIX, GROWTHSEFFIX + 3, blev, apre, alev);
  }

  /**
   * get the rawUnitGrowths references of bals
   *
   * @param lev level of the new A6Row
   * @param titl titl of the new A6Row
   * @return a mew A6Row using the growths section of bals
   */
  A6Row getRawYearlyUnitGrowths(int lev, String titl) {
    return use4(RAWYEARLYUNITGROWTHSIX, lev, titl);
  }

  /**
   * get reference to a single ARow corresponding to RAWUNITGROWTHSIX
   *
   * @param m index of the growths ARow s
   * @return growth ARow for index m
   */
  ARow getRawYearlyUnitGrowthsRow(int m) {
    return A[RAWYEARLYUNITGROWTHSIX + m];
  }

  /**
   * list the rawUnitGrowths rows in bals
   *
   * @param blev highest level that will be listed
   * @param apre prefix of listed rows
   * @param alev level of listed rows
   */
  void listRawYearlyUnitGrowths(int blev, String apre, int alev) {
    sendHist(RAWYEARLYUNITGROWTHSIX, RAWYEARLYUNITGROWTHSIX + 3, blev, apre, alev);
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
   * get reference to a single BonusUnitsARow corresponding to the index
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
   * get the A6 CumulativeUnitDepreciation references from bals
   *
   * @param lev level of the new A6Row
   * @param titl titl of the new A6Row
   * @return the selected bals references in an A6Row
   */
  A6Row getCumDepreciation(int lev, String titl) {
    return use4(DEPRECIATIONIX, lev, titl);
  }

  /**
   * list the CumulativeUnitDepreciation rows in bals
   *
   * @param blev highest level that will be listed
   * @param apre prefix of listed rows
   * @param alev level of listed rows
   */
  void listCumDepreciation(int blev, String apre, int alev) {
    sendHist(DEPRECIATIONIX, DEPRECIATIONIX + 3, blev, apre, alev);
  }

  /**
   * get reference to a single ARow of CumulativeUnitDepreciation corresponding
   * to the index
   *
   * @param m index of the CumulativeUnitDepreciation ARow r c s g
   * @return gdepreciation ARow for index m
   */
  ARow getCumDepreciationRow(int m) {
    return A[DEPRECIATIONIX + m];
  }
}
