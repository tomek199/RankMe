Feature: Player

  Background: I init and cleanup database
    Given I create league "To init league scheme"
    * I create player "To init player scheme" in league "To init league scheme"
    * I cleanup database

  Scenario: Create new player
    Given I create league "Transformers"
    When I create player "Optimus Prime" in league "Transformers"
    Then I have player "Optimus Prime" with deviation 350 and rating 1500

  Scenario: Play draw game
    Given I create league "Marvel"
    * I create player "Batman" in league "Marvel"
    * I create player "Superman" in league "Marvel"
    When I play game between "Batman" and "Superman" with result 1 : 1
    Then I have player "Batman" with deviation 290 and rating 1500
    * I have player "Superman" with deviation 290 and rating 1500

  Scenario: Play two games where player one won both
    Given I create league "Marvel"
    * I create player "Batman" in league "Marvel"
    * I create player "Superman" in league "Marvel"
    When I play game between "Batman" and "Superman" with result 2 : 1
    * I play game between "Batman" and "Superman" with result 3 : 0
    Then I have player "Batman" with deviation 263 and rating 1721
    * I have player "Superman" with deviation 263 and rating 1279

  Scenario: Play few games between three players and list games for players
    Given I create league "Star Wars"
    * I create player "Yoda" in league "Star Wars"
    * I create player "Chewbacca" in league "Star Wars"
    * I create player "R2D2" in league "Star Wars"
    When I play 3 games between "Yoda" and "Chewbacca"
    * I play 4 games between "Chewbacca" and "R2D2"
    * I play 5 games between "R2D2" and "Yoda"
    Then I have first 12 of 12 games connected in league "Star Wars"
    * I have player "Chewbacca" with first 7 of 7 games connected
    * I have player "R2D2" with first 5 of 9 games connected
    * I have player "Yoda" with first 4 of 8 games connected
