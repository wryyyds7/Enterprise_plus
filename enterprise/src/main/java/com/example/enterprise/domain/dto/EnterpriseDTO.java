package com.example.enterprise.domain.dto;

import com.example.common.domain.entity.Enterprise;
import com.example.common.domain.entity.Position;

import java.util.List;

public class EnterpriseDTO {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String website;
    private String regNo;//注册号码
    private String operName;//法人
    private List<Position> position;//招聘职位

    private Integer page;
    private Integer size;
    public EnterpriseDTO(Long id, String name, String address, String phone, String email, String website, String regNo, String operName, List<Position> position) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.regNo = regNo;
        this.operName = operName;
        this.position = position;
    }
    public EnterpriseDTO(Enterprise enterprise, Integer page, Integer size) {
        this.id = enterprise.getEnterpriseId();
        this.name = enterprise.getName();
        this.address = enterprise.getAddress();
        this.phone = enterprise.getPhone();
        this.email = enterprise.getEmail();
        this.website = enterprise.getWebsite();
        this.regNo = enterprise.getRegNo();
        this.operName = enterprise.getOperName();
        this.position = enterprise.getPosition();
        this.page = page;
        this.size = size;
    }

    public Enterprise transferToEnterprise(EnterpriseDTO enterpriseDTO){
        return new Enterprise(enterpriseDTO.getId(), enterpriseDTO.getName(), enterpriseDTO.getAddress(),
                enterpriseDTO.getPhone(), enterpriseDTO.getEmail(), enterpriseDTO.getWebsite(),
                enterpriseDTO.getRegNo(), enterpriseDTO.getOperName(), enterpriseDTO.getPosition()
        );
    }

    public EnterpriseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public List<Position> getPosition() {
        return position;
    }

    public void setPosition(List<Position> position) {
        this.position = position;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
