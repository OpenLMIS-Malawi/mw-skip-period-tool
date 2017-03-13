package org.openlmis.migration.tool.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "tblMtDataChangeLog")
public class DataChangeLog implements Serializable {
  private static final long serialVersionUID = 2806693811003739689L;

  @Id
  @Column(name = "dcl_lngID")
  private Integer id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "dtl_lngID")
  private DataTransferLog dataTransferLog;

  @Column(name = "dcl_memNotes")
  private String notes;

}
