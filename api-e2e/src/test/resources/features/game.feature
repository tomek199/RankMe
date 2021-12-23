Feature: Player

  Background: I init and cleanup database
    Given I cleanup database

  Scenario: Play multiple games to check pagination
    Given I create league "Star Wars"
    * I create player "Darth Vader" in league "Star Wars"
    * I create player "Han Solo" in league "Star Wars"
    When I play 10 games between "Darth Vader" and "Han Solo"
    Then I have first 3 of 10 games listed for league "Star Wars"
    * I have first 6 of 10 games listed for league "Star Wars"
    * I have first 10 of 10 games listed for league "Star Wars"
    * I have first 3 after 2 of 10 games listed for league "Star Wars"
    * I have first 4 after 3 of 10 games listed for league "Star Wars"
    * I have first 2 after 8 of 10 games listed for league "Star Wars"

  Scenario: Play multiple games to check games data
    Given I create league "Transformers"
    * I create player "Bumblebee" in league "Transformers"
    * I create player "Optimus Prime" in league "Transformers"
    When I play game between "Bumblebee" and "Optimus Prime" with result 1 : 3
    * I play game between "Optimus Prime" and "Bumblebee" with result 2 : 5
    Then I have 2 games in league "Transformers":
      | firstName     | firstScore | firstRating | firstRatingDelta | firstDeviation | firstDeviationDelta | secondName    | secondScore | secondRating | secondRatingDelta | secondDeviation | secondDeviationDelta |
      | Optimus Prime | 2          | 1430        | -232             | 263            | -27                 | Bumblebee     | 5           | 1570         | 232               | 263             | -27                  |
      | Bumblebee     | 1          | 1338        | -162             | 290            | -60                 | Optimus Prime | 3           | 1662         | 162               | 290             | -60                  |

  Scenario: Schedule and complete game
    Given I create league "Star Wars"
    * I create player "Chewbacca" in league "Star Wars"
    * I create player "Yoda" in league "Star Wars"
    When I play game between "Chewbacca" and "Yoda" with result 0 : 1
    * I schedule game between "Yoda" and "Chewbacca" in 2 hours
    Then I have 2 games in league "Star Wars":
      | firstName     | firstScore | firstRating | firstRatingDelta | firstDeviation | firstDeviationDelta | secondName    | secondScore | secondRating | secondRatingDelta | secondDeviation | secondDeviationDelta |
      | Yoda          |            | 1662        |                  | 290            |                     | Chewbacca     |             | 1338         |                   | 290             |                      |
      | Chewbacca     | 0          | 1338        | -162             | 290            | -60                 | Yoda          | 1           | 1662         | 162               | 290             | -60                  |
    When I complete game between "Yoda" and "Chewbacca" with result 2 : 3
    Then I have 2 games in league "Star Wars":
      | firstName     | firstScore | firstRating | firstRatingDelta | firstDeviation | firstDeviationDelta | secondName    | secondScore | secondRating | secondRatingDelta | secondDeviation | secondDeviationDelta |
      | Yoda          | 2          | 1430        | -232             | 263            | -27                 | Chewbacca     | 3           | 1570         | 232               | 263             | -27                  |
      | Chewbacca     | 0          | 1338        | -162             | 290            | -60                 | Yoda          | 1           | 1662         | 162               | 290             | -60                  |