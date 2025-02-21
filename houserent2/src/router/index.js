import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        redirect: '/login'
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login.vue')
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/Register.vue')
    },
// 普通用户布局
    {
        path: '/user',
        name: 'UserLayout',
        component: () => import('@/layouts/UserLayout.vue'),
        meta: {role: 'user'},
        children: [
            {
                path: 'home',
                name: 'UserHome',
                component: () => import('@/views/user/UserHome.vue')
            },
            {
                path: 'houseManage',
                name: 'UserHouseManage',
                component: () => import('@/views/user/UserHouseManage.vue')
            },
            {
                path: 'appointment',
                name: 'UserAppointment',
                component: () => import('@/views/user/UserAppointment.vue')
            },
            {
                path: 'order',
                name: 'UserOrder',
                component: () => import('@/views/user/UserOrder.vue')
            },
            {
                path: 'rentPayment',
                name: 'UserRentPayment',
                component: () => import('@/views/user/UserRentPayment.vue')
            },
            {
                path: 'returnHouse',
                name: 'UserReturnHouse',
                component: () => import('@/views/user/UserReturnHouse.vue')
            },
            {
                path: 'repair',
                name: 'UserRepair',
                component: () => import('@/views/user/UserRepair.vue')
            },
            {
                path: 'profile',
                name: 'UserProfile',
                component: () => import('@/views/user/UserProfile.vue')
            },
            {
                path: 'changePwd',
                name: 'UserChangePwd',
                component: () => import('@/views/user/UserChangePwd.vue')
            }
        ]
    },
// 房主布局
    {
        path: '/landlord',
        name: 'LandlordLayout',
        component: () => import('@/layouts/LandlordLayout.vue'),
        meta: {role: 'landlord'},
        children: [
            {
                path: 'home',
                name: 'LandlordHome',
                component: () => import('@/views/landlord/LandlordHome.vue')
            },
            {
                path: 'houseManage',
                name: 'LandlordHouseManage',
                component: () => import('@/views/landlord/LandlordHouseManage.vue')
            },
            {
                path: 'orderManage',
                name: 'LandlordOrderManage',
                component: () => import('@/views/landlord/LandlordOrderManage.vue')
            },
            {
                path: 'rentPaymentManage',
                name: 'LandlordRentPaymentManage',
                component: () => import('@/views/landlord/LandlordRentPaymentManage.vue')
            },
            {
                path: 'returnHouseManage',
                name: 'LandlordReturnHouseManage',
                component: () => import('@/views/landlord/LandlordReturnHouseManage.vue')
            },
            {
                path: 'repairManage',
                name: 'LandlordRepairManage',
                component: () => import('@/views/landlord/LandlordRepairManage.vue')
            },
            {
                path: 'profile',
                name: 'LandlordProfile',
                component: () => import('@/views/landlord/LandlordProfile.vue')
            },
            {
                path: 'changePwd',
                name: 'LandlordChangePwd',
                component: () => import('@/views/landlord/LandlordChangePwd.vue')
            }
        ]
    },
// 管理员布局
    {
        path: '/admin',
        name: 'AdminLayout',
        component: () => import('@/layouts/AdminLayout.vue'),
        meta: {role: 'admin'},
        children: [
            {
                path: 'home',
                name: 'AdminHome',
                component: () => import('@/views/admin/AdminHome.vue')
            },
            {
                path: 'userManage',
                name: 'AdminUserManage',
                component: () => import('@/views/admin/AdminUserManage.vue')
            },
            {
                path: 'landlordManage',
                name: 'AdminLandlordManage',
                component: () => import('@/views/admin/AdminLandlordManage.vue')
            },
            {
                path: 'categoryManage',
                name: 'AdminCategoryManage',
                component: () => import('@/views/admin/AdminCategoryManage.vue')
            },
            {
                path: 'houseManage',
                name: 'AdminHouseManage',
                component: () => import('@/views/admin/AdminHouseManage.vue')
            },
            {
                path: 'appointmentManage',
                name: 'AdminAppointmentManage',
                component: () => import('@/views/admin/AdminAppointmentManage.vue')
            },
            {
                path: 'orderManage',
                name: 'AdminOrderManage',
                component: () => import('@/views/admin/AdminOrderManage.vue')
            },
            {
                path: 'rentPaymentManage',
                name: 'AdminRentPaymentManage',
                component: () => import('@/views/admin/AdminRentPaymentManage.vue')
            },
            {
                path: 'returnHouseManage',
                name: 'AdminReturnHouseManage',
                component: () => import('@/views/admin/AdminReturnHouseManage.vue')
            },
            {
                path: 'repairManage',
                name: 'AdminRepairManage',
                component: () => import('@/views/admin/AdminRepairManage.vue')
            },
            {
                path: 'newsManage',
                name: 'AdminNewsManage',
                component: () => import('@/views/admin/AdminNewsManage.vue')
            },
            {
                path: 'carouselManage',
                name: 'AdminCarouselManage',
                component: () => import('@/views/admin/AdminCarouselManage.vue')
            },
            {
                path: 'profile',
                name: 'AdminProfile',
                component: () => import('@/views/admin/AdminProfile.vue')
            },
            {
                path: 'changePwd',
                name: 'AdminChangePwd',
                component: () => import('@/views/admin/AdminChangePwd.vue')
            }
        ]
    },
// 404
    {
        path: '*',
        name: 'NotFound',
        component: () => import('@/views/NotFound.vue')
    }
]

const router = new VueRouter({
    mode: 'history',
    base: process.env.BASE_URL,
    routes
})

/**
 * 路由守卫示例：
 * 简单思路：如果访问需要登录/角色才能进入的页面，就检查当前 store 中的用户信息
 * 若没有登录或者角色不匹配，则跳转到登录页或提示无权限。
 */
router.beforeEach((to, from, next) => {
    const token = store.state.user.token
    const userRole = store.state.user.role

// 如果目标路由需要角色
    if (to.meta && to.meta.role) {
        if (!token) {
// 未登录
            return next('/login')
        } else {
// 有登录，判断角色是否匹配
            if (userRole === to.meta.role) {
                next()
            } else {
// 如果角色不匹配，可以重定向或提示
                return next('/login')
            }
        }
    } else {
// 不需要角色权限的路由，直接放行
        next()
    }
})

export default router
