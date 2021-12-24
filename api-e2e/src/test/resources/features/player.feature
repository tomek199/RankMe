Feature: Player

  Background: I init and cleanup database
    Given I cleanup database
    * I create league "Marvel"
    * I use league "Marvel"

  Scenario: Create new player
    When I create player "Optimus Prime"
    Then I have player "Optimus Prime" with deviation 350 and rating 1500

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
    * I play 4 games between "Batman" and "Spiderman"
    * I play 5 games between "Spiderman" and "Superman"
    Then I have first 12 of 12 games connected in league
    * I have player "Batman" with first 7 of 7 games connected
    * I have player "Spiderman" with first 5 of 9 games connected
    * I have player "Superman" with first 4 of 8 games connected
