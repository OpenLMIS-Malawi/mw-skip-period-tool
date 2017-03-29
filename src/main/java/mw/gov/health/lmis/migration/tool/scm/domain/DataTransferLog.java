package mw.gov.health.lmis.migration.tool.scm.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@Entity(name = "tblMTDataTransferLog")
public class DataTransferLog implements Serializable {
  private static final long serialVersionUID = 467000460769873062L;

  @Id
  @Column(name = "dtl_lngID")
  private Integer id;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "dc_GuidID", columnDefinition = "character")
  private DataCenter dataCenter;

  @Column(name = "dtl_bytTransferType")
  private Short transferType;

  @Column(name = "dtl_datTransfer")
  private LocalDateTime transferDate;

  @Column(name = "dtl_strFileName")
  private String fileName;

  @Column(name = "dtl_memNotes")
  private String notes;

  @Column(name = "dtl_lngCounter")
  private Integer counter;

  @Column(name = "dtl_GuidFileID", columnDefinition = "character")
  private String fileId;

  @Column(name = "dtl_fResult")
  private Boolean result;

}
