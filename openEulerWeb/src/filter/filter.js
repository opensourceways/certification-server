import Vue from 'vue'

import { divideNumber } from '@/scripts/number'

Vue.filter('divideNumber', value => divideNumber(value))