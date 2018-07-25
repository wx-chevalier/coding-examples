/**
 * 数组扁平化，即将 [2,[3,4],[5,[6,[7]]]] 扁平化为 [2,3,4,5,6,7]
 */

export const flatternArray = arr => {
  const result = [];

  arr.forEach(item => {
    if (Array.isArray(item)) {
      result.push(...flatternArray(item));
    } else {
      result.push(item);
    }
  });

  return result;
};

export const flatternArrayByReduce = arr => {
  return arr.reduce((prev, itemOrArr) => {
    return Array.isArray(itemOrArr)
      ? [...prev, ...flatternArrayByReduce(itemOrArr)]
      : [...prev, itemOrArr];
  }, []);
};

/**
 * 嵌套数组求和，求 [2,[3,4],[5,[6,[7]]]] 中所有元素相加的和
 */
export const flatternArraySumByReduce = arr => {
  return arr.reduce((prev, itemOrArr) => {
    return Array.isArray(itemOrArr)
      ? prev + flatternArraySumByReduce(itemOrArr)
      : prev + itemOrArr;
  }, 0);
};
