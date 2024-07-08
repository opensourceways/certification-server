import Vue from 'vue'
import VueRouter from 'vue-router'
import Home from '../views/Home.vue'
import AccountCenter from "../views/accountCenter.vue"
import AdminApproval from "../views/adminApproval.vue"
import newApproval from "../views/newApproval.vue"
import EnterpriseAudit from "../views/enterpriseAudit.vue"
import EnterpriseAuditDetails from "../views/enterpriseAuditDetails.vue"
import TechnicalCertification from "../views/technicalCertification.vue"
import CertificationDetails from "../views/certificationDetails.vue"
import CertificationDetailsHuawei from "../views/certificationDetailsHuawei.vue"
import RedirectJump from "../views/redirectJump.vue"
import priacyPolicy from "../views/privacyPolicy.vue"
import legalNotice from "../views/legalNotice.vue"
import compatibilityProtocol from "../views/compatibilityProtocol.vue"
import compatibleDetail from "../views/compatibleDetail.vue"
import compatibleDetailHuawei from "../views/compatibleDetailHuawei.vue"
import compatibilityChecklist from "../views/compatibilityChecklist.vue"
import cookieProtocols from "../views/cookieProtocols.vue"
import personalInformation from "../views/personalInformation.vue"


Vue.use(VueRouter)

const routes = [
    {
        path: '/web/certification',
        name: 'certification',
        component: Home
    },
    {
        path: '/',
        name: 'home',
        component: Home
    },
    {
        path: '/redirectJump',
        name: 'redirectJump',
        component: RedirectJump
    },
    {
        path: '/accountCenter',
        name: 'accountCenter',
        component: AccountCenter
    },
    {
        path: '/adminApproval',
        name: 'adminApproval',
        component: AdminApproval
    },
    {
        path: '/adminApproval/newApproval',
        name: 'newApproval',
        component: newApproval
    },
    {
        path: '/enterpriseAudit',
        name: 'enterpriseAudit',
        component: EnterpriseAudit
    },
    {
        path: '/enterpriseAuditDetails',
        name: 'enterpriseAuditDetails',
        component: EnterpriseAuditDetails
    },
    {
        path: '/technicalCertification',
        name: 'technicalCertification',
        component: TechnicalCertification
    },
    {
        path: '/certificationDetails',
        name: 'certificationDetails',
        component: CertificationDetails
    },
    {
        path: '/certificationDetailsHuawei',
        name: 'certificationDetailsHuawei',
        component: CertificationDetailsHuawei
    },
    {
        path: '/priacyPolicy',
        name: 'priacyPolicy',
        component: priacyPolicy
    },
    {
        path: '/legalNotice',
        name: 'legalNotice',
        component: legalNotice
    },
    {
        path: '/compatibilityProtocol',
        name: 'compatibilityProtocol',
        component: compatibilityProtocol
    },
    {
        path: '/compatibleDetail',
        name: 'compatibleDetail',
        component: compatibleDetail
    },
    {
        path: '/compatibleDetailHuawei',
        name: 'compatibleDetailHuawei',
        component: compatibleDetailHuawei
    },
    {
        path: '/compatibilityChecklist',
        name: 'compatibilityChecklist',
        component: compatibilityChecklist
    },
    {
        path: '/cookieProtocols',
        name: 'cookieProtocols',
        component: cookieProtocols
    },
    {
        path: '/personalInformation',
        name: 'personalInformation',
        component: personalInformation
    },
]

const router = new VueRouter({
    routes
})

export default router