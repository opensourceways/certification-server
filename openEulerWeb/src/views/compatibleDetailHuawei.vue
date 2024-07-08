<template>
  <div class="certificationDetails">
    <div class="content">
      <div class="title">
      <span style="color: #555555" @click="goBack" class="cursor"
      >兼容数据管理管理</span>
        <i class="el-icon-arrow-right"></i>
        <span style="font-weight: bold">{{ contentTitle }}</span>
      </div>
      <div class="top-title">
        <div
            class="update"
            @click="handleUpdate"
            v-if="topForm.status === '已通过'"
        >
          修改
        </div>
        <div class="title-desc">
        <span class="title-name"
        >测评产品名称：{{ topForm.productName }}</span>
          <div class="status" v-if="topForm.status === '审核中'">审核中</div>
          <div class="status-red" v-if="topForm.status === '已驳回'">已驳回</div>
          <div class="status-green" v-if="topForm.status === '已通过'">通过</div>
        </div>
        <div class="title-time" v-if="topForm.status === '已驳回'">
          <div class="time">驳回时间：{{ topForm.handlerTime }}</div>
          <div class="time-desc" :title="topForm.handlerComment">
            驳回原因：{{ topForm.handlerComment }}
          </div>
        </div>
        <div class="title-time" v-if="topForm.status === '已通过'">
          <div class="time">通过时间：{{ topForm.handlerTime }}</div>
          <div class="time-desc" :title="topForm.handlerComment">
            审核意见：{{ topForm.handlerComment }}
          </div>
        </div>
      </div>
      <div class="desc">
        <div class="desc-title">兼容性信息</div>
        <div class="content-desc">
          <div class="column">
            <div class="column1">上传公司</div>
            <div class="column2">{{ form.uploadCompany }}</div>
            <div class="column1">上传人</div>
            <div class="column2">{{ form.createdBy }}</div>
          </div>
          <div class="column">
            <div class="column1">ISV公司名称</div>
            <div class="column2">{{ form.companyName }}</div>
            <div class="column1">测试产品版本号</div>
            <div class="column2">{{ form.productVersion }}</div>
          </div>
          <div class="column">
            <div class="column1">操作系统名称</div>
            <div class="column2">{{ form.systemName }}</div>
            <div class="column1">操作系统版本号</div>
            <div class="column2">{{ form.systemVersion }}</div>
          </div>
          <div class="column">
            <div class="column1">硬件算力平台</div>
            <div class="column2">{{ form.serverPlatform }}</div>
            <div class="column1">硬件厂商</div>
            <div class="column2">{{ form.serverSupplier }}</div>
          </div>
          <div class="column">
            <div class="column1">硬件型号</div>
            <div class="column2">{{ form.serverModel }}</div>
            <div class="column1">产品类型</div>
            <div class="column2">{{ form.productType }}</div>
          </div>
          <div class="column">
            <div class="column1">产品子类型</div>
            <div class="column2">{{ form.productSubtype }}</div>
            <div class="column1">申请区域</div>
            <div class="column2">{{ form.region }}</div>
          </div>
          <div class="column">
            <div class="column1">发证时间</div>
            <div class="column2">{{ form.issuanceDate }}</div>
          </div>
          <div class="column">
            <div class="column1">产品功能介绍</div>
            <div class="column3">
              {{ form.productFunctions }}
            </div>
          </div>
          <div class="column">
            <div class="column1">使用场景介绍</div>
            <div class="column3">
              {{ form.usageScene }}
            </div>
          </div>
        </div>
        <div
            class="desc-title"
            style="margin-top: 40px"
            v-if="topForm.status ==='审核中'"
        >
          审核
        </div>
        <div
            style="margin-top: 34px; margin-left: -32px"
            v-if="topForm.status === '审核中'"
        >
          <el-form
              :model="ruleForm"
              :rules="rules"
              ref="ruleForm"
              label-width="100px"
              class="demo-ruleForm"
          >
            <el-form-item label="审核结果" prop="resource">
              <el-radio-group v-model="ruleForm.resource">
                <el-radio :label="1">通过</el-radio>
                <el-radio :label="2">驳回</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="审核意见" prop="desc">
              <el-input
                  placeholder="请输入内容"
                  resize="none"
                  type="textarea"
                  v-model="ruleForm.desc"
                  maxlength="1000"
                  show-word-limit
              ></el-input>
            </el-form-item>
          </el-form>
        </div>
        <div
            class="buttons"
            style="margin-left: 68px"
            v-if="topForm.status === '审核中'"
        >
          <div class="button1" @click="handleResult1">确定</div>
          <div class="button2" @click="goBack">取消</div>
        </div>
        <div class="back" v-if="topForm.status != '审核中'" @click="goBack">
          返回
        </div>
      </div>
    </div>
    <el-dialog
        title="修改审核结果"
        :visible.sync="dialogVisible"
        width="550px"
        center
    >
      <div>
        <div style="padding: 0 24px 0 0px">
          <div style="margin-top: 0px; margin-left: -20px">
            <el-form
                :model="ruleForm"
                :rules="rules"
                ref="ruleForm"
                label-width="100px"
                class="demo-ruleForm"
            >
              <el-form-item label="审核结果" prop="resource">
                <el-radio-group v-model="ruleForm.resource">
                  <el-radio :label="1" disabled>通过</el-radio>
                  <el-radio :label="2">驳回</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="审核意见" prop="desc">
                <el-input
                    placeholder="请输入内容"
                    resize="none"
                    type="textarea"
                    v-model="ruleForm.desc"
                    maxlength="1000"
                    show-word-limit
                ></el-input>
              </el-form-item>
            </el-form>
          </div>
          <div class="buttons1" style="margin-left: 80px">
            <div class="button1" @click="handleResult">确定</div>
            <div class="button2" @click="dialogVisible = false">取消</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "compatibleDetail",
  comments: {},
  data() {
    return {
      topForm: {},
      form: {},
      dialogVisible: false,
      ruleForm: {
        resource: "",
        desc: "",
      },
      rules: {
        resource: [
          {required: true, message: "请选着审核结果", trigger: "change"},
        ],
        desc: [{required: true, message: "请填写审核意见"}],
      },
      contentTitle: "详情",
      uuid: "",
    };
  },
  created() {
    this.uuid = this.$route.query.id;
  },
  mounted() {
    this.getUsename();
    this.getTopDetail();
    this.getDetail();
  },
  methods: {
    handleUpdate() {
      this.dialogVisible = true;
      this.$nextTick(() => {
        this.$refs.ruleForm.resetFields();
      });
    },
    handleResult() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          let params = {
            dataIdList: [this.$route.query.dataId],
            handlerResult: this.ruleForm.resource,
            handlerComment: this.ruleForm.desc,
          };
          this.axios
              .post("/compatibleData/approvalCompatibleData", params)
              .then((response) => {
                if (response.data.code === 200) {
                  this.getTopDetail();
                  this.dialogVisible = false;
                  this.$refs.ruleForm.resetFields();
                  this.$message.success("修改成功！");
                } else {
                  this.$message.error(response.data.message);
                }
              })
        } else {
          return false;
        }
      });
    },
    handleResult1() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          let params = {
            dataIdList: [this.$route.query.dataId],
            handlerResult: this.ruleForm.resource,
            handlerComment: this.ruleForm.desc,
          };
          this.axios
              .post("/compatibleData/approvalCompatibleData", params)
              .then((response) => {
                if (response.data.code === 200) {
                  this.goBack();
                  this.$message.success("审核成功！");
                } else {
                  this.$message.error(response.data.message);
                }
              })
        } else {
          return false;
        }
      });
    },
    getTopDetail() {
      this.axios
          .get("/compatibleData/getDataApprovalInfo", {
            params: {
              dataId: this.$route.query.dataId,
            },
          })
          .then((response) => {
            if (response.data.code === 200) {
              this.topForm = response.data.result;
            } else {
              this.$message.error(response.data.message);
            }
          })
    },
    getDetail() {
      this.axios
          .get("/compatibleData/getDataDetailInfo", {
            params: {
              dataId: this.$route.query.dataId,
            },
          })
          .then((response) => {
            if (response.data.code === 200) {
              this.form = response.data.result;
            } else {
              this.$message.error(response.data.message);
            }
          })
    },
    goBack() {
      this.axios
          .get("/user/getUserInfo", {})
          .then((res) => {
            if (res.data.result.roles.indexOf('china_region') !== -1) {
              this.$router.push('/enterpriseAudit')
            } else if (res.data.result.roles.indexOf('admin') !== -1) {
              this.$router.push('/adminApproval')
            } else {
              this.$router.push({
                path: "/",
                query: {
                  isactive: "false",
                },
              });
            }
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message)
          });
    },
    getUsename() {
      this.axios
          .get("/user/getUserInfo", {})
          .then((res) => {
            this.useName = res.data.result
            this.$store.commit('changeStatus', this.useName)
          })
          .catch((err) => {
            this.$message.error(err?.response?.data?.message)
          });
    },
  },
};
</script>

<style lang="less" scoped>
.buttons1 {
  display: flex;
  //justify-content: center;
  margin-top: 40px;

  .button1 {
    width: 80px;
    height: 32px;
    background: #000000;
    font-size: 16px;
    color: #ffffff;
    text-align: center;
    line-height: 32px;
    cursor: pointer;
  }

  .button2 {
    margin-left: 19px;
    width: 80px;
    height: 32px;
    border: 1px solid #000000;
    font-size: 16px;
    text-align: center;
    line-height: 32px;
    color: #000000;
    cursor: pointer;
  }
}

::v-deep .el-textarea .el-input__count {
  line-height: 12px;
  background: none;
}

::v-deep .el-textarea__inner {
  border-radius: 0;
  height: 126px;
  width: 400px;
}

::v-deep .el-textarea {
  width: 400px;
}

::v-deep .el-radio__inner::after {
  width: 9px;
  height: 9px;
  background-color: #000000;
}

::v-deep .el-radio__input.is-checked .el-radio__inner {
  border-color: #000000;
  background: none;
}

::v-deep .el-radio__input.is-checked + .el-radio__label {
  color: #000000;
}

.buttons {
  display: flex;
  //justify-content: center;
  margin-top: 40px;

  .button1 {
    width: 144px;
    height: 48px;
    background: #000000;
    font-size: 16px;
    color: #ffffff;
    text-align: center;
    line-height: 48px;
    cursor: pointer;
  }

  .button2 {
    margin-left: 19px;
    width: 144px;
    height: 48px;
    border: 1px solid #000000;
    font-size: 16px;
    text-align: center;
    line-height: 48px;
    color: #000000;
    cursor: pointer;
  }
}

.desc {
  //height: 642px;
  background: #ffffff;
  box-shadow: 0px 12px 32px 0px rgba(190, 196, 204, 0.2);
  margin-top: 24px;
  padding: 40px 48px;

  .back {
    width: 144px;
    height: 48px;
    border: 1px solid #000000;
    text-align: center;
    line-height: 48px;
    font-size: 16px;
    margin-top: 40px;
    cursor: pointer;
  }

  .desc-title {
    font-size: 20px;
    color: #000000;
  }

  .content-desc {
    padding-top: 24px;

    .column {
      display: flex;
      font-size: 14px;
      color: #000000;
      margin-bottom: 16px;

      .column1 {
        width: 138px;
      }

      .column2 {
        width: 362px;
        padding-right: 20px;
      }

      .column3 {
        width: 1182px;
        word-break: break-all;
        text-align: justify;
      }
    }
  }
}

.title-time {
  display: flex;
  padding-left: 48px;
  padding-top: 12px;
  color: #4f5661;
  font-size: 14px;

  .time {
    width: 225px;
    border-right: 1px solid #dfe5ef;
  }

  .time-desc {
    margin-left: 20px;
    width: 800px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.title-desc {
  display: flex;
  padding-left: 48px;
  padding-top: 27px;
  align-items: center;

  .title-name {
    font-size: 22px;
  }

  .status {
    width: 72px;
    height: 24px;
    background: #52a3ff;
    border-radius: 2px;
    color: #ffffff;
    font-size: 14px;
    line-height: 24px;
    text-align: center;
    margin-left: 20px;
  }

  .status-red {
    width: 72px;
    height: 24px;
    background: #e32020;
    border-radius: 2px;
    color: #ffffff;
    font-size: 14px;
    line-height: 24px;
    text-align: center;
    margin-left: 20px;
  }

  .status-green {
    width: 72px;
    height: 24px;
    background: #24ab36;
    border-radius: 2px;
    color: #ffffff;
    font-size: 14px;
    line-height: 24px;
    text-align: center;
    margin-left: 20px;
  }
}

.top-title {
  position: relative;
  width: 100%;
  height: 120px;
  background: #ffffff;

  .update {
    position: absolute;
    width: 144px;
    height: 48px;
    border: 1px solid #000000;
    text-align: center;
    line-height: 48px;
    top: 36px;
    right: 40px;
    cursor: pointer;
  }
}

.certificationDetails {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;

  .content {
    width: 1416px;

    .title {
      height: 96px;
      line-height: 96px;
      font-size: 12px;

      i {
        margin: 0 4px;
      }
    }
  }
}
</style>