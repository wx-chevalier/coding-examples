/**
 * 数组扁平化
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
