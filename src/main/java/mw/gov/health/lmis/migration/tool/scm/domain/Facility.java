package mw.gov.health.lmis.migration.tool.scm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Facility {
  private String code;
  private String name;
  private String type;
  private String supplyingCode;
  private String program;
  private String contact;
  private String address1;
  private String address2;
  private String city;
  private String state;
  private String rescode;
  private String phone;
  private String fax;
  private Boolean dispense;
  private Double maxMonthsOfSupply;
  private Double minMonthsOfSupply;
  private Boolean active;
  private Boolean warehouse;
  private Boolean highestLevel;
  private Integer role;
  private String dataCenter;
  private String id;

}
