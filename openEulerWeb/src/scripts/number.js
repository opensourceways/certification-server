
/**
 * 分割固定数字的整数部分如1000.20 => 1,000.20
 * @param value 原始数字
 * @param divider 分隔符
 */

export function divideNumber(value, divider = ',') {
    if (!value) {
        return value
    }
    const valueStr = value.toString().split('.')
    const array = valueStr[0].split('').reverse()
    const copyArray = []
    let result = ''
    for (let i = 0; i < array.length; i++) {
        if (i % 3 === 0 && i !== 0) {
            copyArray.push(divider)
        }
        copyArray.push(array[i])
    }
    result = copyArray.reverse().join('')
    if (valueStr[1]) {
        result += '.' + valueStr[1]
    }
    return result
}