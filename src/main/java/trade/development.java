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
/*
******* sometimes new lines went to the end, recently they went to start.  not always done

5/29/2024 increase value 0f map pair to include age and aiScore.
In EM.doStartYear() read all MAP entries, increase age by 1 year, drop overage entries with too few cnts(tbd).
try to determine how well aiScore follows last position.  Create arrays of 52 elements , scoreVal(scoreIx), cnts, cntsMatch position4.  Display these in display, maybe set clan nudge for shipFrac, also arrays for tradeFrac, and accepted goal value.
5/28.2024 Start keeping track of what configurations win, which econ configuations win are in position 4.
What portion of time do the econ high score match position 4
potentually as the map gets full, I can decide not to write low econ score not position 4 entries with a low count.
The EM.buildAICvals will put into the saved values, the nudges for tradeFrac, Catastrophy, ships for each clan
Build key with prev's input values before Assets.CashFlow start processing the year in Assets.CashFlow.startYearAI(), so these values are available in Assets.CashFlow.saveAI() to write a new key. These Econ values like econScore, aiWorth, prevAIWorth, incAIWorth fyWAIWorth

5/23/2024 in looking for a way to go Byte to String, one of the results said you should really be using char, so I am going to try that
5/20/2024 Ideas to proceed, an Assets joy is a combination of size of in Assets score, combined increase in score this year
At EM. start of year, create 10 keys for ps and clan leave blank the Assets part of the keys, so keys settings are not derived for each run of Assets.CashFlow.aStartCashFlow() first started.  Assets.CashFlow.aStartCashFlow() is started for Trades as well as Assets.CashFlow.yearEnd().  The first Assets.CashFlow.aStartCashFlow() !didCashFlowStart processes the key map to determine which keys have the highest joy to pick the nudge in settings that may help increase the score. It will build an array of keys, consilidating keys that only differ in values that are masked.  If it appears a slight change in a setting produced more joy for the 3 changable settings. The previous value of the settings is saved to be restored after generating the result key pair in Assets.CashFlow.yearEnd().  The Assets score is derived from the individual values of results that go into the annual pick winner process.  I think it is important to save the values that go into the score as well as the settings that result from the score

5/16/2024 I don't know if this is really AI, I am just trying to set up winning by looking at history.  I realize I have never had a strategy for winning.  I suspect that gaining the most economies will gain the most offering in trades, and there is a question about the number of ships or planets. I want to see if keeping a history of settings, results and position in score will enable me to have the machine try to win.  At this point I want to remember position for each clan each year, remember resilience, hope, worth offers/worth, age, perhaps changing settings for tradeFrac, ships, catastrophies.  The three settings changes will have for planets and ships bias values for 2 or 3 smart clans.  These bias values are picked by trying to choose settings that were most often in first score place.  I think to add a Critera class that will contain the settings and results along with a count of the occurance count.  These will be reset each year before the start of the year, this will set the bias values for smart plnets and ships.  Assets.CashFlow.yearEnd will still map the new keys and counts. The criteria entries will be set in several different orders one order is for accending order of each setting and result so that matching keys can be added together.  Only specific values in the keys are matched so that many differences are ignored.  There needs to be a consolidation process so that the number of remembered critera is limited to something like 100.  The critera can be ordered in the best hope for the given input values of a given economy.  And different critera can be chosen for more or less hope, resilience, age, worth.
5/14/2024  I think I need to have a set of readings and record the position of the clan of the economy for each map entry.  At some point you also keep track of how many years the clan is in first place. Both of these entries in EM static ARow entries. At the end of each year, keep track of positions.  You als0 have a 20 entry array good settings, see which ones are in top position.  I also need to define resilience and hope for each run of Assets.CashFlow.yearEnd last getNeed. resilience is the ability to do helpful swaps: probably the rawProspects2 row0 entries * row1 entries  then the resil.max /resil.ave
Hope is the resilience * phe
5/12/2024 build a results array in Assets.CashFlow, the applies only to this Assets.CashFlow for choosing joyful resetting to hopefully increase the score of this clan for the next year.  One prev input is the clan position in the score list, as well as how far from the top.  The array has an input for up to 5 strategies as to how close the joy is to the goal for this strategy, and if another group of settings get closer to the goal
5/11/2024 The AI problem is that I can influence settings at the economy stage, but the score is set in terms of the clan.  The clan could try for more population, or less fatalities, or more percent offer but each strategy is complex, in part dependent on what the other clans are doing.  At the same time we are starting with a minimal approach, only change 2 or 3 settings, and key off of 3 to 5 results.  We can devise several strategies settings based on determined results and see how helpful the strategies are, how well their results reflect how the clan is doing in its score position.  A successful AI would be able to have strategies the enable a high score in a variety of situations (strategies by other clans).  My plan is to have 2 AI clans and 3 other clans, whose settings may be changed.  With map files saved at end of each year to a text file, multuple strategies can be done using the saved text AIFile.
AI mapfile results are shown by strategy in the "display" tab.  It is most useful without master setting changes, except potentially for strategy settings.
4/30/2024 I've gone back and forth on whether the AIFile is text or binary. I think text at this point and while bytes are used to create the key's I think the keys should also be a string and not a binary array. Convert result arrays to put results into the byte array, which is finally converte to a string
4/20/2024 I think the memory fine AIFile should be a text file like keep it will consist of multiple lines of a string key and then an integer value of the number of instances of that key.  Each key contains one the values of the settings and one or more values interesting value like worth, trade given, tradesAccepted, settings pushed etc.  The idea is to judge the "joy" (closeness to success) of a given push.  The success is of course set by the score settings indicating which values lead to success.
4/19/2024 I took time off for some health issues.  I solved a significant problem that cleared the tradeAccepted flag before most of the reslults were saved, so that the map values did not show accepted.  I created both a map and a large static array to save a limited set of values.  I have not developed the means of saving the map betwen runs like I do with the "keep" file.
3/19/2024 now look at values beyound settings to pput into count , 5 separate values each
PHE
3/15/2024 started some AI coding, stuck at conversion from settings value with the start and end of values
3/12/2024 Continued AI plalnning.  I need to first generate a smaller map, decide how to use the map to pick better alternatives for an economy year.  Perhaps I will treat diffeent clans differently to see how well it served the clan.  I need to see how many different pots I need for making good decisions.  What do I divid the relative values by 20 or 10 or 5 or 7.3?  Another question is how do I populate the options for AI. Some things are save in the Assets as memory for CashFlow and SubAssets.  And there is an ordering of SubAssets by priority.
3/5/2024  Thinking about AI or a learning system remembering results incrementing map values from multiple runs of StarTrader using keys from the effective set of settings from the previous round, and the new score with the current effective settings and the change in score, there will be multiple keys for each setting that is part of the learned response system, there may also be keys as combinations of economy values in various ways.
The goal for each economy is to learn how to increase the changed score/pevious score, and then to record the result in 1 of 7 keys, increasing the count for the key. possible saved scores where "m"=(val-min)/5) A:dead B:<=val-3*m C:=val-2*m D:=val-m.99 E:=val F:=val+m G:=val+2*m H:>=val+3*m  keys for 5 unit settings are 65+val/5.0
An effective setting, is the game setting, plus any variation on that setting from the learning sytem.
I will try a set of learnings based on the current priority of the 7 services, not by the name of the services
also the values of the settings are divided into 5 values and 20 values to see if the 20 values can do a better job of increasing the score.  setting scores are saved as newscore/prevscore added into the score values, there is also a floating count of the number of values save, the result value is thus the scoresum/times.  The effective setting is thus the (game setting + the result value)/2.0
At economy end of year for each active value (up to 7) start with only 2, save result values by increasing the count for 2 keys, the AA key with all set values except the active setting is set to its result value, the BB key is built with all set values except each active setting is set to its result value.  The map entries for the AA and BB keys are increased by 1.
At the year start, each active setting is derived a possible value if the AA keys E thru H key counts exceed the A thru D counts.
use either a local map with a file backup, or file map
:Order of Keys
Calculate scores for each economy, and save change in score to the map using keys calculated
10/20/2023 I am trying to make test work.  I found that test in nbactions.xml if given a bad -ae commond instead of -ea failed with an illegal command upon calling the "test project" command under run.  I also found that any changess to nbaction.xml only took effect at the next start of netbeans.  In additon I found that at netbeans19 when you right click that top line of the project is get a new menue, at netbeans19 the "Run Maven" line disappeared from the new menue at the right click.  In netbeans18 the value of that is to show whether the nbactions.xml is broken.  If it is none of the "CUSTOM ..." commands show up, I think it means ther is an illegal line in nbactions.xml and it is never transferred to the .m2
10/01/2023 I struggled because the stats for the clan did not show the growth of the depreciation.  Then I realized that the history of the clan is different than the history of the individual economy, because the population of the clan is changed each year by deaths and new creations.  Max stats may be useful, I have already written the maxstat methods in trade.Assets.  Trouble that I had with errors was resolved by moving the calculation  and breaking a complex statment into a larger set of statments that are smaller, I don't know why that worked.  I also found that reducing the pixels in the display, changed the number of pages the same fount took in my Doctor google doc's.  This was part of an effort to check how trade.StarTrader's response to smaller sizes of the display.  I continue to be sure my tests run in a folder in my "Display folder" which is not part of OneDrive or GoogleDrive, both Quiken and NetBeans had trouble with operating files in a folder that had always active backup, because that had 2 operations working on the same file, the backup file was not neessarily the history of an active file in it's finished state.
9/15/2023 I found a problem, maybe a solution,  when I cleaned up so all the Assets methods that created a CashFlow nulled that CashFlow at their exit.  The problem is that I want most of the CashFlow variables to disappear at the end of CashFlow, but a few need to survive throubout the year but not into the next year.  I am starting to make most of the Assets ARpw variable instances to live in bals a ABalRows array of ARows
Some but not all can be nulled at the end of CashFlow the problem invGrowEfficiency are an example of created in yearStart but needed in barter and yeaEnd by getNeeds.
9/4/2023 I have been struggleing to get growth up, but costs are based  in part on balance, then getNeeds finds the health by requirements the health modifies the costs, then it determins available units after travel and maintenance costs the remaining goes for growth and growth costs.
8/1/2023 I have had some very funny errors, I found recently there was a background java running that took space and most 
of the CPUs  I couldn't tell what it was, and nothing bad when I killed it, but it might have been responsible for some 
impossible kind of errors, index out of range, missing or null request.
I am working now with setting difficulty at 50% and having perhaps 20% up to 40% deaths, currently only new ones are deaths, and year 2 only the ones that pere present earlier died, the new ones lived. it appears all the 0 year created econs are killed, 
following years only the new ones are killed, but not new next year???
2/19/2023 version 19.49 put paintCurDisplay in doYear, do one stats
12/27/22 now have some kind of looping in testing rnd6 add more System.out lines
12/24/22 Still have liveLock with threads, the Assets.CashFlow.yearEnd() has too
many calls to setStats in the same sequence requiring synchronization, this 
eliminates any parallel processing by threads.  So I disabled threads as an
option.  After running out of memory, I assign memory -Xmxs3072m
12/14/22 found that static declarations are all part of class declaration lock t
his causes static EM.setStat locking problems.  Multiple threads can occur 
during Assets.CashFlow.calcFutureFund() which runs several time in swaps and
does a lot of the same sequence of EM.setStats.  I hope eliminating static setStats
will avoid what appears to be liveLock
 12/6/2022 Runtime runtime           = Runtime.getRuntime();
long presumableFreeMemory = runtime.maxMemory() - allocatedMemory;
long allocatedMemory      = runtime.totalMemory() - runtime.freeMemory();
figure out limits to start increasing difficulty to limit the number of active econs as presumableFreeMemory starts to decrease

12/03/2022 separating checkEconState(animation) and setEconState(background) both are synchronized meant 
that animation no longer waited on background.  I still don't know why animation does not run 
while doyear is running --MJ--millisecs=15648 next --AA--atime 0.492
------MB------- runYears2; aTime 15.646RunningY-1 (animation around startY3
-------MC--------runYears2; aTime 15.648 at start cnt-11RunningY-1
--AA-- aTime 0 aaTime 15.648 ry2Time 0.008 ry3Time 0 StartingY-1 cnt0, econ=no Econ name
--CES-- ry2Time 0.008 ry3Time 0 TH3
then --SES--ry2Time 0.049 ry3Time 0.041since no Econ name 0.039, TH2
--MMb--aTime 0.107 stateCnt0 Creating4Y0 name=P0003
-MMb--------Init new Econ3 aTime 0.137 stateCnt0 Creating4Y0 name=P0004
-MMb--------Init new Econ7 aTime 0.187 stateCnt0 Creating4Y0 name=S0008
--MMb--------Init new Econ10 aTime 0.235 stateCnt0 Creating4Y0 name=S0011 (no--CES
------DA-----paintCurDisplay 15903 StartYearY0 eCnt=11:11:null::S0011 controlPanelIx=3:3:9:6 (finally)
SES Thread-3 StartYear=> Trading null, sameEconStatecnt=0 millisecs= ry2Time 0.278 ry3Time 0.27
12/03/2022 trying to fix getting stuck both run and text.  It seems that the animation 
thread does not run while the background4 is running doyear runYear
Still trying to make sense out of when setEconState is called what value
5/8/2022 continue to look for cause of errors after changing to st.runYears(2)
5/4/22 [it om statments to reduce costs if asset units decrease, saw little effect.
remove *ps[][] from Assets.CashFlow.calcRawCosts
shorten the years of running tests
4/28/22 in EM put parnes () around a conditional sub statment that tests for null, the result always needs 
to be Char or String
4/27/22 in EM keep working on reducing costs so that some growth can work, but want also deaths
synchronize EM resI and resV between doStartYear and getCurCum... be sure they are volatile so they
aren't in 2 different CPU storages, prevent getCurCum... seeing a null during the start year update
4/23/22 E.distributal change to false, enable lots of out
4/22/22 finish getNeeds consumerEMTGC6 cost of growth with consumer view
4/21/22 put back most of what was lost with google destroy "My Drive/Netbeans
fix forwardCreate to do up to 50 econs, stats for rcsg init, live, incr
3/17/22 reset smallest trader critical request to 5.0
reset E.distributable to true, eliminating all tests, expecting -ea not to be set in call voiding asserts
reset out to StarTraderOuput err to StarTraderErrors in the folder in which the jar is started
jar("test") starts the StarTrader test mode running through a series of tests
3/15/22 still in Assets.CashFlow.Trades.enforceStrategicSums first in not valid while starting
first limits the size of requests, (offers from the other)
The other can increase offers to the emerg limit as needed, 
Increases can only be done incValueOK before term 9, after that only decreases allowed
3/14/22 fix E.myTest call in Assets.CashFlow.SubAsset.putValue
small requests in calcTrades turn into 0 requests in enforceGoals
check for negatives as < E.NZERO or E.NNZERO during barters
3/13/22 another fix in Assets.CashFlow.Trades.calcTrades and initTrade fix requests 
3/10/22 still in Assets.CashFlow.Trades.enforceStrategicSums put Ix in the trades and if*(E.tradeDebugTerms)
fix hiCrit def
Still try to understand why hiCrit requests are not set
Fix calcTrade to correctly set the need and allow ARows
3/9/22 add more System.out history to find why over .5 trades fail after year 5
3/8/22 on E.debugDisplayTrade in Assets.CashFlow.Trades.enforceStrategicSums put logs of activity go to System.out
3/7/22 reduce stats from reject, put in else to reduce duplicate DIED stats for the same run
try to detect null in StarTrader.setEconState
3/6/2022 fix setting offer too large, start maxReq to limit need, try to fix a loop on accept
reduce fraction length of E printing
limit the worth or manuals but not the amount to a percent of traded requests
convert tiny request to a warning not rejection
3/4/2022 in Assets.CashFlow.Trades.barter and testTrade eliminate skip to term1, march until no changes
3/4/22 with Assets.CashFlow.Trades.enforceStrategicGoal, try to fix increasing offers too much, fix rF and oF to use current bid2 and do a count, use rnd23 to allow testing for sv > rGoal0
3/3/2022 continued work in the Assets.CashFlow.Trades inner class, especially with enforceStrategicGoal, making sure that although fneed1 and need1 are both >0., but first, bid(requests) and need are all < 0.
The previous run failed with no completed trades, so I hope to have trades be large and most accepted.

This is the development log and planning for Star Trader, along with a description of the story of the game
12/19/2020 Start the routine startNextYear in CashFlows to have everything prepared for a trade the next year, this does the calcCatastrophy, calcGrowth, calcEfficiency and handles the 1 time year 0 allocation of cash in to the SubAssets by financial sectors, knowledge by sectors, bad health and trading options.

Warning when moving trade, be sure to either restart project, or scan infrastructure files to move location of all project files.

General organization and plan for StarTraders
The game consists of planets and ships that must trade to survive.  The planets and ship are divided into 5 groups/clans.  Planets can mine/grow resources and colonists, while ships transport materials and colonists .  

A game boss sets the initial parameters for the game.  There are many options, and they can change the play of the game in many different ways.

Up to 5 clan bosses get to set the strategy parameters for both ships and planets in his clan.  Planets and ships may flourish, grow large and also grow small and die.  The goal is to increase the nominal worth of the clan, worth is the  sum of assets: materials, material reserves-cargo, staff and staff reserves-guests, knowledge and manuals and cash on hand.  Output views show a boss how his clan is doing, in many different ways, and a boss may revise clan strategy based on the view of current assets.  After strategies are revised, the game is advanced 1 - 5 years, and a new set of views show current assets.

Clans can spawn new ships and planets using part of their assets to sponsor creation of new economies (ships or discovered planets).  Different clans my grow their economies to a larger size, which actually limits trade partners to the larger economies.

The game boss may permit clan bosses to see a comparison of the assets of each clan.  It may be decided to run for "N" years, or until a clan reaches a specified worth.

The assets of a ship or planet are divided into 7 financial sectors. Because of priorities there are always shortages in one or more financial sectors.  Each year a new set of random multipliers are derived.  These multipliers are applied at different places in the annual cycle.  In addition, each planet or ship; environment starts with a level of difficulty that reduces the efficiency of services.  Through the years, knowledge grows and can overcome difficulty increasing efficiency.  Ships can only trade, and have a small staff growth, therefore they cary cargo and guests to trade with other planets. Ships must always get more than they give because they need to cover travel costs



Each year services wear out and personnel die, if services and personnel cannot be maintained; health is bad, than wear and deaths occur faster.  Perhaps trading can correct the problem, if not, the environment will die.  If services and personnel prosper than worth increases.  Initial parameters should allow ships to survive for 5 to 7 years, and planets to survive for 5-7 years.  Ships must be able to carry significant cargo and guests for 3-6 years to find a good trade.  Ships have a much smaller growth of both staff, and almost no growth of services (energy and fuel) may be found in space while traveling.

The aim of the game is to gain worth.  A secondary goal is to minimize deaths.
A third is to optimize trades to foster the first 2 goals.  Each clan has a variety of variables to change.  The best changes bring the highest scores.  Throughout the game random events cause great losses and deaths, but also leave significant amounts of one or more resources.

This involves a lot of bookkeeping done in the required array that is passed to most methods.  The D class is also passed because all of the row and column definitions for required are defined in this class, this also contains all final and other static variables.  StarTrader the user interface must change parameters here.

The game employs some play ahead to try to learn stratagies that will enhance this environment.  See below for thoughts on communicating play ahead learnings to the play environment by environment

Each star or ship is created by creating and Envirn class.  StarTrader keeps pointers to each Envirn to call the run and trade methods, and to get the worth, colonists and resources and health values that characterize a ship or star.

Each environment has 7 services
life: includes air, water, food, medical facilities essentials of life support
struct:  buildings, hulls, houses, roads, markets etc
energy:  energy for travel, light, machinery, etc
propel:  travel resources, propellant, trains, busses
defense:  shields, disaster rescue resources against floods, drought, hurricanes disasters
gov:  officers of the ship, governmental organization of stars
colonist: includes teachers, doctors, counslers, essentials for development of personnel

Each service has a number of different parts:
  1) raw resource, physical resource
  2) personnel to work/develope/mine/maintain the resource
  3) cargo, resource that can be traded, costs less to maintain
  4) guests, personnel that can be traded, costs less to maintain
  5) knowledge, more complex trading, ships learn all knowledge from a star, stars trade for additional knowledge.  Traded knowledge is some new and some already known.

Initially at construction of an environment Envirn communicates with StarTrader, and creates the req array that holds the state data for a given star or planet, CalcReq handles upper part of req processing, Resource handles details about resources but hands off staff processing to Personnel.  Personnel is complex with stages of growth and learning of personnel, knowledge growth is handled by personnel.  As knowledge grows it increases efficiency decreasing the effect of the initial difficulty level.  A class D holds the enums and row and column indexes for req.  D is instantiated in StarTrader which holds variables that users may change, the rest of the constants are in D.

Display Levels: see Class History

Around 2/2018 I continue to move continuing values from AssetsYr to Assets so that 
the amount of storage needed between yearEnd swaps, or one or more trades
is minimized.  Cur (cashFlow) is constructed using reference to
continuing values balances and other continuing values

Around 7/2014 I decided I ought to try writing more object oriented instead of having the main values for each economy in the req double[][] array, and acknowledge that this is really about economies not environments.  So I started the Assets, and AssetsYr  containing SubAsset. Later on a whim I move AssetsYr into Assets, which was too much, but was too much trouble to separate again.

The point of calculating costs is to be able to calculate health, fertility, worth and the corresponding sub assets.  Swap is done to increase both the health and the fertility values.  The knowledge value is important for each sector because more knowledge increases efficiency.  Knowledge is increased each year by the effective researcher value of the staff in each sector.  The staff SubAssets contain 16 grades of staff.  Each year some  move up a grade depending on effective faculty value, and new staff are introduced at the 0 grade.  There are 4 types of staff colonist, engineer, faculty and researcher, each with 4 grades.  Staff perform work the amount of xhich depends on grade.  Work is needed for maintenence, growth etc.
Growth of a service resource depends on the SWork or staff work ability for that resource.  It is limited by the maintenance and growth requirements.  Growth requirements are calculated depending on health from maintenance requirements and source available after maintenance and travel costs are deducted.  The calcRun routine repeatedly varies scenario's to lengthen the life of the environment and increase the growth of the environment.

I want the data about users to be serializable, so that on a given command, the game is serialized, and can be restored later. Probably I can store the pointer to the last file in system.properties??  I also want to keep backups of the current game for 5 - 10 years.  Saving the current values and restoreing a previous move, causes to game to be moved backward as needed.
1) Finish the work to have CalcReq calcCostsSwapsLimits do a set of play ahead steps, and when an adjustment is made in a play ahead step, make an appropriate adjustment in the level 0 Required  by changing values in a slice of the full Required array.  

Each environment has 3 or 4 major phases:
doTrials:  calculates some trial years to try to predict future years, this is not yet well developed, and is not yet very helpful

doGrow:  3b. 3d. sets up for the best growth of both resources and personnel, growness and growRequirements must be satisfied so personnel may be moved and minimal resources to satisfy growRequirements.

doNoTradeHealth:  4c. 4d. calculate health after no trade

doNoTradeGrow:  5c. 5d. calculate growth after no trade

restoreGrowValues:  (3b.) go back to grow values to calculate trade.

doTrade:  6b. calculates the values for a trade, sets up resources and personnel to be able to calculate the strategic value of each resource and associated personnel.

DoHealth:  7b. 7c. 7d. after a possible trade, calculates the highest wellness (maintenance requiremnts).  Die will be calculated at the end of this

year values stored, next
Thinking about bids and trades
The planet value of elements in a bid are derived from the previous step 5 values.  These values are kept in saveData(-2) and compared on each try.  The try with the highest score wins.
Dynamic "user" values are changed in the swaps process.  The existing and previous
swapp1 .. swapp4 values of swapp may be used to make decisions about raising or lowering dynamic values.

4/20/2014 Work a lot to push the calculations for which swap to do into the calcMaxMin.  The calculations there permit choosing the best resource to do a kind of swap.  The swaps are ordered, so that the first swap test that finds a min that is negative is evoked for this run.  Phases of a run were introduced to vary which of the process flags will be used: gMaxn (growth), gfMaxn (growFirst0, fMaxn (future calculations), hMaxn(health)

G6/4/2013 I realized after several problems, that I can't really count on class variables being 
available to a class method unless the variables or a root of the variables are passed into the method.  If the method is invoked in a class outside its class, then the class variables are not available.  So each method requires either the class as a parameter, or all of the variable used in the method.

4/8/2012 I realize I need a irrg ... to count growth by consumers, because the costs 
and the growth is by consumers, we use the min growth for any service.

4/16/2012 required == req is the central database for operations for each environment.  Initially resources are distributed to R
4/19/2012 Have a problem with IL short move highest consumer Avail to C, the count looks bad.  For other reasons have been moving the staff values into the Required array to eliminate the Personnel.staff array.  In the process Guest has the same set of stages as Staff, and update works on both Staff and Guests.  This requires a Personnel.pre-store, that save values of staff and guest, for swaps, these will be used to find differences.
At the same time move the call of Personnel.storeStaffReq  from routines in Personnel to the corresponding Resource.storeStaffReq1 and storeStaffReq2.  There will be a Resource.recount that only goes through the store's in sequential nonswap mode and resets the counts for Resource and Personnel both now in CalcReq.Required
Move wear and update Req into the n loop and after update go around once more last=2 to calculate something like LBI and KBI which includes the Required Maint, and several maint and travel.  Use the remainder values to move 1/2? set by player from Avail to Cargo, and from Staff to Guest.

6/15/2013  opps, all of the global variable in a class do not work in methods that are called in other instantiations.  This means each method must be reworked to have the first parameter be the pointer to the correct instance of the class.  This is particularly evident in doTrades

rework swapP and the multiple step processes e.g. swapCargoAndResToRes, each process must work standalone.
Each process works with variables swapP[E.swpMaxRes] = min(swapP[E.swpMax[ResCargo]From,E.swp[RC]FromIn) and 
swapP[E.swpMaxStaff] = min(swapP[E.swpMax[SG]From,E.swp[SG]FromIn)
the swapP[E.swp[SR]cost] and swapP[E.swpmovd] are applied against the correct swapP[E.swp[SGRC]Decr and swapP[E.swp[SGRC]cost, these than go against the correct to req and from req

Max[RCSG} are all influenced by future calculations with have a "standAlone" contingency multiplier (.9 to 1.5) and a "trading" contingency multiplier (.9 - 1.7), the trading future generally looks further the tradingFutureYears  (3-7)  the "standalone" futureyears (2-5) 

The play ahead goes thru trials, a preset value.  If a trial runs longer and has better worth and health, than use its parameters as the next potential set for the real play.  Must still define relation of better health and worth and steps, clearly if 2 are equal and one better, this is a better solution.

Possible parameters are the ...maxn govern the number of computes the health, growth and lookahead parameters are used.  Also the added bias  for health, growth and lookahead needs can help in the choice of value.  Note computation is done on unbiased values, the biases are only used in the strategy selection process.  The process of remembering swap amounts probably is not useful.

The display  laptop w1366 h768, desktop w1280-h1024
reducing tabbed pane (control panels) to 500 500  had a large gray window, with a small window and table inside.  setting the bounds on the jframe 0 0 w1000 h700

Set values for trading based on results at the 5'th step.  When a run makes several tries.  Save the previous try in saveData(-2).  Have a method to compare #5 to #-2 and save it if appropriate, based on a better worth score.

Establish a value for each cargo, each guest service and knowledge and information differences.  Do swapping to test a given trade option.  Allow clan trades which are more friendly than other trades. Allow friends trades (defined friendship), allow rescue trades to save a sick planet which sent an SOS. Each clan decides whether to rescue its own members, or also friends.  allow decommisioning  trades which remove all possible resources and persons and allow a unhealthy planet or ship to die.

11/23/2013  the values of minXRIncr seem strange.  This because the values of limACbyX seem wrong, probably related to acsglim.  Change to acRes and sgRes which represent the fraction of Working resource with maintenance and growth requirements already removed, that is to be reserved.  Also note that the wellness, health and growth values are all related to work not staff, so make a conversion for that.

7/2013 I found I had to stop using global variables, since class routines can be invoked from several different instantiations, and all of the variables from that instantiation must be conveyed in some way in a method call.  It is enough to add the variable "this", which gives access to a specific set of variables in an instantiation.

Moved to simplify the swap routines, so that functions are separated as much as possible.  This means that for example there is a Resource swap1 and a Personnel swap1, but they use common routines calcMovd1 and calcMovd2,  fixes to move calculation is isolated into only those routines.  I also centralized the personnel move and death routines into 1 routine putSwaps.  Again this means all corrections are localized in one routine for move and death calculations.  Resource instances including personnel are designated at Working elements (available, staffConsumers) or Reserved elements (cargo, guests).

ideas for testing
/**
   If <code>aText</code> does not satisfy {@link Util#textHasContent}, then 
   throw an <code>IllegalArgumentException</code>.
  
   <P>Most text used in an application is meaningful only if it has visible content.
  */
  


  /**
   If <tt>aNumber</tt> is less than <tt>1</tt>, then throw an 
   <tt>IllegalArgumentException</tt>.
 
  public static void checkForPositive(int aNumber) {
    if (aNumber < 1) {
      throw new IllegalArgumentException(aNumber + " is less than 1");
    }
  }
 */
  /**
   If {@link Util#matches} returns <tt>false</tt>, then 
   throw an <code>IllegalArgumentException</code>. 
 
  public static void checkForMatch(Pattern aPattern, String aText){
    if ( ! Util.matches(aPattern, aText) ){
      throw new IllegalArgumentException(
        "Text " + Util.quote(aText) + " does not match '" +aPattern.pattern()+ "'"
      );
    }
  }
   */
  /**
   If <code>aObject</code> is null, then throw a <code>NullPointerException</code>.
  
   <P>Use cases :
  <pre>
   doSomething( Football aBall ){
     //1. call some method on the argument : 
     //if aBall is null, then exception is automatically thrown, so 
     //there is no need for an explicit check for null.
     aBall.inflate();
    
     //2. assign to a corresponding field (common in constructors): 
     //if aBall is null, no exception is immediately thrown, so 
     //an explicit check for null may be useful here
     Args.checkForNull( aBall );
     fBall = aBall;
     
     //3. pass on to some other method as parameter : 
     //it may or may not be appropriate to have an explicit check 
     //for null here, according the needs of the problem
     Args.checkForNull( aBall ); //??
     fReferee.verify( aBall );
   }
   </pre>

  public static void checkForNull(Object aObject) {
    if ( aObject == null ) {
      throw new NullPointerException();
    }
  }
  
  // PRIVATE //
  private Args(){
    //empty - prevent construction
  }
  
}
 
 e.printStackTrace();

new Throwable().getStackTrace();

10/16/2012 test offline saving
10/15/2012
I am pursuing the problem that resource worth sums are off.  Other sums are off a little.
I need the worth to find the 5'th step with highest worth to use to calculate trade worth of each resource by looking somewhat into the future.
5/9/12 observations
1IL never gets cargo moved, but moves too much staff
with no doLoop or doFailed, should move to doStep but doesn't
4/6/15  Thoughts on trade with the new Assets class.
18)  Initial offer from ship, up to 5 highest strategic requests, negative and then starting from lowest strategic value match the value of the requests to the strategic value ratio.  manuals value is know and counted in ratio.  Offer cash if have some and min health is < 40%  Requests amount are the negative remnant values of highest strategic values.  ReqMaintRemnant,RMTlimitedGHPenRemnant, ReqGrowthMTHPenRemnant set required amounts for health. 
RawGrowthMTHPenRemnant, is too large if there is much penalty, so initial request is Min of the first 3, averaged with the RawGrowthMTHPenRemnant.  Subtracting the strategic value times the requested units. After the first 5, start offering no more than the average positive number of units until the value is again neutral.
17) First check if offer exceeds strategic value ratio, respond with 0, accept.  Planet first offer with the same logic as 18, but remember the initial 50 offer.  Allow any conflicts with 50 offer.  but do not request against ship request of more than .2 of total ship requests, do the top 4 up to 6 requests, 0 any lower requests / ship offers.
Starting at min strategic value,  make offers unless ship made .2 of offers for this item.  continue to make offers until match strategic values ratio of request
16) Ship request only the 4 top request with values no higher than previous values, only request 5 if it is offered.  Repeat ship offers up to ratio.
15) check for accept, keep track of raises rejected, either ruduce later requests, increase offers but not above rejection, or offer cash if urgent and cash available. Planet request only the 4 top requests, 5'th only if offered, repeat planet offers
14) Ship request only 2 top requests no higher than previous requests 3 - 5 only if offered, raise only those a previous raise was not  refused, and only the amount offered, repeat ship offers to offer fraction.
13) planet request only 2 top requests no higher than previous requests.  3 - 5 only if offered and amount of offer. repeat planet offer to offer faction
12) ship request 1 top request no higher than previous, 2 -5 only to amount of planet offer, repeat ship offers to offer fraction
11 planet request top 1 request no higher than previous, 2 - 5 only to amount of ship offer, repeat planet offers to offer fraction
10,9,8,7,6,5,4,3,2 ship planet alternate, only trim what is offered to offer fraction
1) final offer, no change allowed, either ship or planet may jump to this value.  May jumped to at any offer after 45
0) offer accepted as offered. may be issued at any offer after 45.
-1) offer rejected as offered, may be issues at any offer after 45.

Strategic value, multiply a set of resource+cargo and staff+guests
revalue 
Staff nominal value 3, resource nominal value 2
a. both balance to  5 to .4;  3 to .2
b. 3remnant to  6 to .3;      4.5 to .2
c  rawGrowthRemnant 5 to .4;  4 to .2
d. unit growth   5 to .4;  4 to .25
e. next planet values 2 to 4;        ----
f. next next planet values 2.3 3.7;  ----

  multipliers for annual costs 3/15/15 

Required  assets should be 2 to 3 times the annual cost
 Resource maintenance includes maintenance for each sector and should be around .15 to .25 of the resource balance (includes cargo exp) 
  4yr life
  P cargo/resource costs 0life .1, 1struct .3, 2energy .2, 3prop .3   4def .2, 5Gov .2 6Col .1 (.2)
  S cargo/resource  0life .1, 1struc .3, 2energy .5, 3prop 1. 4def .2  5Gov .2 6Col .1   (.3)

Staff Annual costs .02 to .04 balance staff+guests, 30yr life
  P guests/staff 0life .7, 1struct .3, 2 energy .3, 3prop .3  4def .4, 5Gov .2, 6Col .1 (,03)
  S guests/staff 0life .7, 1struc .3, 2energy .5, 3prop 1.  4def .2 5Gov .2 6Col .1  (.03)
religion is not a separate sector, but it is part of 
   defense (against disease, disaster etc.) 
   government (influences how well people work together, 
   col[onists] because it supports colonist collective life.
   planet guests are unemployed workers, not youth, babies or seniors   all of whom in some way are working
    transmuting or repurposing resource (magic or politics) 
      is very costly in resources and staff, 
      but may be the only solution to allow growth, 
      or fix health problems
   
  resource growth is from .2 up to .5 raw growth, but it depends 
    on  staff work for that sector.  Growth decreases 
    from use as they are depleted.  Random crises can reduce balances 
    but bonus growth.

   * of all cost with health penalty at 80 percent difficulty
   * multipliers in E and EA set the various parameters to create these annual costs
   */
/*
4-7/8   continued to develop the annual cycle.
developed the cycle, 
Year start  TRADE
  TRADE costs  to determine what we need and a basis for developing strategic values

Trade occurs independently, ship economies starting from newest to oldest 
are initiated by StarTrader to find a trading planet partner.
Than StarTrader initiates the trade option, and the ship Econ initiates a 
barter in the planet with a given offer.  
When the ship search is initiated,  the Assets.AssetsYr.TRADE class is instantiated by the
Assets.AssetsYr class and the barter is passed to Assets.AssetsYr.Trade.

If a trade is initiated, the economy on Trade initialization determines 
Strategic values of the resources and staff.   It also determines trade max for
each of the resources and staff, and for positive values, moves the tradable 
amount to the cargo and guests balances.
Then offers are passed back and forth as barters until one side finds that
it will "accept" the offer.  Then Offer.accepted is called to complete the trade 
using the references to the resource balances, staff balances and grades and cash
for each trader.  It moves values as specified in the offer and sets the 
term to 0, indicating to each trader that the trade is done and the 
instantiation of Trade can be destroyed.

After the trade, or trades are all done.

StarTrader initiates the YearEnd method which is processed by AssetsYr.\

YearEnd first does a Growth set of cycles determining methods of swapping staff,guests
,cargo, and resources.  The costs of maintenance, travel and growth are all charged
to the working staff and resource subAssets.  

For health there is a maintenance requirement, the concept is that you need a house
to survive, but you don't build a house every year, so the health requirement is 4 or 5 times
the annual maintenance cost, and of course the multiplier for required maintenance 
is adjusted by the game master for each game.  Default maintenance cost is to rebuild 
maintenance in 15 to 20 years.  Maintenance includes every sector, so that it is 
for example not just the house, but the water, electricity, roads, police, teachers,
school etc.  Maintenance has costs for both resources and staff including the costs for
cargo and guests.  The cost of maintenance and all costs are modified by difficulty.
Difficulty is overcome by experience or knowledge, so that maybe 30 or 50 years of 
knowledge growth increases the efficiency to 100% or more, and the costs are
reduced in accordance with efficiency

Around 6 -7/2015 I decided that the whole AssetsYr was not a good temporary storage
class even if unused members are not instantiated. So the class of Assets.HAssetsYr was created
with most of the data members of AssetsYr, also created Assets.HAssetsYr.SubAsset which contains only the variables needed for some kind of future use.  copyy and copyn and copyyn was implemented in Assets.HAssetsYr and Assets.HAssetsYr.SubAsset.
10/20/15 moved trader19 sources from googleDrive to BitTorrentSync.Sync.nb2r.Trader18,  This update required no changes to the trader18 infrastructure files.  This will see if BitTorrent can handle changes

I am hung again, without a RUN.  I rebooted the machine without killing netbeans I think.


6/20/17  thoughts on trading strategy

Planets: get what you need, trade away surpluses, acknowledge ships need more than planets in trade.
Planets keep reserves for 2 reasons, trading potentials increases costs on resources for the year, 
so keep a reserve for the cost increase, also pay attention to the mtgg the future, because the future trader
may not have what you will need in coming years.  Each year a new set of random factors are calculated.
The costs per units change, in addition catastrophies change future needs.
To repurpose is costly, trading reduces repurpose.
The manuals add worth to any trade, no trade, no exchange of manuals.
Manuals need engineers and researchers to turn manuals into common knowledge.
During a trade some new knowledge and some common knowledge can become manual to be traded.

Ships trade for ship needs, and try to trade away high and trade in at a low cost

barter by barter strategy
18 ship to planet, make requests from mtgg years forecast assuming the same costs which is false.
make available mtg amounts with a reserve.  The reserve is a given percent of the costs for that financial 
sector + 5% of the mtgg needs for this sector (if the needs are positive).

17 planet to ship  (receive the ship bib, increase any planet offers: mtg with reserve of .7? clan planet reserve
increase request to mtgg values (count eacj cjamge.  Planet is allowed 2 changes

16 ship to planet  receive planet bid save 1st planet bid.  Limit offers to an the emergency value.
Also limit requests to mtgg or mtg max value, except that the 4 lowest strategic value, accept 2 times
normal request (buy low to trade)

1/25/2018 Proposed Plans
1) get trading to work again, ensure that the new little planet economies get at least an offer to trade
even if the trade is not successful.
   a) limit size of candidate planet for trade -- set trade variables at end of year or
      at the end of init.
   b) all planet trade offers are available to ship trading searches
   c) adjust clan trading expections at each trade success or failure
   d) clan master can change adjust trading exp, distance to trade, value of 
       presious trades with planet
2) minimize memory
   a) AssetsYr, Trade are both temps, with Assets holds year to year values
   b) Planets Assets init invokes AssetsYr, Trade to set trade offer 
   b) Ships search invokes AssetsYr, Trade get trade offer for search
   c) StarTrader passes X (7?) trade options to Ship, enforce Trade limits
3) Develop forwardFund to create new clan ships or planets
   a) Define any limits on staff growth, gradual decline in growth
   b) Finish catastrophy developments
   c) 100 times growth worth at least 3-5 years
   d) 1 death in 3 - 7 years?
4) Offer saved games
   a) allow the saving of a running game, and restarting to a previous version
   b) all objects must permit copy, Econ, Assets, EM, E?, Offer
   c) EM contains most saved game values
   d) EM contains version descriptor, visible for restart choices
5) Long future
   a) flashier playing display showing the clans in each trade, success or not
   b) enhance to run on current version android
2/3/18  The story -- developer version
  This is a trading game, ships that move with limited growth, planets that grow
but have 2 sectors with little growth.  Ships trade resources and staff for the 
limited planet resources, and receive 2 to 4 times more resources and staff to grow
and to be able to trade to other planets.  Successful trades also trade manuals,
written sources of knowledge as staff study the manuals.
  Growth occurs at the end of each year, depending on the fertility of the given
resource and staff sectors, overall growth is set by the GameMaster and may be
changed during the game, or a gameVersion may be saved, than the growth may be
changed. 
 There are 5 clans red orange yellow green blue, with 5 clan masters that seemed enough.  
There are 7 resource sectors, thats what fit in a row on my desktop screen with min, ave,
sum at the end of the row.  The details of the year are kept in a log, except 
the game master sets the year at which logs begin to be truncated as no longer
used.
At the end of a year or 5year the gameMaster and the clanMasters can change
some of the rule values to change the behavior of the game.  This limited
strategic choices are the methods of input to the game.  The stats tab is 
the useful output of the game, showing differences between clans.
*/
