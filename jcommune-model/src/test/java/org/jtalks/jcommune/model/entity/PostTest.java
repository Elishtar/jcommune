/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.model.entity;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Kirill Afonin
 */
public class PostTest {
    private static final String LONG_TEXT = "Lorem ipsum dolor sit amet, " +
            "consectetur adipisicing elit, sed do eiusmod tempor incididunt ut " +
            "labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud " +
            "exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
            "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum " +
            "dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non " +
            "proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
    private static final String SHORT_TEXT = "labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud";

    Post post = new Post();

    @Test
    public void shortContentShouldBeMax200Symbols() {
        post.setPostContent(LONG_TEXT);

        String shortContent = post.getShortContent();

        assertTrue(shortContent.length() <= 200);
    }

    @Test
    public void shortContentShouldEndsWithThreeDots() {
        post.setPostContent(LONG_TEXT);

        String shortContent = post.getShortContent();

        assertTrue(shortContent.endsWith("..."));
    }

    @Test
    public void shortContentAndShortTextShouldBeEqual() {
        post.setPostContent(SHORT_TEXT);

        String shortContent = post.getShortContent();

        assertEquals(shortContent, SHORT_TEXT);
    }

}
