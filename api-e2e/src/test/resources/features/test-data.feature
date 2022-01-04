@Ignore
Feature: Test data generator

  Scenario: I create "Star Wars", players and play multiple games
    Given I cleanup database
    And I create league "Star Wars"
    * I use league "Star Wars"
    And I create player "Han Solo"
    * I create player "Yoda"
    * I create player "Luke Skywalker"
    * I create player "Chewbacca"
    * I create player "Darth Vader"
    And I play 8 games between "Han Solo" and "Yoda"
    * I play 7 games between "Chewbacca" and "Luke Skywalker"
    * I play 12 games between "Luke Skywalker" and "Han Solo"
    * I play 10 games between "Darth Vader" and "Yoda"
    * I play 9 games between "Yoda" and "Han Solo"
    * I play 5 games between "Han Solo" and "Chewbacca"
    * I play 14 games between "Luke Skywalker" and "Yoda"
    * I play 6 games between "Han Solo" and "Darth Vader"
    * I schedule game between "Han Solo" and "Luke Skywalker" in 4 hours
    * I play 13 games between "Chewbacca" and "Luke Skywalker"
    * I schedule game between "Yoda" and "Chewbacca" in 1 hours
    * I schedule game between "Darth Vader" and "Luke Skywalker" in 2 hours

  Scenario: I create multiple leagues
    * I create league "Marvel"
    * I create league "Transformers"
    * I create 5 leagues
