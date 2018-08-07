/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Seppälä
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.specification.app;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.iluwatar.specification.creature.Creature;
import com.iluwatar.specification.creature.Dragon;
import com.iluwatar.specification.creature.Goblin;
import com.iluwatar.specification.creature.KillerBee;
import com.iluwatar.specification.creature.Octopus;
import com.iluwatar.specification.creature.Shark;
import com.iluwatar.specification.creature.Troll;
import com.iluwatar.specification.property.Color;
import com.iluwatar.specification.property.Movement;
import com.iluwatar.specification.selector.ColorSelector;
import com.iluwatar.specification.selector.MovementSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * The central idea of the Specification pattern is to separate the statement of how to match a
 * candidate, from the candidate object that it is matched against. As well as its usefulness in
 * selection, it is also valuable for validation and for building to order.
 * <p>
 * In this example we have a pool of creatures with different properties. We then have defined
 * separate selection rules (Specifications) that we apply to the collection and as output receive
 * only the creatures that match the selection criteria.
 * <p>
 * http://martinfowler.com/apsupp/spec.pdf
 *
 */
public class App {
  
  private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

  /**
   * Program entry point
   */
  public static void main(String[] args) {
    // initialize creatures list
    List<Creature> creatures =
        Arrays.asList(new Goblin(), new Octopus(), new Dragon(), new Shark(), new Troll(),
            new KillerBee());
    // find all walking creatures
    LOGGER.info("Find all walking creatures");
    List<Creature> walkingCreatures =
        creatures.stream().filter(new MovementSelector(Movement.WALKING))
            .collect(Collectors.toList());
    walkingCreatures.stream().forEach(c -> LOGGER.info(c.toString()));
    // find all dark creatures
    LOGGER.info("Find all dark creatures");
    List<Creature> darkCreatures =
        creatures.stream().filter(new ColorSelector(Color.DARK)).collect(Collectors.toList());
    darkCreatures.stream().forEach(c -> LOGGER.info(c.toString()));
    // find all red and flying creatures
    LOGGER.info("Find all red and flying creatures");
    List<Creature> redAndFlyingCreatures =
        creatures.stream()
            .filter(new ColorSelector(Color.RED).and(new MovementSelector(Movement.FLYING)))
            .collect(Collectors.toList());
    redAndFlyingCreatures.stream().forEach(c -> LOGGER.info(c.toString()));
  }
}
