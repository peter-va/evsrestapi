
package gov.nih.nci.evs.api.properties;

import java.util.Map;

/**
 * The Class ThesaurusProperties.
 */
public class ThesaurusProperties {

  /** The sources - for source definitions. */
  private Map<String, String> sources;

  /** The term types - for term type definitions. */
  private Map<String, String> termTypes;

  /**
   * Returns the sources.
   *
   * @return the sources
   */
  public Map<String, String> getSources() {
    return sources;
  }

  /**
   * Sets the sources.
   *
   * @param sources the sources
   */
  public void setsources(Map<String, String> sources) {
    this.sources = sources;
  }

  /**
   * Returns the term types.
   *
   * @return the term types
   */
  public Map<String, String> getTermTypes() {
    return termTypes;
  }

  /**
   * Sets the term types.
   *
   * @param termTypesSources the term types sources
   */
  public void setTermTypes(Map<String, String> termTypes) {
    this.termTypes = termTypes;
  }

}
