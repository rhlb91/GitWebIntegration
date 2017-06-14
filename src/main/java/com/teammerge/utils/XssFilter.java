package com.teammerge.utils;


/**
 * Defines the contract for an XSS filter implementation.
 *
 *
 */
public interface XssFilter {

	/**
	 * Returns a filtered version of the input value that contains no html
	 * elements.
	 *
	 * @param input
	 * @return a plain text value
	 */
	String none(String input);

	/**
	 * Returns a filtered version of the input that contains structural html
	 * elements.
	 *
	 * @param input
	 * @return a filtered html value
	 */
	String relaxed(String input);

	/**
	 * A NOOP XSS filter.
	 *
	 * @author James Moger
	 *
	 */
	public class AllowXssFilter implements XssFilter {

		@Override
		public String none(String input) {
			return input;
		}

		@Override
		public String relaxed(String input) {
			return input;
		}

	}

}

