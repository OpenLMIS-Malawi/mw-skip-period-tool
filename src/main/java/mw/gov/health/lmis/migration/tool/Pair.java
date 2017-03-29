package mw.gov.health.lmis.migration.tool;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Pair<L, R> {
  private final L left;
  private final R right;
}
