/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package loottables;

/**
 * An interface for retrieving a loot table from loot_tables.yml or from an individual loot table file.
 */
public interface LootConfig {

    /**
     * Retrieves the loot table with the specified name.
     *
     * @param name The name of the loot table to retrieve.
     * @return The loot table by that name or null if none found.
     */
    LootTable getLootTable(String name);
}
