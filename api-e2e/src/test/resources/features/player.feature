Feature: Player

  Background: I init and cleanup database
    Given I cleanup database
    * I create league "Marvel"
    * I use league "Marvel"

  Scenario: Create and list players
    When I create player "Optimus Prime"
    * I create player "Bumblebee"
    * I create player "Megatron"
    Then I have player "Optimus Prime" with deviation 350 and rating 1500
    * I have player "Bumblebee" with deviation 350 and rating 1500
    * I have player "Megatron" with deviation 350 and rating 1500
    * I have players in league:
      | name          | deviation | rating |
      | Optimus Prime | 350       | 1500   |
      | Bumblebee     | 350       | 1500   |
      | Megatron      | 350       | 1500   |

  Scenario: Play draw game
    * I create player "Batman"
    * I create player "Superman"
    When I play game between "Batman" and "Superman" with result 1 : 1
    Then I have player "Batman" with deviation 290 and rating 1500
    * I have player "Superman" with deviation 290 and rating 1500

  Scenario: Play two games where player one won both
    * I create player "Batman"
    * I create player "Superman"
    When I play game between "Batman" and "Superman" with result 2 : 1
    * I play game between "Batman" and "Superman" with result 3 : 0
    Then I have player "Batman" with deviation 263 and rating 1721
    * I have player "Superman" with deviation 263 and rating 1279

  Scenario: Play few games between three players and list games for players
    * I create player "Superman"
    * I create player "Batman"
    * I create player "Spiderman"
    When I play 3 games between "Superman" and "Batman"
    * I schedule 2 games between "Batman" and "Superman" in 4 hours
    * I play 4 games between "Batman" and "Spiderman"
    * I schedule 5 games between "Spiderman" and "Batman" in 5 hours
    * I play 5 games between "Spiderman" and "Superman"
    * I schedule 3 games between "Superman" and "Spiderman" in 6 hours
    Then I have 22 games connected in league
    * I have player "Superman" with 13 games connected
    * I have player "Batman" with 14 games connected
    * I have player "Spiderman" with 17 games connected
    * I have player "Superman" with 8 completed and 5 scheduled games connected
    * I have player "Batman" with 7 completed and 7 scheduled games connected
    * I have player "Spiderman" with 9 completed and 8 scheduled games connected