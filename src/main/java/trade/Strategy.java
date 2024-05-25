/*

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

This file is part of the AI process for StarTrader it hold information for a subset of all
the keys in Map myAIlearnings and a subset of the information in each key.  This
information is used to determine the best strategy to win this game.
It contains a subset of the settings and a subset of the results
 */
package trade;

/**
 *
 * @author aljst
 */
public class Strategy {

  // E static volatile int E.bValsStart = 25, E.bValsEnd = -2,EM.vvend = -1;
  char[] key;

  /**
   * create new Strategy
   *
   * @param pors the pors for this strategy
   * @param clan the clan for this strategy
   * @param aKey The key being offered
   * @param clanKey the clanKey in EM
   * @param clanMask the mask in em
   */
  Strategy(int pors, int clan, char[] aKey, char[] clanKey, char[] clanMask) {

  }

  /**
   * add another key to array sAr
   *
   * @param sAr array of keys
   * @param aKey key to be added
   * @return 0 if none added, number of strategies if key process ok
   */
  int addKey(Strategy[] sAr, char[] aKey) {
    int ret = 0;

    return ret;
  }

}// end Strategy class
